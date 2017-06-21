package graph;

import gui.DrawableCanvas;

import java.util.ArrayList;
import java.util.Arrays;

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
    private boolean isSNP;

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
     * Return the outdegree of the node
     * @return outdegree
     */
    public int getOutDegree() {
        return this.getChildren().size();
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

    public boolean isHighlighted() {
        return this.highlighted;
    }

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

    public boolean isSNP() {
        return isSNP;
    }

    public void setSNP(boolean SNP) {
        isSNP = SNP;
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
        stringBuilder.append("Column index:\t\t").append(this.column).append("\n");
        appendChildren(stringBuilder);
        appendParents(stringBuilder);
        stringBuilder.append("SequenceLength:\t").append(this.sequenceLength).append("\n");
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
        for (Integer i: offSets) {
            stringBuilder.append(i).append(" ");
        }
        stringBuilder.append("\n");
    }

    /**
     * Appends genomes to a string builder.
     * @param stringBuilder string builder to append to.
     */
    private void appendGenomes(StringBuilder stringBuilder) {
        stringBuilder.append("Genomes:\t\t");
        int[] sortedGenomes = this.getGenomes();
        Arrays.sort(sortedGenomes);
        for (Integer i: sortedGenomes) {
            stringBuilder.append(DrawableCanvas.getInstance().getAllGenomesReversed().get(i)).append(" ");
        }
        stringBuilder.append("\n");
    }

    /**
     * Append sequence to a string builder.
     * @param sequence the sequence
     * @param stringBuilder string builder to append to.
     */
    private void appendSequence(String sequence, StringBuilder stringBuilder) {
        stringBuilder.append("Sequence:\t\t");
        if (isDummy) {
            stringBuilder.append("-\n");
        } else {
            stringBuilder.append(sequence).append("\n");
        }
    }

    /**
     * Appends parents to a string builder.
     * @param stringBuilder string builder to append to.
     */
    private void appendParents(StringBuilder stringBuilder) {
        stringBuilder.append("Parents:\t\t\t");
        for (Integer i: parents) {
            stringBuilder.append(i).append(" ");
        }
        stringBuilder.append("\n");
    }
    /**
     * Appends children to a string builder.
     * @param stringBuilder string builder to append to.
     */
    private void appendChildren(StringBuilder stringBuilder) {
        stringBuilder.append("Children:\t\t\t");
        for (Integer i : children) {
            stringBuilder.append(i).append(" ");
        }
        stringBuilder.append("\n");
    }
}
