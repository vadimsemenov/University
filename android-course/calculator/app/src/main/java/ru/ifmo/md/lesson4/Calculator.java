package ru.ifmo.md.lesson4;

import android.util.Log;

/**
 * Created by vadim on 06/10/14.
 */
public class Calculator implements CalculationEngine {
    private static final String TAG = "Calculator";

    @Override
    public double calculate(String expression) throws CalculationException {
        init(expression);
        return parseExpression();
    }

    private double parseExpression() throws CalculationException {
        double result = parseSummand();
        while (nextLexeme == Lexeme.PLUS || nextLexeme == Lexeme.MINUS) {
            Lexeme prevLexeme = nextLexeme;
            nextLexeme();
            if (prevLexeme == Lexeme.PLUS) {
                result += parseSummand();
            } else {
                result -= parseSummand();
            }
        }
        if (nextLexeme != Lexeme.EOE && nextLexeme != Lexeme.CLOSED) {
            Log.d(TAG, "yoy " + nextLexeme);
            throw new CalculationException();
        }
        Log.d(TAG, "result = " + result);
        return result;
    }

    private double parseSummand() throws CalculationException {
        double result = parseMultipiler();
        Log.d(TAG, "yo1");
        while (nextLexeme == Lexeme.TIMES || nextLexeme == Lexeme.OBELUS) {
            Lexeme prevLexeme = nextLexeme;
            nextLexeme();
            if (prevLexeme == Lexeme.TIMES) {
                result *= parseMultipiler();
            } else {
                result /= parseMultipiler();
            }
        }
        if (nextLexeme != Lexeme.PLUS && nextLexeme != Lexeme.MINUS &&
                nextLexeme != Lexeme.EOE && nextLexeme != Lexeme.CLOSED) {
            Log.d(TAG, "yo3 " + nextLexeme);
            throw new CalculationException();
        }
        Log.d(TAG, "summand = " + result);
        return result;
    }

    private double parseMultipiler() throws CalculationException {
        double result;
        if (nextLexeme == Lexeme.OPENED) {
            nextLexeme();
            result = parseExpression();
            assert nextLexeme == Lexeme.CLOSED;
            nextLexeme();
        } else if (nextLexeme == Lexeme.NUMBER) {
            result = nextNumber;
            nextLexeme();
        } else {
            throw new CalculationException();
        }
        Log.d(TAG, "multipiler = " + result);
        return result;
    }

    private void init(String expression) throws CalculationException {
        this.expression = expression;
        this.pointer = 0;
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
                pointer++;
                return Lexeme.PLUS;
            case '-':
                pointer++;
                return Lexeme.MINUS;
            case '*':
                pointer++;
                return Lexeme.TIMES;
            case '/':
                pointer++;
                return Lexeme.OBELUS;
            default:
                int ptr = pointer + 1;
                while (ptr < expression.length() && belongToDecimal(expression.charAt(ptr))) {
                    ptr++;
                }
                try {
                    nextNumber = Double.parseDouble(expression.substring(pointer, ptr));
                } catch (NumberFormatException e) {
                    throw new CalculationException(e);
                }
                pointer = ptr;
                return Lexeme.NUMBER;
        }
    }

    private boolean belongToDecimal(char c) {
        return c == '.' || ('0' <= c && c <= '9');
    }

    private enum Lexeme { NUMBER, PLUS, MINUS, TIMES, OBELUS, OPENED, CLOSED, EOE };
    private Lexeme nextLexeme;
    private double nextNumber;

    private int pointer;
    private String expression;
}
