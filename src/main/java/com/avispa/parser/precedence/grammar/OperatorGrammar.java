package com.avispa.parser.precedence.grammar;

import com.avispa.parser.precedence.function.GraphPrecedenceFunctions;
import com.avispa.parser.precedence.function.PrecedenceFunctions;
import com.avispa.parser.precedence.function.PrecedenceFunctionsException;
import com.avispa.parser.precedence.function.PrecedenceFunctionsMode;
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
public abstract class OperatorGrammar extends ContextFreeGrammar {
    protected PrecedenceFunctions functions;
    protected PrecedenceTable table;

    private final PrecedenceFunctionsMode precedenceFunctionsMode;

    protected OperatorGrammar(String name, Set<Terminal> terminals, List<Production> productions, NonTerminal start, PrecedenceFunctionsMode precedenceFunctionsMode) throws IncorrectGrammarException {
        super(name, terminals, productions, start);

        if(!isOperatorGrammar()) {
            throw new IncorrectGrammarException("Grammar is not an operator grammar");
        }

        this.precedenceFunctionsMode = precedenceFunctionsMode;

        this.terminals.add(Terminal.BOUNDARY_MARKER);
        this.nonTerminals.add(NonTerminal.START);
        this.productions.add(0, Production.of(NonTerminal.START, List.of(Terminal.BOUNDARY_MARKER, this.start, Terminal.BOUNDARY_MARKER)));
        this.start = NonTerminal.START;
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
        return this.table.get().values().stream().anyMatch(Precedence.LESS_THAN_OR_EQUALS::equals);
    }

    protected void generatePrecedenceFunctions() {
        if(PrecedenceFunctionsMode.GRAPH_PRECEDENCE_FUNCTIONS.equals(precedenceFunctionsMode)) {
            try {
                this.functions = new GraphPrecedenceFunctions(this.table);
            } catch (PrecedenceFunctionsException e) {
                log.warn("Precedence functions can't be calculated. Precedence table will be used instead.", e);
            }
        } else {
            log.warn("Precedence functions are disabled.");
        }
    }

    public boolean precedenceLessThan(Symbol a, Symbol b) {
        a = a.unwrap();
        b = b.unwrap();

        if(null != functions) {
            int fA = functions.getFFor(a);
            int gB = functions.getGFor(b);

            if(log.isDebugEnabled()) {
                log.debug("Precedence check: f({}) < g({}), is: {} {} {}", a, b, fA, getRelationCharacter(fA, gB), gB);
            }

            return fA < gB;
        } else {
            Precedence precedence = getPrecedence(a, b, Precedence.LESS_THAN);

            return Precedence.LESS_THAN.equals(precedence) || Precedence.LESS_THAN_OR_EQUALS.equals(precedence);
        }
    }

    public boolean precedenceGreaterThan(Symbol a, Symbol b) {
        a = a.unwrap();
        b = b.unwrap();

        if(null != functions) {
            int fA = functions.getFFor(a);
            int gB = functions.getGFor(b);

            if(log.isDebugEnabled()) {
                log.debug("Precedence check: f({}) > g({}), is: {} {} {}", a, b, fA, getRelationCharacter(fA, gB), gB);
            }

            return fA > gB;
        } else {
            Precedence precedence = getPrecedence(a, b, Precedence.GREATER_THAN);

            return Precedence.GREATER_THAN.equals(precedence);
        }
    }

    public boolean precedenceEquals(Symbol a, Symbol b) {
        a = a.unwrap();
        b = b.unwrap();

        if(null != functions) {
            int fA = functions.getFFor(a);
            int gB = functions.getGFor(b);

            if(log.isDebugEnabled()) {
                log.debug("Precedence check: f({}) = g({}), is: {} {} {}", a, b, fA, getRelationCharacter(fA, gB), gB);
            }
            return fA == gB;
        } else {
            Precedence precedence = getPrecedence(a, b, Precedence.EQUALS);

            return Precedence.EQUALS.equals(precedence) || Precedence.LESS_THAN_OR_EQUALS.equals(precedence);
        }
    }

    private Precedence getPrecedence(Symbol a, Symbol b, Precedence expected) {
        Precedence precedence = table.get(a, b);

        if(null == precedence) {
            log.warn("Precedence not found");
        } else {
            log.debug("Precedence check: {} {} {}, is: {} {} {}", a, expected, b, a, precedence, b);
        }

        return precedence;
    }

    private char getRelationCharacter(int a, int b) {
        if(a < b) {
            return '<';
        } else if(a > b) {
            return '>';
        } else {
            return '=';
        }
    }
}
