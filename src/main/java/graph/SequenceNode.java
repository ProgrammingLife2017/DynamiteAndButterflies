package graph;

import gui.DrawableCanvas;

import java.util.ArrayList;

/**
 * Class Node2, which represents sequences of DNA. A sequence is a part of a genome.
 * The sequence is a String consisting of A, C T and G.
 */
public class SequenceNode {

    private int id;
    private int index;
    private int column;
    private int sequenceLength;
    private int inDegree;
    private int[] genomes;
    private int[] offSets;
    private float baryCenterValue;
    private boolean highlighted;
    private boolean isDummy;
    private boolean isSNP;
    private boolean isCollapsed;

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
        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();
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

    /**
     * method to resolve the baryCenterValue.
     *
     * @return - returns the barycenterValue / inDegree
     */
    float getBaryCenterValue() {
        return baryCenterValue / inDegree;
    }

    /**
     * Add a child to the children list. No duplicates allowed.
     *
     * @param id Child to be added.
     */
    void addChild(Integer id) {
        if (!this.children.contains(id)) {
            this.children.add(id);
        }
    }

    /**
     * Remove a child from the children list.
     *
     * @param id Child to be removed.
     */
    void removeChild(Integer id) {
        this.children.remove(id);
    }

    /**
     * Check if this node has any children.
     *
     * @return True if the node has at least 1 child, false otherwise.
     */
    boolean hasChildren() {
        return children.size() > 0;
    }

    /**
     * Add a parent to the parents list. No duplicates allowed.
     *
     * @param id The parent to be added.
     */
    void addParent(Integer id) {
        if (!this.parents.contains(id)) {
            this.parents.add(id);
        }
    }

    /**
     * Increment the value of column by some amount.
     *
     * @param i The amount to increment column by.
     */
    void incrementColumn(int i) {
        if (this.column < i + 1) {
            column = i + 1;
        }
    }

    /**
     * Increment the baryCenterValue by some amount.
     *
     * @param baryCenterValue Amount to increase baryCenterValue with
     */
    void incrementBaryCenterValue(float baryCenterValue) {
        this.baryCenterValue += baryCenterValue;
    }

    /**
     * Increment inDegree by one.
     */
    void incrementInDegree() {
        this.inDegree++;
    }

    public boolean isHighlighted() {
        return this.highlighted;
    }

    public Integer getId() {
        return id;
    }

    public Integer getChild(int id) {
        return children.get(id);
    }

    public ArrayList<Integer> getParents() {
        return parents;
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

    public void setGenomes(int[] genomesArg) {
        this.genomes = genomesArg;
    }

    public void setOffSets(int[] offSets) {
        this.offSets = offSets;
    }

    public int[] getOffsets() {
        return this.offSets;
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

    public boolean isSNP() {
        return isSNP;
    }

    public void setSNP(boolean snp) {
        isSNP = snp;
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    public void setCollapsed(boolean collapsed) {
        isCollapsed = collapsed;
    }

    /**
     * Forms a string of the sequence node.
     *
     * @param sequence With it's sequence which we do not constantly want in memory
     * @return A string representation of the node.
     */
    public String toString(String sequence) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Node ID:\t\t\t").append(this.id).append("\n");
        appendSequence(sequence, stringBuilder);
        appendGenomes(stringBuilder);
        appendGenomeCoords(stringBuilder);
        return stringBuilder.toString();
    }

  
    /**
     * Appends genome coordinates to a string builder.
     * @param stringBuilder string builder to append to.
     */
    private void appendGenomeCoords(StringBuilder stringBuilder) {
        stringBuilder.append("Genome coords:\t");
        if(this.offSets != null) {
            for (Integer i : this.offSets) {
                stringBuilder.append(i).append(" ");
            }
            stringBuilder.append("\n");
        }
    }

    /**
     * Appends genomes to a string builder.
     * @param stringBuilder string builder to append to.
     */
    private void appendGenomes(StringBuilder stringBuilder) {
        if (this.getGenomes().length != 0) {
            stringBuilder.append("Genomes:\t\t");
            for (Integer i: this.getGenomes()) {
                stringBuilder.append(DrawableCanvas.getInstance().getAllGenomesReversed().get(i)).append(" ");
            }
            stringBuilder.append("\n");
        }
    }

    /**
     * Append sequence to a string builder.
     * @param sequence the sequence
     * @param stringBuilder string builder to append to.
     */
    private void appendSequence(String sequence, StringBuilder stringBuilder) {
        stringBuilder.append("Sequence: " + "(length: " + this.sequenceLength + ")" + "\t\t");
        if (isDummy) {
            stringBuilder.append("-\n");
        } else {
            stringBuilder.append(sequence).append("\n");
        }
    }



    public int getOutDegree() {
        return children.size();
    }
}
