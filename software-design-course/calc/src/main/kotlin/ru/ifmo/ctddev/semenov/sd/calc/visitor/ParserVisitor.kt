package ru.ifmo.ctddev.semenov.sd.calc.visitor

import ru.ifmo.ctddev.semenov.sd.calc.parser.Parser
import ru.ifmo.ctddev.semenov.sd.calc.token.*

class ParserVisitor(private val normalList: List<Token>): TokenVisitor, Parser {
    private val stack = arrayListOf<Token>()
    private val ops = arrayListOf<OperationToken>()
    private val open = arrayListOf<Int>()

    private val op2priority = mapOf(
            Add to 1,
            Sub to 1,
            Mul to 2,
            Div to 2
    )

    private fun OperationToken.priority(): Int = op2priority[this] ?:
            throw IllegalStateException("Unknown operation: '$this'")

    private fun <T> ArrayList<T>.pop(): T {
        if (isEmpty()) throw IllegalStateException("Stack is empty") else {
            val result = this[lastIndex]
            this.removeAt(lastIndex)
            return result
        }
    }

    override fun visit(number: NumberToken) {
        stack.add(number)
    }

    override fun visit(brace: BraceToken) {
        when (brace) {
            is Left -> open.add(ops.size)
            is Right -> {
                if (open.isEmpty()) throw IllegalStateException("There was no opening parenthesis") else {
                    val bottom = open.pop()
                    while (bottom < ops.size) {
                        stack.add(ops.pop())
                    }
                }
            }
            else -> throw IllegalStateException("Unknown brace: '$brace'")
        }
    }

    override fun visit(operation: OperationToken) {
        val bottom = if (open.size > 0) open[open.lastIndex] else 0
        while (ops.size > bottom && ops[ops.lastIndex].priority() >= operation.priority()) {
            stack.add(ops.pop())
        }
        ops.add(operation)
    }

    override fun parse(): List<Token> {
        visit(Left)
        normalList.forEach { it.accept(this) }
        visit(Right)
        if (!ops.isEmpty()) throw IllegalStateException("Operations left: $ops")
        return stack
    }
}