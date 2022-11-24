package com.avispa.parser.precedence.function;

import com.avispa.parser.precedence.grammar.GenericToken;
import com.avispa.parser.precedence.table.Precedence;
import com.avispa.parser.precedence.table.PrecedenceTable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@Getter
public class PrecedenceFunctions implements IPrecedenceFunctions {
    private enum Function {
        F,
        G
    }

    private final Map<GenericToken, Integer> f = new HashMap<>();
    private final Map<GenericToken, Integer> g = new HashMap<>();

    public PrecedenceFunctions(PrecedenceTable table) throws PrecedenceFunctionsException {
        var graph = createGraph(table);

        var cycleDetector = new CycleDetector<>(graph);
        if(cycleDetector.detectCycles()) {
            throw new PrecedenceFunctionsException("Cycle detected. Precedence functions cannot be constructed");
        }

        var allDirectedPaths = new AllDirectedPaths<>(graph);
        var paths = allDirectedPaths.getAllPaths(graph.vertexSet(), graph.vertexSet(), true, null);

        // TODO: fuse equals

        for(var vertex : graph.vertexSet()) {
            int longestPath = paths.stream()
                    .filter(path -> path.getStartVertex().equals(vertex))
                    .map(GraphPath::getLength)
                    .max(Integer::compareTo)
                    .orElseThrow(() -> new PrecedenceFunctionsException("Can't"));

            if(vertex.getLeft().equals(Function.F)) {
                f.put(vertex.getRight(), longestPath);
            } else {
                g.put(vertex.getRight(), longestPath);
            }
        }

        log.info("f: " + f);
        log.info("g: " +  g);
    }

    private Graph<Pair<Function, GenericToken>, DefaultEdge> createGraph(PrecedenceTable table) {
        Graph<Pair<Function, GenericToken>, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        // add vertices
        table.getTokens().forEach(token -> {
            graph.addVertex(Pair.of(Function.F, token));
            graph.addVertex(Pair.of(Function.G, token));
        });

        // add edges
        table.get().forEach((key, value) -> {
            if(Precedence.LESS_THAN.equals(value)) {
                graph.addEdge(Pair.of(Function.G, key.getRight()), Pair.of(Function.F, key.getLeft()));
            } else if(Precedence.GREATER_THAN.equals(value)) {
                graph.addEdge(Pair.of(Function.F, key.getLeft()), Pair.of(Function.G, key.getRight()));
            }
        });

        if(log.isDebugEnabled()) {
            log.debug("Created graph: {}", graph);
        }
        return graph;
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
