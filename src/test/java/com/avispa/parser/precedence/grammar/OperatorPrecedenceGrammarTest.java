package com.avispa.parser.precedence.grammar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static com.avispa.parser.precedence.TokenUtil.A;
import static com.avispa.parser.precedence.TokenUtil.B;
import static com.avispa.parser.precedence.TokenUtil.a;
import static com.avispa.parser.precedence.TokenUtil.b;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Rafał Hiszpański
 */
class OperatorPrecedenceGrammarTest {
    @Test
    void givenCFG_whenOperatorPrecedenceTableCannotBeConstructed_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(B)), Production.of(B, List.of(A, B)));

        // when/then
        assertThrows(IncorrectGrammarException.class, () -> new OperatorPrecedenceGrammar("Test", terminals, productions));
    }

    @Test
    void givenCFG_whenWeakPrecedenceConflictExists_thenThrowException() {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(a, a, B)), Production.of(B, List.of(a, B)));

        // when/then
        assertThrows(IncorrectGrammarException.class, () -> new OperatorPrecedenceGrammar("Test", terminals, productions));
    }
}