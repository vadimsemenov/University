package ru.ifmo.ctddev.semenov.sd.calc.visitor

import ru.ifmo.ctddev.semenov.sd.calc.token.BraceToken
import ru.ifmo.ctddev.semenov.sd.calc.token.NumberToken
import ru.ifmo.ctddev.semenov.sd.calc.token.OperationToken
import ru.ifmo.ctddev.semenov.sd.calc.token.Token

interface TokenVisitor {
    fun visit(token: Token): Unit = when (token) {
        is NumberToken -> visit(token)
        is BraceToken -> visit(token)
        is OperationToken -> visit(token)
        else -> throw RuntimeException("Unsupported token: " + token)
    }
    fun visit(number: NumberToken)
    fun visit(brace: BraceToken)
    fun visit(operation: OperationToken)
}