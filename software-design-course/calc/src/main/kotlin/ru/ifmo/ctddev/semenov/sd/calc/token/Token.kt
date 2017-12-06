package ru.ifmo.ctddev.semenov.sd.calc.token

import ru.ifmo.ctddev.semenov.sd.calc.visitor.TokenVisitor


interface Token {
    fun accept(visitor: TokenVisitor) = visitor.visit(this)
}

val char2token = mapOf(
        '(' to Left,
        ')' to Right,
        '+' to Add,
        '-' to Sub,
        '*' to Mul,
        '/' to Div
)

interface WhitespaceToken: Token

object SpaceToken: WhitespaceToken
object EofToken: WhitespaceToken

data class NumberToken(val number: Int): Token {
    override fun toString(): String = "NUMBER($number)"
}

interface BraceToken: Token

object Left : BraceToken {
    override fun toString(): String = "("
}
object Right: BraceToken {
    override fun toString(): String = ")"
}

interface OperationToken: Token {
    fun apply(lhs: Int, rhs: Int): Int
}

object Add: OperationToken {
    override fun apply(lhs: Int, rhs: Int): Int = lhs + rhs
    override fun toString(): String = "+"
}
object Sub: OperationToken {
    override fun apply(lhs: Int, rhs: Int): Int = lhs - rhs
    override fun toString(): String = "-"
}
object Mul: OperationToken {
    override fun apply(lhs: Int, rhs: Int): Int = lhs * rhs
    override fun toString(): String = "*"
}
object Div: OperationToken {
    override fun apply(lhs: Int, rhs: Int): Int = if (rhs == 0) throw RuntimeException("Division by zero") else
        lhs / rhs
    override fun toString(): String = "/"
}
