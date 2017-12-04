package ru.ifmo.ctddev.semenov.sd.calc.parser

import ru.ifmo.ctddev.semenov.sd.calc.token.Token

interface Parser {
    fun parse(): List<Token>
}

