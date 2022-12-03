package com.avispa.parser.shuntingyard.output;

import com.avispa.parser.shuntingyard.Parser;
import com.avispa.parser.shuntingyard.ShuntingYard;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter(AccessLevel.PROTECTED)
public abstract class AbstractOutputTransformer<T> implements OutputTransformer<T> {
    private final Parser parser;

    protected AbstractOutputTransformer() {
        this(new ShuntingYard());
    }

    protected AbstractOutputTransformer(Parser parser) {
        this.parser = parser;
    }
}
