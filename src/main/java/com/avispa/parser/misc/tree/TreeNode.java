package com.avispa.parser.misc.tree;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Getter
@EqualsAndHashCode
public class TreeNode<T> {
    private final T value;
    private final List<TreeNode<T>> children = new ArrayList<>();

    public TreeNode(T value) {
        this.value = value;
    }

    public void addChild(TreeNode<T> child) {
        children.add(child);
    }

    public TreeNode<T> getChild(T value) {
        for(var child : children) {
            if(child.getValue().equals(value)) {
                return child;
            }
        }

        return null;
    }

    /**
     * Adds child as the first node on children list
     * @param child
     */
    public void addFirstChild(TreeNode<T> child) {
        children.add(0, child);
    }
}