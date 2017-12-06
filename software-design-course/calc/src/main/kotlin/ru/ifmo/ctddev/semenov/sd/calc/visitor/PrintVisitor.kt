package ru.ifmo.ctddev.semenov.sd.calc.visitor

import ru.ifmo.ctddev.semenov.sd.calc.token.*
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter

class PrintVisitor(outputStream: OutputStream, private val rpn: Boolean = true): TokenVisitor, AutoCloseable {
    private val writer = BufferedWriter(OutputStreamWriter(outputStream))
    private var first = true

    private fun print(token: Token) {
        if (first) first = false else writer.write(' '.toInt())
        writer.write(token.toString())
    }

    override fun visit(number: NumberToken) {
        print(number)
    }

    override fun visit(brace: BraceToken) {
        if (rpn) throw IllegalStateException("Found brace '$brace' in Reverse Polish notation") else {
            if (brace is Right) first = true
            print(brace)
            if (brace is Left) first = true
        }
    }

    override fun visit(operation: OperationToken) {
        print(operation)
    }

    override fun close() {
        writer.close()
    }
}