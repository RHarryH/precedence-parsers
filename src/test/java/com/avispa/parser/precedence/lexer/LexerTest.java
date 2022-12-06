package com.avispa.parser.precedence.lexer;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.avispa.parser.precedence.TokenUtil.add;
import static com.avispa.parser.precedence.TokenUtil.number;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class LexerTest {
    private static ContextFreeGrammar grammar;

    @BeforeAll
    static void init() throws IncorrectGrammarException {
        grammar = new GrammarFile("src/test/resources/grammar/operator-precedence-grammar.txt").readOperatorPrecedence();
    }

    @Test
    void givenCorrectInput_whenTokenize_thenReturnThreeTokens() throws LexerException {
        // given
        String input = "2+3";
        Lexer lexer = new Lexer(input, grammar);

        List<Lexeme> expected = List.of(
                Lexeme.of("2", number),
                Lexeme.of("+", add),
                Lexeme.of("3", number));

        // when
        List<Lexeme> result = getLexemes(lexer);

        // then
        assertEquals(expected, result);
    }

    @Test
    void givenInputWithUnknownToken_whenTokenize_thenThrowException() {
        // given
        String input = "2a3";
        Lexer lexer = new Lexer(input, grammar);

        // when/then
        assertThrows(LexerException.class, () -> getLexemes(lexer));
    }

    private List<Lexeme> getLexemes(Lexer lexer) throws LexerException {
        List<Lexeme> result = new ArrayList<>();
        while(lexer.hasCharactersLeft()) {
            result.add(lexer.getNext());
        }
        return result;
    }
}