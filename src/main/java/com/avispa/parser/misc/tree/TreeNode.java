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
     * Returns true if node have child with specific value, which is not a leaf
     * @param value
     * @return
     */
    public boolean hasNonLeafChildOf(T value) {
        return children.stream().anyMatch(child -> !child.isLeaf() && child.getValue().equals(value));
    }

    @Override
    public String toString() {
        return null != value ? value.toString() : "<root>";
    }
}