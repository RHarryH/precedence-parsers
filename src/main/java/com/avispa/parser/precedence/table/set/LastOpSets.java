package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public final class LastOpSets extends OperatorPrecedenceSets {
    public LastOpSets(ContextFreeGrammar grammar) {
        super(grammar, "LAST_OP");
    }

    /**
     * Finds last terminal for right-hand side production tokens
     * @param rhsTokens
     * @return
     */
    @Override
    protected Terminal findTerminal(List<GenericToken> rhsTokens) {
        log.debug("Looking for last terminal in {} right-hand side tokens.", rhsTokens);
        var it = rhsTokens.listIterator(rhsTokens.size());
        while(it.hasPrevious()) {
            GenericToken token = it.previous();
            if (token instanceof Terminal) {
                log.debug("'{}' found.", token);
                return (Terminal) token;
            }
        }

        return null;
    }
}
