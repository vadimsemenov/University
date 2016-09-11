package ru.ifmo.ctddev.semenov;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This implementation of LRU Cache is not thread-safe and requires external synchronization
 * Null keys and null values are prohibited. Keys should define correct {@link Object#hashCode() hashCode}
 * method, because this implementation relies on it.
 *
 * @author Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class LruCache<K, V> implements Cache<K, V> {
    private static final int DEFAULT_MAX_SIZE = 100;
    private static final float DEFAULT_LOAD_FACTOR = 0.8f;
    private final Map<K, V> map;

    /**
     * Creates LRU Cache witch stores no more than 100 elements
     */
    public LruCache() {
        this(DEFAULT_MAX_SIZE);
    }

    public LruCache(int maxSize) {
        this(maxSize, DEFAULT_LOAD_FACTOR);
    }

    public LruCache(int maxSize, float loadFactor) {
        if (maxSize < 1) {
            throw new IllegalArgumentException("maxSize should be positive");
        }
        this.map = new LinkedHashMap<K, V>(maxSize, loadFactor) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };
    }

    /**
     * @throws NullPointerException if key or value is null
     */
    @Override
    public boolean put(K key, V value) {
        Objects.requireNonNull(key, "key should be non-null");
        Objects.requireNonNull(value, "value should be non-null");
        // update entry to maintain LRU cache invariants
        V previous = map.remove(key);
        map.put(key, value);
        return previous == null || previous != value;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            return null;
        }
        V value = map.remove(key);
        if (value != null) {
            // update entry to maintain LRU cache invariants
            V previous = map.put(key, value);
            assert previous == null;
        }
        return value;
    }
}
