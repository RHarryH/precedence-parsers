package com.avispa.parser.precedence.grammar;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class Terminal extends GenericToken {
    @EqualsAndHashCode.Exclude
    private final Pattern pattern;

    public static Terminal of(String name, String regex) {
        return new Terminal(name, regex);
    }

    private Terminal(String name, String regex) {
        super(name);
        try {
            this.pattern = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            String message = String.format("Provided pattern is not a valid regular expression: %s", e.getMessage());
            if(log.isDebugEnabled()) {
                log.error(message);
                log.error("Original exception: ", e);
            }
            throw new IllegalStateException(message);
        }
    }

    public boolean is(String value) {
        return pattern.matcher(value).matches();
    }
}
