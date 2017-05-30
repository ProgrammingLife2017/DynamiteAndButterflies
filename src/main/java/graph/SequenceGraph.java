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

    /**
     * The constructor initializes the SequenceGraph with it's basic values.
     */
    public SequenceGraph() {
        this.nodes = new HashMap<Integer, SequenceNode>();
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
            }
        }
        SequenceNode lastNode = new SequenceNode(upperBoundID);
        this.nodes.put(upperBoundID, lastNode);


       // layerizeGraph(lowerBoundID);
        LinkedList<Integer> test = Dfs(lowerBoundID);
    }


    private LinkedList<Integer> Dfs(int startNode) {
        HashMap<Integer, Boolean> visited = new HashMap<Integer, Boolean>(nodes.size());
        LinkedList<Integer> longestPath = new LinkedList<Integer>();
        return DFShelper(startNode, visited, longestPath);

    }
    private LinkedList<Integer> DFShelper(int currentNode, HashMap<Integer, Boolean> visited, LinkedList<Integer> longestPath) {
        visited.put(currentNode, true);
        longestPath.add(currentNode);
        for(int children: this.getNode(currentNode).getChildren()) {
            if(!visited.get(children)) {
                DFShelper(children, visited, longestPath);
            }
        }
        return longestPath;
    }

    /**
     * Adds dummy nodes to the graph for visualisation purposes.
     */
//    private void addDummies() {
//        while (!list.isEmpty()){
//            AbstractNode parent = this.getNode(list.poll());
//            int size = parent.getChildren().size();
//            for (int i = 0; i < size; i++) {
//                int childId = parent.getChildren().get(i);
//                int span = getNode(childId).getLayer() - parent.getLayer();
//                if (span > 1) {
//                    addDummyHelper(span, parent, getNode(childId));
//                }
//            }
//        }
//    }
//
//

    /** Helper function for addDummy()
     *
     * @param span - the difference in layer level of parent and child
     * @param parent - the parent node
     * @param target - the target node
     */
    /*
    private void addDummyHelper(int span, AbstractNode parent, AbstractNode target) {
        if (span > 1) {
            DummyNode dummy = new DummyNode(this.size+1, parent.getLayer() + 1);
            int size = parent.getChildren().size();
            for (int i = 0; i < size; i++) {
                if (parent.getChildren().get(i) == target.getId()) {
                    parent.getChildren().remove(i);
                    parent.addChild(dummy.getId());
                    break;
                }
            }
            dummy.addChild(target.getId());
            this.addNode(dummy);
            span --;
            addDummyHelper(span, dummy, target);
        }
    }
    */



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

}
