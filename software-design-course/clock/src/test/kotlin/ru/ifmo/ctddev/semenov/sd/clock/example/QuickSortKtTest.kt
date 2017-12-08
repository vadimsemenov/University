package ru.ifmo.ctddev.semenov.sd.clock.example

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*


internal class QuickSortKtTest {

    @Test
    fun quickSortPermutationsUpTo7() {
        for (p in 2..7) {
            var list: MutableList<Int>? = ArrayList((1..p).toList())
            do {
                val copy = ArrayList(list)
                quickSort(copy)
                assertTrue(checkSorted(copy)) {
                    "original: $list, result: $copy"
                }
                list = nextPermutation(list!!)
            } while (list != null)
        }
    }

    @Test
    fun quickSortLargeRandom() {
        val times = 10
        val length = 100500
        val rng = Random(58)
        val list = ArrayList<Int>(length)
        for (it in 1..times) {
            list.clear()
            for (i in 0 until length) list.add(rng.nextInt())
            quickSort(list)
            assertTrue(checkSorted(list))
        }
    }

    @Test
    fun quickSortEmpty() {
        val list: MutableList<Int> = arrayListOf()
        quickSort(list)
        assertTrue(checkSorted(list))
    }

    @Test
    fun quickSortSingleton() {
        val list: MutableList<Int> = arrayListOf(100500)
        quickSort(list)
        assertTrue(checkSorted(list))
    }

    private fun <T: Comparable<T>> checkSorted(list: List<T>): Boolean {
        return (1 until list.size - 1).none { list[it] < list[it - 1] }
    }

    private fun <T: Comparable<T>> nextPermutation(permutation: List<T>): MutableList<T>? {
        val list = ArrayList(permutation)
        val idx = (list.size - 1 downTo 1)
                .firstOrNull { list[it] > list[it - 1] }
                ?.let { it - 1 } ?: return null
        val jdx = (idx + 1 until list.size)
                .filter { list[it] > list[idx] }
                .minBy { list[it] }!!
        val tmp = list[idx]
        list[idx] = list[jdx]
        list[jdx] = tmp
        Collections.sort(list.subList(idx + 1, list.size))
        return list
    }
}