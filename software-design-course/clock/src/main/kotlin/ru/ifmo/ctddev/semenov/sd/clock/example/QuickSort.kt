package ru.ifmo.ctddev.semenov.sd.clock.example

import ru.ifmo.ctddev.semenov.sd.clock.ActualClock
import ru.ifmo.ctddev.semenov.sd.clock.EventStatistic
import ru.ifmo.ctddev.semenov.sd.clock.Rpp
import ru.ifmo.ctddev.semenov.sd.clock.RppStatistic
import java.time.temporal.ChronoUnit


fun main(args: Array<String>) {
    val list = arrayListOf(6,5,4,3,2,1)
    val stats = quickSort(list)
    stats.printStatistic()
}

fun <T: Comparable<T>> quickSort(list: MutableList<T>,
                  from: Int = 0,
                  to: Int = list.size,
                  stats: EventStatistic<Rpp> = RppStatistic(ActualClock(), 10, ChronoUnit.MILLIS)
): EventStatistic<Rpp> {
    require(from <= to)
    var (lo, hi) = Pair(from, to)
    while (lo + 1 < hi) {
        val pivot = list[lo]
        var (i, j) = Pair(lo - 1, hi)
        do {
            do {
                ++i
                stats.incEvent(Events.COMPARISON)
            } while (list[i] < pivot)
            do {
                --j
                stats.incEvent(Events.COMPARISON)
            } while (list[j] > pivot)
            if (i < j) {
                stats.incEvent(Events.SWAP)
                val tmp = list[i]
                list[i] = list[j]
                list[j] = tmp
            }
        } while (i < j)
        stats.incEvent(Events.RECURSIVE_CALL)
        j++
        if (j - lo >= hi - j) {
            quickSort(list, j, hi, stats)
            hi = j
        } else {
            quickSort(list, lo, j, stats)
            lo = j
        }
    }
    return stats
}

private object Events {
    val COMPARISON = "Comparison"
    val SWAP = "Swap"
    val RECURSIVE_CALL = "Recursive call"
}