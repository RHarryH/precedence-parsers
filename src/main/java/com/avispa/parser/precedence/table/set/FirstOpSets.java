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
public final class FirstOpSets extends OperatorPrecedenceSets {
    public FirstOpSets(ContextFreeGrammar grammar) {
        super(grammar, "FIRST_OP");
    }

    /**
     * Finds first terminal for right-hand side production tokens
     * @param rhsTokens
     * @return
     */
    @Override
    protected Terminal findTerminal(List<GenericToken> rhsTokens) {
        log.debug("Looking for first terminal in {} right-hand side tokens.", rhsTokens);
        for (GenericToken token : rhsTokens) {
            if (token instanceof Terminal) {
                log.debug("{} found.", token);
                return (Terminal) token;
            }
        }

        return null;
    }
}
