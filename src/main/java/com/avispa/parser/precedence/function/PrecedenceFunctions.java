/*
 * MIT License
 *
 * Copyright (c) 2022 Rafał Hiszpański
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.avispa.parser.precedence.function;

import com.avispa.parser.precedence.grammar.Symbol;

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
    Map<Symbol, Integer> getF();
    Map<Symbol, Integer> getG();

    int getFFor(Symbol symbol);
    int getGFor(Symbol symbol);
}
