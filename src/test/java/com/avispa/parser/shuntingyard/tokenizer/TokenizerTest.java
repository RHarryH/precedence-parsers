package com.avispa.parser.shuntingyard.tokenizer;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.shuntingyard.token.BinaryOperatorToken;
import com.avispa.parser.shuntingyard.token.Misc;
import com.avispa.parser.shuntingyard.token.Operand;
import com.avispa.parser.shuntingyard.token.UnaryOperatorToken;
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
    void givenEmptyString_whenTokenize_thenEmptyArray() throws LexerException {
        assertEquals(List.of(), tokenizer.tokenize(""));
    }

    @Test
    void givenUnknownSymbol_whenTokenize_thenThrowException() {
        assertThrows(LexerException.class, () -> tokenizer.tokenize("a"));
    }

    @Test
    void givenOperand_whenTokenize_thenCorrectOutput() throws LexerException {
        assertEquals(List.of(two).toString(), tokenizer.tokenize("2").toString());
    }

    @Test
    void givenBinaryOperator_whenTokenize_thenCorrectOutput() throws LexerException {
        assertEquals(List.of(BinaryOperatorToken.DIVIDE).toString(), tokenizer.tokenize("/").toString());
    }

    @Test
    void givenMisc_whenTokenize_thenCorrectOutput() throws LexerException {
        assertEquals(List.of(Misc.LEFT_PARENTHESIS).toString(), tokenizer.tokenize("(").toString());
    }

    @Test
    void givenSingleMinus_whenTokenize_thenCorrectOutput() throws LexerException {
        assertEquals(List.of(UnaryOperatorToken.MINUS).toString(), tokenizer.tokenize("-").toString());
    }

    @Test
    void givenSubtraction_whenTokenize_thenCorrectOutput() throws LexerException {
        assertEquals(List.of(two, BinaryOperatorToken.SUBTRACT, two).toString(), tokenizer.tokenize("2-2").toString());
    }

    @Test
    void givenMinusPrecedingOperator_whenTokenize_thenUnaryOperatorDetected() throws LexerException {
        assertEquals(List.of(two, BinaryOperatorToken.ADD, UnaryOperatorToken.MINUS, two).toString(), tokenizer.tokenize("2+-2").toString());
    }

    @Test
    void givenMinusPrecedingLeftParenthesis_whenTokenize_thenUnaryOperatorDetected() throws LexerException {
        assertEquals(List.of(two, BinaryOperatorToken.ADD, Misc.LEFT_PARENTHESIS, UnaryOperatorToken.MINUS, two, Misc.RIGHT_PARENTHESIS).toString(), tokenizer.tokenize("2+(-2)").toString());
    }

    @Test
    void givenSubtractionBeforeLeftParenthesis_whenTokenize_thenBinaryOperatorDetected() throws LexerException {
        assertEquals(List.of(two, BinaryOperatorToken.SUBTRACT, Misc.LEFT_PARENTHESIS, two, BinaryOperatorToken.ADD, two, Misc.RIGHT_PARENTHESIS).toString(), tokenizer.tokenize("2-(2+2)").toString());
    }

    @Test
    void givenOperatorWithOperands_whenTokenize_thenCorrectOutput() throws LexerException {
        var expected = List.of(two, BinaryOperatorToken.MULTIPLY, Operand.from("3"));
        assertEquals(expected.toString(), tokenizer.tokenize("2*3").toString());
    }
}