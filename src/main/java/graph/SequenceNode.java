package graph;

import gui.GraphDrawer;
import gui.sub_controllers.ColourController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Class Node2, which represents sequences of DNA. A sequence is a part of a genome.
 * The sequence is a String consisting of A, C T and G.
 */
public class SequenceNode {

    private int id;
    private int[] genomes;
    private int[] offSets;
    private int index;
    private int column;
    private int sequenceLength;
    private boolean highlighted;
    private boolean isDummy;
    private float baryCenterValue;
    private int inDegree;


    private ArrayList<Integer> children;
    private ArrayList<Integer> parents;

    /**
     * Constructor for the sequenceNode.
     *
     * @param id The id of the node.
     */
    SequenceNode(int id) {
        this.id = id;
        this.index = 0;
        this.column = 0;
        this.inDegree = 0;
        this.baryCenterValue = 0;
        this.parents = new ArrayList<Integer>();
        this.children = new ArrayList<Integer>();
        this.isDummy = false;
        this.genomes = new int[0];
        this.offSets = new int[0];
    }

    /**
     * Draw the node highlighted.
     */
    public void highlight() {
        this.highlighted = true;
    }

    /**
     * Draw the node lowlighted.
     */
    public void lowlight() {
        this.highlighted = false;
    }

    public boolean isHighlighted() {return this.highlighted;}


    public Integer getId() {
        return id;
    }

    public Integer getChild(int id) {
        return children.get(id);
    }

    public void addChild(Integer id) {
        if (!this.children.contains(id))
            this.children.add(id);
    }

    void removeChild(Integer id) {
        this.children.remove(id);
    }

    public ArrayList<Integer> getParents() {
        return parents;
    }

    void addParent(Integer id) {
        if (!this.parents.contains(id)) {
            this.parents.add(id);
        }
    }

    boolean hasChildren() {
        return children.size() > 0;
    }

    public ArrayList<Integer> getChildren() {
        return this.children;
    }

    public boolean isDummy() {
        return isDummy;
    }

    void setDummy(boolean dummy) {
        isDummy = dummy;
    }

    public int getIndex() {
        return this.index;
    }

    void setIndex(int index) {
        this.index = index;
    }

    public int getColumn() {
        return column;
    }

    void setColumn(int col) {
        this.column = col;
    }

    void incrementColumn(int i) {
        if (this.column < i + 1) {
            column = i + 1;
        }
    }

    public void setGenomes(int[] genomesArg) {
        this.genomes = genomesArg;
    }

    public void setOffSets(int[] offSets) {
        this.offSets = offSets;
    }

    public int[] getOffsets() {
        return this.offSets;
    }

    /**
     * method to resolve the baryCenterValue.
     *
     * @return - returns the barycenterValue / inDegree
     */
    float getBaryCenterValue() {
        return baryCenterValue / inDegree;
    }


    void incrementBaryCenterValue(float baryCenterValue) {
        this.baryCenterValue += baryCenterValue;
    }


    void incrementInDegree() {
        this.inDegree++;
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    public void setSequenceLength(int sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    public int[] getGenomes() {
        return genomes;
    }

    /**
     * Forms a string of the sequence node.
     *
     * @param sequence With it's sequence which we do not constantly want in memory
     * @return A string representation of the node.
     */
    public String toString(String sequence) {
        String str = "Node ID:\t" + this.id + "\n"
                + "Column index:\t" + this.column + "\n"
                + "Children:\t";
        for (Integer i : children) {
            str += i.toString() + ", ";
        }
        str = str.substring(0, str.length() - 2) + "\n" + "Parents:\t";
        for (Integer i : parents) {
            str += i.toString() + ", ";
        }
        str = str.substring(0, str.length() - 2) + "\n"
                + "SequenceLength:\t" + this.sequenceLength + "\n"
                + "Sequence:\t";
        if (isDummy) {
            str += "-\n";
        } else {
            str += sequence + "\n";
        }
        str += "Genomes that go through this:\t";
        for (Integer i : this.getGenomes()) {
            str += i.toString() + ", ";
        }
        str += "\nCo-Ordinates of the genomes that go through this";
        for (int offSet : offSets) {
            str += offSet + ", ";
        }

        str = str.substring(0, str.length() - 2);
        return str;
    }
}
