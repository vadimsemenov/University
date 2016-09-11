package ru.ifmo.ctddev.semenov.test;

import org.junit.Test;
import ru.ifmo.ctddev.semenov.Cache;
import ru.ifmo.ctddev.semenov.LruCache;

import static junit.framework.TestCase.*;

/**
 * @author Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class LruCacheTest {
    @Test
    public void testGeneral() {
        int qty = 1000;
        String[] keys = new String[qty];
        Integer[] values = new Integer[qty];
        for (int i = 0; i < values.length; ++i) {
            keys[i] = "" + i;
            values[i] = new Integer(i);
        }
        Cache<String, Integer> cache = new LruCache<>();
        int maxSize = 100;
        for (int i = 0; i < qty; ++i) {
            assertTrue("new key already exists in cache", cache.put(keys[i], values[i]));
            for (int j = 0; j < qty; ++j) {
                if (0 <= i - j && i - j < maxSize) {
                    // should be in cache
                    Integer value = cache.get(keys[j]);
                    assertNotNull("not found existing key", value);
                    assertEquals("found value differs from expected", values[j], value);
                } else {
                    assertNull(cache.get(keys[j]));
                }
            }
        }
    }

    @Test
    public void testLru() {
        String[][] pairs = new String[][]{
                {"key0", "val0"},
                {"key1", "val1"},
                {"key2", "val2"},
                {"key3", "val3"},
                {"key4", "val4"}
        };
        Cache<String, String> cache = new LruCache<>(3);
        assertTrue("new key already exists in cache", put(cache, pairs[0]));
        assertTrue("new key already exists in cache", put(cache, pairs[1]));
        assertTrue("new key already exists in cache", put(cache, pairs[2]));
        // update first pair
        assertEquals("not found", pairs[0][1], cache.get(pairs[0][0]));
        assertTrue("new key already exists in cache", put(cache, pairs[3]));
        assertTrue("new key already exists in cache", put(cache, pairs[4]));
        assertNull("found not existing", cache.get(pairs[1][0]));
        assertNull("found not existing", cache.get(pairs[2][0]));
        assertEquals("not found expected value", pairs[0][1], cache.get(pairs[0][0]));
        assertEquals("not found expected value", pairs[3][1], cache.get(pairs[3][0]));
        assertEquals("not found expected value", pairs[4][1], cache.get(pairs[4][0]));
    }

    private static boolean put(Cache<String, String> cache, String[] pair) {
        return cache.put(pair[0], pair[1]);
    }
}