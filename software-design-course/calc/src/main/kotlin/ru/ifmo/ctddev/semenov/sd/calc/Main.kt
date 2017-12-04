package ru.ifmo.ctddev.semenov.sd.calc

import ru.ifmo.ctddev.semenov.sd.calc.parser.StateParser

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
fun main(args: Array<String>) {
    val parser = StateParser(System.`in`)
    val list = parser.parse()
    println(list)
}