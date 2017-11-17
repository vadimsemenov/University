package ru.ifmo.ctddev.semenov;

/**
 * Cache stores key-value pairs.
 *
 * @author Vadim Semenov (semenov@rain.ifmo.ru)
 */
public interface Cache<K, V> {
    /**
     * Memoize (key, value) pair in cache.
     * Rewrites previous associated value.
     *
     * @param key associated with a value
     * @param value associated with a key
     * @return {@code true} iff new  {@code (key, value)} pair was introduced
     */
    boolean put(K key, V value);

    /**
     * Returns value associated with {@code key}
     *
     * @param key to find value for
     * @return {@code value} associated with given {@code key}
     */
    V get(K key);
}
