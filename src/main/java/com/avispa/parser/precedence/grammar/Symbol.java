package com.avispa.parser.precedence.grammar;

import com.avispa.parser.precedence.lexer.Lexeme;
import lombok.EqualsAndHashCode;

/**
 * @author Rafał Hiszpański
 */
@EqualsAndHashCode
public abstract class Symbol {
    protected String name;

    protected Symbol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * If symbol is a lexeme, unwrap it to get actual terminal symbol
     * @return
     */
    public Symbol unwrap() {
        if(this instanceof Lexeme) {
            return ((Lexeme) this).getTerminal();
        }
        return this;
    }

    @Override
    public String toString() {
        return name;
    }
}
