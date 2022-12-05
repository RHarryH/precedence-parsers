package com.avispa.parser.precedence.grammar;

import com.avispa.parser.precedence.function.PrecedenceFunctions;
import com.avispa.parser.precedence.table.Precedence;
import com.avispa.parser.precedence.table.PrecedenceTable;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

/**
 * Context-free grammar is considered as operator grammar when it fulfills two conditions:
 * - does not have empty productions
 * - there are no two consecutive non-terminals in any production
 * @author Rafał Hiszpański
 */
@Slf4j
abstract class OperatorGrammar extends ContextFreeGrammar {
    protected PrecedenceFunctions functions;
    protected PrecedenceTable<? extends Symbol> table;

    OperatorGrammar(String name, Set<Terminal> terminals, List<Production> productions) throws IncorrectGrammarException {
        super(name, terminals, productions);

        if(!isOperatorGrammar()) {
            throw new IncorrectGrammarException("Grammar is not an operator grammar");
        }

        this.getTerminals().add(Terminal.BOUNDARY_MARKER); // add boundary marker as known terminal symbol
    }

    private boolean isOperatorGrammar() {
        for(Production production : getProductions()) {
            List<Symbol> rhs = production.getRhs();

            if(rhs.isEmpty()) {
                log.error("Empty productions are not all allowed for operator grammars: {}.", production);
                return false;
            }

            if(hasConsecutiveNonTerminals(rhs)) {
                log.error("Two consecutive non-terminal are not allowed for operator grammars: {}.", production);
                return false;
            }
        }

        return true;
    }

    private boolean hasConsecutiveNonTerminals(List<Symbol> rhs) {
        boolean previousIsNonTerminal = false;
        for(var symbol : rhs) {
            if(symbol instanceof NonTerminal) {
                if(previousIsNonTerminal) {
                    return true;
                }
                previousIsNonTerminal = true;
            } else {
                previousIsNonTerminal = false;
            }
        }

        return false;
    }

    protected boolean hasLessThanOrEqualsConflict() {
        return table.get().values().stream().anyMatch(Precedence.LESS_THAN_OR_EQUALS::equals);
    }
}
