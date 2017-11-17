package ru.ifmo.ctddev.semenov

import java.util.*

class WeighedLruCache<K, V>(private val capacity: Long, private val weight: (V) -> Long): Cache<K, V> {
    init {
        if (capacity <= 0) {
            throw IllegalArgumentException("Capacity should be positive")
        }
    }

    private val cache = object : LinkedHashMap<K, V>() {
        private var _totalWeight: Long = 0
        val totalWeight
            get() = _totalWeight

        override fun put(key: K, value: V): V? {
            remove(key)
            val w = weight(value)
            if (w < 0) {
                throw IllegalArgumentException("Negative weight")
            }
            _totalWeight += w
            return super.put(key, value)
        }

        override fun get(key: K): V? {
            val value = remove(key)
            if (value != null) {
                val prev = super.put(key, value)
                assert(prev == null)
            }
            return value
        }

        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
            val it = iterator()
            while (_totalWeight > capacity) {
                val w = weight(it.next().value)
                _totalWeight -= w
                it.remove()
            }
            return false
        }

        override fun clear() {
            super.clear()
            _totalWeight = 0
        }
    }

    override fun put(key: K, value: V): Boolean {
        val prev = cache.put(key, value)
        checkWeight()
        return prev == null
    }

    override fun get(key: K): V? {
        checkWeight()
        return cache[key]
    }

    private fun checkWeight() {
        assert(cache.totalWeight <= capacity)
    }
}