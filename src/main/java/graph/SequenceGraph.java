package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Our own Graph Class.
 */
public class SequenceGraph   {


    private Integer size;
    private HashMap<Integer, SequenceNode> nodes;
    private ArrayList<Edge> edges;

    /**
     * Constructor.
     */
    public SequenceGraph() {
        this.size = 0;
        this.nodes = new HashMap<Integer, SequenceNode>();
        this.edges = new ArrayList<Edge>();
    }

    /**
     * Get function for the ArrayList of Edges.
     * @return The Edges.
     */
    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    /**
     * Add a node to the ArrayList of Nodes.
     * @param node The node to be added.
     */
    public void addNode(SequenceNode node) {
        this.nodes.put(node.getId(), node);
        this.size++;
    }

    /**
     * Get a specific Node.
     * @param id The Id of the Node to get.
     * @return The Node with the given Id.
     */
    public SequenceNode getNode(Integer id) {
        return nodes.get(id);
    }

    /**
     * Getter for the HashmMap with Nodes.
     * @return The Hashmap with Nodes.
     */
    public HashMap<Integer, SequenceNode> getNodes() {
        return this.nodes;
    }

    /**
     * Add a child to each Node.
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
    private void createColumns() {
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
    private void createEdgeColumns() {

        for (Edge edge : edges) {
            int parColumn = nodes.get(edge.getParent()).getColumn();
            int childColumn = nodes.get(edge.getChild()).getColumn();
            edge.setEntireColumnSpan(parColumn, childColumn);
        }
    }

    /**
     * Get the List of all Columns.
     * @return The List of Columns.
     */
    public ArrayList<ArrayList<Node>> getColumnList() {
        ArrayList<ArrayList<Node>> columns = new ArrayList<ArrayList<Node>>();

        for (Object o : nodes.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            SequenceNode node = (SequenceNode) pair.getValue();
            while (columns.size() <= node.getColumn()) {
                columns.add(new ArrayList<Node>());
            }
            columns.get(node.getColumn()).add(node);
            //it.remove();
        }

        for (Edge edge : edges) {
            for (int i : edge.getColumnSpan()) {
                columns.get(i).add(0, new DummyNode());
            }
        }
        return columns;
    }

}
