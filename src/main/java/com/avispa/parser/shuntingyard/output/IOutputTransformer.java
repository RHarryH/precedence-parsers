package com.avispa.parser.shuntingyard.output;

/**
 * @author Rafał Hiszpański
 */
public interface IOutputTransformer<T> {
    T parse(String expression);
}
