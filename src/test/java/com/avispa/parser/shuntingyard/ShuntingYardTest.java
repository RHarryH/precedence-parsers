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

package com.avispa.parser.shuntingyard;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.precedence.parser.SyntaxException;
import com.avispa.parser.shuntingyard.output.ReversePolishNotationText;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class ShuntingYardTest {

    private static final ReversePolishNotationText shuntingYard = new ReversePolishNotationText();

    @Test
    void givenEmptyString_whenParse_thenEmptyArray() throws SyntaxException, LexerException {
        assertEquals("", shuntingYard.parse(""));
    }

    @Test
    void givenNumber_whenParse_thenNumber() throws SyntaxException, LexerException {
        assertEquals("2", shuntingYard.parse("2"));
    }

    @Test
    void givenSimpleAddition_whenParse_thenCorrectOutput() throws SyntaxException, LexerException {
        assertEquals("2 3 +", shuntingYard.parse("2+3"));
    }

    @Test
    void givenInputWithWhitespaces_whenParse_thenCorrectOutput() throws SyntaxException, LexerException {
        assertEquals("2 3 +", shuntingYard.parse("   2 \t + \r\n 3 \t"));
    }

    @Test
    void givenMultipleAdditions_whenParse_thenCorrectOutput() throws SyntaxException, LexerException {
        assertEquals("2 3 + 4 +", shuntingYard.parse("2+3+4"));
    }

    @Test
    void givenOperatorsWithHigherPrecedence_whenParse_thenCorrectOutput() throws SyntaxException, LexerException {
        assertEquals("2 3 4 * +", shuntingYard.parse("2+3*4"));
    }

    @Test
    void givenRightAssociativeOperator_whenParse_thenCorrectOutput() throws SyntaxException, LexerException {
        assertEquals("2 3 ^ 4 +", shuntingYard.parse("2^3+4"));
    }

    @Test
    void givenParentheses_whenParse_thenCorrectOutput() throws SyntaxException, LexerException {
        assertEquals("2 3 + 4 *", shuntingYard.parse("(2+3)*4"));
    }

    @Test
    void givenNestedParentheses_whenParse_thenCorrectOutput() throws SyntaxException, LexerException {
        assertEquals("4 2 * 3 + 4 *", shuntingYard.parse("((4*2)+3)*4"));
    }

    @Test
    void givenMismatchedParentheses_whenParse_thenThrowException() {
        assertThrows(SyntaxException.class, () -> shuntingYard.parse("2+3)*4"));
    }

    @Test
    void givenNestedMismatchedParentheses_whenParse_thenThrowException() {
        assertThrows(SyntaxException.class, () -> shuntingYard.parse("(4*2)+3)*4"));
    }

    @Test
    void givenFunction_whenParse_thenCorrectOutput() throws SyntaxException, LexerException {
        assertEquals("4 sqrt", shuntingYard.parse("sqrt(4)"));
    }

    @Test
    void givenTwoArgumentFunction_whenParse_thenCorrectOutput() throws SyntaxException, LexerException {
        assertEquals("4 2 max", shuntingYard.parse("max(4, 2)"));
    }

    @Test
    void givenFunctionWithExpressionArgument_whenParse_thenCorrectOutput() throws SyntaxException, LexerException {
        assertEquals("4 3 * 2 max", shuntingYard.parse("max(4 * 3, 2)"));
    }

    @Test
    void givenNestedFunction_whenParse_thenCorrectOutput() throws SyntaxException, LexerException {
        assertEquals("4 sqrt sqrt", shuntingYard.parse("sqrt(sqrt(4))"));
    }

    @Test
    void givenTwoArgumentNestedFunction_whenParse_thenCorrectOutput() throws SyntaxException, LexerException {
        assertEquals("4 3 max sqrt", shuntingYard.parse("sqrt(max(4, 3))"));
    }

    @Test
    void givenIncorrectArgumentsNumberToFunction_whenParse_thenThrowException() {
        assertThrows(SyntaxException.class, () -> shuntingYard.parse("max(4)"));
    }
}