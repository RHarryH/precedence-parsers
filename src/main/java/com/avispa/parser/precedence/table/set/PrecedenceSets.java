package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
abstract class PrecedenceSets {
    protected final Map<NonTerminal, List<Production>> groupProductionsByLhs(ContextFreeGrammar grammar) {
        return grammar.getProductions()
                .stream()
                .collect(Collectors.groupingBy(Production::getLhs));
    }
}