package com.avispa.precedence_parsers.shunting_yard;

import com.avispa.precedence_parsers.shunting_yard.output.StringReversePolishNotation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class ShuntingYardTest {

    private static final StringReversePolishNotation shuntingYard = new StringReversePolishNotation();

    @Test
    void givenEmptyString_whenParse_thenEmptyArray() {
        assertEquals("", shuntingYard.parse(""));
    }

    @Test
    void givenNumber_whenParse_thenNumber() {
        assertEquals("2", shuntingYard.parse("2"));
    }

    @Test
    void givenSimpleAddition_whenParse_thenCorrectOutput() {
        assertEquals("2 3 +", shuntingYard.parse("2+3"));
    }

    @Test
    void givenInputWithWhitespaces_whenParse_thenCorrectOutput() {
        assertEquals("2 3 +", shuntingYard.parse("   2 \t + \r\n 3 \t"));
    }

    @Test
    void givenMultipleAdditions_whenParse_thenCorrectOutput() {
        assertEquals("2 3 + 4 +", shuntingYard.parse("2+3+4"));
    }

    @Test
    void givenOperatorsWithHigherPrecedence_whenParse_thenCorrectOutput() {
        assertEquals("2 3 4 * +", shuntingYard.parse("2+3*4"));
    }

    @Test
    void givenRightAssociativeOperator_whenParse_thenCorrectOutput() {
        assertEquals("2 3 ^ 4 +", shuntingYard.parse("2^3+4"));
    }

    @Test
    void givenParentheses_whenParse_thenCorrectOutput() {
        assertEquals("2 3 + 4 *", shuntingYard.parse("(2+3)*4"));
    }

    @Test
    void givenNestedParentheses_whenParse_thenCorrectOutput() {
        assertEquals("4 2 * 3 + 4 *", shuntingYard.parse("((4*2)+3)*4"));
    }

    @Test
    void givenMismatchedParentheses_whenParse_thenThrowException() {
        assertThrows(IllegalStateException.class, () -> shuntingYard.parse("2+3)*4"));
    }

    @Test
    void givenNestedMismatchedParentheses_whenParse_thenThrowException() {
        assertThrows(IllegalStateException.class, () -> shuntingYard.parse("(4*2)+3)*4"));
    }

    @Test
    void givenFunction_whenParse_thenCorrectOutput() {
        assertEquals("4 sqrt", shuntingYard.parse("sqrt(4)"));
    }

    @Test
    void givenTwoArgumentFunction_whenParse_thenCorrectOutput() {
        assertEquals("4 2 max", shuntingYard.parse("max(4, 2)"));
    }

    @Test
    void givenFunctionWithExpressionArgument_whenParse_thenCorrectOutput() {
        assertEquals("4 3 * 2 max", shuntingYard.parse("max(4 * 3, 2)"));
    }

    @Test
    void givenNestedFunction_whenParse_thenCorrectOutput() {
        assertEquals("4 sqrt sqrt", shuntingYard.parse("sqrt(sqrt(4))"));
    }

    @Test
    void givenTwoArgumentNestedFunction_whenParse_thenCorrectOutput() {
        assertEquals("4 3 max sqrt", shuntingYard.parse("sqrt(max(4, 3))"));
    }

    @Test
    void givenIncorrectArgumentsNumberToFunction_whenParse_thenThrowException() {
        assertThrows(IllegalStateException.class, () -> shuntingYard.parse("max(4)"));
    }
}