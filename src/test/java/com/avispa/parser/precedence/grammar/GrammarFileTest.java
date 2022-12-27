package com.avispa.parser.precedence.grammar;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.avispa.parser.precedence.TestSymbols.expression;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class GrammarFileTest {

    @Test
    void givenCorrectGrammar_whenRead_thenCreated() throws IncorrectGrammarException, IOException {
        Grammar grammar = ContextFreeGrammar.from(new GrammarFile("src/test/resources/grammar/grammar-correct.txt"),expression);

        assertEquals("CorrectGrammar", grammar.getName());
        assertEquals("expression", grammar.getStart().getName());
        assertEquals(Set.of("COUNTRY_CODE", "NUMBER"), terminalsToValues(grammar));
        assertEquals(Set.of("expression", "number"), nonTerminalsToValues(grammar));
        assertEquals(2, grammar.getProductions().size());
        assertEquals(List.of("expression -> [COUNTRY_CODE, number]", "number -> [NUMBER]"), productionsToStrings(grammar));
    }

    @Test
    void givenGrammarWithAlternatives_whenRead_thenCreated() throws IncorrectGrammarException, IOException {
        Grammar grammar = ContextFreeGrammar.from(new GrammarFile("src/test/resources/grammar/grammar-alternative-correct.txt"), expression);

        assertEquals("CorrectGrammar", grammar.getName());
        assertEquals("expression", grammar.getStart().getName());
        assertEquals(Set.of("COUNTRY_CODE", "NUMBER"), terminalsToValues(grammar));
        assertEquals(Set.of("expression", "number"), nonTerminalsToValues(grammar));
        assertEquals(3, grammar.getProductions().size());
        assertEquals(List.of("expression -> [COUNTRY_CODE, number]", "expression -> [COUNTRY_CODE]", "number -> [NUMBER]"), productionsToStrings(grammar));
    }

    @Test
    void givenGrammarWithWhitespaceAfterAlternative_whenRead_thenCreated() throws IncorrectGrammarException, IOException {
        Grammar grammar = ContextFreeGrammar.from(new GrammarFile("src/test/resources/grammar/grammar-alternative-whitespaces.txt"), expression);

        assertEquals("CorrectGrammar", grammar.getName());
        assertEquals("expression", grammar.getStart().getName());
        assertEquals(Set.of("COUNTRY_CODE", "NUMBER"), terminalsToValues(grammar));
        assertEquals(Set.of("expression", "number"), nonTerminalsToValues(grammar));
        assertEquals(3, grammar.getProductions().size());
        assertEquals(List.of("expression -> [COUNTRY_CODE, number]", "expression -> [COUNTRY_CODE]", "number -> [NUMBER]"), productionsToStrings(grammar));
    }

    @Test
    void givenGrammarWithIncorrectLine_whenRead_thenCreated() throws IncorrectGrammarException, IOException {
        Grammar grammar = ContextFreeGrammar.from(new GrammarFile("src/test/resources/grammar/grammar-incorrect-line.txt"), expression);

        assertEquals("IncorrectGrammar", grammar.getName());
        assertEquals("expression", grammar.getStart().getName());
        assertEquals(Set.of("COUNTRY_CODE", "NUMBER"), terminalsToValues(grammar));
        assertEquals(Set.of("expression", "number"), nonTerminalsToValues(grammar));
        assertEquals(2, grammar.getProductions().size());
        assertEquals(List.of("expression -> [COUNTRY_CODE, number]", "number -> [NUMBER]"), productionsToStrings(grammar));
    }

    @Test
    void givenGrammarWithUndefinedTerminal_whenRead_thenThrowException() {
        assertThrows(IllegalStateException.class, () -> new GrammarFile("src/test/resources/grammar/grammar-undefined-terminal.txt"));
    }

    @Test
    void givenGrammarWithWrongRegex_whenRead_thenThrowException() {
        assertThrows(IllegalStateException.class, () -> new GrammarFile("src/test/resources/grammar/grammar-wrong-regex.txt"));
    }

    private Set<String> terminalsToValues(Grammar grammar) {
        return grammar.getTerminals().stream().map(Symbol::getName).collect(Collectors.toSet());
    }

    private Set<String> nonTerminalsToValues(Grammar grammar) {
        return grammar.getNonTerminals().stream().map(Symbol::getName).collect(Collectors.toSet());
    }

    private List<String> productionsToStrings(Grammar grammar) {
        return grammar.getProductions().stream().map(Production::toString).collect(Collectors.toList());
    }
}