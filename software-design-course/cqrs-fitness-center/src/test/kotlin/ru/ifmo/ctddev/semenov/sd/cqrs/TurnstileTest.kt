package ru.ifmo.ctddev.semenov.sd.cqrs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.util.*


internal class TurnstileTest {
    private val random = Random(58)

    private lateinit var repo: InMemoryRepo
    private lateinit var manager: Manager
    private lateinit var turnstile: Turnstile

    private lateinit var startTime: LocalDateTime
    private lateinit var clients: MutableList<Client>
    private lateinit var clientsInside: Set<Uid>

    @BeforeEach
    fun setUp() {
        repo = InMemoryRepo()
        manager = Manager(repo)
        turnstile = Turnstile(repo)
        startTime = LocalDateTime.of(
                LocalDate.of(2018, Month.JANUARY, 1),
                LocalTime.of(7, 0)
        )
        clients = mutableListOf()
        repeat(123) { newClient(startTime.plusMonths(random.nextInt(12) + 1L)) }
        clientsInside = mutableSetOf()
    }

    @Test
    fun simulateYear() {
        val finishTime = startTime.plusYears(1)
        var currentTime = startTime
        while (currentTime < finishTime) {
            val prob = random.nextDouble()
            when {
                prob < 0.01 -> newClient(currentTime)
                prob < 0.02 -> advanceSubscription()
                prob < 0.6  -> checkin(currentTime)
                else        -> checkout(currentTime)
            }
            currentTime = currentTime.plusMinutes(random.nextInt(50) + 1L)
        }
    }

    private fun checkin(time: LocalDateTime) {
        val idx = random.nextInt(clients.size)
        val (uid, until, inside) = clients[idx]
        if (inside || time >= until) {
            assertThrows<IllegalArgumentException> { turnstile.checkin(uid, time) }
        } else {
            val event = turnstile.checkin(uid, time)
            assertEquals(uid, event.uid)
            assertEquals(time, event.time)
            assertTrue(repo.isInside(uid))
            clients[idx] = Client(uid, until, true)
        }
    }

    private fun checkout(time: LocalDateTime) {
        val idx = random.nextInt(clients.size)
        val (uid, until, inside) = clients[idx]
        if (!inside) {
            assertThrows<IllegalArgumentException> { turnstile.checkout(uid, time) }
        } else {
            val event = turnstile.checkout(uid, time)
            assertEquals(uid, event.uid)
            assertEquals(time, event.time)
            assertTrue(!repo.isInside(uid))
            clients[idx] = Client(uid, until, false)
        }
    }

    private fun advanceSubscription() {
        val idx = random.nextInt(clients.size)
        val (uid, expiration, inside) = clients[idx]
        val newExpiration = expiration.plusMonths(random.nextInt(3) + 1L)
        val event = manager.advanceSubscription(uid, newExpiration)
        assertEquals(uid, event.uid)
        assertEquals(newExpiration, event.until)
        assertEquals(newExpiration, repo.subscribedUntil(uid))
        clients[idx] = Client(uid, newExpiration, inside)
    }

    private fun newClient(until: LocalDateTime = startTime.plusMonths(6)): Client {
        val event = manager.createUser("Masha #${random.nextInt(100500)}")
        manager.advanceSubscription(event.uid, until)
        val client = Client(event.uid, until)
        clients.add(client)
        return client
    }

    data class Client(val uid: Uid, val until: LocalDateTime, val inside: Boolean = false)
}