package com.avispa.parser.precedence.parser;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.OperatorPrecedenceGrammar;
import com.avispa.parser.precedence.lexer.Lexeme;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.avispa.parser.precedence.TestSymbols.add;
import static com.avispa.parser.precedence.TestSymbols.number;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class OperatorPrecedenceParserTest {
    private static OperatorPrecedenceParser parser;

    @BeforeAll
    static void init() throws IncorrectGrammarException {
        OperatorPrecedenceGrammar grammar = new GrammarFile("src/test/resources/grammar/operator-precedence-grammar.txt").readOperatorPrecedence();
        parser = new OperatorPrecedenceParser(grammar);
    }

    @Test
    void givenEmptyString_whenParse_thenEmptyArray() throws SyntaxException, LexerException {
        assertEquals(List.of(), parser.parse(""));
    }

    @Test
    void givenNumber_whenParse_thenNumber() throws SyntaxException, LexerException {
        Lexeme number_1 = Lexeme.of("2", number, 1);
        assertEquals(List.of(number_1), parser.parse("2"));
    }

    @Test
    void givenSimpleAddition_whenParse_thenCorrectOutput() throws SyntaxException, LexerException {
        Lexeme number_1 = Lexeme.of("2", number, 1);
        Lexeme add_1 = Lexeme.of("+", add, 1);
        Lexeme number_2 = Lexeme.of("3", number, 2);
        assertEquals(List.of(number_1, number_2, add_1), parser.parse("2+3"));
    }

    /**
     * Two consecutive numbers should give syntax exception but because precedence functions are used,
     * we're losing some error detection capabilities
     * @throws SyntaxException
     * @throws LexerException
     */
    @Test
    void givenIncorrectInput_whenParse_thenOutputReturned() throws SyntaxException, LexerException {
        Lexeme number_1 = Lexeme.of("1", number, 1);
        Lexeme number_2 = Lexeme.of("2", number, 2);
        assertEquals(List.of(number_2, number_1), parser.parse("12"));
    }
}