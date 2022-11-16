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
// TODO: when production contains unknown terminals throw exception
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

        this.start = findStartToken(lhsNonTerminals, productions);

        lhsNonTerminals.add(start); // add start symbol to lhs, this becomes the full set of non-terminals

        return lhsNonTerminals;
    }

    /**
     * Finds start token.
     *
     * First all non-terminals are candidates but when the non-terminal will be found on the productions right-hand
     * side then it is eliminated. Non-terminal is not eliminated when it is left-hand side of the production in single
     * production rule.
     * @param nonTerminals non-terminals set
     * @param productions productions list
     * @return
     * @throws IncorrectGrammarException
     */
    private NonTerminal findStartToken(Set<NonTerminal> nonTerminals, List<Production> productions) throws IncorrectGrammarException {
        Set<NonTerminal> candidates = new HashSet<>(nonTerminals);
        for(Production production : productions) {
            NonTerminal candidate = production.getLhs();

            List<NonTerminal> rhsNonTerminals = production.getRhs()
                    .stream()
                    .filter(NonTerminal.class::isInstance)
                    .map(NonTerminal.class::cast)
                    .collect(Collectors.toList());

            for(NonTerminal rhsNonTerminal : rhsNonTerminals) {
                if(candidates.contains(rhsNonTerminal) && !rhsNonTerminal.equals(candidate)) {
                    candidates.remove(rhsNonTerminal);
                }
            }
        }

        return candidates.stream().reduce((a, b) -> {
            throw new IllegalStateException("Multiple start token candidates: " + a + ", " + b);
        }).orElseThrow(() -> new IncorrectGrammarException("Start token not detected"));
    }

    @Override
    public String toString() {
        return "G(" + name + ") = (" + terminals + ", " + nonTerminals + ", " + productions + ", " + start + ")";
    }
}
