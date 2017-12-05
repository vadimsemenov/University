package ru.ifmo.ctddev.semenov.sd.calc

import java.io.OutputStream
import java.io.PrintStream


fun <T> ArrayList<T>.pop(): T {
    if (isEmpty()) throw IllegalStateException("Stack is empty") else {
        val result = this[lastIndex]
        this.removeAt(lastIndex)
        return result
    }
}

internal fun keepOpen(outputStream: OutputStream, action: AlwaysOpenOutputStream.() -> Unit) {
    AlwaysOpenOutputStream(outputStream).apply {
        action()
        forceClose()
    }
}

internal class AlwaysOpenOutputStream(underlying: OutputStream): PrintStream(underlying) {
    fun `do`(action: (PrintStream) -> Unit) {
        action(this)
        println()
    }

    override fun close() {
        // ignore
    }

    fun forceClose() {
        super.close()
    }
}