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
    private final Terminal terminal;

    public static Lexeme of(String value, Terminal terminal) {
        return new Lexeme(value, terminal);
    }

    private Lexeme(String value, Terminal terminal) {
        super("");
        this.value = value;
        this.terminal = terminal;
    }

    public void setIndex(int index) {
        setName(Integer.toString(index));
    }

    public int getValueLength() {
        return value.length();
    }

    @Override
    public String toString() {
        String name = getName();
        String index = name.isEmpty() ? "" : "_" + name;

        return terminal + index + ": " + value;
    }
}