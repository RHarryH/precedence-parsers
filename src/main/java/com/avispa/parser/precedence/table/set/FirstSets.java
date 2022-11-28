package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class FirstSets extends PrecedenceSets<GenericToken, Terminal> {
    public FirstSets(FirstAllSets firstAll, Set<Terminal> terminals) {
        super("FIRST");
        log.debug("Constructing {} set.", name);
        initialize(terminals);
        construct(firstAll);
        log.debug("{}", this);
    }

    /**
     * For each terminal add self to the FIRST set
     * @param terminals
     */
    private void initialize(Set<Terminal> terminals) {
        for(Terminal terminal : terminals) {
            this.sets.put(terminal, Set.of(terminal));
        }
    }

    /**
     * Updates FIRST set by adding entries for non-terminals. FIRST for non-terminal is a FIRST_ALL for that non-terminal
     * with terminals only.
     */
    private void construct(FirstAllSets firstAll) {
        var firstAllMap = firstAll.get();
        for(var firstAllForToken : firstAllMap.entrySet()) {
            GenericToken setToken = firstAllForToken.getKey();
            if(NonTerminal.isOf(setToken)) {
                for(GenericToken token : firstAllForToken.getValue()) {
                    if(Terminal.isOf(token)) {
                        update(setToken, (Terminal) token);
                    }
                }
            }
        }
    }

    @Override
    protected GenericToken findToken(List<GenericToken> rhsTokens) {
        throw new UnsupportedOperationException("This method is not required as FIRST is derived from FIRST_ALL");
    }
}
