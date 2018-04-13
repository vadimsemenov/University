package ru.ifmo.ctddev.semenov.sd.cqrs

import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.pipeline.PipelineContext
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.html.body
import kotlinx.html.p
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

lateinit var manager: Manager
lateinit var turnstile: Turnstile
lateinit var summary: Summary

val log = LoggerFactory.getLogger("ru.ifmo.ctddev.semenov.sd.ServerKt")!!

@Suppress("unused")
fun Application.main() {
    install(CallLogging)
    install(DefaultHeaders)
    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            call.respondText { "404 :/" }
        }
    }
    routing {
        get("/manager/registerClient") { registerClient() }
        get("/manager/advanceSubscription") { advanceSubscription() }
        get("/turnstile/checkin") { onTurnstile { uid, time -> tryCheckin(uid, time) } }
        get("/turnstile/checkout") { onTurnstile { uid, time -> tryCheckout(uid, time) } }
        get("/summary/perWeek") { perWeekSummary()}
        get("/summary/averageSession") { averageSessionSummary() }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.registerClient() {
    val name = call.request.queryParameters["name"]
    when (name) {
        null -> call.respondText { "Error: please specify 'name'" }
        else -> {
            val (uid, _) = manager.createUser(name)
            call.respondText { "$name is successfully registered!\nHis uid is $uid" }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.advanceSubscription() {
    val uid = call.request.queryParameters["uid"]
    val until = call.request.queryParameters["until"]
    when {
        uid == null   -> call.respondText { "Error: please specify 'uid'" }
        until == null -> call.respondText { "Error: please specify 'until'" }
        else          -> try {
            log.debug("... advancing subscription: $uid, $until")
            val event = manager.advanceSubscription(uid.toLong(), LocalDateTime.parse(until))
            log.debug("... advanced event: $event")
            call.respondHtml {
                body {
                    p {
                        +"User #$uid advanced subscription until $until"
                    }
                }
            }
        } catch (e: Exception) {
            call.respondText { "Error: ${e.message}" }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.onTurnstile(
        action: suspend PipelineContext<Unit, ApplicationCall>.(Uid, LocalDateTime) -> Unit
) {
    val uid = call.request.queryParameters["uid"]
    val timeParameter = call.request.queryParameters["time"]
    val time = if (timeParameter == null) LocalDateTime.now() else LocalDateTime.parse(timeParameter)
    log.debug("Client #$uid: checked in/out at $time")
    when (uid) {
        null -> call.respondText { "Error: please specify 'uid'" }
        else -> action(uid.toLong(), time)
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.tryCheckin(uid: Uid, time: LocalDateTime) {
    try {
        turnstile.checkin(uid = uid, time = time)
        call.respondText { "User #$uid checked in" }
    } catch (e: Exception) {
        call.respondText { "Checkin error: ${e.message}" }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.tryCheckout(uid: Uid, time: LocalDateTime) {
    try {
        turnstile.checkout(uid = uid, time = time)
        call.respondText { "User #$uid checked out" }
    } catch (e: Exception) {
        call.respondText { "Checkout error: ${e.message}" }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.perWeekSummary() {
    try {
        val uid = call.request.queryParameters["uid"]
        val fromParameter = call.request.queryParameters["time"]
        val from = if (fromParameter == null) LocalDateTime.MIN else LocalDateTime.parse(fromParameter)
        val toParameter = call.request.queryParameters["time"]
        val to = if (toParameter == null) LocalDateTime.MAX else LocalDateTime.parse(toParameter)
        when (uid) {
            null -> call.respondText { "Error: please specify 'uid'" }
            else -> {
                val averageVisitsQty = summary.averageVisitsPerWeek(uid = uid.toLong(), from = from, to = to)
                call.respondText { "User #$uid visits $averageVisitsQty times per week" }
            }
        }
    } catch (e: Exception) {
        call.respondText { "Summary error: ${e.message}" }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.averageSessionSummary() {
    try {
        val uid = call.request.queryParameters["uid"]
        when (uid) {
            null -> call.respondText { "Error: please specify 'uid'" }
            else -> {
                val averageDuration = summary.averageSession(uid = uid.toLong())
                call.respondText { "Average duration of client #$uid is $averageDuration" }
            }
        }
    } catch (e: Exception) {
        call.respondText { "Summary error: ${e.message}" }
    }
}
