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

package com.avispa.parser.precedence.parser;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.lexer.Lexeme;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
    static void init() throws IncorrectGrammarException, IOException, ParserCreationException {
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