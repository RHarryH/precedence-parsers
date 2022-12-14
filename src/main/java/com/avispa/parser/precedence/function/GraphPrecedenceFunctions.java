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
import com.avispa.parser.precedence.table.Precedence;
import com.avispa.parser.precedence.table.PrecedenceTable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of precedence functions using directed acyclic graph.
 * @author Rafał Hiszpański
 */
@Slf4j
@Getter
public final class GraphPrecedenceFunctions implements PrecedenceFunctions {
    private final Map<Symbol, Integer> f = new HashMap<>();
    private final Map<Symbol, Integer> g = new HashMap<>();

    public GraphPrecedenceFunctions(PrecedenceTable table) throws PrecedenceFunctionsException {
        var graph = createGraph(table);

        for (var node : graph.vertexSet()) { // for each node
            Integer longestPath = getLongestPathFor(node, graph);

            node.getFSet().forEach(symbol -> f.put(symbol, longestPath));
            node.getGSet().forEach(symbol -> g.put(symbol, longestPath));
        }

        if(log.isDebugEnabled()) {
            log.debug("Precedence functions generated.");
            log.debug("f()={}", f);
            log.debug("g()={}", g);
        }
    }

    /**
     * Creates graph from the precedence table according to the rules of its creation.
     * @param table precedence table
     * @return
     * @throws PrecedenceFunctionsException
     */
    private DirectedAcyclicGraph<GraphNode, DefaultEdge> createGraph(PrecedenceTable table) throws PrecedenceFunctionsException {
        DirectedAcyclicGraph<GraphNode, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge.class);

        var nodes = generateNodes(table);

        // add vertices
        nodes.forEach(graph::addVertex);

        // add edges
        try {
            table.get().forEach((key, value) -> {
                var leftNode = nodes.stream().filter(node -> node.containsF(key.getLeft())).findFirst().orElseThrow();
                var rightNode = nodes.stream().filter(node -> node.containsG(key.getRight())).findFirst().orElseThrow();

                log.debug("Processing {} pair with {} precedence", key, value);
                if (Precedence.LESS_THAN.equals(value)) {
                    log.debug("Adding edge from {} to {}", rightNode, leftNode);
                    graph.addEdge(rightNode, leftNode);
                } else if (Precedence.GREATER_THAN.equals(value)) {
                    log.debug("Adding edge from {} to {}", leftNode, rightNode);
                    graph.addEdge(leftNode, rightNode);
                }
            });
        } catch (IllegalArgumentException e) {
            throw new PrecedenceFunctionsException("Cycle detected. Precedence functions cannot be constructed", e);
        }

        if(log.isDebugEnabled()) {
            log.debug("Created graph: {}", graph);

            log.debug("Topological order");
            for (GraphNode graphNode : graph) {
                log.debug("{}", graphNode);
            }
        }
        return graph;
    }

    /**
     * Generates set of nodes. If the precedence for two symbols is set to equals, symbols are merged into single node.
     * @param table precedence table
     * @return
     */
    private Set<GraphNode> generateNodes(PrecedenceTable table) {
        Set<GraphNode> nodes = new HashSet<>();

        for(var entry : table.get().entrySet()) {
            var left = entry.getKey().getLeft();
            var right = entry.getKey().getRight();
            if(Precedence.EQUALS == entry.getValue()) { // fuse symbols if precedence is set to equal
                fuseSymbols(left, right, nodes);
            } else {
                if(nodes.stream().noneMatch(node -> node.containsF(left))) {
                    nodes.add(GraphNode.ofF(left));
                }

                if(nodes.stream().noneMatch(node -> node.containsG(right))) {
                    nodes.add(GraphNode.ofG(right));
                }
            }
        }

        nodes = new HashSet<>(nodes);

        log.debug("Generated nodes: {}", nodes);

        return nodes;
    }

    /**
     * If left or right symbol is present in any of existing nodes, append second one to it. If nothing was fused,
     * create new node.
     * @param left
     * @param right
     * @param nodes
     */
    private void fuseSymbols(Symbol left, Symbol right, Set<GraphNode> nodes) {
        boolean fused = false;
        for (GraphNode node : nodes) {
            if (node.containsF(left)) {
                node.addG(right);
                fused = true;
            } else if (node.containsG(right)) {
                node.addF(left);
                fused = true;
            }
        }

        if(!fused) {
            nodes.add(GraphNode.ofF(left).addG(right));
        }
    }

    /**
     * Finds the longest path for specific node. In fact the algorithm finds the longest paths to all nodes, but it is
     * later reduced to max value.
     *
     * Algorithm uses topological order provided by JGraphT library when building the graph. It iterates all nodes in that
     * order, finds all adjacent nodes (edges direction matter) and compares each node with adjacent node distances.
     * @param from we'll get the longest path from this node
     * @param graph graph where the search will be performed
     * @return
     * @throws PrecedenceFunctionsException
     */
    private Integer getLongestPathFor(GraphNode from, DirectedAcyclicGraph<GraphNode, DefaultEdge> graph) throws PrecedenceFunctionsException {
        Map<GraphNode, Integer> distances = new HashMap<>();
        distances.put(from, 0); // distance to self is always zero

        for (var currentNode : graph) { // nodes are in topological order
            if (distances.containsKey(currentNode)) {
                var edges = graph.outgoingEdgesOf(currentNode);
                for (DefaultEdge edge : edges) { // for each adjacent node
                    GraphNode adjacent = graph.getEdgeTarget(edge);

                    if (!distances.containsKey(adjacent) || distances.get(adjacent) < distances.get(currentNode) + 1) {
                        distances.put(adjacent, distances.get(currentNode) + 1);
                    }
                }
            }
        }

        log.debug("Found following distances from {} node: {}", from, distances);

        return findLongestPath(distances);
    }

    /**
     * Finds the longest path out of all longest paths for specific node
     * @param distances map of distances from each node
     * @return
     * @throws PrecedenceFunctionsException
     */
    private Integer findLongestPath(Map<GraphNode, Integer> distances) throws PrecedenceFunctionsException {
        return distances.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new PrecedenceFunctionsException("Longest path can't be found"))
                .getValue();
    }

    @Override
    public int getFFor(Symbol symbol) {
        return f.get(symbol);
    }

    @Override
    public int getGFor(Symbol symbol) {
        return g.get(symbol);
    }

    @Override
    public String toString() {
        return "f()=" + f + ", g()={}" + g;
    }
}
