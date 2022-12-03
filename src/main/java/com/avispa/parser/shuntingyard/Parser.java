package com.avispa.parser.shuntingyard;

import com.avispa.parser.token.Token;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
public interface Parser {
    List<Token> parse(String expression);
}
