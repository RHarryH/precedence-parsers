package com.avispa.parser.precedence.grammar;

import com.avispa.parser.token.Token;
import lombok.EqualsAndHashCode;

/**
 * @author Rafał Hiszpański
 */
@EqualsAndHashCode
public abstract class GenericToken implements Token {
    private final String name;

    GenericToken(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
