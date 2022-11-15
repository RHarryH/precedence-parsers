package com.avispa.parser.precedence.grammar;

import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
@Getter
public class ContextFreeGrammar implements IGrammar {
    private final String name;

    private final Set<Terminal> terminals;
    private final Set<NonTerminal> nonTerminals;
    private final List<Production> productions;
    private NonTerminal start;

    public ContextFreeGrammar(String name, Set<Terminal> terminals, List<Production> productions) throws IncorrectGrammarException {
        this.name = name;

        this.terminals = terminals;
        this.productions = productions;

        this.nonTerminals = buildNonTerminalsList(productions);

        if(this.terminals.isEmpty() || this.productions.isEmpty()) {
            throw new IncorrectGrammarException("Terminals and productions must be a non-empty collections");
        }
    }

    /**
     * Scans lhs and rhs of productions. If there is a difference of only one token (lhs has one more token)
     * then this token is a start symbol.
     * @param productions
     * @return non-terminals list
     * @throws IncorrectGrammarException
     */
    private Set<NonTerminal> buildNonTerminalsList(List<Production> productions) throws IncorrectGrammarException {
        Set<NonTerminal> lhsNonTerminals = productions.
                stream().
                map(Production::getLhs).
                collect(Collectors.toSet());

        Set<NonTerminal> rhsNonTerminals = new HashSet<>();
        for(Production production : productions) {
            for(GenericToken token : production.getRhs()) {
                if (token instanceof NonTerminal) {
                    if(!lhsNonTerminals.contains(token)) {
                        throw new IncorrectGrammarException("Token " + token + " is not present on the left-hand side of any production");
                    }
                    if(!rhsNonTerminals.contains(token)) {
                        rhsNonTerminals.add((NonTerminal) token);
                    }
                }
            }
        }

        this.start = findStartToken(lhsNonTerminals, rhsNonTerminals);

        lhsNonTerminals.add(start); // add start symbol to lhs, this becomes the full set of non-terminals

        return lhsNonTerminals;
    }

    /**
     * Finds start token by comparing lhs non-terminals and rhs non-terminals. Start symbol is the
     * difference of this two sets. If there are zero or more than one results then this is unexpected bahavior.
     * @param lhsNonTerminals
     * @param rhsNonTerminals
     * @return start token
     */
    private NonTerminal findStartToken(Set<NonTerminal> lhsNonTerminals, Set<NonTerminal> rhsNonTerminals) throws IncorrectGrammarException {
        return lhsNonTerminals.stream()
                .filter(e -> !rhsNonTerminals.contains(e))
                .reduce((a, b) -> {
                    throw new IllegalStateException("Multiple start token candidates: " + a + ", " + b);
                }).orElseThrow(() -> new IncorrectGrammarException("Start token not detected"));
    }

    @Override
    public String toString() {
        return "G(" + name + ") = (" + terminals + ", " + nonTerminals + ", " + productions + ", " + start + ")";
    }
}
