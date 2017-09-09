package ru.ifmo.ctddev.semenov

import junit.framework.TestCase.*
import org.junit.Test

class WeighedLruCacheTest: LruCacheTest() {
    override fun <K, V> createLruCache(maxSize: Int): Cache<K, V> {
        return WeighedLruCache(maxSize.toLong(), { _ -> 1 })
    }

    @Test
    fun testWeighed() {
        val cache = WeighedLruCache<Long, Long>(3, { it })
        val one: Long = 1
        val two: Long = 2
        val three: Long = 3
        val ten: Long = 10

        assertNull(cache.get(one))
        cache.put(one, one)
        assertNotNull(cache.get(one))

        assertNull(cache.get(two))
        cache.put(two, two)
        assertNotNull(cache.get(two))

        assertNotNull(cache.get(one))
        assertNotNull(cache.get(two))

        assertNull(cache.get(three))
        cache.put(three, three)
        assertNotNull(cache.get(three))

        assertNull(cache.get(one))
        assertNull(cache.get(two))

        assertNull(cache.get(ten))
        cache.put(ten, ten)

        assertNull(cache.get(ten))
        assertNull(cache.get(one))
        assertNull(cache.get(two))
        assertNull(cache.get(three))
    }
}
