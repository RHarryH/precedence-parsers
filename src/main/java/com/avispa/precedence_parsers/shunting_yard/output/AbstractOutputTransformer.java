package com.avispa.precedence_parsers.shunting_yard.output;

import com.avispa.precedence_parsers.shunting_yard.IParser;
import com.avispa.precedence_parsers.shunting_yard.ShuntingYard;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter(AccessLevel.PROTECTED)
public abstract class AbstractOutputTransformer<T> implements IOutputTransformer<T> {
    private final IParser parser;

    protected AbstractOutputTransformer() {
        this(new ShuntingYard());
    }

    protected AbstractOutputTransformer(IParser parser) {
        this.parser = parser;
    }
}
