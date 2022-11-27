package com.avispa.parser.precedence.table;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import com.avispa.parser.precedence.table.set.SimplePrecedenceSets;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.avispa.parser.precedence.TokenUtil.A;
import static com.avispa.parser.precedence.TokenUtil.B;
import static com.avispa.parser.precedence.TokenUtil.C;
import static com.avispa.parser.precedence.TokenUtil.D;
import static com.avispa.parser.precedence.TokenUtil.a;
import static com.avispa.parser.precedence.TokenUtil.b;
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
        ContextFreeGrammar grammar = new ContextFreeGrammar("Test", terminals, productions);
        SimplePrecedenceSets sets = new SimplePrecedenceSets(grammar);

        // then
        assertEquals(Set.of(), sets.getFirstAllFor(a));
        assertEquals(Set.of(a), sets.getFirstAllFor(A));
        assertEquals(Set.of(), sets.getLastAllFor(a));
        assertEquals(Set.of(a), sets.getLastAllFor(A));
        assertEquals(Set.of(a), sets.getFirstFor(A));
        assertEquals(Set.of(a), sets.getFirstFor(a));
    }

    @Test
    void givenProductionWithNonTerminalOnRhs_whenSetsCreated_thenAreCorrect() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(B, a)), Production.of(B, List.of(a)));

        // when
        ContextFreeGrammar grammar = new ContextFreeGrammar("Test", terminals, productions);
        SimplePrecedenceSets sets = new SimplePrecedenceSets(grammar);

        // then
        assertEquals(Set.of(), sets.getFirstAllFor(a));
        assertEquals(Set.of(a, B), sets.getFirstAllFor(A));
        assertEquals(Set.of(a), sets.getFirstAllFor(B));
        assertEquals(Set.of(), sets.getLastAllFor(a));
        assertEquals(Set.of(a), sets.getLastAllFor(A));
        assertEquals(Set.of(a), sets.getLastAllFor(B));
        assertEquals(Set.of(a), sets.getFirstFor(a));
        assertEquals(Set.of(a), sets.getFirstFor(A));
        assertEquals(Set.of(a), sets.getFirstFor(B));
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
        ContextFreeGrammar grammar = new ContextFreeGrammar("Test", terminals, productions);
        SimplePrecedenceSets sets = new SimplePrecedenceSets(grammar);

        // then
        assertEquals(Set.of(), sets.getFirstAllFor(a));
        assertEquals(Set.of(), sets.getFirstAllFor(b));
        assertEquals(Set.of(a, B), sets.getFirstAllFor(A));
        assertEquals(Set.of(a), sets.getFirstAllFor(B));
        assertEquals(Set.of(b), sets.getFirstAllFor(C));
        assertEquals(Set.of(a), sets.getFirstAllFor(D));

        assertEquals(Set.of(), sets.getLastAllFor(a));
        assertEquals(Set.of(), sets.getLastAllFor(b));
        assertEquals(Set.of(b, D), sets.getLastAllFor(A));
        assertEquals(Set.of(a), sets.getLastAllFor(B));
        assertEquals(Set.of(b), sets.getLastAllFor(C));
        assertEquals(Set.of(b), sets.getLastAllFor(D));

        assertEquals(Set.of(a), sets.getFirstFor(a));
        assertEquals(Set.of(b), sets.getFirstFor(b));
        assertEquals(Set.of(a), sets.getFirstFor(A));
        assertEquals(Set.of(a), sets.getFirstFor(B));
        assertEquals(Set.of(b), sets.getFirstFor(C));
        assertEquals(Set.of(a), sets.getFirstFor(D));
    }
}