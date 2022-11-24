package com.avispa.parser.precedence.function;

/**
 * @author Rafał Hiszpański
 */
public class PrecedenceFunctionsException extends Exception {
    public PrecedenceFunctionsException(String message) {
        super(message);
    }

    public PrecedenceFunctionsException(String message, Throwable cause) {
        super(message, cause);
    }
}
