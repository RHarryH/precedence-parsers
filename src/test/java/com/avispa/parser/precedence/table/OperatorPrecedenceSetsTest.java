package com.avispa.parser.precedence.table;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.table.set.FirstOpSets;
import com.avispa.parser.precedence.table.set.LastOpSets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.avispa.parser.precedence.TokenUtil.add;
import static com.avispa.parser.precedence.TokenUtil.expression;
import static com.avispa.parser.precedence.TokenUtil.factor;
import static com.avispa.parser.precedence.TokenUtil.lpar;
import static com.avispa.parser.precedence.TokenUtil.marker;
import static com.avispa.parser.precedence.TokenUtil.mul;
import static com.avispa.parser.precedence.TokenUtil.number;
import static com.avispa.parser.precedence.TokenUtil.rpar;
import static com.avispa.parser.precedence.TokenUtil.start;
import static com.avispa.parser.precedence.TokenUtil.term;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
class OperatorPrecedenceSetsTest {

    @Test
    void givenOperatorPrecedenceGrammar_whenCreateSets_thenSetsAreCorrect() throws IncorrectGrammarException {
        // given
        ContextFreeGrammar grammar = new GrammarFile("src/test/resources/grammar/operator-precedence-grammar.txt").read();

        // when
        FirstOpSets firstOpSets = new FirstOpSets(grammar);
        LastOpSets lastOpSets = new LastOpSets(grammar);

        log.debug("{}", firstOpSets);
        log.debug("{}", lastOpSets);

        // then
        assertEquals(Set.of(marker), firstOpSets.getFor(start));
        assertEquals(Set.of(lpar, add, mul, number), firstOpSets.getFor(expression));
        assertEquals(Set.of(lpar, mul, number), firstOpSets.getFor(term));
        assertEquals(Set.of(lpar, number), firstOpSets.getFor(factor));

        assertEquals(Set.of(marker), lastOpSets.getFor(start));
        assertEquals(Set.of(rpar, add, mul, number), lastOpSets.getFor(expression));
        assertEquals(Set.of(rpar, mul, number), lastOpSets.getFor(term));
        assertEquals(Set.of(rpar, number), lastOpSets.getFor(factor));
    }

}