package com.avispa.parser;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.precedence.parser.SyntaxException;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
public interface Parser<O> {
    List<O> parse(String input) throws LexerException, SyntaxException;
}
