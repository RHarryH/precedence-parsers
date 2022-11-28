package com.avispa.parser.precedence.table;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.avispa.parser.precedence.TokenUtil.A;
import static com.avispa.parser.precedence.TokenUtil.B;
import static com.avispa.parser.precedence.TokenUtil.a;
import static com.avispa.parser.precedence.TokenUtil.add;
import static com.avispa.parser.precedence.TokenUtil.b;
import static com.avispa.parser.precedence.TokenUtil.expression;
import static com.avispa.parser.precedence.TokenUtil.factor;
import static com.avispa.parser.precedence.TokenUtil.marker;
import static com.avispa.parser.precedence.TokenUtil.mul;
import static com.avispa.parser.precedence.TokenUtil.number;
import static com.avispa.parser.precedence.TokenUtil.start;
import static com.avispa.parser.precedence.TokenUtil.term;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class SimplePrecedenceTableTest {

    @Test
    void givenSimpleGrammar_whenPrecedenceTable_thenCorrectTable() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a)), Production.of(B, List.of(b)));

        ContextFreeGrammar grammar = new ContextFreeGrammar("Test", terminals, productions);

        // when
        SimplePrecedenceTable precedenceTable = new SimplePrecedenceTable(grammar);

        // then
        Map<Pair<GenericToken, GenericToken>, Precedence> expected = new HashMap<>();
        expected.put(Pair.of(B, a), Precedence.EQUALS);

        expected.put(Pair.of(a, a), Precedence.EQUALS);
        expected.put(Pair.of(a, marker), Precedence.GREATER_THAN);

        expected.put(Pair.of(b, a), Precedence.GREATER_THAN);

        expected.put(Pair.of(marker, B), Precedence.LESS_THAN);
        expected.put(Pair.of(marker, b), Precedence.LESS_THAN);

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
        List<Production> productions = List.of(Production.of(start, List.of(A)), Production.of(A, List.of(A, B)), Production.of(A, List.of(a)), Production.of(B, List.of(B, A)), Production.of(B, List.of(a)));

        ContextFreeGrammar grammar = new ContextFreeGrammar("Test", terminals, productions);

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
        List<Production> productions = List.of(Production.of(start, List.of(A)), Production.of(A, List.of(A, a, B)), Production.of(A, List.of(a)), Production.of(B, List.of(B, b, A)), Production.of(B, List.of(a)));

        ContextFreeGrammar grammar = new ContextFreeGrammar("Test", terminals, productions);

        // when/then
        assertThrows(PrecedenceTableException.class, () -> new SimplePrecedenceTable(grammar));
    }

    @Test
    void givenWeakPrecedenceGrammar_whenPrecedenceTable_thenCorrectTable() throws IncorrectGrammarException {
        // given
        ContextFreeGrammar grammar = new GrammarFile("src/test/resources/grammar/weak-precedence-grammar.txt").read();

        // when
        SimplePrecedenceTable precedenceTable = new SimplePrecedenceTable(grammar);

        // then
        Map<Pair<GenericToken, GenericToken>, Precedence> expected = new HashMap<>();
        expected.put(Pair.of(expression, add), Precedence.EQUALS);

        expected.put(Pair.of(term, add), Precedence.GREATER_THAN);
        expected.put(Pair.of(term, mul), Precedence.EQUALS);
        expected.put(Pair.of(term, marker), Precedence.GREATER_THAN);

        expected.put(Pair.of(factor, add), Precedence.GREATER_THAN);
        expected.put(Pair.of(factor, mul), Precedence.GREATER_THAN);
        expected.put(Pair.of(factor, marker), Precedence.GREATER_THAN);

        expected.put(Pair.of(add, term), Precedence.LESS_THAN_OR_EQUALS);
        expected.put(Pair.of(add, factor), Precedence.LESS_THAN);
        expected.put(Pair.of(add, number), Precedence.LESS_THAN);

        expected.put(Pair.of(mul, factor), Precedence.EQUALS);
        expected.put(Pair.of(mul, number), Precedence.LESS_THAN);

        expected.put(Pair.of(number, add), Precedence.GREATER_THAN);
        expected.put(Pair.of(number, mul), Precedence.GREATER_THAN);
        expected.put(Pair.of(number, marker), Precedence.GREATER_THAN);

        expected.put(Pair.of(marker, expression), Precedence.LESS_THAN);
        expected.put(Pair.of(marker, term), Precedence.LESS_THAN);
        expected.put(Pair.of(marker, factor), Precedence.LESS_THAN);
        expected.put(Pair.of(marker, number), Precedence.LESS_THAN);

        assertEquals(expected.entrySet(), precedenceTable.get().entrySet());
    }
}