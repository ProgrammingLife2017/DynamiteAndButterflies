package graph; /**
 * Created by Lex on 25-4-17.
 */

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class Node2, which represents sequences of DNA. A sequence is a part of a genome.
 * The sequence is a String consisting of A, C T and G.
 */
public class SequenceNode extends AbstractNode {
    private int id;
    private String seq;
    private ArrayList<Integer> adjacencyList;
    private ArrayList<Integer> ids;
    private int layer;

    public SequenceNode(int id, String seq){
        this.id = id;
        this.seq = seq;
        this.adjacencyList = new ArrayList<Integer>();
        this.ids = new ArrayList<Integer>();
        this.layer = -1;
    }


    public void setLayer(int layer) {
        this.layer = layer;
    }
    public int getLayer() {
        return this.layer;
    }

    public void incrementLayer(int parLayer) {
        if (this.layer < parLayer + 1) {
            this.layer = parLayer + 1;
        }
    }
    public void addId(Integer id) {
        this.ids.add(id);
    }


    public ArrayList<Integer> getChildren() {
        return this.adjacencyList;
    }

    public ArrayList<Integer> returnChildren() {
        return this.getChildren();
    }

    public boolean hasChildren() {
        return getChildren().size() != 0;
    }
    public void addChild(int child) {
        this.getChildren().add(child);
    }

    public int getId() {
        return id;
    }


    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getSequence() {
        return this.seq;
    }

}
