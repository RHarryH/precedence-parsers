package com.avispa.parser.output;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.precedence.parser.SyntaxException;

/**
 * @author Rafał Hiszpański
 */
public interface OutputTransformer<T> {
    T parse(String input) throws SyntaxException, LexerException;
}
