package ru.ifmo.ctddev.semenov.sd.catalog


import rx.Observable
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.netty.protocol.http.server.HttpServer
import io.netty.buffer.ByteBuf
import io.reactivex.netty.protocol.http.server.HttpServerRequest
import io.reactivex.netty.protocol.http.server.HttpServerResponse
import com.google.gson.Gson
import java.nio.charset.StandardCharsets

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
fun main(args: Array<String>) {
    HttpServer.newServer(8090)
            .start { request, response ->
                val method = request.httpMethod
                val path = request.decodedPath
                println("$method $path on ${Thread.currentThread().name}")
                when {
                    method == HttpMethod.POST && path == "/register" -> router.postRegisterRoute(request, response)
                    method == HttpMethod.GET && path == "/users"     -> router.getUsersRoute(response)
                    method == HttpMethod.GET && path == "/products"  -> router.getProductsRoute(request, response)
                    method == HttpMethod.POST && path == "/products" -> router.postProductsRoute(request, response)
                    else                                             -> response.apply { status = HttpResponseStatus.NOT_FOUND }
                }.doOnError { it.printStackTrace() }
            }
            .awaitShutdown()
}

val router = Router(createMongoDB())

class Router(private val mongoDB: Database) {
    private val gson = Gson()

    internal fun postRegisterRoute(
            request: HttpServerRequest<ByteBuf>,
            response: HttpServerResponse<ByteBuf>
    ): Observable<Void> = request.content
            .map { byteBuf -> byteBuf.toString(StandardCharsets.UTF_8) }
            .reduce("") { s, s2 -> s + s2 }
            .map { s -> gson.fromJson(s, User::class.java) }
            .flatMap { user ->
                println(user)
                if (user == null) {
                    response.status = HttpResponseStatus.BAD_REQUEST
                    response.writeString(Observable.just("Error: Specify user"))
                } else {
                    mongoDB.registerUser(user)
                            .flatMap { _ ->
                                response.setStatus(HttpResponseStatus.OK)
                            }
                }
            }

    internal fun getUsersRoute(response: HttpServerResponse<ByteBuf>): Observable<Void> {
        return Observable.defer {
            mongoDB.users.doOnNext { user -> response.writeString(Observable.just("$user\n")) }
                    .doOnCompleted { response.setStatus(HttpResponseStatus.OK) }
                    .lastOrDefault(null)
                    .flatMap { response }
        }
    }

    internal fun getProductsRoute(
            request: HttpServerRequest<ByteBuf>,
            response: HttpServerResponse<ByteBuf>
    ): Observable<Void> {
        val email = request.getHeader("x-email")
        println("email: $email")
        return Observable.defer {
            mongoDB.getProductsForUser(email)
                    .toList()
                    .single()
                    .doOnNext { list ->
                        response.apply {
                            writeString(Observable.just(gson.toJson(list)))
                            setStatus(HttpResponseStatus.OK)
                        }
                    }
                    .flatMap { response }
        }
    }

    internal fun postProductsRoute(
            request: HttpServerRequest<ByteBuf>,
            response: HttpServerResponse<ByteBuf>
    ): Observable<Void> = request.content
            .map { byteBuf -> byteBuf.toString(StandardCharsets.UTF_8) }
            .reduce("") { s1, s2 -> s1 + s2 }
            .map { s -> gson.fromJson(s, Product::class.java) }
            .flatMap { product ->
                if (product == null) response.apply {
                    status = HttpResponseStatus.BAD_REQUEST
                    writeString(Observable.just("Error: product is null"))
                }
                else mongoDB.addProduct(product).flatMap {
                    response.apply { status = HttpResponseStatus.OK }
                }
            }
}
