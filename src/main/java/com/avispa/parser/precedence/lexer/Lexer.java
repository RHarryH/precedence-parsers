package com.avispa.parser.precedence.lexer;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class Lexer {
    private String input;
    private final Set<Terminal> terminals;

    public Lexer(String input, ContextFreeGrammar grammar) {
        this.input = input;
        this.terminals = grammar.getTerminals();
    }

    /**
     * When multiple terminals matches the value, the longest match is used. If matches length
     * is the same, the behavior is undefined.
     *
     * @return lexer token if it matches any of terminals regex
     * @throws LexerException when nothing was found
     */
    public Lexeme getNext() throws LexerException {
        Lexeme longestMatchToken = null;
        int maxMatchLength = Integer.MIN_VALUE;
        for(Terminal terminal : terminals) {
            int matchLength = terminal.lastMatchedChar(input);
            if(matchLength > 0) {
                String value = input.substring(0, matchLength);

                Lexeme token = Lexeme.of(value, terminal);
                if (matchLength > maxMatchLength) {
                    // new longest match
                    longestMatchToken = token;
                    maxMatchLength = matchLength;
                }
            }
        }

        if(null == longestMatchToken) {
            throw new LexerException(input);
        }

        input = input.substring(maxMatchLength);

        return longestMatchToken;
    }

    public boolean hasCharactersLeft() {
        return !input.isEmpty();
    }
}
