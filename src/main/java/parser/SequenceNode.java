package parser; /**
 * Created by Lex on 25-4-17.
 */

import java.util.ArrayList;

/**
 * A class specifying nodes to be used to draw the graph.
 * A sequence is a part of a genome.
 * The sequence is a String consisting of A, C T and G.
 */
public class SequenceNode {
    private int id;
    private String seq;
    private ArrayList<SequenceNode> adjecencyList;
    private ArrayList<Integer> ids;
    private int column;

    /**
     * Constructor of the Sequence node object.
     * @param id The id the node should receive
     * @param seq The sequence the node should receive
     */
    public SequenceNode(int id, String seq) {
        this.id = id;
        this.seq = seq;
        this.adjecencyList = new ArrayList<SequenceNode>();
        this.ids = new ArrayList<Integer>();
        this.column = 0;
    }

    /**
     * Adds an id to the node.
     * @param id a new id to be added
     */
    public void addId(Integer id) {
        this.ids.add(id);
    }

    /**
     * Returns all the children that the node has.
     * @return An ArrayList of nodes that contain all the children of the node
     */
    public ArrayList<SequenceNode> getChildren() {
        return this.adjecencyList;
    }

    /**
     * Returns all the children that the node has.
     * @return An ArrayList of nodes that contain all the children of the node
     */
    public ArrayList<SequenceNode> returnChildren() {
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
     * @param child a node to be added as a child to this node.
     */
    public void addChild(SequenceNode child) {
        this.getChildren().add(child);
    }

    /**
     * Returns the id of the node.
     * @return the id of the node
     */
    public Integer getId() {
        return id;
    }

    /**
     * Returns the sequence of the node.
     * @return the sequence of the node
     */
    public String getSeq() {
        return seq;
    }

    /**
     * Sets the sequence of the node to a different one.
     * @param seq The new sequence for the node
     */
    public void setSeq(String seq) {
        this.seq = seq;
    }

    /**
     * Returns the sequence of the node.
     * @return the sequence of the node
     */
    public String getSequence() {
        return getSeq();
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
    public void setColumn(int newColumn) {
        this.column = newColumn;
    }

    /**
     * Increments the column of the node.
     * @param parColumn the column that might overwrite.
     */
    public void incrementColumn(int parColumn) {
        if (this.column < parColumn + 1) {
            setColumn(parColumn + 1);
        }
    }

}
