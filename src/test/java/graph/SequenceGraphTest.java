package graph;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jip on 16-5-2017.
 */
public class SequenceGraphTest {

    SequenceGraph graph;
    SequenceNode node1, node2, node3, node4;
    Edge edge1, edge2, edge3, edge4;
    HashMap<Integer, SequenceNode> hash;
    ArrayList<Edge> edges;

    @Before
    public void setUp() throws Exception {
        node1 = new SequenceNode(1);
        node2 = new SequenceNode(2);
        node3 = new SequenceNode(3);
        node4 = new SequenceNode(4);

        edge1 = new Edge(1, 2);
        edge2 = new Edge(2, 3);
        edge3 = new Edge(2, 4);
        edge4 = new Edge(3, 4);

        hash = new HashMap<Integer, SequenceNode>();

        edges = new ArrayList<Edge>();

        graph = new SequenceGraph();
    }

    public void startLargeGraph() {
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);

        hash.put(1, node1);
        hash.put(2, node2);
        hash.put(3, node3);
        hash.put(4, node4);

//        graph.setEdges(edges);
        graph.setNodes(hash);
    }

    @Test
    public void constructorTest() {
        assertEquals(graph.getNodes(), new HashMap<Integer, SequenceNode>());
//        assertEquals(graph.getEdges(), new ArrayList<Edge>());
    }

    @Test
    public void getNode() throws Exception {
        graph.addNode(node1);
        graph.addNode(node2);

        assertEquals(graph.getNode(1), node1);
    }


    @Test
    public void sizeTest() throws Exception {
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        assertEquals(4, graph.size());
    }
}
