package graph;

import java.util.ArrayList;

/**
 * Class Node2, which represents sequences of DNA. A sequence is a part of a genome.
 * The sequence is a String consisting of A, C T and G.
 */
public class SequenceNode {

    private int id;
    private int index;
    private int column;
    private ArrayList<Integer> children;
    private boolean isDummy;

    public SequenceNode(int id) {
        this.id = id;
        this.index = 0;
        this.column = 0;
        this.children = new ArrayList<Integer>();
        this.isDummy = false;
    }

    public Integer getId() {
        return id;
    }

    public Integer getChild(int id) {
        return children.get(id);
    }

    public void addChild(Integer id) {
        if(!this.children.contains(id))
            this.children.add(id);
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }
    public ArrayList<Integer> getChildren() {
        return this.children;
    }

    public boolean isDummy() {
        return isDummy;
    }

    public void setDummy(boolean dummy) {
        isDummy = dummy;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Gets the column of the node.
     * @return the column of the node.
     */
    public int getColumn() {
        return column;
    }

    public void setColumn(int col) {
        this.column = col;
    }

    public void incrementColumn(int i) {
        if (this.column < i + 1) {
            column = i + 1;
        }
    }

}
