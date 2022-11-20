package com.avispa.parser.precedence.table;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Rafał Hiszpański
 */
class FirstLastSetsTest {

    private static final NonTerminal A = NonTerminal.of("A");
    private static final NonTerminal B = NonTerminal.of("B");

    private static final Terminal a = Terminal.of("a", "a");
    private static final Terminal b = Terminal.of("b", "b");

    @Test
    void givenSingleProduction_whenSetsCreated_thenAreCorrect() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(a)));

        // when
        ContextFreeGrammar grammar = new ContextFreeGrammar("Test", terminals, productions);
        FirstLastSets firstLastSets = new FirstLastSets(grammar);

        // then
        assertEquals(Set.of(), firstLastSets.getFirstAllFor(a));
        assertEquals(Set.of(a), firstLastSets.getFirstAllFor(A));
        assertEquals(Set.of(), firstLastSets.getLastAllFor(a));
        assertEquals(Set.of(a), firstLastSets.getLastAllFor(A));
        assertEquals(Set.of(a), firstLastSets.getFirstFor(A));
        assertEquals(Set.of(a), firstLastSets.getFirstFor(a));
    }

    @Test
    void givenProductionWithNonTerminalOnRhs_whenSetsCreated_thenAreCorrect() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(B, a)), Production.of(B, List.of(a)));

        // when
        ContextFreeGrammar grammar = new ContextFreeGrammar("Test", terminals, productions);
        FirstLastSets firstLastSets = new FirstLastSets(grammar);

        // then
        assertEquals(Set.of(), firstLastSets.getFirstAllFor(a));
        assertEquals(Set.of(a, B), firstLastSets.getFirstAllFor(A));
        assertEquals(Set.of(a), firstLastSets.getFirstAllFor(B));
        assertEquals(Set.of(), firstLastSets.getLastAllFor(a));
        assertEquals(Set.of(a), firstLastSets.getLastAllFor(A));
        assertEquals(Set.of(a), firstLastSets.getLastAllFor(B));
        assertEquals(Set.of(a), firstLastSets.getFirstFor(a));
        assertEquals(Set.of(a), firstLastSets.getFirstFor(A));
        assertEquals(Set.of(a), firstLastSets.getFirstFor(B));
    }
}