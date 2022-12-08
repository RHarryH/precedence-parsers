package com.avispa.parser.precedence.grammar;

import com.avispa.parser.precedence.function.GraphPrecedenceFunctions;
import com.avispa.parser.precedence.function.PrecedenceFunctionsException;
import com.avispa.parser.precedence.table.OperatorPrecedenceTable;
import com.avispa.parser.precedence.table.PrecedenceTableException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

/**
 * Grammar is an operator-precedence grammar when it is an operator grammar, and it is possible to construct and
 * operator-precedence table without conflicts (when there are existing more than one precedence for any pair of symbols).
 * @author Rafał Hiszpański
 */
@Slf4j
public class OperatorPrecedenceGrammar extends OperatorGrammar {
    OperatorPrecedenceGrammar(String name, Set<Terminal> terminals, List<Production> productions) throws IncorrectGrammarException {
        super(name, terminals, productions);

        if(!isOperatorPrecedence()) {
            throw new IncorrectGrammarException("Grammar is not an operator-precedence grammar as operator-precedence table cannot be constructed.");
        }

        if(hasLessThanOrEqualsConflict()) {
            throw new IllegalStateException("Weak-precedence detected. It is not supported for operator-precedence grammars");
        }

        try {
            this.functions = new GraphPrecedenceFunctions(this.table);
        } catch (PrecedenceFunctionsException e) {
            log.warn("Precedence functions can't be calculated. Precedence table will be used instead.", e);
        }

        if(log.isDebugEnabled()) {
            if(null != functions) {
                log.debug("Precedence functions: {}", functions);
            } else {
                log.debug("Precedence table: {}", table);
            }
        }
    }

    private boolean isOperatorPrecedence() {
        try {
            this.table = new OperatorPrecedenceTable(this);
            return true;
        } catch (PrecedenceTableException e) {
            log.error("Grammar is not an operator-precedence grammar as there are conflicts when building operator-precedence table.", e);
        }

        return false;
    }

    @Override
    public String toString() {
        return "Operator-precedence grammar:" + super.toString();
    }
}
