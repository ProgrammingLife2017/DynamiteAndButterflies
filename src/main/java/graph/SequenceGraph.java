package graph;

import sun.awt.image.ImageWatched;

import java.util.*;

/**
 * Our own Graph Class.
 * This class is the SequenceGraph.
 * A Graph handling a Directed-Acyclic-Graph.
 * This is our own data structure we will use to draw the eventual graph.
 */
public class SequenceGraph {


    private HashMap<Integer, SequenceNode> nodes;
    private ArrayList<ArrayList<SequenceNode>> columns;
    private int upperBoundID;
    private int lowerBoundID;
    private int dummyNodeIDCounter;

    /**
     * The constructor initializes the SequenceGraph with it's basic values.
     */
    public SequenceGraph() {
        this.nodes = new HashMap<Integer, SequenceNode>();
    }

    public int size() {
        return nodes.size();
    }

    // upperbound in incorrect for TB10, the last node is not the highest one.
    public void createSubGraph(int centerNodeID, int range, int[] parentArray, int[] childArray) {
        upperBoundID = childArray[childArray.length -1];
        lowerBoundID = parentArray[0];
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
            }
        }
        SequenceNode lastNode = new SequenceNode(upperBoundID);
        this.nodes.put(upperBoundID, lastNode);
        // order: longest path, column, dummy's,
       // layerizeGraph(lowerBoundID);
        initColumnLongestPath();
        initColumns();
        addDummies();
        this.columns = createColumnList();
        createIndex();
    }

    //TODO: weird node is null? and dummy nodes at 0 column?
    // remove the null check.
    private void initColumns() {
        System.out.println(upperBoundID);
        for(int i = lowerBoundID; i < upperBoundID; i++) {
            int parentColumn = this.getNode(i).getColumn();
            for(int id: this.getNode(i).getChildren()) {
                if(this.getNode(id) != null && this.getNode(id).getColumn() == 0) {
                    this.getNode(id).setColumn(parentColumn + 1);
                }
            }
        }
    }

    private void initColumnLongestPath() {
        LinkedList<Integer> longestPath = Dfs(lowerBoundID, upperBoundID);
        int count = 0;
        for(int id: longestPath) {
            this.getNode(id).setColumn(count);
            count++;
        }

    }

    private LinkedList<Integer> Dfs(int startNode, int endNode) {
        HashMap<Integer, Boolean> visited = new HashMap<Integer, Boolean>(nodes.size());
        LinkedList<Integer> longestPath = new LinkedList<Integer>();
        return DFShelper(startNode, endNode, visited, longestPath);

    }
    private LinkedList<Integer> DFShelper(int currentNode,int endNode, HashMap<Integer, Boolean> visited, LinkedList<Integer> longestPath) {
        visited.put(currentNode, true);
        longestPath.add(currentNode);

        for(int child: this.getNode(currentNode).getChildren()) {
            if(visited.get(child) == null) {
                if(child == endNode) {
                    longestPath.add(child);
                    return longestPath;
                } else {
                    return DFShelper(child,endNode, visited, longestPath);
                }
            }
        }
        return longestPath;
    }

    /**
     * Adds dummy nodes to the graph for visualisation purposes.
     */
    private void addDummies() {
        dummyNodeIDCounter = upperBoundID + 1;
        for(int i = lowerBoundID + 1; i <= upperBoundID; i++) {
            SequenceNode parent = this.getNode(i);
            int size = parent.getChildren().size();
            for (int j = 0; j < size; j++) {
                int childId = parent.getChildren().get(j);
                int span = getNode(childId).getColumn() - parent.getColumn();
                if (span > 1) {
                    addDummyHelper(span, parent, getNode(childId));
                }
            }
        }
    }

    /** Helper function for addDummy()
     *
     * @param span - the difference in layer level of parent and child
     * @param parent - the parent node
     * @param target - the target node
     */
    private void addDummyHelper(int span, SequenceNode parent, SequenceNode target) {
        if (span > 1) {
            SequenceNode dummy = new SequenceNode(dummyNodeIDCounter++);
            dummy.setDummy(true);
            int size = parent.getChildren().size();
            for (int i = 0; i < size; i++) {
                if (parent.getChildren().get(i) == target.getId()) {
                    parent.getChildren().remove(i);
                    parent.addChild(dummy.getId());
                    dummy.setColumn(parent.getColumn() + 1);
                    break;
                }
            }
            dummy.addChild(target.getId());
            this.addNode(dummy);
            span --;
            addDummyHelper(span, dummy, target);
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

    }


    private ArrayList<ArrayList<SequenceNode>> createColumnList() {
        ArrayList<ArrayList<SequenceNode>> columns = new ArrayList<ArrayList<SequenceNode>>();

        for (Object o : nodes.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            SequenceNode node = (SequenceNode) pair.getValue();
            while (columns.size() <= node.getColumn()) {
                columns.add(new ArrayList<SequenceNode>());
            }
            columns.get(node.getColumn()).add(node);
        }
        return columns;
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

}
