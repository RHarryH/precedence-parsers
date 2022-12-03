package com.avispa.parser.precedence.function;

import com.avispa.parser.precedence.grammar.GenericToken;

import java.util.Map;

/**
 * Precedence functions are useful for optimizing precedence tables size. In fact this optimization sacrifices error
 * detection capabilities by ignoring cases which are not allowed by the grammar.
 *
 * The entries in a precedence table have four values: ⋖, ≐ , ⋗ and blank. Since precedence functions can only represent
 * three relations: <, = and >, the blank is sacrificed, to the detriment of error detection. A weak precedence table holds
 * only three kinds of entries: ⩿, ⋗ and blank, which can be mapped onto <, > and =. The resulting matrix will normally not
 * allow precedence functions, but it will if a number of the =’s are sacrificed. An algorithm is given to (heuristically)
 * determine the minimal set of =’s to sacrifice; unfortunately this is done by calling upon a heuristic algorithm for
 * partitioning graphs.
 * @author Rafał Hiszpański
 */
public interface PrecedenceFunctions {
    Map<GenericToken, Integer> getF();
    Map<GenericToken, Integer> getG();

    int getFFor(GenericToken token);
    int getGFor(GenericToken token);
}
