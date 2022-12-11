package com.avispa.parser.precedence.grammar;

import com.avispa.parser.precedence.function.PrecedenceFunctionsMode;
import com.avispa.parser.precedence.table.OperatorPrecedenceTable;
import com.avispa.parser.precedence.table.PrecedenceTableException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

import static com.avispa.parser.precedence.function.PrecedenceFunctionsMode.GRAPH_PRECEDENCE_FUNCTIONS;

/**
 * Grammar is an operator-precedence grammar when it is an operator grammar, and it is possible to construct and
 * operator-precedence table without conflicts (when there are existing more than one precedence for any pair of symbols).
 * @author Rafał Hiszpański
 */
@Slf4j
public class OperatorPrecedenceGrammar extends OperatorGrammar {

    public OperatorPrecedenceGrammar(GrammarFile grammarFile, NonTerminal start) throws IncorrectGrammarException {
        this(grammarFile, start, GRAPH_PRECEDENCE_FUNCTIONS);
    }

    public OperatorPrecedenceGrammar(GrammarFile grammarFile, NonTerminal start, PrecedenceFunctionsMode precedenceFunctionsMode) throws IncorrectGrammarException {
        this(grammarFile.getName(), grammarFile.getTerminals(), grammarFile.getProductions(), start, precedenceFunctionsMode);
    }

    public OperatorPrecedenceGrammar(String name, Set<Terminal> terminals, List<Production> productions, NonTerminal start) throws IncorrectGrammarException {
        this(name, terminals, productions, start, GRAPH_PRECEDENCE_FUNCTIONS);
    }

    public OperatorPrecedenceGrammar(String name, Set<Terminal> terminals, List<Production> productions, NonTerminal start, PrecedenceFunctionsMode precedenceFunctionsMode) throws IncorrectGrammarException {
        super(name, terminals, productions, start, precedenceFunctionsMode);

        if(!isOperatorPrecedence()) {
            throw new IncorrectGrammarException("Grammar is not an operator-precedence grammar as operator-precedence table cannot be constructed.");
        }

        if(hasLessThanOrEqualsConflict()) {
            throw new IllegalStateException("Weak-precedence detected. It is not supported for operator-precedence grammars");
        }

        generatePrecedenceFunctions();
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
