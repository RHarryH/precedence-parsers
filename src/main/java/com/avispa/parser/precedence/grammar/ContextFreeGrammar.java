package com.avispa.parser.precedence.grammar;

import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
@Getter
public class ContextFreeGrammar implements Grammar {
    private final String name;

    private final Set<Terminal> terminals;
    private final Set<NonTerminal> nonTerminals;
    private final List<Production> productions;
    private NonTerminal start;

    public ContextFreeGrammar(String name, Set<Terminal> terminals, List<Production> productions) throws IncorrectGrammarException {
        this.name = name;

        this.terminals = new HashSet<>(terminals);
        this.productions = productions;

        verifyTerminalsMatch(productions);

        this.nonTerminals = buildNonTerminalsList(productions);

        if(this.terminals.isEmpty() || this.productions.isEmpty()) {
            throw new IncorrectGrammarException("Terminals and productions must be a non-empty collections");
        }
    }

    /**
     * Verifies if terminals defined in productions matches list of terminals
     * @param productions
     * @throws IncorrectGrammarException
     */
    private void verifyTerminalsMatch(List<Production> productions) throws IncorrectGrammarException {
        Set<Terminal> productionTerminals = productions.stream()
                .map(Production::getRhs)
                .flatMap(Collection::stream)
                .filter(Terminal.class::isInstance)
                .map(Terminal.class::cast)
                .collect(Collectors.toSet());

        if(!this.terminals.containsAll(productionTerminals)) {
            throw new IncorrectGrammarException("There are undefined terminals found in productions. Terminals: " + this.terminals + ", found: " + productionTerminals);
        }
    }

    /**
     * Scans lhs and rhs of productions. If there is a difference of only one symbol (lhs has one more symbol)
     * then this symbol is a start symbol.
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
            for(Symbol symbol : production.getRhs()) {
                if (NonTerminal.isOf(symbol)) {
                    if(!lhsNonTerminals.contains(symbol)) {
                        throw new IncorrectGrammarException("Symbol " + symbol + " is not present on the left-hand side of any production");
                    }
                    if(!rhsNonTerminals.contains(symbol)) {
                        rhsNonTerminals.add((NonTerminal) symbol);
                    }
                }
            }
        }

        this.start = findStartSymbol(lhsNonTerminals, productions);

        lhsNonTerminals.add(start); // add start symbol to lhs, this becomes the full set of non-terminals

        return lhsNonTerminals;
    }

    /**
     * Finds start symbol.
     *
     * First all non-terminals are candidates but when the non-terminal will be found on the productions right-hand
     * side then it is eliminated. Non-terminal is not eliminated when it is left-hand side of the production in single
     * production rule.
     * @param nonTerminals non-terminals set
     * @param productions productions list
     * @return
     * @throws IncorrectGrammarException
     */
    private NonTerminal findStartSymbol(Set<NonTerminal> nonTerminals, List<Production> productions) throws IncorrectGrammarException {
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
            throw new IllegalStateException("Multiple start symbol candidates: " + a + ", " + b);
        }).orElseThrow(() -> new IncorrectGrammarException("Start symbol not detected"));
    }

    /**
     * Add special terminal symbol not defined directly in grammar.
     * @param terminal
     */
    protected final void addTerminal(Terminal terminal) {
        this.terminals.add(terminal);
    }

    public Set<Terminal> getTerminals() {
        return Collections.unmodifiableSet(this.terminals);
    }

    public Set<NonTerminal> getNonTerminals() {
        return Collections.unmodifiableSet(this.nonTerminals);
    }

    public List<Production> getProductions() {
        return Collections.unmodifiableList(this.productions);
    }

    @Override
    public String toString() {
        return "G(" + name + ") = (" + terminals + ", " + nonTerminals + ", " + productions + ", " + start + ")";
    }
}
