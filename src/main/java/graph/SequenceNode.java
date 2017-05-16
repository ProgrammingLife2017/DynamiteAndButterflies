package graph;

import java.util.ArrayList;

/**
 * Class Node2, which represents sequences of DNA. A sequence is a part of a genome.
 * The sequence is a String consisting of A, C T and G.
 */
public class SequenceNode extends Node {

    private int id;
    private int index;
    private ArrayList<Integer> ids;
    private boolean isDummy;

    public SequenceNode(int id) {
        this.id = id;
        this.ids = new ArrayList<Integer>();
        this.index = 0;
        this.isDummy = false;

    }

    public Integer getId() {
        return id;
    }

    public void addId(Integer id) {
        this.ids.add(id);
    }


    public boolean isDummy() {
        return isDummy;
    }

    public void setDummy(boolean dummy) {
        isDummy = dummy;
    }
}
