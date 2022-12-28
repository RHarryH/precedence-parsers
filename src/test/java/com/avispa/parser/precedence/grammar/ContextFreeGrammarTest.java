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

package com.avispa.parser.precedence.grammar;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.avispa.parser.precedence.TestSymbols.A;
import static com.avispa.parser.precedence.TestSymbols.B;
import static com.avispa.parser.precedence.TestSymbols.C;
import static com.avispa.parser.precedence.TestSymbols.a;
import static com.avispa.parser.precedence.TestSymbols.b;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class ContextFreeGrammarTest {

    @Test
    void givenEmptyTerminalsSet_whenCreatingGrammar_thenThrowException() {
        List<Production> productions = List.of(Production.of(A, List.of(B)), Production.of(B, List.of(A)));

        assertThrows(IncorrectGrammarException.class, () -> ContextFreeGrammar.from("Test", Set.of(), productions, A));
    }

    @Test
    void givenEmptyProductionsList_whenCreatingGrammar_thenThrowException() {
        Set<Terminal> terminals = Set.of(a, b);

        assertThrows(IncorrectGrammarException.class, () -> ContextFreeGrammar.from("Test", terminals, List.of(), A));
    }

    @Test
    void givenProductionWithUnknownTerminal_whenCreatingGrammar_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(b)));

        // when/then
        assertThrows(IncorrectGrammarException.class, () -> ContextFreeGrammar.from("Test", terminals, productions, A));
    }

    @Test
    void givenBasicGrammar_whenCreateGrammar_thenStartSymbolDetected() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a)), Production.of(B, List.of(b)));

        // when
        ContextFreeGrammar grammar = ContextFreeGrammar.from("Test", terminals, productions, A);

        // then
        assertEquals(A, grammar.getStart());
    }

    @Test
    void givenProductionsWithIncorrectStartToken_whenCreateGrammar_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a)), Production.of(B, List.of(A)));

        // when/then
        assertThrows(IncorrectGrammarException.class, () -> ContextFreeGrammar.from("Test", terminals, productions, C));
    }

    @Test
    void givenProductionsWithRecursion_whenCreateGrammar_thenStartSymbolDetected() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(A, a, B)), Production.of(A, List.of(B)), Production.of(B, List.of(a)));

        // when
        ContextFreeGrammar grammar = ContextFreeGrammar.from("Test", terminals, productions, A);

        // then
        assertEquals(A, grammar.getStart());
    }

    @Test
    void givenProductionsWithUnknownRhsNonTerminal_whenCreateGrammar_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a, NonTerminal.of("C"))), Production.of(B, List.of(b)));

        // when/then
        assertThrows(IncorrectGrammarException.class, () -> ContextFreeGrammar.from("Test", terminals, productions, A));
    }
}