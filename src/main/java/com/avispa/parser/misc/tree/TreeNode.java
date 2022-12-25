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

    /**
     * Adds child as the first node on children list
     * @param child
     */
    public void addFirstChild(TreeNode<T> child) {
        children.add(0, child);
    }

    /**
     * Get direct child with exact value
     * @param value
     * @return
     */
    public Optional<TreeNode<T>> getChild(T value) {
        return children.stream()
                .filter(child -> child.getValue().equals(value))
                .findFirst();
    }

    /**
     * Returns first direct leaf of current node
     * @return
     */
    public Optional<TreeNode<T>> findClosestLeaf() {
        return children.stream()
                .filter(TreeNode::isLeaf)
                .findFirst();
    }

    /**
     * Checks if node does not have any children.
     * @return
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * Returns true if node have at least one child, which is not a leaf
     * @param value
     * @return
     */
    public boolean hasNonLeafChild(T value) {
        return children.stream().anyMatch(child -> !child.isLeaf() && child.getValue().equals(value));
    }

    @Override
    public String toString() {
        return null != value ? value.toString() : "<root>";
    }
}