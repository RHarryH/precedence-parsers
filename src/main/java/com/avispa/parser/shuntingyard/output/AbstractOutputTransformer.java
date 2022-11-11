package com.avispa.parser.shuntingyard.output;

import com.avispa.parser.shuntingyard.IParser;
import com.avispa.parser.shuntingyard.ShuntingYard;
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
