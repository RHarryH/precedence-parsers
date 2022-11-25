package com.avispa.parser.precedence.function;

import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.table.Precedence;
import com.avispa.parser.precedence.table.PrecedenceTable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@Getter
public class PrecedenceFunctions implements IPrecedenceFunctions {

    private final Map<GenericToken, Integer> f = new HashMap<>();
    private final Map<GenericToken, Integer> g = new HashMap<>();

    public PrecedenceFunctions(PrecedenceTable table) throws PrecedenceFunctionsException {
        var graph = createGraph(table);

        for (var node : graph.vertexSet()) { // for each node
            Integer longestPath = getLongestPathFor(node, graph);

            if (node.isFNode()) {
                node.getTokens().forEach(token -> f.put(token, longestPath));
            } else {
                node.getTokens().forEach(token -> g.put(token, longestPath));
            }
        }

        log.debug("f()={}", f);
        log.debug("g()={}", g);
    }

    /**
     * Creates graph from the precedence table according to the rules of its creation.
     * @param table precedence table
     * @return
     * @throws PrecedenceFunctionsException
     */
    private DirectedAcyclicGraph<GraphNode, DefaultEdge> createGraph(PrecedenceTable table) throws PrecedenceFunctionsException {
        DirectedAcyclicGraph<GraphNode, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge.class);

        var fusedTokensMap = fuseTokens(table);

        // add vertices
        fusedTokensMap.values().forEach(fusedTokens -> {
            graph.addVertex(GraphNode.ofF(fusedTokens));
            graph.addVertex(GraphNode.ofG(fusedTokens));
        });

        // add edges
        try {
            table.get().forEach((key, value) -> {
                if (Precedence.LESS_THAN.equals(value)) {
                    graph.addEdge(GraphNode.ofG(fusedTokensMap.get(key.getRight())), GraphNode.ofF(fusedTokensMap.get(key.getLeft())));
                } else if (Precedence.GREATER_THAN.equals(value)) {
                    graph.addEdge(GraphNode.ofF(fusedTokensMap.get(key.getLeft())), GraphNode.ofG(fusedTokensMap.get(key.getRight())));
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
     * Builds map of fused tokens. When for two tokens the precedence is set to equal then they should
     * be fused into single token represented by list of tokens.
     * @param table precedence table
     * @return
     */
    private Map<GenericToken, List<GenericToken>> fuseTokens(PrecedenceTable table) {
        Map<GenericToken, List<GenericToken>> fusedTokens = new HashMap<>();

        for(var entry : table.get().entrySet()) {
            var pair = entry.getKey();
            if(Precedence.EQUALS == entry.getValue()) { // fuse tokens if precedence is set to equal
                List<GenericToken> fusedToken = List.of(pair.getLeft(), pair.getRight());
                fusedTokens.put(pair.getLeft(), fusedToken);
                fusedTokens.put(pair.getRight(), fusedToken);
            } else {
                fusedTokens.putIfAbsent(pair.getLeft(), List.of(pair.getLeft()));
                fusedTokens.putIfAbsent(pair.getRight(), List.of(pair.getRight()));
            }
        }

        log.debug("Fused tokens map: {}", fusedTokens);

        return fusedTokens;
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
    public int getFFor(GenericToken token) {
        return f.get(token);
    }

    @Override
    public int getGFor(GenericToken token) {
        return g.get(token);
    }
}
