package graph; /**
 * Created by Lex on 25-4-17.
 */

import java.util.ArrayList;

/**
 * Class Node2, which represents sequences of DNA. A sequence is a part of a genome.
 * The sequence is a String consisting of A, C T and G.
 */
public class SequenceNode extends Node {

    private String seq;

    public SequenceNode(int id, String seq){
        super(id);
        this.seq = seq;
    }

    public void setSequence(String seq) {
        this.seq = seq;
    }

    public String getSequence() {
        return this.seq;
    }


}
