package com.avispa.parser.precedence.grammar;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.avispa.parser.precedence.TestSymbols.A;
import static com.avispa.parser.precedence.TestSymbols.B;
import static com.avispa.parser.precedence.TestSymbols.a;
import static com.avispa.parser.precedence.function.PrecedenceFunctionsMode.GRAPH_PRECEDENCE_FUNCTIONS;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class OperatorGrammarTest {
    @Test
    void givenCFG_whenEmptyProduction_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a)), Production.of(B, List.of()));

        // when/then
        assertThrows(IncorrectGrammarException.class, () -> new OperatorPrecedenceGrammar("Test", terminals, productions, A, GRAPH_PRECEDENCE_FUNCTIONS));
    }

    @Test
    void givenCFG_whenTwoConsecutiveNonTerminals_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a)), Production.of(B, List.of(A, A)));

        // when/then
        assertThrows(IncorrectGrammarException.class, () -> new OperatorPrecedenceGrammar("Test", terminals, productions, A, GRAPH_PRECEDENCE_FUNCTIONS));
    }
}