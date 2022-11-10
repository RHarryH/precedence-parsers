package com.avispa.precedence_parsers.shunting_yard;

import com.avispa.precedence_parsers.shunting_yard.token.Token;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class ShuntingYardTest {

    private static final ShuntingYard shuntingYard = new ShuntingYard();

    @Test
    void givenEmptyString_whenParse_thenEmptyArray() {
        assertEquals(List.of(), shuntingYard.parse(""));
    }

    @Test
    void givenNumber_whenParse_thenNumber() {
        assertEquals("2", joinTokens(shuntingYard.parse("2")));
    }

    @Test
    void givenSimpleAddition_whenParse_thenCorrectOutput() {
        assertEquals("23+", joinTokens(shuntingYard.parse("2+3")));
    }

    @Test
    void givenInputWithWhitespaces_whenParse_thenCorrectOutput() {
        assertEquals("23+", joinTokens(shuntingYard.parse("   2 \t + \r\n 3 \t")));
    }

    @Test
    void givenMultipleAdditions_whenParse_thenCorrectOutput() {
        assertEquals("23+4+", joinTokens(shuntingYard.parse("2+3+4")));
    }

    @Test
    void givenOperatorsWithHigherPrecedence_whenParse_thenCorrectOutput() {
        assertEquals("234*+", joinTokens(shuntingYard.parse("2+3*4")));
    }

    @Test
    void givenRightAssociativeOperator_whenParse_thenCorrectOutput() {
        assertEquals("23^4+", joinTokens(shuntingYard.parse("2^3+4")));
    }

    @Test
    void givenParentheses_whenParse_thenCorrectOutput() {
        assertEquals("23+4*", joinTokens(shuntingYard.parse("(2+3)*4")));
    }

    @Test
    void givenNestedParentheses_whenParse_thenCorrectOutput() {
        assertEquals("42*3+4*", joinTokens(shuntingYard.parse("((4*2)+3)*4")));
    }

    @Test
    void givenMismatchedParentheses_whenParse_thenThrowException() {
        assertThrows(IllegalStateException.class, () -> shuntingYard.parse("2+3)*4"));
    }

    @Test
    void givenNestedMismatchedParentheses_whenParse_thenThrowException() {
        assertThrows(IllegalStateException.class, () -> shuntingYard.parse("(4*2)+3)*4"));
    }

    private String joinTokens(List<Token> tokens) {
        return tokens.stream().map(Token::getValue).collect(Collectors.joining());
    }
}