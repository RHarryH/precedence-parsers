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
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.avispa.parser.precedence.TestSymbols.A;
import static com.avispa.parser.precedence.TestSymbols.B;
import static com.avispa.parser.precedence.TestSymbols.C;
import static com.avispa.parser.precedence.TestSymbols.D;
import static com.avispa.parser.precedence.TestSymbols.a;
import static com.avispa.parser.precedence.TestSymbols.add;
import static com.avispa.parser.precedence.TestSymbols.b;
import static com.avispa.parser.precedence.TestSymbols.expression;
import static com.avispa.parser.precedence.TestSymbols.factor;
import static com.avispa.parser.precedence.TestSymbols.lpar;
import static com.avispa.parser.precedence.TestSymbols.mul;
import static com.avispa.parser.precedence.TestSymbols.number;
import static com.avispa.parser.precedence.TestSymbols.rpar;
import static com.avispa.parser.precedence.TestSymbols.term;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class OperatorPrecedenceSetsTest {

    @Test
    void givenSingleProduction_whenSetsCreated_thenAreCorrect() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(a)));

        // when
        var grammar = ContextFreeGrammar.from("Test", terminals, productions, A);
        var firstOp = new FirstOpSets(grammar);
        var lastOp = new LastOpSets(grammar);

        // then
        assertEquals(Set.of(a), firstOp.getFor(A));
        assertEquals(Set.of(a), lastOp.getFor(A));
    }

    @Test
    void givenProductionWithNonTerminalOnRhs_whenSetsCreated_thenAreCorrect() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(B, a)), Production.of(B, List.of(a)));

        // when
        var grammar = ContextFreeGrammar.from("Test", terminals, productions, A);
        var firstOp = new FirstOpSets(grammar);
        var lastOp = new LastOpSets(grammar);

        // then
        assertEquals(Set.of(a), firstOp.getFor(A));
        assertEquals(Set.of(a), firstOp.getFor(B));
        assertEquals(Set.of(a), lastOp.getFor(A));
        assertEquals(Set.of(a), lastOp.getFor(B));
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
        var firstOp = new FirstOpSets(grammar);
        var lastOp = new LastOpSets(grammar);

        // then
        assertEquals(Set.of(a), firstOp.getFor(A));
        assertEquals(Set.of(a), firstOp.getFor(B));
        assertEquals(Set.of(b), firstOp.getFor(C));
        assertEquals(Set.of(a), firstOp.getFor(D));

        assertEquals(Set.of(a, b), lastOp.getFor(A));
        assertEquals(Set.of(a), lastOp.getFor(B));
        assertEquals(Set.of(b), lastOp.getFor(C));
        assertEquals(Set.of(b), lastOp.getFor(D));
    }

    @Test
    void givenOperatorPrecedenceGrammar_whenCreateSets_thenSetsAreCorrect() throws IncorrectGrammarException, IOException {
        // given
        ContextFreeGrammar grammar = ContextFreeGrammar.from(new GrammarFile("src/test/resources/grammar/operator-precedence-grammar.txt"), expression);

        // when
        var firstOp = new FirstOpSets(grammar);
        var lastOp = new LastOpSets(grammar);

        // then
        assertEquals(Set.of(lpar, add, mul, number), firstOp.getFor(expression));
        assertEquals(Set.of(lpar, mul, number), firstOp.getFor(term));
        assertEquals(Set.of(lpar, number), firstOp.getFor(factor));

        assertEquals(Set.of(rpar, add, mul, number), lastOp.getFor(expression));
        assertEquals(Set.of(rpar, mul, number), lastOp.getFor(term));
        assertEquals(Set.of(rpar, number), lastOp.getFor(factor));
    }

}