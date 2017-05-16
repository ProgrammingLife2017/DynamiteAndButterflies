package graph; /**
 * Created by Lex on 25-4-17.
 */

import java.util.ArrayList;

/**
 * Class Node2, which represents sequences of DNA. A sequence is a part of a genome.
 * The sequence is a String consisting of A, C T and G.
 */
public class SequenceNode extends Node {

    private int id;
    private ArrayList<Integer> ids;

    public SequenceNode(int id) {
        this.id = id;
        this.ids = new ArrayList<Integer>();

    }

    public Integer getId() {
        return id;
    }

    public void addId(Integer id) {
        this.ids.add(id);
    }

}
