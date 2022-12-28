/*
 * MIT License
 *
 * Copyright (c) 2022 Rafał Hiszpański
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.avispa.parser.shuntingyard.output;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.precedence.parser.SyntaxException;
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
    void givenNothing_whenParse_thenReturnZero() throws SyntaxException, LexerException {
        assertEquals(new BigDecimal("0"), evaluator.parse(""));
    }

    @Test
    void givenSimpleUnaryOperation_whenParse_thenCorrectResult() throws SyntaxException, LexerException {
        assertEquals(new BigDecimal("-2"), evaluator.parse("-2"));
    }

    @Test
    void givenSimpleBinaryOperation_whenParse_thenCorrectResult() throws SyntaxException, LexerException {
        assertEquals(new BigDecimal("15"), evaluator.parse("3*5"));
    }

    @Test
    void givenRightAssociativeOperation_whenParse_thenCorrectResult() throws SyntaxException, LexerException {
        assertEquals(new BigDecimal("25"), evaluator.parse("5^2"));
    }

    @Test
    void givenPowerWithDecimalExponent_whenParse_thenThrowError() {
        assertThrows(IllegalArgumentException.class, () -> evaluator.parse("5^2.1"));
    }

    @Test
    void givenFunction_whenParse_thenCorrectResult() throws SyntaxException, LexerException {
        assertEquals(new BigDecimal("5"), evaluator.parse("max(5, 2)"));
    }

    @Test
    void givenNestedFunction_whenParse_thenCorrectResult() throws SyntaxException, LexerException {
        assertEquals(new BigDecimal("5"), evaluator.parse("max(5, sqrt(4))"));
    }

    @Test
    void givenComplexExpression_whenParse_thenCorrectResult() throws SyntaxException, LexerException {
        assertEquals(new BigDecimal("11"), evaluator.parse("15.4 - 16.4 + (4 * max(2, 3))"));
    }
}