package ru.ifmo.ctddev.semenov.sd.clock

import java.time.Instant
import java.time.temporal.TemporalUnit
import java.util.*


typealias EventName = String

interface EventStatistic<out T> {
    fun incEvent(name: EventName) = incEvent(name, null)
    fun incEvent(name: EventName, comment: String?)
    fun getEventStatisticByName(name: EventName): T
    fun getAllEventStatistic(): List<T>
    fun printStatistic()
}

fun Instant.inside(begin: Instant, end: Instant) = this in begin..end

data class Event(val eventName: EventName, val timestamp: Instant, val comment: String?) {
    override fun toString(): String {
        return "Event(eventName=$eventName, timestamp=$timestamp" +
                (if (comment == null) "" else ", comment=$comment") + ")"
    }
}

class Rpp(val begin: Instant, val end: Instant) {
    private val events: MutableList<Event> = arrayListOf()

    fun getEvents(): List<Event> = events

    internal fun put(event: Event) {
        if (!event.timestamp.inside(begin, end)) {
            throw IllegalArgumentException("Event $event is out of interval [$begin, $end)")
        }
        var pos = Collections.binarySearch(events, event, Comparator.comparing<Event, Instant> { it.timestamp })
        if (pos < 0) pos = -pos - 1
        events.add(pos, event)
    }

    internal fun filter(predicate: (Event) -> Boolean): Rpp {
        val result = Rpp(begin, end)
        result.events += events.filter(predicate)
        return result
    }

    override fun toString(): String {
        return "Requests per period [$begin, $end): $events"
    }
}

class RppStatistic(private val clock: Clock, private val periodQty: Int, private val timeUnit: TemporalUnit): EventStatistic<Rpp> {
    private val queue = ArrayDeque<Rpp>(periodQty)

    override fun incEvent(name: EventName, comment: String?) = sync { now ->
        val event = Event(name, now, comment)
        while (queue.isEmpty() || queue.last.end <= event.timestamp) {
            val from = if (queue.isEmpty()) now.truncatedTo(timeUnit) else queue.last.end
            val rpp = Rpp(from, from.plus(1, timeUnit))
            queue.add(rpp)
        }
        assert(event.timestamp.inside(queue.last.begin, queue.last.end))
        queue.last.put(event)
    }

    override fun getEventStatisticByName(name: EventName): Rpp = sync { _ ->
        queue.last.filter { it.eventName == name }
    }

    override fun getAllEventStatistic(): List<Rpp> = sync { _ ->
        listOf(*queue.toTypedArray())
    }

    override fun printStatistic() = sync { now ->
        val stats = StringBuilder("Requests per period stats: ").run {
            append("now = ")
            append(now)
            append(", stats:\n")
            for (rpp in queue.reversed()) {
                append(rpp)
                append('\n')
            }
            toString()
        }
        println(stats)
    }

    private fun <T> sync(action: RppStatistic.(Instant) -> T): T {
        val now = clock.now()
        while (queue.isNotEmpty() && queue.peek().end < now.minus(periodQty.toLong(), timeUnit)) {
            queue.poll()
        }
        return action(now)
    }
}
