package com.avispa.parser.precedence.table;

/**
 * @author Rafał Hiszpański
 */
public class UnresolvablePrecedenceConflictException extends RuntimeException {
    public UnresolvablePrecedenceConflictException(String message) {
        super(message);
    }
}
