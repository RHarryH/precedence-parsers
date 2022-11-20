package com.avispa.parser.precedence.grammar;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class ContextFreeGrammarTest {

    private static final NonTerminal A = NonTerminal.of("A");
    private static final NonTerminal B = NonTerminal.of("B");

    private static final Terminal a = Terminal.of("a", "a");
    private static final Terminal b = Terminal.of("b", "b");

    @Test
    void givenEmptyTerminalsSet_whenCreatingGrammar_thenThrowException() {
        List<Production> productions = List.of(Production.of(A, List.of(B)), Production.of(B, List.of(A)));

        assertThrows(IncorrectGrammarException.class, () -> new ContextFreeGrammar("Test", Set.of(), productions));
    }

    @Test
    void givenEmptyProductionsList_whenCreatingGrammar_thenThrowException() {
        Set<Terminal> terminals = Set.of(a, b);

        assertThrows(IncorrectGrammarException.class, () -> new ContextFreeGrammar("Test", terminals, List.of()));
    }

    @Test
    void givenProductionWithUnknownTerminal_whenCreatingGrammar_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(b)));

        // when/then
        assertThrows(IncorrectGrammarException.class, () -> new ContextFreeGrammar("Test", terminals, productions));
    }

    @Test
    void givenBasicGrammar_whenCreateGrammar_thenStartSymbolDetected() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a)), Production.of(B, List.of(b)));

        // when
        ContextFreeGrammar grammar = new ContextFreeGrammar("Test", terminals, productions);

        // then
        assertEquals(A, grammar.getStart());
    }

    @Test
    void givenProductionsWithNoStartToken_whenCreateGrammar_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a)), Production.of(B, List.of(A)));

        // when/then
        assertThrows(IncorrectGrammarException.class, () -> new ContextFreeGrammar("Test", terminals, productions));
    }

    @Test
    void givenProductionsWithMultipleStartTokenCandidates_whenCreateGrammar_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(b)), Production.of(B, List.of(b)));

        // when/then
        assertThrows(IllegalStateException.class, () -> new ContextFreeGrammar("Test", terminals, productions));
    }

    @Test
    void givenProductionsWithRecursion_whenCreateGrammar_thenStartSymbolDetected() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(A, a, B)), Production.of(A, List.of(B)), Production.of(B, List.of(a)));

        // when
        ContextFreeGrammar grammar = new ContextFreeGrammar("Test", terminals, productions);

        // then
        assertEquals(A, grammar.getStart());
    }

    @Test
    void givenProductionsWithUnknownRhsNonTerminal_whenCreateGrammar_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a, NonTerminal.of("C"))), Production.of(B, List.of(b)));

        // when/then
        assertThrows(IncorrectGrammarException.class, () -> new ContextFreeGrammar("Test", terminals, productions));
    }
}