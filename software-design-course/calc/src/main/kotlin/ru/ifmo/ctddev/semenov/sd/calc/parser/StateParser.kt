package ru.ifmo.ctddev.semenov.sd.calc.parser

import ru.ifmo.ctddev.semenov.sd.calc.token.*
import java.io.InputStream
import java.io.InputStreamReader


class StateParser(inputStream: InputStream): Parser {
    private val reader = InputStreamReader(inputStream)
    private var state: State = Begin

    override fun parse(): List<Token> {
        val result = arrayListOf<Token>()
        while (state !is Eof) {
            val input = reader.read()
            val (token, nextState) = state.nextState(input)
            if (token !is WhitespaceToken) {
                if (nextState is Number && state is Number) {
                    assert(result.size > 0 && result[result.size - 1] is NumberToken)
                    result[result.size - 1] = token
                } else {
                    result.add(token)
                }
            }
            state = nextState
        }
        return result
    }
}

interface State {
    fun nextState(next: Int): Pair<Token, State> = if (next < 0) EofToken to Eof else {
        val char = next.toChar()
        when {
            char.isWhitespace() -> SpaceToken to Begin
            char.isDigit() -> {
                val number = nextNumber(char - '0')
                NumberToken(number) to Number(number)
            }
            else -> {
                val token = char2token[char] ?: throw RuntimeException("Unexpected char: '$char'")
                token to Begin
            }
        }
    }
    fun nextNumber(digit: Int): Int = digit
}

object Begin: State

data class Number(private val number: Int): State {
    override fun nextNumber(digit: Int): Int = number * 10 + digit
}

object Eof: State {
    override fun nextState(next: Int): Pair<Token, State> = throw IllegalStateException("Cannot accept char after EOF")
}