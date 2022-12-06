package com.avispa.parser.precedence.lexer;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.extern.slf4j.Slf4j;

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

    private Map<Terminal, Integer> occurence = new HashMap<>();
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
        Lexeme longestMatchLexeme = null;
        for(Terminal terminal : terminals) {
            int matchLength = terminal.lastMatchedIndex(input);
            if(matchLength > 0) {
                String value = input.substring(0, matchLength);

                Lexeme lexeme = Lexeme.of(value, terminal);
                if (longestMatchLexeme == null || matchLength > longestMatchLexeme.getValueLength()) {
                    // new longest match
                    longestMatchLexeme = lexeme;
                }
            }
        }

        if(null == longestMatchLexeme) {
            throw new LexerException(input);
        }

        setLexemeIndex(longestMatchLexeme);

        return longestMatchLexeme;
    }

    /**
     * In case more than one lexeme is matched by the same terminal, set up additional index to distinguish them from
     * each other.
     * @param lexeme
     */
    private void setLexemeIndex(Lexeme lexeme) {
        Terminal terminal = lexeme.getTerminal();

        // get value for terminal or initialize with 1, when value exists one is added
        int number = occurence.merge(terminal, 1, Integer::sum);

        lexeme.setIndex(number);
    }

    public boolean hasCharactersLeft() {
        return !input.isEmpty();
    }
}
