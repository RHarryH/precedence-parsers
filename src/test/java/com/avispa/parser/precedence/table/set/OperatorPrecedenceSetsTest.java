package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.avispa.parser.precedence.TokenUtil.A;
import static com.avispa.parser.precedence.TokenUtil.B;
import static com.avispa.parser.precedence.TokenUtil.C;
import static com.avispa.parser.precedence.TokenUtil.D;
import static com.avispa.parser.precedence.TokenUtil.a;
import static com.avispa.parser.precedence.TokenUtil.add;
import static com.avispa.parser.precedence.TokenUtil.b;
import static com.avispa.parser.precedence.TokenUtil.expression;
import static com.avispa.parser.precedence.TokenUtil.factor;
import static com.avispa.parser.precedence.TokenUtil.lpar;
import static com.avispa.parser.precedence.TokenUtil.mul;
import static com.avispa.parser.precedence.TokenUtil.number;
import static com.avispa.parser.precedence.TokenUtil.rpar;
import static com.avispa.parser.precedence.TokenUtil.start;
import static com.avispa.parser.precedence.TokenUtil.term;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class OperatorPrecedenceSetsTest {

    @Test
    void givenSingleProduction_whenSetsCreated_thenAreCorrect() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(a)));

        // when
        var grammar = new ContextFreeGrammar("Test", terminals, productions);
        var firstOp = new FirstOpSets(grammar);
        var lastOp = new LastOpSets(grammar);

        // then
        assertEquals(Set.of(a), firstOp.getFor(A));
        assertEquals(Set.of(a), lastOp.getFor(A));
    }

    @Test
    void givenProductionWithNonTerminalOnRhs_whenSetsCreated_thenAreCorrect() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a);

        List<Production> productions = List.of(Production.of(A, List.of(B, a)), Production.of(B, List.of(a)));

        // when
        var grammar = new ContextFreeGrammar("Test", terminals, productions);
        var firstOp = new FirstOpSets(grammar);
        var lastOp = new LastOpSets(grammar);

        // then
        assertEquals(Set.of(a), firstOp.getFor(A));
        assertEquals(Set.of(a), firstOp.getFor(B));
        assertEquals(Set.of(a), lastOp.getFor(A));
        assertEquals(Set.of(a), lastOp.getFor(B));
    }

    @Test
    void givenProductionsWithMultipleNonTerminals_whenSetsCreated_thenAreCorrect() throws IncorrectGrammarException {
        // given
        Set<Terminal> terminals = Set.of(a, b);

        List<Production> productions = List.of(
                Production.of(A, List.of(B, a, C, D)),
                Production.of(B, List.of(a)),
                Production.of(C, List.of(b)),
                Production.of(D, List.of(a, b)));

        // when
        var grammar = new ContextFreeGrammar("Test", terminals, productions);
        var firstOp = new FirstOpSets(grammar);
        var lastOp = new LastOpSets(grammar);

        // then
        assertEquals(Set.of(a), firstOp.getFor(A));
        assertEquals(Set.of(a), firstOp.getFor(B));
        assertEquals(Set.of(b), firstOp.getFor(C));
        assertEquals(Set.of(a), firstOp.getFor(D));

        assertEquals(Set.of(a, b), lastOp.getFor(A));
        assertEquals(Set.of(a), lastOp.getFor(B));
        assertEquals(Set.of(b), lastOp.getFor(C));
        assertEquals(Set.of(b), lastOp.getFor(D));
    }

    @Test
    void givenOperatorPrecedenceGrammar_whenCreateSets_thenSetsAreCorrect() throws IncorrectGrammarException {
        // given
        ContextFreeGrammar grammar = new GrammarFile("src/test/resources/grammar/operator-precedence-grammar.txt").read();

        // when
        var firstOp = new FirstOpSets(grammar);
        var lastOp = new LastOpSets(grammar);

        // then
        assertEquals(Set.of(lpar, add, mul, number), firstOp.getFor(start));
        assertEquals(Set.of(lpar, add, mul, number), firstOp.getFor(expression));
        assertEquals(Set.of(lpar, mul, number), firstOp.getFor(term));
        assertEquals(Set.of(lpar, number), firstOp.getFor(factor));

        assertEquals(Set.of(rpar, add, mul, number), lastOp.getFor(start));
        assertEquals(Set.of(rpar, add, mul, number), lastOp.getFor(expression));
        assertEquals(Set.of(rpar, mul, number), lastOp.getFor(term));
        assertEquals(Set.of(rpar, number), lastOp.getFor(factor));
    }

}