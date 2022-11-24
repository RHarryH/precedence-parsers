package com.avispa.parser.precedence.function;

import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.table.Precedence;
import com.avispa.parser.precedence.table.PrecedenceTable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@Getter
public class PrecedenceFunctions implements IPrecedenceFunctions {
    @AllArgsConstructor
    @EqualsAndHashCode
    private static class Node {
        private enum Function {
            F,
            G
        }

        private Function function;
        @Getter
        private GenericToken token;

        public static Node ofF(GenericToken token) {
            return new Node(Function.F, token);
        }

        public static Node ofG(GenericToken token) {
            return new Node(Function.G, token);
        }

        public boolean isFNode() {
            return function == Function.F;
        }

        public boolean isGNode() {
            return function == Function.G;
        }

        @Override
        public String toString() {
            return function + "_" + token;
        }
    }

    private final Map<GenericToken, Integer> f = new HashMap<>();
    private final Map<GenericToken, Integer> g = new HashMap<>();

    public PrecedenceFunctions(PrecedenceTable table) throws PrecedenceFunctionsException {
        var graph = createGraph(table);

        for (var node : graph.vertexSet()) { // for each node
            Integer longestPath = getLongestPathFor(node, graph);

            if (node.isFNode()) {
                f.put(node.getToken(), longestPath);
            } else {
                g.put(node.getToken(), longestPath);
            }
        }

        log.info("f()={}", f);
        log.info("g()={}", g);
    }

    /**
     * Creates graph from the precedence table according to the rules of its creation
     * @param table
     * @return
     * @throws PrecedenceFunctionsException
     */
    private DirectedAcyclicGraph<Node, DefaultEdge> createGraph(PrecedenceTable table) throws PrecedenceFunctionsException {
        DirectedAcyclicGraph<Node, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge.class);

        // TODO: fuse equals

        // add vertices
        table.getTokens().forEach(token -> {
            graph.addVertex(Node.ofF(token));
            graph.addVertex(Node.ofG(token));
        });

        // add edges
        try {
            table.get().forEach((key, value) -> {
                if (Precedence.LESS_THAN.equals(value)) {
                    graph.addEdge(Node.ofG(key.getRight()), Node.ofF(key.getLeft()));
                } else if (Precedence.GREATER_THAN.equals(value)) {
                    graph.addEdge(Node.ofF(key.getLeft()), Node.ofG(key.getRight()));
                }
            });
        } catch (IllegalArgumentException e) {
            throw new PrecedenceFunctionsException("Cycle detected. Precedence functions cannot be constructed", e);
        }

        if(log.isDebugEnabled()) {
            log.debug("Created graph: {}", graph);

            log.debug("Topological order");
            for (Node node : graph) {
                log.debug("{}", node);
            }
        }
        return graph;
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
    private Integer getLongestPathFor(Node from, DirectedAcyclicGraph<Node, DefaultEdge> graph) throws PrecedenceFunctionsException {
        Map<Node, Integer> distances = new HashMap<>();
        distances.put(from, 0); // distance to self is always zero

        for (var currentNode : graph) { // nodes are in topological order
            if (distances.containsKey(currentNode)) {
                var edges = graph.outgoingEdgesOf(currentNode);
                for (DefaultEdge edge : edges) { // for each neighbor
                    Node adjacent = graph.getEdgeTarget(edge);

                    if (!distances.containsKey(adjacent) || distances.get(adjacent) < distances.get(currentNode) + 1) {
                        distances.put(adjacent, distances.get(currentNode) + 1);
                    }
                }
            }
        }

        if(log.isDebugEnabled()) {
            log.debug("Found following distances from {} node: {}", from, distances);
        }

        return findLongestPath(distances);
    }

    /**
     * Finds the longest path out of all longest paths for specific node
     * @param distances map of distances from each node
     * @return
     * @throws PrecedenceFunctionsException
     */
    private Integer findLongestPath(Map<Node, Integer> distances) throws PrecedenceFunctionsException {
        return distances.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new PrecedenceFunctionsException("Longest path can't be found"))
                .getValue();
    }

    @Override
    public int getFFor(GenericToken token) {
        return f.get(token);
    }

    @Override
    public int getGFor(GenericToken token) {
        return g.get(token);
    }
}
