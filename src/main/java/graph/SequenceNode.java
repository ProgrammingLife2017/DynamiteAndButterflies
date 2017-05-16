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

    public SequenceNode(int id) {
        this.id = id;
        this.ids = new ArrayList<Integer>();
        this.index = 0;

    }

    public Integer getId() {
        return id;
    }

    public void addId(Integer id) {
        this.ids.add(id);
    }


}
