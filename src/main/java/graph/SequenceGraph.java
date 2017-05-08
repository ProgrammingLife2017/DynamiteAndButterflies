package graph;

import parser.Edge;
import parser.SequenceNode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is the SequenceGraph.
 * A Graph handling a Directed-Acyclic-Graph.
 * This is our own data structure we will use to draw the eventual graph.
 */
public class SequenceGraph   {


    private Integer size;
    private HashMap<Integer, SequenceNode> nodes;
    private boolean initialized;
    private ArrayList<Edge> edges;

    /**
     * Initializes the SequenceGraph with it's basic values.
     */
    public SequenceGraph() {
        this.size = 0;
        this.nodes = new HashMap<Integer, SequenceNode>();
        this.initialized = false;
        this.edges = new ArrayList<Edge>();

    }

    /**
     * Returns all the edges contained in the graph.
     * @return a arrayList of Edges containing all the edges of the graph.
     */
    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    /**
     * Adds a node to the graph.
     * @param node a new node for the graph
     */
    public void addNode(SequenceNode node) {
        this.nodes.put(node.getId(), node);
        this.size++;
    }

    /**
     * Returns one of the nodes in the graph.
     * @param id An id specifying the node.
     * @return A node specified by its id.
     */
    public SequenceNode getNode(Integer id) {
        return nodes.get(id);
    }

    /**
     * Returns all nodes contained in the graph.
     * @return A HashMap of all nodes and their IDs contained in the graph.
     */
    public HashMap<Integer, SequenceNode> getNodes() {
        return this.nodes;
    }

    /**
     * Will initialize all the edges of the graph.
     */
    public void initialize() {
        for (Edge edge : getEdges()) {
            // aan elke parent de child toevoegen
            this.getNode(edge.getParent()).addChild(this.getNode(edge.getChild()));
        }
    }

    /**
     * Will add columns to all the nodes and to all the edges.
     */
    public void layerizeGraph() {
            createColumns();
            createEdgeColumns();
    }

    /**
     * Gives each node a column where it should be built.
     */
    public void createColumns() {
        for (int i = 1; i <= size; i++) {
            SequenceNode parent = nodes.get(i);     // Start at first node
            ArrayList<SequenceNode> children = parent.getChildren();    // Get all children
            for (SequenceNode child : children) {
                child.incrementColumn(parent.getColumn());      // Assign layer
            }
        }
    }

    /**
     * Gives each edge it's ghost nodes.
     */
    public void createEdgeColumns() {

        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            int parColumn = nodes.get(edge.getParent()).getColumn();
            int childColumn = nodes.get(edge.getChild()).getColumn();
            edge.setEntireColumnSpan(parColumn, childColumn);
        }
    }

}
