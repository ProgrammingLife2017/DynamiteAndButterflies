package graph;

import java.util.ArrayList;

/**
 * Created by Jasper van Tilburg on 8-5-2017.
 * A class specifying nodes to be used to draw the graph.
 * A sequence is a part of a genome.
 * The sequence is a String consisting of A, C T and G.
 */
public class Node {
    private ArrayList<Integer> adjecencyList;
    private int column;
    private int index;

    /**
     * Constructor of the Node object.
     */
    public Node(){
        this.adjecencyList = new ArrayList<Integer>();
        this.column = 0;
        this.index = 0;
    }

    /**
     * Returns all the children that the node has.
     * @return An ArrayList of nodes that contain all the children of the node
     */
    ArrayList<Integer> getChildren() {
        return this.adjecencyList;
    }

    /**
     * Returns all the children that the node has.
     * @return An ArrayList of nodes that contain all the children of the node
     */
    public ArrayList<Integer> returnChildren() {
        return this.getChildren();
    }

    /**
     * Confirms that the node has children or not.
     * @return true if the node has children. False if it does not.
     */
    public boolean hasChildren() {
        return getChildren().size() != 0;
    }

    /**
     * Adds a child to the node.
     * @param childID a nodeID to be added as a child to this node.
     */
    void addChild(Integer childID) {
        this.getChildren().add(childID);
    }

    /**
     * Gets the column of the node.
     * @return the column of the node.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Sets the column of the node.
     * @param newColumn the newColumn value of the node
     */
    private void setColumn(int newColumn) {
        this.column = newColumn;
    }

    /**
     * Increments the column of the node.
     * @param parColumn the column that might overwrite.
     */
    void incrementColumn(int parColumn) {
        if (this.column < parColumn + 1) {
            setColumn(parColumn + 1);
        }
    }

    /**
     * Sets the index of the node.
     * @param index the index value of the node
     */
    void setIndex(int index) {
        this.index = index;
    }
}
