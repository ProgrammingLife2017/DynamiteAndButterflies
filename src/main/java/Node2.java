/**
 * Created by Lex on 25-4-17.
 */

import java.util.ArrayList;

/**
 * Class Node2, which represents sequences of DNA. A sequence is a part of a genome.
 * The sequence is a String consisting of A, C T and G.
 */
public class Node2 {
    private int id;
    private String seq;
    private ArrayList<Integer> parent;
    private ArrayList<Integer> child;


    public Node2(int id, String seq){
        this.id = id;
        this.seq = seq;
        this.child = new ArrayList<Integer>();
        this.parent = new ArrayList<Integer>();
    }

    public ArrayList<Integer> getParent() {
        return parent;
    }

    public void setParent(ArrayList<Integer> parent) {
        this.parent = parent;
    }

    public ArrayList<Integer> getChild() {
        return child;
    }

    public void setChild(ArrayList<Integer> child) {
        this.child = child;
    }

    public void addChild(Integer i) {
        this.child.add(i);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
}
