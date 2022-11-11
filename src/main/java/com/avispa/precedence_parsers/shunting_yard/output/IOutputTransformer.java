package com.avispa.precedence_parsers.shunting_yard.output;

/**
 * @author Rafał Hiszpański
 */
public interface IOutputTransformer<T> {
    T parse(String expression);
}
