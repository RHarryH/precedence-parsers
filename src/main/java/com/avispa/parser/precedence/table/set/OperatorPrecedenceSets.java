package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
abstract class OperatorPrecedenceSets extends PrecedenceSets<NonTerminal, Terminal> {
    OperatorPrecedenceSets(ContextFreeGrammar grammar, String setsName) {
        super(setsName);
        log.debug("Constructing {} set for '{}' grammar.", setsName, grammar.getName());
        construct(grammar);
        log.debug("{}", this);
    }

    /**
     * Iterate through all productions to find sets elements.
     *
     * @param grammar context free grammar for which sets should be built
     */
    private void construct(ContextFreeGrammar grammar) {
        // group productions by left-hand side non-terminals so alternatives can be processed in single external loop run
        Map<NonTerminal, List<Production>> productionsByLhs =
                groupProductionsByLhs(grammar);

        for(NonTerminal nonTerminal : grammar.getNonTerminals()) {
            constructFor(nonTerminal, productionsByLhs, new ArrayDeque<>());
        }
    }

    /**
     * Iterate through productions of provided left-hand side non-terminal to find its operator precedence set.
     *
     * @param lhs current lhs non-terminal
     * @param productionsByLhs list of productions grouped by lhs non-terminal
     */
    private Set<Terminal> constructFor(NonTerminal lhs, Map<NonTerminal, List<Production>> productionsByLhs, Deque<NonTerminal> recursionChain) {
        log.debug("Started processing of '{}' non-terminal.", lhs);

        Set<Terminal> set = new HashSet<>();
        for (Production production : productionsByLhs.get(lhs)) { // for all alternatives
            set.addAll(constructDownstream(lhs, production.getRhs(), productionsByLhs, recursionChain));
        }
        return set;
    }

    /**
     * Method constructs set of terminals, which applies to FIRST_OP or LAST_OP sets defined for left-hand side non-terminal.
     * First it gets first/last token of production and it i
     * First/last terminals on the right are added to FIRST_OP and LAST_OP sets respectively. If token is a non-terminal
     * it is recursively checked for it's first/last tokens until end of possible derivation
     * is reached. The algorithm does not do recursive check if non-terminal was already visited to prevent infinite loop.
     *
     * @param lhs
     * @param rhsTokens
     * @param productionsByLhs
     * @param recursionChain
     * @return
     */
    private Set<Terminal> constructDownstream(NonTerminal lhs, List<GenericToken> rhsTokens, Map<NonTerminal, List<Production>> productionsByLhs, Deque<NonTerminal> recursionChain) {
        GenericToken token = findToken(rhsTokens);

        Set<Terminal> downstreamTerminals = new HashSet<>();
        if(NonTerminal.isOf(token)) {
            NonTerminal nonTerminal = (NonTerminal) token;

            if(this.sets.containsKey(token)) {
                log.debug("Set for '{}' already exists. It will be reused.", token);
                downstreamTerminals = new HashSet<>(this.sets.get(token)); // make a copy, otherwise set for last token non-terminal will be overwritten
            } else if(recursionChain.contains(nonTerminal)) {
                log.debug("Set for '{}' does not exist and is already under construction.", nonTerminal);
            } else {
                log.debug("Set for '{}' does not exists. It will be created by recursive construction.", nonTerminal);

                recursionChain.push(nonTerminal);
                downstreamTerminals = constructFor(nonTerminal, productionsByLhs, recursionChain);
                recursionChain.pop();
            }

            Terminal terminal = findTerminal(rhsTokens); // find first terminal
            if(null != terminal) {
                downstreamTerminals.add(terminal); // add found terminal to propagate it upstream
            }
        } else {
            downstreamTerminals.add((Terminal) token);
        }

        log.debug("Generated set for '{}' non-terminal: {}", lhs, downstreamTerminals);
        update(lhs, downstreamTerminals);

        return downstreamTerminals;
    }

    /**
     * Find first or last terminal from right-hand side tokens
     * @param rhsTokens
     * @return
     */
    protected abstract Terminal findTerminal(List<GenericToken> rhsTokens);
}