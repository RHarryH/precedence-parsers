package com.avispa.parser.precedence.lexer;

import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public final class Lexeme extends Symbol {
    private final String value;
    private final int index;
    private final Terminal terminal;

    public static Lexeme of(String value, Terminal terminal, int index) {
        if(index < 1) {
            throw new IllegalArgumentException("Index must be greater than 0");
        }
        return new Lexeme(value, terminal, index);
    }

    private Lexeme(String value, Terminal terminal, int index) {
        super(terminal.getName());
        this.index = index;
        this.value = value;
        this.terminal = terminal;
    }

    public int getValueLength() {
        return value.length();
    }

    @Override
    public String toString() {
        return super.toString() + "_" + index + ":" + value;
    }
}
