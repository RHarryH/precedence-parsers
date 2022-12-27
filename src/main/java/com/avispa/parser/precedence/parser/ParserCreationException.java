package com.avispa.parser.precedence.parser;

/**
 * @author Rafał Hiszpański
 */
public class ParserCreationException extends Exception {
    public ParserCreationException(String message) {
        super(message);
    }

    public ParserCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
