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

package com.avispa.parser.precedence.lexer;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.avispa.parser.precedence.TestSymbols.add;
import static com.avispa.parser.precedence.TestSymbols.expression;
import static com.avispa.parser.precedence.TestSymbols.number;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class LexerTest {
    private static ContextFreeGrammar grammar;

    @BeforeAll
    static void init() throws IncorrectGrammarException, IOException {
        grammar = ContextFreeGrammar.from(new GrammarFile("src/test/resources/grammar/operator-precedence-grammar.txt"), expression);
    }

    @Test
    void givenCorrectInput_whenTokenize_thenReturnThreeTokens() throws LexerException {
        // given
        String input = "2+3";
        Lexer lexer = new Lexer(input, grammar);

        List<Lexeme> expected = List.of(
                Lexeme.of("2", number, 1),
                Lexeme.of("+", add, 1),
                Lexeme.of("3", number, 2));

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