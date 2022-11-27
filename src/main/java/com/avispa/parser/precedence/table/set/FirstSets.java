package com.avispa.parser.precedence.table.set;

import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
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
     * Add self to the FIRST set
     * @param terminals
     */
    private void initialize(Set<Terminal> terminals) {
        for(Terminal terminal : terminals) {
            this.sets.put(terminal, Set.of(terminal));
        }
    }

    /**
     * Updates FIRST set by adding  entries for non-terminals. FIRST for non-terminal is a FIRST_ALL with terminals only.
     */
    private void construct(FirstAllSets firstAll) {
        var firstAllMap = firstAll.get();
        for(var firstAllForToken : firstAllMap.entrySet()) {
            GenericToken lhs = firstAllForToken.getKey();
            if(NonTerminal.isOf(lhs)) {
                for(GenericToken token : firstAllForToken.getValue()) {
                    if(Terminal.isOf(token)) {
                        this.sets.computeIfAbsent(lhs, key -> new HashSet<>())
                                .add((Terminal) token);
                    }
                }
            }
        }
    }
}
