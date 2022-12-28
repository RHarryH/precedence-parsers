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

package com.avispa.parser.precedence.output;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.precedence.ParserUtil;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.lexer.Lexeme;
import com.avispa.parser.precedence.parser.SyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.avispa.parser.precedence.TestSymbols.B;
import static com.avispa.parser.precedence.TestSymbols.C;
import static com.avispa.parser.precedence.TestSymbols.a;
import static com.avispa.parser.precedence.TestSymbols.b;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
class DerivationTest {
    private static final Derivation derivation = new Derivation(ParserUtil.getSampleParser());

    @Test
    void givenParser_whenParseEmptyString_thenEmptyList() throws SyntaxException, LexerException {
        // when
        List<List<Symbol>> derivationList = derivation.parse("");

        // then
        assertTrue(derivationList.isEmpty());
    }

    @Test
    void givenParser_whenNonEmptyInput_thenCorrectParseTree() throws SyntaxException, LexerException {
        // when
        List<List<Symbol>> derivationList = derivation.parse("aab");

        Lexeme a1 = Lexeme.of("a", a, 1);
        Lexeme a2 = Lexeme.of("a", a, 2);
        Lexeme b1 = Lexeme.of("b", b, 1);
        List<List<Symbol>> expectedDerivation = List.of(
                List.of(B),
                List.of(B, a2, C),
                List.of(B, a2, b1),
                List.of(a1, a2, b1)
        );

        // then
        assertEquals(expectedDerivation, derivationList);
    }
}