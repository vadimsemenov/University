package ru.ifmo.ctddev.semenov.sd.calc

import ru.ifmo.ctddev.semenov.sd.calc.parser.StateParser
import ru.ifmo.ctddev.semenov.sd.calc.visitor.CalcVisitor
import ru.ifmo.ctddev.semenov.sd.calc.visitor.ParserVisitor
import ru.ifmo.ctddev.semenov.sd.calc.visitor.PrintVisitor

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
fun main(args: Array<String>) {
    val parser = StateParser(System.`in`)
    val list = parser.parse()
    keepOpen(System.out) {
        `do` {
            PrintVisitor(it, false).use { visitor ->
                list.forEach { visitor.visit(it) }
            }
        }

        `do` {
            PrintVisitor(it).use { visitor ->
                ParserVisitor(list).parse().forEach { visitor.visit(it) }
            }
        }

        `do` { output ->
            CalcVisitor().let { calc ->
                ParserVisitor(list).parse().forEach { calc.visit(it) }
                output.print(calc.get())
            }
        }
    }
}