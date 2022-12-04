package com.avispa.parser.precedence.lexer;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Rafał Hiszpański
 */
public class LexerException extends Exception {
    public LexerException(String input) {
        super("Unknown lexeme at the vicinity of: " + StringUtils.abbreviate(input, "...", 20));
    }
}
