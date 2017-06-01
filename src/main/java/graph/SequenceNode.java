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
    private ArrayList<Integer> parents;
    private boolean isDummy;
    private float baryCenterValue;
    private int inDegree;

    public SequenceNode(int id) {
        this.id = id;
        this.index = 0;
        this.column = 0;
        this.inDegree = 0;
        this.baryCenterValue = 0;
        this.parents = new ArrayList<Integer>();
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

    public void removeChild(Integer id) {
        this.children.remove(id);
    }

    public void addParent(Integer id) {
        if(!this.parents.contains(id)) {
            this.parents.add(id);
        }
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

    /**
     * method to resolve the baryCenterValue.
     * @return - returns the barycenterValue / inDegree
     */
    public float getBaryCenterValue() {
        return baryCenterValue / inDegree;
    }

    public void setBaryCenterValue(float baryCenterValue) {
        this.baryCenterValue = baryCenterValue;
    }

    public void incrementBaryCenterValue(float baryCenterValue) {
        this.baryCenterValue += baryCenterValue;
    }

    public int getInDegree() {
        return inDegree;
    }

    public void setInDegree(int inDegree) {
        this.inDegree = inDegree;
    }

    public void incrementInDegree() {
        this.inDegree++;
    }
}
