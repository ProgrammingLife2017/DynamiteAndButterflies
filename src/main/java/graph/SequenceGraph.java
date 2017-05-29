package graph;

import javafx.util.Pair;
import parser.GfaParser;
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
    private HashMap<Integer, ArrayList<Integer>> adjacencyMap;

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


    public void createSubGraph(int centerNodeID, int range, int[] parentArray, int[] childArray) {
        int upperBoundID = childArray[childArray.length -1];
        int lowerBoundID = parentArray[0];
        range = range + 5;


        if(centerNodeID + range <= upperBoundID) {
            upperBoundID = centerNodeID + range;
        }
        if(centerNodeID - range >= lowerBoundID) {
            lowerBoundID = centerNodeID - range;
        }

        for(int i = 0; i < parentArray.length; i++) {
            int parentID = parentArray[i];
            int childID = childArray[i];

            if(parentID >= lowerBoundID) {
                if (nodes.get(parentID) == null) {
                    SequenceNode node = new SequenceNode(parentID);
                    node.addChild(childID);
                    nodes.put(parentID, node);
                } else {
                    nodes.get(parentID).addChild(childID);
                }
//                SequenceNode node = new SequenceNode(parentID);
//                node.addChild(childID);
//                this.nodes.put(parentID, node);
//                addToList(parentID, childID);

            }
        }
        SequenceNode lastNode = new SequenceNode(upperBoundID);
        this.nodes.put(upperBoundID, lastNode);


        layerizeGraph(lowerBoundID);
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
     * Will add columns to all the nodes and to all the edges.
     */
    public void layerizeGraph(int lowerBoundID) {
            createColumns(lowerBoundID);
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
    private void createColumns(int lowerBoundID) {
        for (int i = lowerBoundID; i <= nodes.size() + lowerBoundID - 1; i++) {
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


    //TODO: rework placement of nodes and dummy nodes
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

    /**
     * Adder for Hashmap<Integer, List<Integer>)
     *
     * @param mapKey - the key in which to add a nodeID
     * @param nodeID - the nodeID to be added
     */
    private synchronized void addToList(Integer mapKey, Integer nodeID) {
        if (adjacencyMap.get(mapKey) == null) {
            ArrayList<Integer> idList = new ArrayList<Integer>();
            idList.add(nodeID);
            adjacencyMap.put(mapKey, idList);

        }
        else if(adjacencyMap.get(mapKey) != null) {
            ArrayList<Integer> idList = adjacencyMap.get(mapKey);
            // if list does not exist create it
            if (idList == null) {
                idList = new ArrayList<Integer>();
                idList.add(nodeID);
                adjacencyMap.put(mapKey, idList);
            } else {
                // add if item is not already in list
                if (!idList.contains(nodeID)) idList.add(nodeID);
            }
        }
    }

}
