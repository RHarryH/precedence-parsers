package com.avispa.parser.shuntingyard.output;

/**
 * @author Rafał Hiszpański
 */
public interface OutputTransformer<T> {
    T parse(String expression);
}
