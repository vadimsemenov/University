package ru.ifmo.ctddev.semenov.sd.clock

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit


internal class RppStatisticTest {
    private val periodQty = 10
    private val minute = ChronoUnit.MINUTES
    private val second = ChronoUnit.SECONDS
    private lateinit var clock: SettableClock
    private lateinit var stats: EventStatistic<Rpp>

    @BeforeEach
    fun setup() {
        clock = SettableClock(Instant.now().truncatedTo(minute))
        stats = RppStatistic(clock, periodQty, minute)
    }

    @Test
    fun incEvent() {
        stats.run {
            inc("a")
            inc("b")
            inc("a")
            inc("c")
            inc("a")
            inc("b")
            inc("a")
        }
    }

    @Test
    fun getEventStatisticByName() {
        val foo = "foo"
        val bar = "bar"
        stats.inc(foo)
        stats.inc(bar)
        clock.add(1, second)
        stats.inc(foo)
        assertEquals(2, stats.qty(foo))
        clock.add(3, minute)
        assertEquals(0, stats.qty(foo))
        assertEquals(0, stats.qty(bar))
        stats.inc(foo)
        stats.inc(bar)
        assertEquals(1, stats.qty(foo))
        assertEquals(1, stats.qty(bar))
    }

    @Test
    fun getAllEventStatistic() {
        val foo = "foo"
        val bar = "bar"
        val baz = "baz"

        stats.inc(foo)
        stats.inc(bar)
        clock.add(30, second)
        stats.inc(baz)
        assertEquals(3, stats.allQty())

        clock.add(8, minute)
        stats.inc(bar)
        stats.inc(foo)
        assertEquals(5, stats.allQty())

        clock.add(2, minute)
        stats.inc(baz)
        assertEquals(6, stats.allQty())

        clock.add(1, minute)
        assertEquals(3, stats.allQty())
        clock.add(20, second)
        stats.inc(baz)
        assertEquals(4, stats.allQty())
    }

    private fun SettableClock.add(qty: Long, timeUnit: TemporalUnit) {
        set(now().plus(qty, timeUnit))
    }

    private fun EventStatistic<Rpp>.inc(eventName: EventName) {
        val qty = qty(eventName)
        incEvent(eventName)
        assertEquals(qty + 1, qty(eventName))
    }

    private fun EventStatistic<Rpp>.qty(eventName: EventName): Int = getEventStatisticByName(eventName).getEvents().size

    private fun EventStatistic<Rpp>.allQty(): Int = getAllEventStatistic().flatMap { it.getEvents() }.size
}
