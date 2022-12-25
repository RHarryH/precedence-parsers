package com.avispa.parser.precedence.grammar.validation;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.avispa.parser.precedence.TestSymbols.A;
import static com.avispa.parser.precedence.TestSymbols.B;
import static com.avispa.parser.precedence.TestSymbols.a;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Rafał Hiszpański
 */
class OperatorGrammarValidatorTest {
    private static final GrammarValidator validator = new OperatorGrammarValidator();

    @Test
    void givenCFG_whenEmptyProduction_thenThrowException() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a)), Production.of(B, List.of()));

        ContextFreeGrammar grammar = ContextFreeGrammar.from("Test", terminals, productions, A);

        // when/then
        assertFalse(validator.is(grammar));
    }

    @Test
    void givenCFG_whenTwoConsecutiveNonTerminals_thenThrowException() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a)), Production.of(B, List.of(A, A)));

        ContextFreeGrammar grammar = ContextFreeGrammar.from("Test", terminals, productions, A);

        // when/then
        assertFalse(validator.is(grammar));;
    }
}