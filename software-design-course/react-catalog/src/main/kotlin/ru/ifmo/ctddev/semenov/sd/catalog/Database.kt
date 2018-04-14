package ru.ifmo.ctddev.semenov.sd.catalog

import com.mongodb.rx.client.MongoClients
import com.mongodb.rx.client.MongoCollection
import com.mongodb.rx.client.Success
import org.bson.Document
import rx.Observable

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */

interface Database {
    val users: Observable<User>
    fun getProductsForUser(email: String): Observable<Product>
    fun registerUser(user: User): Observable<Success>
    fun addProduct(product: Product): Observable<Success>
}

fun createMongoDB(): MongoDB {
    val client = MongoClients.create()
    val database = client.getDatabase("catalog")
    val usersCollection = database.getCollection(USERS)
    val productsCollection = database.getCollection(PRODUCTS)
    val currencyCollection = database.getCollection(CURRENCIES)
    // cleanup
    usersCollection.drop().toBlocking().single()
    currencyCollection.drop().toBlocking().single()
    currencyCollection.insertMany(listOf(
            Currency("RUB", 61.3).toDocument(),
            Currency("EUR", 0.92).toDocument(),
            Currency("USD", 1.00).toDocument()
    ))
    return MongoDB(usersCollection, productsCollection, currencyCollection)
}

open class MongoDB(
        private var usersCollection: MongoCollection<Document>,
        private var productsCollection: MongoCollection<Document>,
        private var currencyCollection: MongoCollection<Document>
): Database {
    override val users: Observable<User>
        get() {
            return usersCollection.find().toObservable().map { it.toUser() }
        }

    override fun getProductsForUser(email: String): Observable<Product> {
        return usersCollection.find(Document(EMAIL_KEY, email))
                .toObservable()
                .singleOrDefault(null)
                .filter { d -> d != null }
                .map { it.toUser().currency }
                .flatMap { currency ->
                    currencyCollection
                            .find(Document(CURRENCY_KEY, currency))
                            .toObservable()
                            .map { it.toCurrency().multiplier }
                }
                .flatMap { multiplier ->
                    productsCollection.find()
                            .toObservable()
                            .map { it.toProduct().withScaledPrice(multiplier) }
                }
    }

    override fun registerUser(user: User): Observable<Success> {
        return usersCollection.insertOne(user.toDocument())
    }

    override fun addProduct(product: Product): Observable<Success> {
        return productsCollection.insertOne(product.toDocument())
    }
}