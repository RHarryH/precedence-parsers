package com.avispa.parser.precedence.lexer;

import com.avispa.parser.precedence.grammar.Terminal;
import com.avispa.parser.token.Token;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * @author Rafał Hiszpański
 */
@EqualsAndHashCode
@AllArgsConstructor
public class Lexeme implements Token {
    private String value;
    private Terminal terminal;

    public static Lexeme of(String value, Terminal terminal) {
        return new Lexeme(value, terminal);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Value: " + value + ", terminal: " + terminal;
    }
}
