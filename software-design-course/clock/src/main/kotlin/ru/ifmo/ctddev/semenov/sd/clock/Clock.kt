package ru.ifmo.ctddev.semenov.sd.clock

import java.time.Instant


interface Clock {
    fun now(): Instant
}

class ActualClock: Clock {
    override fun now(): Instant = Instant.now()
}

class SettableClock(private var instant: Instant): Clock {
    override fun now(): Instant = instant

    fun set(now: Instant) {
        instant = now
    }
}