package ru.ifmo.ctddev.semenov.sd.calc.visitor

import ru.ifmo.ctddev.semenov.sd.calc.pop
import ru.ifmo.ctddev.semenov.sd.calc.token.BraceToken
import ru.ifmo.ctddev.semenov.sd.calc.token.NumberToken
import ru.ifmo.ctddev.semenov.sd.calc.token.OperationToken


class CalcVisitor: TokenVisitor {
    private val stack = arrayListOf<Int>()

    override fun visit(number: NumberToken) {
        stack.add(number.number)
    }

    override fun visit(brace: BraceToken) {
        throw IllegalStateException("Found brace '$brace' in Reverse Polish notation")
    }

    override fun visit(operation: OperationToken) {
        if (stack.size < 2) throw IllegalStateException("Too small stack $stack for operation '$operation'")
        val (rhs, lhs) = Pair(stack.pop(), stack.pop())
        stack.add(operation.apply(lhs, rhs))
    }

    fun get(): Int {
        if (stack.size != 1) throw IllegalStateException("Size of $stack should be 1")
        return stack[0]
    }
}