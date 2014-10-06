package ru.ifmo.md.lesson4;

public class CalculationException extends Exception {
    public CalculationException() {
        super();
    }

    public CalculationException(Exception cause) {
        super(cause);
    }
}
