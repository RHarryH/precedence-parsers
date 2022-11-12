package com.avispa.parser.util.tree;

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

        sb.append(tree.getValue()).append(lineSeparator);

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
