/**
 * Created by Lex on 25-4-17.
 */

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class Node2, which represents sequences of DNA. A sequence is a part of a genome.
 * The sequence is a String consisting of A, C T and G.
 */
public class Node2 {
    private int id;
    private String seq;
    private ArrayList<Integer> parent;
    private ArrayList<Integer> child;
    private int columnID;
    private int mutationLevel;


    public Node2(int id, String seq){
        this.id = id;
        this.seq = seq;
        this.child = new ArrayList<Integer>();
        this.parent = new ArrayList<Integer>();
        this.columnID = 0;
        this.mutationLevel = 0;
    }

    public ArrayList<Integer> getParent() {
        return parent;
    }

    public void setParent(ArrayList<Integer> parent) {
        this.parent = parent;
    }

    public void addParent(Integer i) {
        this.parent.add(i);
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

    public int getMutationLevel() {
        return this.mutationLevel;
    }

    public void incrementMutationLevel(int currentMutLvl) {
        this.mutationLevel = currentMutLvl + 1;
    }

    public boolean amIYourChild (Node2 your){
        if(your.equals(this)) {
            return false;
        }

        ArrayList<Integer> theirChildren = your.getChild();
        return theirChildren.contains(this.getId());
    }

    public ArrayList<Node2> getParentNodes (HashMap<Integer, Node2> hash) {
        ArrayList<Integer> parents = this.getParent();
        ArrayList<Node2> res = new ArrayList<Node2>();
        for (Integer parent : parents) {
            res.add(hash.get(parent));
        }
        return res;
    }

    public ArrayList<Node2> getChildrenNodes (HashMap<Integer, Node2> hash) {
        ArrayList<Integer> children = this.getChild();
        ArrayList<Node2> res = new ArrayList<Node2>();
        for (Integer child : children) {
            res.add(hash.get(child));
        }
        return res;
    }

    public boolean incrementColumn(int parentColumnID) {
        if(this.columnID > parentColumnID) {
            return false;
        } else {
            this.columnID = parentColumnID + 1;
            return true;
        }
    }

    public int getColumnID() {
        return columnID;
    }

    //A method that can add all parents to a list of nodes. Needs a hashmap from NodeIDs to Nodes to do so.
    public static void setAllParents(ArrayList<Node2> nodes, HashMap<Integer, Node2> hash) {
        for (Node2 node : nodes) {
            ArrayList<Integer> children = node.getChild();
            for (Integer aChildren : children) {
                Node2 child = hash.get(aChildren);
                child.addParent(node.getId());
            }
        }
    }
}
