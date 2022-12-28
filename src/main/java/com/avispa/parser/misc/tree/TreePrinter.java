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

package com.avispa.parser.misc.tree;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@NoArgsConstructor(access = AccessLevel.NONE)
public class TreePrinter {
    public static final String PARENT_PIPE = "│   ";
    public static final String PARENT_EMPTY = "    ";

    public static final String CHILD_POINTER = "├── ";
    public static final String LAST_CHILD_POINTER = "└── ";

    public static <T> String print(TreeNode<T> tree) {
        if(null == tree) {
            return "";
        }
        return print(tree, new StringBuilder(), new ArrayList<>(), System.lineSeparator()).toString();
    }

    private static <T> StringBuilder print(TreeNode<T> tree, StringBuilder sb, List<Boolean> hasUnprocessedChildren, String lineSeparator) {
        List<TreeNode<T>> children = tree.getChildren();

        sb.append(tree).append(lineSeparator);

        for(int i = 0; i < children.size(); i++) {
            if(i == children.size() - 1) {
                addLastNode(sb, hasUnprocessedChildren);
                hasUnprocessedChildren.add(false);
            } else {
                addNode(sb, hasUnprocessedChildren);
                hasUnprocessedChildren.add(true);
            }

            sb = print(children.get(i), sb, hasUnprocessedChildren, lineSeparator);

            hasUnprocessedChildren.remove(hasUnprocessedChildren.size() - 1);
        }

        return sb;
    }

    private static void addNode(StringBuilder sb, List<Boolean> hasUnprocessedChildren) {
        for(boolean hasUnprocessedChild : hasUnprocessedChildren) {
            sb.append(hasUnprocessedChild ? PARENT_PIPE : PARENT_EMPTY);
        }
        sb.append(CHILD_POINTER);
    }

    private static void addLastNode(StringBuilder sb, List<Boolean> hasUnprocessedChildren) {
        for(boolean hasUnprocessedChild : hasUnprocessedChildren) {
            sb.append(hasUnprocessedChild ? PARENT_PIPE : PARENT_EMPTY);
        }
        sb.append(LAST_CHILD_POINTER);
    }
}
