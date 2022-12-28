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

package com.avispa.parser.precedence;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import com.avispa.parser.precedence.parser.ParserCreationException;
import com.avispa.parser.precedence.parser.ParserFactory;
import com.avispa.parser.precedence.parser.PrecedenceParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

import static com.avispa.parser.precedence.TestSymbols.A;
import static com.avispa.parser.precedence.TestSymbols.B;
import static com.avispa.parser.precedence.TestSymbols.C;
import static com.avispa.parser.precedence.TestSymbols.a;
import static com.avispa.parser.precedence.TestSymbols.b;

/**
 * @author Rafał Hiszpański
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParserUtil {
    public static PrecedenceParser<Production> getSampleParser() {
        Set<Terminal> terminals = Set.of(a, b, Terminal.BOUNDARY_MARKER);

        List<Production> productions = List.of(
                Production.of(A, List.of(Terminal.BOUNDARY_MARKER, B, Terminal.BOUNDARY_MARKER)),
                Production.of(B, List.of(B, a, C)),
                Production.of(B, List.of(a)),
                Production.of(C, List.of(b)));

        try {
            Grammar grammar = ContextFreeGrammar.from("Test", terminals, productions, A);
            return ParserFactory.newSimplePrecedenceParser(grammar);
        } catch (IncorrectGrammarException | ParserCreationException e) {
            throw new IllegalStateException("Parser should be initialized", e);
        }
    }
}
