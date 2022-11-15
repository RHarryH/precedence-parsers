package com.avispa.parser.precedence.grammar;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class GrammarFileTest {

    @Test
    void givenCorrectGrammar_whenRead_thenCreated() throws IncorrectGrammarException {
        IGrammar grammar = new GrammarFile("src/test/resources/grammar-correct.txt").read();

        assertEquals("CorrectGrammar", grammar.getName());
        assertEquals("expression", grammar.getStart().getValue());
        assertEquals(Set.of("COUNTRY_CODE", "NUMBER"), terminalsToValues(grammar));
        assertEquals(Set.of("expression", "number"), nonTerminalsToValues(grammar));
        assertEquals(2, grammar.getProductions().size());
        assertEquals(List.of("expression -> [COUNTRY_CODE, number]", "number -> [NUMBER]"), productionsToStrings(grammar));
    }

    @Test
    void givenGrammarWithAlternatives_whenRead_thenCreated() throws IncorrectGrammarException {
        IGrammar grammar = new GrammarFile("src/test/resources/grammar-alternative-correct.txt").read();

        assertEquals("CorrectGrammar", grammar.getName());
        assertEquals("expression", grammar.getStart().getValue());
        assertEquals(Set.of("COUNTRY_CODE", "NUMBER"), terminalsToValues(grammar));
        assertEquals(Set.of("expression", "number"), nonTerminalsToValues(grammar));
        assertEquals(3, grammar.getProductions().size());
        assertEquals(List.of("expression -> [COUNTRY_CODE, number]", "expression -> [COUNTRY_CODE]", "number -> [NUMBER]"), productionsToStrings(grammar));
    }

    @Test
    void givenGrammarWithIncorrectLine_whenRead_thenCreated() throws IncorrectGrammarException {
        IGrammar grammar = new GrammarFile("src/test/resources/grammar-incorrect-line.txt").read();

        assertEquals("IncorrectGrammar", grammar.getName());
        assertEquals("expression", grammar.getStart().getValue());
        assertEquals(Set.of("COUNTRY_CODE", "NUMBER"), terminalsToValues(grammar));
        assertEquals(Set.of("expression", "number"), nonTerminalsToValues(grammar));
        assertEquals(2, grammar.getProductions().size());
        assertEquals(List.of("expression -> [COUNTRY_CODE, number]", "number -> [NUMBER]"), productionsToStrings(grammar));
    }

    @Test
    void givenGrammarWithUndefinedTerminal_whenRead_thenThrowException() {
        GrammarFile file = new GrammarFile("src/test/resources/grammar-undefined-terminal.txt");
        assertThrows(IllegalStateException.class, file::read);
    }

    @Test
    void givenGrammarWithWrongRegex_whenRead_thenThrowException() {
        GrammarFile file = new GrammarFile("src/test/resources/grammar-wrong-regex.txt");
        assertThrows(IllegalStateException.class, file::read);
    }

    private Set<String> terminalsToValues(IGrammar grammar) {
        return grammar.getTerminals().stream().map(GenericToken::getValue).collect(Collectors.toSet());
    }

    private Set<String> nonTerminalsToValues(IGrammar grammar) {
        return grammar.getNonTerminals().stream().map(GenericToken::getValue).collect(Collectors.toSet());
    }

    private List<String> productionsToStrings(IGrammar grammar) {
        return grammar.getProductions().stream().map(Production::toString).collect(Collectors.toList());
    }
}