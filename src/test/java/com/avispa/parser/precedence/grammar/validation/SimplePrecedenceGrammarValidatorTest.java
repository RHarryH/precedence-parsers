package com.avispa.parser.precedence.grammar.validation;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.avispa.parser.precedence.TestSymbols.A;
import static com.avispa.parser.precedence.TestSymbols.B;
import static com.avispa.parser.precedence.TestSymbols.a;
import static com.avispa.parser.precedence.TestSymbols.b;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Rafał Hiszpański
 */
class SimplePrecedenceGrammarValidatorTest {
    private static final GrammarValidator validator = new SimplePrecedenceGrammarValidator();

    @Test
    void givenCFG_whenHasEmptyProductions_thenIsNotSimplePrecedenceGrammar() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(B)), Production.of(B, List.of()));

        Grammar grammar = ContextFreeGrammar.from("Test", terminals, productions, A);

        // when/then
        assertFalse(validator.is(grammar));
    }

    @Test
    void givenCFG_whenHasNonUniqueProductions_thenIsNotSimplePrecedenceGrammar() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(a, B)), Production.of(B, List.of(a, B)));

        Grammar grammar = ContextFreeGrammar.from("Test", terminals, productions, A);

        // when/then
        assertFalse(validator.is(grammar));
    }
}