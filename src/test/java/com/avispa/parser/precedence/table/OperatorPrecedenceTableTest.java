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

package com.avispa.parser.precedence.table;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.grammar.Terminal;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.avispa.parser.precedence.TestSymbols.A;
import static com.avispa.parser.precedence.TestSymbols.B;
import static com.avispa.parser.precedence.TestSymbols.a;
import static com.avispa.parser.precedence.TestSymbols.add;
import static com.avispa.parser.precedence.TestSymbols.b;
import static com.avispa.parser.precedence.TestSymbols.expression;
import static com.avispa.parser.precedence.TestSymbols.lpar;
import static com.avispa.parser.precedence.TestSymbols.mul;
import static com.avispa.parser.precedence.TestSymbols.number;
import static com.avispa.parser.precedence.TestSymbols.rpar;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class OperatorPrecedenceTableTest {

    @Test
    void givenSimpleGrammar_whenPrecedenceTable_thenCorrectTable() throws IncorrectGrammarException, PrecedenceTableException {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a)), Production.of(B, List.of(b)));

        ContextFreeGrammar grammar = ContextFreeGrammar.from("Test", terminals, productions, A);

        // when
        var precedenceTable = new OperatorPrecedenceTable(grammar);

        // then
        Map<Pair<Symbol, Symbol>, Precedence> expected = new HashMap<>();
        expected.put(Pair.of(a, a), Precedence.EQUALS);

        expected.put(Pair.of(b, a), Precedence.GREATER_THAN);

        assertEquals(expected.entrySet(), precedenceTable.get().entrySet());
    }

    @Test
    void givenOperatorPrecedenceGrammar_whenPrecedenceTable_thenCorrectTable() throws IncorrectGrammarException, PrecedenceTableException, IOException {
        // given
        ContextFreeGrammar grammar = ContextFreeGrammar.from(new GrammarFile("src/test/resources/grammar/operator-precedence-grammar.txt"), expression);

        // when
        PrecedenceTable precedenceTable = new OperatorPrecedenceTable(grammar);

        // then
        Map<Pair<Symbol, Symbol>, Precedence> expected = new HashMap<>();
        expected.put(Pair.of(add, add), Precedence.GREATER_THAN);
        expected.put(Pair.of(add, lpar), Precedence.LESS_THAN);
        expected.put(Pair.of(add, mul), Precedence.LESS_THAN);
        expected.put(Pair.of(add, number), Precedence.LESS_THAN);
        expected.put(Pair.of(add, rpar), Precedence.GREATER_THAN);

        expected.put(Pair.of(lpar, add), Precedence.LESS_THAN);
        expected.put(Pair.of(lpar, lpar), Precedence.LESS_THAN);
        expected.put(Pair.of(lpar, mul), Precedence.LESS_THAN);
        expected.put(Pair.of(lpar, number), Precedence.LESS_THAN);
        expected.put(Pair.of(lpar, rpar), Precedence.EQUALS);

        expected.put(Pair.of(mul, add), Precedence.GREATER_THAN);
        expected.put(Pair.of(mul, lpar), Precedence.LESS_THAN);
        expected.put(Pair.of(mul, mul), Precedence.GREATER_THAN);
        expected.put(Pair.of(mul, number), Precedence.LESS_THAN);
        expected.put(Pair.of(mul, rpar), Precedence.GREATER_THAN);

        expected.put(Pair.of(number, add), Precedence.GREATER_THAN);
        expected.put(Pair.of(number, mul), Precedence.GREATER_THAN);
        expected.put(Pair.of(number, rpar), Precedence.GREATER_THAN);

        expected.put(Pair.of(rpar, add), Precedence.GREATER_THAN);
        expected.put(Pair.of(rpar, mul), Precedence.GREATER_THAN);
        expected.put(Pair.of(rpar, rpar), Precedence.GREATER_THAN);

        assertEquals(expected.entrySet(), precedenceTable.get().entrySet());
    }

    @Test
    void givenNonOperatorGrammar_whenPrecedenceTable_thenThrowException() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        /* S -> A
         * A -> AB | a
         * B -> BA | a
         */
        List<Production> productions = List.of(Production.of(A, List.of(A, B)), Production.of(A, List.of(a)), Production.of(B, List.of(B, A)), Production.of(B, List.of(a)));

        ContextFreeGrammar grammar = ContextFreeGrammar.from("Test", terminals, productions, A);

        // when/then
        assertThrows(PrecedenceTableException.class, () -> new OperatorPrecedenceTable(grammar));
    }

    @Test
    void givenGrammarWithConflict_whenPrecedenceTable_thenThrowException() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        /* S -> A
         * A -> AaB | a
         * B -> BbA | a
         */
        List<Production> productions = List.of(Production.of(A, List.of(A, a, B)), Production.of(A, List.of(a)), Production.of(B, List.of(B, b, A)), Production.of(B, List.of(a)));

        ContextFreeGrammar grammar = ContextFreeGrammar.from("Test", terminals, productions, A);

        // when/then
        assertThrows(PrecedenceTableException.class, () -> new OperatorPrecedenceTable(grammar));
    }

    @Test
    void givenWeakPrecedenceGrammar_whenPrecedenceTable_thenCorrectTable() throws IncorrectGrammarException, PrecedenceTableException, IOException {
        // given
        ContextFreeGrammar grammar = ContextFreeGrammar.from(new GrammarFile("src/test/resources/grammar/weak-precedence-grammar.txt"), expression);

        // when
        PrecedenceTable precedenceTable = new OperatorPrecedenceTable(grammar);

        // then
        Map<Pair<Symbol, Symbol>, Precedence> expected = new HashMap<>();
        expected.put(Pair.of(add, add), Precedence.GREATER_THAN);
        expected.put(Pair.of(add, mul), Precedence.LESS_THAN);
        expected.put(Pair.of(add, number), Precedence.LESS_THAN);

        expected.put(Pair.of(mul, add), Precedence.GREATER_THAN);
        expected.put(Pair.of(mul, mul), Precedence.GREATER_THAN);
        expected.put(Pair.of(mul, number), Precedence.LESS_THAN);

        expected.put(Pair.of(number, add), Precedence.GREATER_THAN);
        expected.put(Pair.of(number, mul), Precedence.GREATER_THAN);

        assertEquals(expected.entrySet(), precedenceTable.get().entrySet());
    }
}