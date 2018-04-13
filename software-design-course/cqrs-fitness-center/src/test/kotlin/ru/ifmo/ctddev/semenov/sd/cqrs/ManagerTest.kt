package ru.ifmo.ctddev.semenov.sd.cqrs

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


internal class ManagerTest {
    private lateinit var executor: ExecutorService

    private lateinit var repo: InMemoryRepo
    private lateinit var manager: Manager

    @BeforeEach
    fun setup() {
        executor = Executors.newFixedThreadPool(16)!!
        repo = InMemoryRepo()
        manager = Manager(repo)
    }

    @AfterEach
    fun teardown() {
        executor.shutdownNow()
        executor.awaitTermination(2, TimeUnit.SECONDS)
    }

    @Test
    fun createUser() {
        val times = 1000
        val uidSet = mutableSetOf<Uid>()
        val futures = Array(times) {
            executor.submit {
                val name = "Vasya #$it"
                val event = manager.createUser(name)
                assertFalse(uidSet.contains(event.uid))
                uidSet.add(event.uid)
                assertEquals(name, event.name)
            }
        }
        executor.shutdown()
        futures.forEach { it.get() }
        for (uid in uidSet) {
            val events = repo.getEvents(uid).toList()
            assertEquals(1, events.size)
            assertTrue(events[0] is UserCreated)
            assertEquals(uid, (events[0] as UserCreated).uid)
        }
    }
}