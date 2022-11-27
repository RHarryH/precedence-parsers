package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public abstract class SimplePrecedenceSets extends PrecedenceSets<GenericToken, GenericToken> {
    SimplePrecedenceSets(ContextFreeGrammar grammar, String setsName) {
        super(setsName);
        log.debug("Constructing {} set for '{}' grammar.", setsName, grammar.getName());
        initialize(grammar.getTerminals());
        construct(grammar);
        log.debug("{}", this);
    }

    /**
     * Add empty list for terminals for simple precedence sets (FIRST_ALL/LAST_ALL)
     * @param terminals
     */
    private void initialize(Set<Terminal> terminals) {
        for(Terminal terminal : terminals) {
            this.sets.put(terminal, Set.of());
        }
    }

    /**
     * Iterate through all productions to find sets elements
     * @param grammar context free grammar for which sets should be built
     */
    private void construct(ContextFreeGrammar grammar) {
        // group productions by left-hand side non-terminals so alternatives can be processed in single external loop run
        Map<NonTerminal, List<Production>> productionsByLhs =
                groupProductionsByLhs(grammar);

        for(var productionsEntry : productionsByLhs.entrySet()) { // for all lhs
            NonTerminal lhs = productionsEntry.getKey();
            List<Production> productions = productionsEntry.getValue();

            log.debug("Started processing of {} non-terminal.", lhs);
            constructFor(lhs, lhs, productions, productionsByLhs, new HashSet<>());
        }
    }

    /**
     * Recursively iterate through productions. Last token on the right is added to LAST_ALL set. If token is a non-terminal
     * it is recursively checked for it's last token until end of possible derivation is reached. The algorithm does not do
     * recursive check if non-terminal was already visited.
     *  @param topLhs non-terminal for which LAST_ALL is built
     * @param currentLhs currently visited production's lhs
     * @param currentRhsProductions currently visited production's rhs
     * @param productionsByLhs list of productions grouped by lhs non-terminal
     * @param visited set of already visited nodes for top lhs non-terminal
     */
    private void constructFor(NonTerminal topLhs, NonTerminal currentLhs, List<Production> currentRhsProductions, Map<NonTerminal, List<Production>> productionsByLhs, Set<NonTerminal> visited) {
        visited.add(currentLhs); // do not visit already visiting token to avoid endless loop

        for (Production production : currentRhsProductions) { // for all alternatives
            log.debug("Checking {} production for {} set.", production, setsName);
            List<GenericToken> rhsTokens = production.getRhs();

            GenericToken lastToken = findToken(rhsTokens);
            log.debug("Last token for {} production is {}. Adding to {} set.", production, lastToken, setsName);

            update(topLhs, lastToken);
            if(lastToken instanceof NonTerminal && !visited.contains(lastToken)) {
                log.debug("Token {} is a non-terminal and wasn't visited before. Check it recursively for it's first token.", lastToken);
                constructFor(topLhs, (NonTerminal) lastToken, productionsByLhs.get(lastToken), productionsByLhs, visited);
            } else {
                log.debug("Token {} is a terminal or was visited before. Skipping.", lastToken);
            }
        }
    }

    protected abstract GenericToken findToken(List<GenericToken> rhsTokens);
}
