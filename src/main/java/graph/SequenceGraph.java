package graph;

import java.util.ArrayList;
import java.util.HashMap;

public class SequenceGraph   {


    private Integer size;
    private HashMap<Integer, SequenceNode> nodes;
    private boolean initialized;
    private ArrayList<Edge> edges;

    public SequenceGraph() {
        this.size = 0;
        this.nodes = new HashMap<Integer, SequenceNode>();
        this.initialized = false;
        this.edges = new ArrayList<Edge>();

    }

    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    public void addNode(SequenceNode node) {
        this.nodes.put(node.getId(), node);
        this.size++;
    }

    public SequenceNode getNode(Integer id) {
        return nodes.get(id);
    }

    public HashMap<Integer, SequenceNode> getNodes() {
        return this.nodes;
    }

    public void initialize() {

        for (Edge edge : getEdges()) {
            // aan elke parent de child toevoegen
            this.getNode(edge.getParent()).addChild(this.getNode(edge.getChild()));
        }
    }

    /**
     * Will add columns to all the nodes and to all the edges
     */
    public void layerizeGraph() {
            createColumns();
            createEdgeColumns();
    }

    /**
     * Gives each node a column where it should be built
     */
    public void createColumns() {
        for(int i = 1; i <= size; i++) {
            SequenceNode parent = nodes.get(i);     // Start at first node
            ArrayList<SequenceNode> children = parent.getChildren();    // Get all children
            for (SequenceNode child : children) {
                child.incrementColumn(parent.getColumn());      // Assign layer
            }
        }
    }

    /**
     * Gives each edge it's ghost nodes
     */
    public void createEdgeColumns() {

        for(int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            int parColumn = nodes.get(edge.getParent()).getColumn();
            int childColumn = nodes.get(edge.getChild()).getColumn();
            edge.setEntireColumnSpan(parColumn, childColumn);
        }
    }

}
