package com.avispa.parser.output;

import com.avispa.parser.Parser;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter(AccessLevel.PROTECTED)
public abstract class AbstractOutputTransformer<T, O> implements OutputTransformer<T> {
    private final Parser<O> parser;

    protected AbstractOutputTransformer(Parser<O> parser) {
        this.parser = parser;
    }
}
