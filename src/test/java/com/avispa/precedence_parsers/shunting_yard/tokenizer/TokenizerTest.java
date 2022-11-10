package com.avispa.precedence_parsers.shunting_yard.tokenizer;

import com.avispa.precedence_parsers.shunting_yard.token.BinaryOperator;
import com.avispa.precedence_parsers.shunting_yard.token.Misc;
import com.avispa.precedence_parsers.shunting_yard.token.Operand;
import com.avispa.precedence_parsers.shunting_yard.token.UnaryOperator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class TokenizerTest {

    private static final Tokenizer tokenizer = new Tokenizer();
    private static final Operand two = new Operand("2");

    @Test
    void givenEmptyString_whenTokenize_thenEmptyArray() {
        assertEquals(List.of(), tokenizer.tokenize(""));
    }

    @Test
    void givenUnknownSymbol_whenTokenize_thenThrowException() {
        assertThrows(IllegalStateException.class, () -> tokenizer.tokenize("a"));
    }

    @Test
    void givenOperand_whenTokenize_thenCorrectOutput() {
        assertEquals(List.of(two).toString(), tokenizer.tokenize("2").toString());
    }

    @Test
    void givenBinaryOperator_whenTokenize_thenCorrectOutput() {
        assertEquals(List.of(BinaryOperator.DIVIDE).toString(), tokenizer.tokenize("/").toString());
    }

    @Test
    void givenMisc_whenTokenize_thenCorrectOutput() {
        assertEquals(List.of(Misc.LEFT_PARENTHESIS).toString(), tokenizer.tokenize("(").toString());
    }

    @Test
    void givenSingleMinus_whenTokenize_thenCorrectOutput() {
        assertEquals(List.of(UnaryOperator.MINUS).toString(), tokenizer.tokenize("-").toString());
    }

    @Test
    void givenSubtraction_whenTokenize_thenCorrectOutput() {
        assertEquals(List.of(two, BinaryOperator.SUBTRACT, two).toString(), tokenizer.tokenize("2-2").toString());
    }

    @Test
    void givenMinusPrecedingOperator_whenTokenize_thenUnaryOperatorDetected() {
        assertEquals(List.of(two, BinaryOperator.ADD, UnaryOperator.MINUS, two).toString(), tokenizer.tokenize("2+-2").toString());
    }

    @Test
    void givenMinusPrecedingLeftParenthesis_whenTokenize_thenUnaryOperatorDetected() {
        assertEquals(List.of(two, BinaryOperator.ADD, Misc.LEFT_PARENTHESIS, UnaryOperator.MINUS, two, Misc.RIGHT_PARENTHESIS).toString(), tokenizer.tokenize("2+(-2)").toString());
    }

    @Test
    void givenSubtractionBeforeLeftParenthesis_whenTokenize_thenBinaryOperatorDetected() {
        assertEquals(List.of(two, BinaryOperator.SUBTRACT, Misc.LEFT_PARENTHESIS, two, BinaryOperator.ADD, two, Misc.RIGHT_PARENTHESIS).toString(), tokenizer.tokenize("2-(2+2)").toString());
    }

    @Test
    void givenOperatorWithOperands_whenTokenize_thenCorrectOutput() {
        var expected = List.of(two, BinaryOperator.MULTIPLY, new Operand("3"));
        assertEquals(expected.toString(), tokenizer.tokenize("2*3").toString());
    }
}