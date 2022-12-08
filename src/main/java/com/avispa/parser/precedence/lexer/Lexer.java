package com.avispa.parser.precedence.lexer;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class Lexer {
    private String input;
    private final Set<Terminal> terminals;

    private final Map<Terminal, Integer> occurrenceCounterMap = new HashMap<>();
    private Lexeme lastLexeme = null;

    public Lexer(String input, ContextFreeGrammar grammar) {
        this.input = input;
        this.terminals = grammar.getTerminals();
    }

    /**
     * Returns next lexeme without moving the pointer. Calling this method multiple times will return the same lexeme.
     * Found value is stored and reused if getNext will be invoked.
     *
     * @return lexer token if it matches any of terminals regex
     * @throws LexerException when nothing was found
     */
    public Lexeme peekNext() throws LexerException {
        Lexeme lexeme;
        if(lastLexeme == null) {
            lexeme = next();
            lastLexeme = lexeme; // store value so when getNext will be called, already found value will be used
        } else {
            lexeme = lastLexeme;
        }

        return lexeme;
    }

    /**
     * Returns next lexeme and moves the pointer. If peekNext was called before, already found lexeme will be reused.
     * @return
     * @throws LexerException
     */
    public Lexeme getNext() throws LexerException {
        Lexeme lexeme;
        if(lastLexeme != null) {
            lexeme = lastLexeme;
            lastLexeme = null;
        } else {
            lexeme = next();
        }

        input = input.substring(lexeme.getValueLength());

        return lexeme;
    }

    /**
     * When multiple terminals matches the value, the longest match is used. If matches length
     * is the same, the behavior is undefined.
     *
     * @return
     * @throws LexerException
     */
    private Lexeme next() throws LexerException {
        return terminals.stream()
                .map(terminal -> Pair.of(terminal, terminal.lastMatchedIndex(input))) // get terminal with its match length
                .filter(match -> match.getRight() > 0) // filter results without match
                .max(Comparator.comparingInt(Pair::getRight)) // get max result
                .map(match -> Lexeme.of(input.substring(0, match.getRight()), match.getLeft(), getIndex(match.getLeft()))) // map result to lexeme
                .orElseThrow(() -> new LexerException(input)); // missing terminal matching input
    }

    /**
     * In case more than one lexeme is matched by the same terminal, calculate additional index to distinguish
     * them from each other.
     * @param terminal
     */
    private int getIndex(Terminal terminal) {
        // get value for terminal or initialize with 1, when value exists one is added
        return occurrenceCounterMap.merge(terminal, 1, Integer::sum);
    }

    public boolean hasCharactersLeft() {
        return !input.isEmpty();
    }
}
