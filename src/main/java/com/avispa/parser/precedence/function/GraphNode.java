package com.avispa.parser.precedence.function;

import com.avispa.parser.precedence.grammar.GenericToken;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@AllArgsConstructor
@EqualsAndHashCode
class GraphNode {
    private enum Function {
        F,
        G
    }

    private Function function;
    @Getter
    private List<GenericToken> tokens;

    public static GraphNode ofF(List<GenericToken> tokens) {
        return new GraphNode(Function.F, tokens);
    }

    public static GraphNode ofG(List<GenericToken> tokens) {
        return new GraphNode(Function.G, tokens);
    }

    public boolean isFNode() {
        return function == Function.F;
    }

    public boolean isGNode() {
        return function == Function.G;
    }

    @Override
    public String toString() {
        return function + "_" + tokens;
    }
}
