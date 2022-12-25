package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Symbol;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public abstract class SimplePrecedenceSets extends PrecedenceSets<Symbol, Symbol> {
    SimplePrecedenceSets(Grammar grammar, String setsName) {
        super(setsName);
        log.debug("Constructing {} set for '{}' grammar.", setsName, grammar.getName());
        construct(grammar);
        log.debug("{}", this);
    }

    /**
     * Iterate through all productions to find sets elements
     * @param grammar context free grammar for which sets should be built
     */
    private void construct(Grammar grammar) {
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
     * Recursively iterate through productions. Last symbol on the right is added to LAST_ALL set. If symbol is a non-terminal
     * it is recursively checked for it's last symbol until end of possible derivation is reached. The algorithm does not do
     * recursive check if non-terminal was already visited.
     *  @param topLhs non-terminal for which LAST_ALL is built
     * @param currentLhs currently visited production's lhs
     * @param currentRhsProductions currently visited production's rhs
     * @param productionsByLhs list of productions grouped by lhs non-terminal
     * @param visited set of already visited nodes for top lhs non-terminal
     */
    private void constructFor(NonTerminal topLhs, NonTerminal currentLhs, List<Production> currentRhsProductions, Map<NonTerminal, List<Production>> productionsByLhs, Set<NonTerminal> visited) {
        visited.add(currentLhs); // do not visit already visiting symbol to avoid endless loop

        for (Production production : currentRhsProductions) { // for all alternatives
            log.debug("Checking {} production for {} set.", production, name);
            List<Symbol> rhsSymbols = production.getRhs();

            Symbol symbol = findSymbol(rhsSymbols);
            log.debug("First/last symbol for {} production is {}. Adding to {} set.", production, symbol, name);

            update(topLhs, symbol);
            if(NonTerminal.isOf(symbol) && !visited.contains(symbol)) {
                log.debug("Symbol {} is a non-terminal and wasn't visited before. Check it recursively for it's first symbol.", symbol);
                constructFor(topLhs, (NonTerminal) symbol, productionsByLhs.get(symbol), productionsByLhs, visited);
            } else {
                log.debug("Symbol {} is a terminal or was visited before. Skipping.", symbol);
            }
        }
    }
}
