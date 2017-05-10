package graph;

import java.util.ArrayList;

/**
 * Created by Jasper van Tilburg on 8-5-2017.
 */
public class Node {
    private ArrayList<SequenceNode> adjecencyList;
    private int column;

    public Node(){
        this.adjecencyList = new ArrayList<SequenceNode>();
        this.column = 0;
    }

    public ArrayList<SequenceNode> getChildren() {
        return this.adjecencyList;
    }

    public ArrayList<SequenceNode> returnChildren() {
        return this.getChildren();
    }

    public boolean hasChildren() {
        return getChildren().size() != 0;
    }
    public void addChild(SequenceNode child) {
        this.getChildren().add(child);
    }


    public int getColumn() { return column; }

    public void setColumn(int newColumn) { this.column = newColumn;}

    public void incrementColumn(int parColumn) {
        if (this.column < parColumn + 1) {
            this.column = parColumn + 1;
        }
    }

}
