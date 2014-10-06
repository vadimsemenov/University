package ru.ifmo.md.lesson4;

import android.util.Log;

/**
 * Simple recursive descent parser for following simple grammar:
 * <p/>
 * expression = ["+"|"-"] term {("+"|"-") term}
 * term = factor {("*"|"/") factor}
 * factor = number | "(" expression ")"
 *
 * @author Vadim Semenov
 */
public class Calculator implements CalculationEngine {
    private static final String TAG = "Calculator";

    private Lexeme nextLexeme;
    private double nextNumber;
    private int pointer;
    private String expression;

    @Override
    public double calculate(String expression) throws CalculationException {
        init(expression);
        return parseExpression();
    }

    private double parseExpression() throws CalculationException {
        double result = parseTerm();
        while (nextLexeme == Lexeme.PLUS || nextLexeme == Lexeme.MINUS) {
            Lexeme prevLexeme = nextLexeme;
            nextLexeme();
            if (prevLexeme == Lexeme.PLUS) {
                result += parseTerm();
            } else {
                result -= parseTerm();
            }
        }
        if (nextLexeme != Lexeme.EOE && nextLexeme != Lexeme.CLOSED) {
            throw new CalculationException();
        }
        return result;
    }

    private double parseTerm() throws CalculationException {
        double result = parseFactor();
        while (nextLexeme == Lexeme.TIMES || nextLexeme == Lexeme.OBELUS) {
            Lexeme prevLexeme = nextLexeme;
            nextLexeme();
            if (prevLexeme == Lexeme.TIMES) {
                result *= parseFactor();
            } else {
                result /= parseFactor();
            }
        }
        if (nextLexeme != Lexeme.PLUS && nextLexeme != Lexeme.MINUS &&
                nextLexeme != Lexeme.EOE && nextLexeme != Lexeme.CLOSED) {
            throw new CalculationException();
        }
        return result;
    }

    private double parseFactor() throws CalculationException {
        double result;
        if (nextLexeme == Lexeme.OPENED) {
            nextLexeme();
            result = parseExpression();
            if (nextLexeme != Lexeme.CLOSED) {
                throw new CalculationException("can't find closed parenthesis; found: '" + nextLexeme + "'");
            }
            nextLexeme();
        } else if (nextLexeme == Lexeme.NUMBER) {
            result = nextNumber;
            nextLexeme();
        } else {
            throw new CalculationException("can't parse factor; found: '" + nextLexeme + "'");
        }
        return result;
    }

    private void init(String expression) throws CalculationException {
        this.expression = expression;
        this.pointer = 0;
        this.nextLexeme = null;
        nextLexeme();
    }

    private void nextLexeme() throws CalculationException {
        nextLexeme = getNextLexeme();
        Log.d(TAG, "nextLexeme is '" + nextLexeme + "'");
    }

    private Lexeme getNextLexeme() throws CalculationException {
        if (pointer >= expression.length()) {
            return Lexeme.EOE;
        }
        nextNumber = Double.NaN;
        switch (expression.charAt(pointer)) {
            case '(':
                pointer++;
                return Lexeme.OPENED;
            case ')':
                pointer++;
                return Lexeme.CLOSED;
            case '+':
                if (nextLexeme == null || nextLexeme == Lexeme.PLUS || nextLexeme == Lexeme.MINUS ||
                        nextLexeme == Lexeme.TIMES || nextLexeme == Lexeme.OBELUS) {
                    nextNumber = parseNumber();
                    return Lexeme.NUMBER;
                }
                pointer++;
                return Lexeme.PLUS;
            case '-':
                if (nextLexeme == null || nextLexeme == Lexeme.PLUS || nextLexeme == Lexeme.MINUS ||
                        nextLexeme == Lexeme.TIMES || nextLexeme == Lexeme.OBELUS) {
                    nextNumber = parseNumber();
                    return Lexeme.NUMBER;
                }
                pointer++;
                return Lexeme.MINUS;
            case '*':
                pointer++;
                return Lexeme.TIMES;
            case '/':
                pointer++;
                return Lexeme.OBELUS;
            default:
                nextNumber = parseNumber();
                return Lexeme.NUMBER;
        }
    }

    private double parseNumber() throws CalculationException {
        int ptr = pointer;
        boolean positive = true;
        while (ptr < expression.length() && (expression.charAt(ptr) == '+' || expression.charAt(ptr) == '-')) {
            if (expression.charAt(ptr) == '-') {
                positive = !positive;
            }
            ptr++;
        }
        int end = ptr + 1;
        while (end < expression.length() && belongToDouble(expression.charAt(end))) {
            end++;
        }
        if (Character.toLowerCase(expression.charAt(end - 1)) == 'e') {
            while (expression.charAt(end) == '+' || expression.charAt(end) == '-') {
                end++;
            }
            while (end < expression.length() && belongToDouble(expression.charAt(end))) {
                end++;
            }
        }
        double result;
        try {
            result = Double.parseDouble(expression.substring(ptr, end));
        } catch (NumberFormatException e) {
            throw new CalculationException(e);
        }
        pointer = end;
        return positive ? result : -result;
    }

    private boolean belongToDouble(char c) {
        return c == '.' || c == 'e' || c == 'E' || ('0' <= c && c <= '9');
    }

    private enum Lexeme {NUMBER, PLUS, MINUS, TIMES, OBELUS, OPENED, CLOSED, EOE}
}
