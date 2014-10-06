package ru.ifmo.md.lesson4;

/**
 * Created by vadim on 06/10/14.
 */
public class Calculator implements CalculationEngine {
    @Override
    public double calculate(String expression) throws CalculationException {
        throw new CalculationException();
    }
}
