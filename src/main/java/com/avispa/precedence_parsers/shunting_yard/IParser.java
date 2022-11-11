package com.avispa.precedence_parsers.shunting_yard;

import com.avispa.precedence_parsers.shunting_yard.token.Token;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
public interface IParser {
    List<Token> parse(String expression);
}
