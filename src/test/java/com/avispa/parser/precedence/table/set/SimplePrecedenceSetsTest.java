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

package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.avispa.parser.precedence.TestSymbols.A;
import static com.avispa.parser.precedence.TestSymbols.B;
import static com.avispa.parser.precedence.TestSymbols.C;
import static com.avispa.parser.precedence.TestSymbols.D;
import static com.avispa.parser.precedence.TestSymbols.a;
import static com.avispa.parser.precedence.TestSymbols.b;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class SimplePrecedenceSetsTest {

    @Test
    void givenSingleProduction_whenSetsCreated_thenAreCorrect() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(a)));

        // when
        var grammar = ContextFreeGrammar.from("Test", terminals, productions, A);
        var firstAll = new FirstAllSets(grammar);
        var lastAll = new LastAllSets(grammar);
        var first = new FirstSets(firstAll, grammar.getTerminals());

        // then
        assertEquals(Set.of(), firstAll.getFor(a));
        assertEquals(Set.of(a), firstAll.getFor(A));
        assertEquals(Set.of(), lastAll.getFor(a));
        assertEquals(Set.of(a), lastAll.getFor(A));
        assertEquals(Set.of(a), first.getFor(A));
        assertEquals(Set.of(a), first.getFor(a));
    }

    @Test
    void givenProductionWithNonTerminalOnRhs_whenSetsCreated_thenAreCorrect() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(B, a)), Production.of(B, List.of(a)));

        // when
        var grammar = ContextFreeGrammar.from("Test", terminals, productions, A);
        var firstAll = new FirstAllSets(grammar);
        var lastAll = new LastAllSets(grammar);
        var first = new FirstSets(firstAll, grammar.getTerminals());

        // then
        assertEquals(Set.of(), firstAll.getFor(a));
        assertEquals(Set.of(a, B), firstAll.getFor(A));
        assertEquals(Set.of(a), firstAll.getFor(B));
        assertEquals(Set.of(), lastAll.getFor(a));
        assertEquals(Set.of(a), lastAll.getFor(A));
        assertEquals(Set.of(a), lastAll.getFor(B));
        assertEquals(Set.of(a), first.getFor(a));
        assertEquals(Set.of(a), first.getFor(A));
        assertEquals(Set.of(a), first.getFor(B));
    }

    @Test
    void givenProductionsWithMultipleNonTerminals_whenSetsCreated_thenAreCorrect() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(
                Production.of(A, List.of(B, a, C, D)),
                Production.of(B, List.of(a)),
                Production.of(C, List.of(b)),
                Production.of(D, List.of(a, b)));

        // when
        var grammar = ContextFreeGrammar.from("Test", terminals, productions, A);
        var firstAll = new FirstAllSets(grammar);
        var lastAll = new LastAllSets(grammar);
        var first = new FirstSets(firstAll, grammar.getTerminals());

        // then
        assertEquals(Set.of(), firstAll.getFor(a));
        assertEquals(Set.of(), firstAll.getFor(b));
        assertEquals(Set.of(a, B), firstAll.getFor(A));
        assertEquals(Set.of(a), firstAll.getFor(B));
        assertEquals(Set.of(b), firstAll.getFor(C));
        assertEquals(Set.of(a), firstAll.getFor(D));

        assertEquals(Set.of(), lastAll.getFor(a));
        assertEquals(Set.of(), lastAll.getFor(b));
        assertEquals(Set.of(b, D), lastAll.getFor(A));
        assertEquals(Set.of(a), lastAll.getFor(B));
        assertEquals(Set.of(b), lastAll.getFor(C));
        assertEquals(Set.of(b), lastAll.getFor(D));

        assertEquals(Set.of(a), first.getFor(a));
        assertEquals(Set.of(b), first.getFor(b));
        assertEquals(Set.of(a), first.getFor(A));
        assertEquals(Set.of(a), first.getFor(B));
        assertEquals(Set.of(b), first.getFor(C));
        assertEquals(Set.of(a), first.getFor(D));
    }
}