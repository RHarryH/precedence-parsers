package com.avispa.parser.precedence.parser;

/**
 * @author Rafał Hiszpański
 */
public class SyntaxException extends Exception {
    public SyntaxException(String value) {
        super("Syntax error at the vicinity of: " + value);
    }
}
