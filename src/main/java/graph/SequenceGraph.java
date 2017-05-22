package graph;

import javafx.util.Pair;
import parser.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Our own Graph Class.
 * This class is the SequenceGraph.
 * A Graph handling a Directed-Acyclic-Graph.
 * This is our own data structure we will use to draw the eventual graph.
 */
public class SequenceGraph {


    private HashMap<Integer, SequenceNode> nodes;
    private ArrayList<Edge> edges;
    private ArrayList<ArrayList<SequenceNode>> columns;

    /**
     * The constructor initializes the SequenceGraph with it's basic values.
     */
    public SequenceGraph() {
        this.nodes = new HashMap<Integer, SequenceNode>();
        this.edges = new ArrayList<Edge>();
    }
    public int size() {
        return nodes.size();
    }


    public void createSubGraph(int centerNodeID, int range, List<Tuple> edgeList) {
        int upperBoundID = edgeList.get(edgeList.size()-1).childID;
        int lowerBoundID = 1;
        if(centerNodeID + range <= upperBoundID) {
            upperBoundID = centerNodeID + range;
        }
        if(centerNodeID - range >= lowerBoundID) {
            lowerBoundID = centerNodeID - range;
        }

        for(int i = 0; i < edgeList.size(); i++) {
            int parentID = edgeList.get(i).parentID;
            int childID = edgeList.get(i).childID;

            if(parentID >= lowerBoundID && childID <= upperBoundID) {
                SequenceNode parentNode = new SequenceNode(parentID);
                parentNode.addChild(childID);
                SequenceNode childNode = new SequenceNode(childID);
                this.addNode(parentNode);
                this.addNode(childNode);
                Edge edge = new Edge(parentID, childID);
                this.getEdges().add(edge);
            }
        }
    }

    /**
     * Getter for the column list
     * @return the column arraylist with an arraylist with nodes.
     */
    public ArrayList<ArrayList<SequenceNode>> getColumns() {
        return this.columns;
    }


    /**
     * Returns all the edges contained in the graph.
     * @return a arrayList of Edges containing all the edges of the graph.
     */
    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    public void setEdges(ArrayList<Edge> allEdges) {
        this.edges = allEdges;
    }

    /**
     * Add a node to the ArrayList of Nodes.
     * @param node The node to be added.
     */
    public void addNode(SequenceNode node) {
        this.nodes.put(node.getId(), node);
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
     * Returns all nodes contained in the graph.
     * @return A HashMap of all nodes and their IDs contained in the graph.
     */
    public HashMap<Integer, SequenceNode> getNodes() {
        return this.nodes;
    }

    public void setNodes(HashMap<Integer, SequenceNode> hash) {
        this.nodes = hash;
    }

    /**
     * Add a child to each Node.
     */
    public void initialize() {
        for (Edge edge : getEdges()) {
            // aan elke parent de child toevoegen
            this.getNode(edge.getParent()).addChild(edge.getChild());
        }
    }

    /**
     * Will add columns to all the nodes and to all the edges.
     */
    public void layerizeGraph() {
            createColumns();
            createEdgeColumns();
            this.columns = createColumnList();
            createIndex();

    }

    /**
     * assigns indices to all nodes in the column list.
     */
    private void createIndex() {
        for (ArrayList<SequenceNode> column : columns) {
            for (int j = 0; j < column.size(); j++) {
                column.get(j).setIndex(j);
            }
        }

    }

    /**
     * Gives each node a column where it should be built.
     */
    private void createColumns() {
        for (int i = 1; i <= nodes.size(); i++) {
            SequenceNode parent = nodes.get(i);     // Start at first node
            ArrayList<Integer> children = parent.getChildren();    // Get all children
            for (Integer child : children) {
                this.getNode(child).incrementColumn(parent.getColumn());
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
    private ArrayList<ArrayList<SequenceNode>> createColumnList() {
        ArrayList<ArrayList<SequenceNode>> columns = new ArrayList<ArrayList<SequenceNode>>();

        for (Object o : nodes.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            SequenceNode node = (SequenceNode) pair.getValue();
            while (columns.size() <= node.getColumn()) {
                columns.add(new ArrayList<SequenceNode>());
            }
            columns.get(node.getColumn()).add(node);
            //it.remove();
        }

        int counter = nodes.size()+1;
        for (Edge edge : edges) {
            for (int i : edge.getColumnSpan()) {
                SequenceNode dummyNode = new SequenceNode(counter);
                dummyNode.setDummy(true);
                columns.get(i).add(0, dummyNode);
                counter++;
            }
        }
        return columns;
    }

}
