package com.avispa.parser.misc.tree;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Optional<TreeNode<T>> getChild(T value) {
        for(var child : children) {
            if(child.getValue().equals(value)) {
                return Optional.of(child);
            }
        }

        return Optional.empty();
    }

    /**
     * Adds child as the first node on children list
     * @param child
     */
    public void addFirstChild(TreeNode<T> child) {
        children.add(0, child);
    }

    /**
     * Returns direct leaf of current node
     * @return
     */
    public Optional<TreeNode<T>> findClosestLeaf() {
        for(TreeNode<T> child : children) {
            if(child.isLeaf()) {
                return Optional.of(child);
            }
        }

        return Optional.empty();
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public String toString() {
        return null != value ? value.toString() : "<root>";
    }
}