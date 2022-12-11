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

        assertThrows(IncorrectGrammarException.class, () -> new ContextFreeGrammar("Test", Set.of(), productions, A));
    }

    @Test
    void givenEmptyProductionsList_whenCreatingGrammar_thenThrowException() {
        Set<Terminal> terminals = Set.of(a, b);

        assertThrows(IncorrectGrammarException.class, () -> new ContextFreeGrammar("Test", terminals, List.of(), A));
    }

    @Test
    void givenProductionWithUnknownTerminal_whenCreatingGrammar_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(b)));

        // when/then
        assertThrows(IncorrectGrammarException.class, () -> new ContextFreeGrammar("Test", terminals, productions, A));
    }

    @Test
    void givenBasicGrammar_whenCreateGrammar_thenStartSymbolDetected() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a)), Production.of(B, List.of(b)));

        // when
        ContextFreeGrammar grammar = new ContextFreeGrammar("Test", terminals, productions, A);

        // then
        assertEquals(A, grammar.getStart());
    }

    @Test
    void givenProductionsWithIncorrectStartToken_whenCreateGrammar_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a)), Production.of(B, List.of(A)));

        // when/then
        assertThrows(IncorrectGrammarException.class, () -> new ContextFreeGrammar("Test", terminals, productions, C));
    }

    @Test
    void givenProductionsWithRecursion_whenCreateGrammar_thenStartSymbolDetected() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(A, a, B)), Production.of(A, List.of(B)), Production.of(B, List.of(a)));

        // when
        ContextFreeGrammar grammar = new ContextFreeGrammar("Test", terminals, productions, A);

        // then
        assertEquals(A, grammar.getStart());
    }

    @Test
    void givenProductionsWithUnknownRhsNonTerminal_whenCreateGrammar_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a, NonTerminal.of("C"))), Production.of(B, List.of(b)));

        // when/then
        assertThrows(IncorrectGrammarException.class, () -> new ContextFreeGrammar("Test", terminals, productions, A));
    }
}