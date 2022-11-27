package com.avispa.parser.precedence.table;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import com.avispa.parser.precedence.table.set.FirstAllSets;
import com.avispa.parser.precedence.table.set.FirstSets;
import com.avispa.parser.precedence.table.set.LastAllSets;
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
        var grammar = new ContextFreeGrammar("Test", terminals, productions);
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
        var grammar = new ContextFreeGrammar("Test", terminals, productions);
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
        var grammar = new ContextFreeGrammar("Test", terminals, productions);
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