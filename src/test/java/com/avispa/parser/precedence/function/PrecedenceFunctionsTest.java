package com.avispa.parser.precedence.function;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Terminal;
import com.avispa.parser.precedence.table.Precedence;
import com.avispa.parser.precedence.table.PrecedenceTable;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * @author Rafał Hiszpański
 */
@ExtendWith(MockitoExtension.class)
class PrecedenceFunctionsTest {
    private static final Terminal add = Terminal.of("add", "\\+");
    private static final Terminal mul = Terminal.of("mul", "\\*");
    private static final Terminal marker = Terminal.of("$", "\\$");
    private static final Terminal number = Terminal.of("number", "[0-9]");

    @Test
    void givenOperatorPrecedenceTable_whenPrecedenceFunctionsCreated_thenTheyExistAndAreCorrect() throws PrecedenceFunctionsException {
        // given
        PrecedenceTable precedenceTable = Mockito.mock(PrecedenceTable.class);

        Map<Pair<GenericToken, GenericToken>, Precedence> data = new HashMap<>();
        data.put(Pair.of(number, add), Precedence.GREATER_THAN);
        data.put(Pair.of(number, mul), Precedence.GREATER_THAN);
        data.put(Pair.of(number, marker), Precedence.GREATER_THAN);

        data.put(Pair.of(add, number), Precedence.LESS_THAN);
        data.put(Pair.of(add, add), Precedence.GREATER_THAN);
        data.put(Pair.of(add, mul), Precedence.LESS_THAN);
        data.put(Pair.of(add, marker), Precedence.GREATER_THAN);

        data.put(Pair.of(mul, number), Precedence.LESS_THAN);
        data.put(Pair.of(mul, add), Precedence.GREATER_THAN);
        data.put(Pair.of(mul, mul), Precedence.GREATER_THAN);
        data.put(Pair.of(mul, marker), Precedence.GREATER_THAN);

        data.put(Pair.of(marker, number), Precedence.LESS_THAN);
        data.put(Pair.of(marker, add), Precedence.LESS_THAN);
        data.put(Pair.of(marker, mul), Precedence.LESS_THAN);

        when(precedenceTable.get()).thenReturn(data);
        when(precedenceTable.getTokens()).thenReturn(Set.of(number, add, mul, marker));

        // when
        IPrecedenceFunctions functions = new PrecedenceFunctions(precedenceTable);

        // then
        assertEquals(4, functions.getFFor(number));
        assertEquals(2, functions.getFFor(add));
        assertEquals(4, functions.getFFor(mul));
        assertEquals(0, functions.getFFor(marker));

        assertEquals(5, functions.getGFor(number));
        assertEquals(1, functions.getGFor(add));
        assertEquals(3, functions.getGFor(mul));
        assertEquals(0, functions.getGFor(marker));
    }

    /**
     * Provided mock precedence table should lead to the graph below:
     *
     * F_number ----> G_number
     *    |              |
     *    v              v
     *  G_add <------- F_add
     */
    @Test
    void givenTableWithCycle_whenPrecedenceFunctionsCreated_thenThrowException() {
        // given
        PrecedenceTable precedenceTable = Mockito.mock(PrecedenceTable.class);

        Map<Pair<GenericToken, GenericToken>, Precedence> data = new HashMap<>();
        data.put(Pair.of(number, number), Precedence.GREATER_THAN);
        data.put(Pair.of(add, number), Precedence.LESS_THAN);
        data.put(Pair.of(add, add), Precedence.GREATER_THAN);
        data.put(Pair.of(number, add), Precedence.LESS_THAN);

        when(precedenceTable.get()).thenReturn(data);
        when(precedenceTable.getTokens()).thenReturn(Set.of(number, add));

        // when/then
        assertThrows(PrecedenceFunctionsException.class, () -> new PrecedenceFunctions(precedenceTable));
    }

    @Test
    void givenSimplePrecedenceGrammar_whenPrecedenceFunctionsCreated_thenTheyExistAndAreCorrect() throws IncorrectGrammarException, PrecedenceFunctionsException {
        // given
        ContextFreeGrammar grammar = new GrammarFile("src/test/resources/grammar/simple-precedence-grammar.txt").read();
        PrecedenceTable precedenceTable = new PrecedenceTable(grammar);

        // when
        PrecedenceFunctions functions = new PrecedenceFunctions(precedenceTable);
    }
}