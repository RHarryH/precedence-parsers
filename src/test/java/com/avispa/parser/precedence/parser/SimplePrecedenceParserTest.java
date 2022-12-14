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
import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.lexer.Lexeme;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.avispa.parser.precedence.TestSymbols.add;
import static com.avispa.parser.precedence.TestSymbols.expression;
import static com.avispa.parser.precedence.TestSymbols.expression_prime;
import static com.avispa.parser.precedence.TestSymbols.factor;
import static com.avispa.parser.precedence.TestSymbols.number;
import static com.avispa.parser.precedence.TestSymbols.term;
import static com.avispa.parser.precedence.TestSymbols.term_prime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class SimplePrecedenceParserTest {
    private static SimplePrecedenceParser simpleParser;
    private static SimplePrecedenceParser simpleParserWithoutPrecedenceFunctions;
    private static SimplePrecedenceParser weakParser;

    @BeforeAll
    static void init() throws IncorrectGrammarException, IOException, ParserCreationException {
        GrammarFile simpleGrammarFile = new GrammarFile("src/test/resources/grammar/simple-precedence-grammar.txt");
        GrammarFile weakGrammarFile = new GrammarFile("src/test/resources/grammar/weak-precedence-grammar.txt");

        Grammar simplePrecedenceGrammar = ContextFreeGrammar.fromWithBoundaryMarker(simpleGrammarFile, expression_prime);
        Grammar weakPrecedenceGrammar = ContextFreeGrammar.fromWithBoundaryMarker(weakGrammarFile, expression);

        simpleParser = ParserFactory.newSimplePrecedenceParser(simplePrecedenceGrammar);
        simpleParserWithoutPrecedenceFunctions = ParserFactory.newSimplePrecedenceParser(simplePrecedenceGrammar, false);
        weakParser = ParserFactory.newSimplePrecedenceParser(weakPrecedenceGrammar);
    }

    @Test
    void givenEmptyString_whenParse_thenEmptyProductionsList() throws SyntaxException, LexerException {
        assertEquals(List.of(), simpleParser.parse(""));
    }

    @Test
    void givenNumber_whenParse_thenNumber() throws SyntaxException, LexerException {
        Lexeme number_1 = Lexeme.of("2", number, 1);

        List<Production> expectedProductions = List.of(
                Production.of(factor, List.of(number_1)),
                Production.of(term, List.of(factor)),
                Production.of(term_prime, List.of(term)),
                Production.of(expression, List.of(term_prime)),
                Production.of(expression_prime, List.of(expression))
        );

        assertEquals(expectedProductions, simpleParser.parse("2"));
    }

    @Test
    void givenSimpleAddition_whenParse_thenCorrectOutput() throws SyntaxException, LexerException {
        List<Production> expectedProductions = getAdditionProductions("2", "3");

        assertEquals(expectedProductions, simpleParser.parse("2+3"));
    }

    @Test
    void givenIncorrectInput_whenParse_thenOutputReturned() {
        assertThrows(SyntaxException.class, () -> simpleParser.parse("12"));
    }

    @Test
    void givenIncorrectInputWithoutPrecedenceFunctions_whenParse_thenSyntaxException() {
        assertThrows(SyntaxException.class, () -> simpleParserWithoutPrecedenceFunctions.parse("12"));
    }

    @Test
    void givenEmptyString_whenParseUsingWeakPrecedenceGrammar_thenEmptyProductionList() throws SyntaxException, LexerException {
        assertEquals(List.of(), weakParser.parse(""));
    }

    @Test
    void givenCorrectInput_whenParseUsingWeakPrecedenceGrammar_thenOutputReturned() throws SyntaxException, LexerException {
        List<Production> expectedProductions = getWeakAdditionProductions("5", "6");

        assertEquals(expectedProductions, weakParser.parse("5+6"));
    }

    private List<Production> getAdditionProductions(String addend1, String addend2) {
        Lexeme number_1 = Lexeme.of(addend1, number, 1);
        Lexeme add_1 = Lexeme.of("+", add, 1);
        Lexeme number_2 = Lexeme.of(addend2, number, 2);

        return List.of(
                Production.of(factor, List.of(number_1)),
                Production.of(term, List.of(factor)),
                Production.of(term_prime, List.of(term)),
                Production.of(expression, List.of(term_prime)),
                Production.of(factor, List.of(number_2)),
                Production.of(term, List.of(factor)),
                Production.of(term_prime, List.of(term)),
                Production.of(expression, List.of(expression, add_1, term_prime)),
                Production.of(expression_prime, List.of(expression))
        );
    }

    private List<Production> getWeakAdditionProductions(String addend1, String addend2) {
        Lexeme number_1 = Lexeme.of(addend1, number, 1);
        Lexeme add_1 = Lexeme.of("+", add, 1);
        Lexeme number_2 = Lexeme.of(addend2, number, 2);

        return List.of(
                Production.of(factor, List.of(number_1)),
                Production.of(term, List.of(factor)),
                Production.of(expression, List.of(term)),
                Production.of(factor, List.of(number_2)),
                Production.of(term, List.of(factor)),
                Production.of(expression, List.of(expression, add_1, term))
        );
    }
}