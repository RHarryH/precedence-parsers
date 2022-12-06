package com.avispa.parser.precedence.grammar;

import com.avispa.parser.precedence.function.PrecedenceFunctions;
import com.avispa.parser.precedence.lexer.Lexeme;
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

    OperatorGrammar(String name, Set<Terminal> terminals, List<Production> productions) throws IncorrectGrammarException {
        super(name, terminals, productions);

        if(!isOperatorGrammar()) {
            throw new IncorrectGrammarException("Grammar is not an operator grammar");
        }

        addTerminal(Terminal.BOUNDARY_MARKER); // add boundary marker as known terminal symbol
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

    public boolean precedenceLessThan(Symbol a, Symbol b) {
        a = unwrapLexeme(a);
        b = unwrapLexeme(b);

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
        a = unwrapLexeme(a);
        b = unwrapLexeme(b);

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
        a = unwrapLexeme(a);
        b = unwrapLexeme(b);

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

    private Symbol unwrapLexeme(Symbol lexeme) {
        if(lexeme instanceof Lexeme) {
            return ((Lexeme) lexeme).getTerminal();
        }
        return lexeme;
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
