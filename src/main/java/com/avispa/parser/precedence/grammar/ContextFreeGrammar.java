package com.avispa.parser.precedence.grammar;

import lombok.Getter;

import java.util.ArrayList;
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

    protected final Set<Terminal> terminals;
    protected final Set<NonTerminal> nonTerminals;
    protected final List<Production> productions;
    protected NonTerminal start;

    public static ContextFreeGrammar from(String name, Set<Terminal> terminals, List<Production> productions, NonTerminal start) throws IncorrectGrammarException {
        return new ContextFreeGrammar(name, terminals, productions, start);
    }

    public static ContextFreeGrammar from(GrammarFile grammarFile, NonTerminal start) throws IncorrectGrammarException {
        return new ContextFreeGrammar(grammarFile, start);
    }

    public static ContextFreeGrammar fromWithBoundaryMarker(GrammarFile grammarFile, NonTerminal start) throws IncorrectGrammarException {
        Set<Terminal> terminals = grammarFile.getTerminals();
        terminals.add(Terminal.BOUNDARY_MARKER);

        List<Production> productions = grammarFile.getProductions();
        productions.add(0, Production.of(NonTerminal.START, List.of(Terminal.BOUNDARY_MARKER, start, Terminal.BOUNDARY_MARKER)));

        return new ContextFreeGrammar(grammarFile.getName(), terminals, productions, NonTerminal.START);
    }

    private ContextFreeGrammar(GrammarFile grammarFile, NonTerminal start) throws IncorrectGrammarException {
        this(grammarFile.getName(), grammarFile.getTerminals(), grammarFile.getProductions(), start);
    }

    private ContextFreeGrammar(String name, Set<Terminal> terminals, List<Production> productions, NonTerminal start) throws IncorrectGrammarException {
        this.name = name;

        this.terminals = new HashSet<>(terminals);
        this.productions = new ArrayList<>(productions);

        verifyTerminalsMatch(productions);

        this.nonTerminals = buildNonTerminalsList(productions);
        this.start = getStartSymbol(start);

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

        return lhsNonTerminals;
    }

    /**
     * Checks if symbol is present on the non-terminals list and sets it as start symbol
     * @param start
     * @return
     */
    private NonTerminal getStartSymbol(NonTerminal start) throws IncorrectGrammarException {
        if(this.nonTerminals.contains(start)) {
            return start;
        } else {
            throw new IncorrectGrammarException("Start symbol is not defined on the non-terminals list");
        }
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

    /**
     * Get production by its index on the list
     * @param index
     * @return
     */
    public Production getProduction(int index) {
        return this.productions.get(index);
    }

    @Override
    public String toString() {
        final String newLine = System.lineSeparator();

        StringBuilder sb = new StringBuilder("G(");
        sb.append(name).append(") = (N, Σ, P, S)").append(newLine).append(newLine);

        // terminals
        sb.append("Σ = {").append(symbolsToString(terminals)).append("}").append(newLine);
        // non-terminals
        sb.append("N = {").append(symbolsToString(nonTerminals)).append("}").append(newLine);
        // productions
        sb.append("P = {").append(newLine);

        for(int i = 0; i < productions.size(); i++) {
            sb.append(i).append(": ").append(productions.get(i)).append(newLine);
        }

        sb.append("}").append(newLine);
        // start
        sb.append("S = {").append(start).append("}");

        return sb.toString();
    }

    private <T extends Symbol> String symbolsToString(final Set<T> symbols) {
        return symbols.stream().map(Symbol::toString).collect(Collectors.joining(", "));
    }
}
