package com.avispa.parser.precedence.grammar.validation;

import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Symbol;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Context-free grammar is considered as operator grammar when it fulfills two conditions:
 * - does not have empty productions
 * - there are no two consecutive non-terminals in any production
 * @author Rafał Hiszpański
 */
@Slf4j
public class OperatorGrammarValidator implements GrammarValidator {

    @Override
    public boolean is(Grammar grammar) {
        if(!isOperatorGrammar(grammar)) {
            log.error("Grammar is not an operator grammar");
            return false;
        }

        return true;
    }

    private boolean isOperatorGrammar(Grammar grammar) {
        for(Production production : grammar.getProductions()) {
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
}
