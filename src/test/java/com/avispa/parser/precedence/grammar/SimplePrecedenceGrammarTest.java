package com.avispa.parser.precedence.grammar;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.avispa.parser.precedence.TestSymbols.A;
import static com.avispa.parser.precedence.TestSymbols.B;
import static com.avispa.parser.precedence.TestSymbols.a;
import static com.avispa.parser.precedence.TestSymbols.b;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Rafał Hiszpański
 */
class SimplePrecedenceGrammarTest {
    @Test
    void givenCFG_whenSimplePrecedenceTableCannotBeConstructed_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(B)), Production.of(B, List.of(A, B)));

        // when/then
        assertThrows(IncorrectGrammarException.class, () -> new SimplePrecedenceGrammar("Test", terminals, productions));
    }

    @Test
    void givenCFG_whenWeakPrecedenceConflictExists_thenGrammarIsWeakPrecedence() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(a, a, B)), Production.of(B, List.of(a, B)));

        // when/then
        assertTrue(new SimplePrecedenceGrammar("Test", terminals, productions).isWeak());
    }
}