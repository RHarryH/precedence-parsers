package com.avispa.precedence_parsers.shunting_yard.tokenizer;

import com.avispa.precedence_parsers.shunting_yard.token.BinaryOperatorToken;
import com.avispa.precedence_parsers.shunting_yard.token.Misc;
import com.avispa.precedence_parsers.shunting_yard.token.Operand;
import com.avispa.precedence_parsers.shunting_yard.token.UnaryOperatorToken;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class TokenizerTest {

    private static final Tokenizer tokenizer = new Tokenizer();
    private static final Operand two = Operand.from("2");

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
        assertEquals(List.of(BinaryOperatorToken.DIVIDE).toString(), tokenizer.tokenize("/").toString());
    }

    @Test
    void givenMisc_whenTokenize_thenCorrectOutput() {
        assertEquals(List.of(Misc.LEFT_PARENTHESIS).toString(), tokenizer.tokenize("(").toString());
    }

    @Test
    void givenSingleMinus_whenTokenize_thenCorrectOutput() {
        assertEquals(List.of(UnaryOperatorToken.MINUS).toString(), tokenizer.tokenize("-").toString());
    }

    @Test
    void givenSubtraction_whenTokenize_thenCorrectOutput() {
        assertEquals(List.of(two, BinaryOperatorToken.SUBTRACT, two).toString(), tokenizer.tokenize("2-2").toString());
    }

    @Test
    void givenMinusPrecedingOperator_whenTokenize_thenUnaryOperatorDetected() {
        assertEquals(List.of(two, BinaryOperatorToken.ADD, UnaryOperatorToken.MINUS, two).toString(), tokenizer.tokenize("2+-2").toString());
    }

    @Test
    void givenMinusPrecedingLeftParenthesis_whenTokenize_thenUnaryOperatorDetected() {
        assertEquals(List.of(two, BinaryOperatorToken.ADD, Misc.LEFT_PARENTHESIS, UnaryOperatorToken.MINUS, two, Misc.RIGHT_PARENTHESIS).toString(), tokenizer.tokenize("2+(-2)").toString());
    }

    @Test
    void givenSubtractionBeforeLeftParenthesis_whenTokenize_thenBinaryOperatorDetected() {
        assertEquals(List.of(two, BinaryOperatorToken.SUBTRACT, Misc.LEFT_PARENTHESIS, two, BinaryOperatorToken.ADD, two, Misc.RIGHT_PARENTHESIS).toString(), tokenizer.tokenize("2-(2+2)").toString());
    }

    @Test
    void givenOperatorWithOperands_whenTokenize_thenCorrectOutput() {
        var expected = List.of(two, BinaryOperatorToken.MULTIPLY, Operand.from("3"));
        assertEquals(expected.toString(), tokenizer.tokenize("2*3").toString());
    }
}