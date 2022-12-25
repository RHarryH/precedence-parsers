package com.avispa.parser.precedence.parser;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.lexer.Lexeme;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.avispa.parser.precedence.TestSymbols.add;
import static com.avispa.parser.precedence.TestSymbols.expression;
import static com.avispa.parser.precedence.TestSymbols.number;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class OperatorPrecedenceParserTest {
    private static OperatorPrecedenceParser parser;
    private static OperatorPrecedenceParser parserWithoutPrecedenceFunctions;

    @BeforeAll
    static void init() throws IncorrectGrammarException {
        GrammarFile grammarFile = new GrammarFile("src/test/resources/grammar/operator-precedence-grammar.txt");
        parser = ParserFactory.newOperatorPrecedenceParser(ContextFreeGrammar.fromWithBoundaryMarker(grammarFile, expression));
        parserWithoutPrecedenceFunctions = ParserFactory.newOperatorPrecedenceParser(ContextFreeGrammar.fromWithBoundaryMarker(grammarFile, expression), false);
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

    @Test
    void givenIncorrectInputWithoutPrecedenceFunctions_whenParse_thenSyntaxException() {
        assertThrows(SyntaxException.class, () -> parserWithoutPrecedenceFunctions.parse("12"));
    }
}