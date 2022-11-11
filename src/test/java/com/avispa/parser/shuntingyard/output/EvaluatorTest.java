package com.avispa.parser.shuntingyard.output;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class EvaluatorTest {
    private static final Evaluator evaluator = new Evaluator();

    @Test
    void givenSimpleUnaryOperation_whenParse_thenCorrectResult() {
        assertEquals(new BigDecimal("-2"), evaluator.parse("-2"));
    }

    @Test
    void givenSimpleBinaryOperation_whenParse_thenCorrectResult() {
        assertEquals(new BigDecimal("15"), evaluator.parse("3*5"));
    }

    @Test
    void givenRightAssociativeOperation_whenParse_thenCorrectResult() {
        assertEquals(new BigDecimal("25"), evaluator.parse("5^2"));
    }

    @Test
    void givenPowerWithDecimalExponent_whenParse_thenThrowError() {
        assertThrows(IllegalArgumentException.class, () -> evaluator.parse("5^2.1"));
    }

    @Test
    void givenFunction_whenParse_thenCorrectResult() {
        assertEquals(new BigDecimal("5"), evaluator.parse("max(5, 2)"));
    }

    @Test
    void givenNestedFunction_whenParse_thenCorrectResult() {
        assertEquals(new BigDecimal("5"), evaluator.parse("max(5, sqrt(4))"));
    }

    @Test
    void givenComplexExpression_whenParse_thenCorrectResult() {
        assertEquals(new BigDecimal("11"), evaluator.parse("15.4 - 16.4 + (4 * max(2, 3))"));
    }
}