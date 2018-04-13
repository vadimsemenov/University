package ru.ifmo.ctddev.semenov.sd.cqrs

import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicLong


typealias Uid = Long

sealed class UserEvent(open val uid: Uid)

data class UserCreated(override val uid: Uid, val name: String) : UserEvent(uid)

data class UserCheckined(override val uid: Uid, val time: LocalDateTime) : UserEvent(uid)

data class UserCheckouted(override val uid: Uid, val time: LocalDateTime) : UserEvent(uid)

data class SubscriptionAdvanced(override val uid: Uid, val until: LocalDateTime) : UserEvent(uid)


interface EventsRepository {
    fun getEvents(uid: Uid): Sequence<UserEvent>
}

interface EventWriteRepository : EventsRepository {
    fun saveEvent(event: UserEvent)
    fun getNextUid(): Uid
}

class InMemoryRepo : EventsRepository, EventWriteRepository {
    private val events = LinkedBlockingQueue<UserEvent>()
    private val counter = AtomicLong()

    override fun getEvents(uid: Uid): Sequence<UserEvent> =
            events.asSequence().filter { it.uid == uid }

    override fun saveEvent(event: UserEvent): Unit =
            events.put(event)

    override fun getNextUid(): Uid = counter.incrementAndGet()
}

fun <T : UserEvent> EventWriteRepository.saving(producer: () -> T): T {
    val event = producer()
    saveEvent(event)
    return event
}

fun <T : UserEvent> EventWriteRepository.creating(producer: (Uid) -> T): T =
        saving { producer(getNextUid()) }

fun EventsRepository.subscribedUntil(uid: Uid): LocalDateTime =
        (getEvents(uid).lastOrNull { it is SubscriptionAdvanced } as SubscriptionAdvanced?)?.until ?: LocalDateTime.MIN

fun EventsRepository.isInside(uid: Uid): Boolean {
    var inside = false
    for (event in getEvents(uid)) when (event) {
        is UserCheckined  -> {
            assert(!inside)
            inside = true
        }
        is UserCheckouted -> {
            assert(inside)
            inside = false
        }
    }
    return inside
}

class Turnstile(private val repo: EventWriteRepository) {
    fun checkin(uid: Uid, time: LocalDateTime = LocalDateTime.now()): UserCheckined {
        require(time < repo.subscribedUntil(uid)) { "Subscription expired" }
        require(!repo.isInside(uid)) { "Client's already inside" }
        return repo.saving { UserCheckined(uid, time) }
    }

    fun checkout(uid: Uid, time: LocalDateTime = LocalDateTime.now()): UserCheckouted {
        require(repo.isInside(uid)) { "Client's outside" }
        return repo.saving { UserCheckouted(uid, time) }
    }
}

class Manager(private val repo: EventWriteRepository) {
    fun createUser(name: String): UserCreated =
            repo.creating { UserCreated(it, name) }

    fun advanceSubscription(uid: Uid, until: LocalDateTime): SubscriptionAdvanced {
        require(repo.subscribedUntil(uid) < until) { "Client is already subscribed for longer period" }
        return repo.saving { SubscriptionAdvanced(uid, until) }
    }
}

class Summary(private val repo: EventsRepository) {
    fun averageSession(uid: Uid): Duration {
        var qty = 0L
        var duration = Duration.ZERO
        var prevCheckin: LocalDateTime? = null
        for (event in repo.getEvents(uid)) when (event) {
            is UserCheckined  -> {
                qty++
                prevCheckin = event.time
            }
            is UserCheckouted -> {
                requireNotNull(prevCheckin)
                duration += Duration.between(prevCheckin, event.time)
            }
        }
        return if (qty == 0L) duration else duration.dividedBy(qty)
    }

    fun averageVisitsPerWeek(
            uid: Uid,
            from: LocalDateTime = LocalDateTime.MIN,
            to: LocalDateTime = LocalDateTime.now()
    ): Double {
        var qty = 0L
        var firstVisit: LocalDateTime? = null
        for (event in repo.getEvents(uid)) {
            if (event is UserCheckined && event.time in from..to) {
                qty++
                firstVisit = firstVisit ?: event.time
            }
        }
        return if (firstVisit == null) 0.0 else qty / 7.0
    }
}