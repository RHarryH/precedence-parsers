package com.avispa.precedence_parsers.shunting_yard.tokenizer;

import com.avispa.precedence_parsers.shunting_yard.token.Misc;
import com.avispa.precedence_parsers.shunting_yard.token.Operand;
import com.avispa.precedence_parsers.shunting_yard.token.Operator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Rafał Hiszpański
 */
class TokenizerTest {

    private static final Tokenizer tokenizer = new Tokenizer();

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
        assertEquals(List.of(new Operand("2")).toString(), tokenizer.tokenize("2").toString());
    }

    @Test
    void givenOperator_whenTokenize_thenCorrectOutput() {
        assertEquals(List.of(Operator.ADD).toString(), tokenizer.tokenize("+").toString());
    }

    @Test
    void givenMisc_whenTokenize_thenCorrectOutput() {
        assertEquals(List.of(Misc.LEFT_PARENTHESIS).toString(), tokenizer.tokenize("(").toString());
    }

    @Test
    void givenOperatorWithOperands_whenTokenize_thenCorrectOutput() {
        var expected = List.of(new Operand("2"), Operator.MULTIPLY, new Operand("3"));
        assertEquals(expected.toString(), tokenizer.tokenize("2*3").toString());
    }
}