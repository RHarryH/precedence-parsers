package com.avispa.parser.precedence.table;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Rafał Hiszpański
 */
class PrecedenceTableTest {

    private static final NonTerminal A = NonTerminal.of("A");
    private static final NonTerminal B = NonTerminal.of("B");

    private static final Terminal a = Terminal.of("a", "a");
    private static final Terminal b = Terminal.of("b", "b");

    private static final Terminal marker = Terminal.of("$", "\\$");

    @Test
    void givenSimpleGrammar_whenPrecedenceTable_thenCorrectTable() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(Production.of(A, List.of(B, a, a)), Production.of(B, List.of(b)));

        ContextFreeGrammar grammar = new ContextFreeGrammar("Test", terminals, productions);

        // when
        PrecedenceTable precedenceTable = new PrecedenceTable(grammar);

        // then
        Map<Pair<GenericToken, GenericToken>, Precedence> expected = new HashMap<>();
        expected.put(Pair.of(B, a), Precedence.EQUALS);

        expected.put(Pair.of(a, a), Precedence.EQUALS);
        expected.put(Pair.of(a, marker), Precedence.GREATER_THAN);

        expected.put(Pair.of(b, a), Precedence.GREATER_THAN);
        expected.put(Pair.of(b, marker), Precedence.GREATER_THAN);

        expected.put(Pair.of(marker, B), Precedence.LESS_THAN);
        expected.put(Pair.of(marker, b), Precedence.LESS_THAN);

        assertEquals(expected.entrySet(), precedenceTable.get().entrySet());
    }

    @Test
    void givenGrammarWithConflict_whenPrecedenceTable_thenThrowException() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        /* S -> A
         * A -> AB | a
         * B -> BA | a
         */
        List<Production> productions = List.of(Production.of(NonTerminal.of("S"), List.of(A)), Production.of(A, List.of(A, B)), Production.of(A, List.of(a)), Production.of(B, List.of(B, A)), Production.of(B, List.of(a)));

        ContextFreeGrammar grammar = new ContextFreeGrammar("Test", terminals, productions);

        // when/then
        assertThrows(UnresolvablePrecedenceConflictException.class, () -> new PrecedenceTable(grammar));
    }

    @Test
    void givenWeakPrecedenceGrammar_whenPrecedenceTable_thenCorrectTable() throws IncorrectGrammarException {
        // given
        ContextFreeGrammar grammar = new GrammarFile("src/test/resources/grammar/weak-precedence-grammar.txt").read();

        // when
        PrecedenceTable precedenceTable = new PrecedenceTable(grammar);

        // then
        NonTerminal expression = NonTerminal.of("expression");
        NonTerminal term = NonTerminal.of("term");
        NonTerminal factor = NonTerminal.of("factor");

        Terminal add = Terminal.of("ADD", "\\+");
        Terminal mul = Terminal.of("MUL", "\\*");
        Terminal number = Terminal.of("NUMBER", "[0-9]");

        Map<Pair<GenericToken, GenericToken>, Precedence> expected = new HashMap<>();
        expected.put(Pair.of(expression, add), Precedence.EQUALS);

        expected.put(Pair.of(term, add), Precedence.GREATER_THAN);
        expected.put(Pair.of(term, mul), Precedence.EQUALS);
        expected.put(Pair.of(term, marker), Precedence.GREATER_THAN);

        expected.put(Pair.of(factor, add), Precedence.GREATER_THAN);
        expected.put(Pair.of(factor, mul), Precedence.GREATER_THAN);
        expected.put(Pair.of(factor, marker), Precedence.GREATER_THAN);

        expected.put(Pair.of(add, term), Precedence.LESS_THAN_OR_EQUALS);
        expected.put(Pair.of(add, factor), Precedence.LESS_THAN);
        expected.put(Pair.of(add, number), Precedence.LESS_THAN);

        expected.put(Pair.of(mul, factor), Precedence.EQUALS);
        expected.put(Pair.of(mul, number), Precedence.LESS_THAN);

        expected.put(Pair.of(number, add), Precedence.GREATER_THAN);
        expected.put(Pair.of(number, mul), Precedence.GREATER_THAN);
        expected.put(Pair.of(number, marker), Precedence.GREATER_THAN);

        expected.put(Pair.of(marker, expression), Precedence.LESS_THAN);
        expected.put(Pair.of(marker, term), Precedence.LESS_THAN);
        expected.put(Pair.of(marker, factor), Precedence.LESS_THAN);
        expected.put(Pair.of(marker, number), Precedence.LESS_THAN);

        assertEquals(expected.entrySet(), precedenceTable.get().entrySet());
    }
}