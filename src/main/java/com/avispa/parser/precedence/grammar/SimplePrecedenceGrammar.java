package com.avispa.parser.precedence.grammar;

import com.avispa.parser.precedence.function.GraphPrecedenceFunctions;
import com.avispa.parser.precedence.function.PrecedenceFunctionsException;
import com.avispa.parser.precedence.table.PrecedenceTableException;
import com.avispa.parser.precedence.table.SimplePrecedenceTable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

/**
 * Simple precedence grammar is a grammar when precedence table contains all possible symbols and does not have any conflicts.
 * All right-hand sides of productions has to be unique within whole grammar.
 *
 * If there is a conflict of ⋖ with ≐ and none of the rhs is a tail of another rhs then grammar is considered as
 * weak-precedence grammar.
 *
 * Last condition for weak-precedence grammar is not checked. Some sources add additional requirement that if A -> aXb and
 * B -> b are productions then there are no relations X ⋖ B and X ≐ B. Other sources mentions that when there are more
 * than one matching productions, the longest match should be picked by a parser. This approach is used in this application.
 * @author Rafał Hiszpański
 */
@Slf4j
@Getter
public class SimplePrecedenceGrammar extends OperatorGrammar {
    private final boolean weak;

    SimplePrecedenceGrammar(String name, Set<Terminal> terminals, List<Production> productions) throws IncorrectGrammarException {
        super(name, terminals, productions);

        if(!isSimplePrecedence()) {
            throw new IncorrectGrammarException("Grammar is not a simple precedence grammar.");
        }

        this.weak = isWeakPrecedence();

        if(!this.weak) { // generate precedence functions only when there are no weak-precedence, not sure what should happen there
            try {
                this.functions = new GraphPrecedenceFunctions(this.table);
            } catch (PrecedenceFunctionsException e) {
                log.warn("Precedence functions can't be calculated. Precedence table will be used instead.", e);
            }
        } else {
            log.warn("Precedence functions won't be calculated because weak-precedence grammar was detected.");
        }

        if(log.isDebugEnabled()) {
            if(null != functions) {
                log.debug("Precedence functions:");
                log.debug("{}", functions);
            } else {
                log.debug("Precedence table:");
                log.debug("{}", table);
            }
        }
    }

    private boolean isSimplePrecedence() {
        try {
            this.table = new SimplePrecedenceTable(this);

            return areAllRhsUnique();
        } catch (PrecedenceTableException e) {
            log.error("Grammar is not a simple precedence grammar as there are conflicts when building simple precedence table.", e);
        }

        return false;
    }

    private boolean areAllRhsUnique() {
        long rhsSize = getProductions().size(); // number of all is the same as number of productions
        long uniqueRhsSize = getProductions().stream().distinct().count();
        if(rhsSize != uniqueRhsSize) {
            log.error("Not all right-hand sides of productions are unique.");
            return false;
        }

        return true;
    }

    private boolean isWeakPrecedence() {
        return hasLessThanOrEqualsConflict();
    }

    @Override
    public String toString() {
        final String newLine = System.lineSeparator();
        return (weak ? "Weak-precedence grammar: " : "Simple precedence grammar: ") + newLine + super.toString();
    }
}
