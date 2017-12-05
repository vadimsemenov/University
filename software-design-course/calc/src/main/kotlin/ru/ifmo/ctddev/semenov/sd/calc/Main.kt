package ru.ifmo.ctddev.semenov.sd.calc

import ru.ifmo.ctddev.semenov.sd.calc.parser.StateParser
import ru.ifmo.ctddev.semenov.sd.calc.visitor.ParserVisitor
import ru.ifmo.ctddev.semenov.sd.calc.visitor.PrintVisitor
import java.io.OutputStream

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
    }
}

private fun keepOpen(outputStream: OutputStream, action: AlwaysOpenOutputStream.() -> Unit): Unit {
    AlwaysOpenOutputStream(outputStream).apply {
        action()
        forceClose()
    }
}

private class AlwaysOpenOutputStream(private val underlying: OutputStream): OutputStream() {
    override fun write(b: Int) {
        underlying.write(b)
    }

    override fun close() {
        // ignore
    }

    fun `do`(action: (OutputStream) -> Unit) {
        action(this)
        write('\n'.toInt())
    }

    fun forceClose() {
        underlying.close()
    }
}