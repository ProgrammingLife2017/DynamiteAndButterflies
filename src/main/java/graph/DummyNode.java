package graph;

import graph.AbstractNode;

import java.util.ArrayList;

/**
 * Created by eric on 10-5-17.
 */
public class DummyNode extends AbstractNode {

    private ArrayList<Integer> adjacencyList;
    private int layer;
    private int id;

    public DummyNode(int id, int layer) {
        this.layer = layer;
        this.id = id;
        this.adjacencyList = new ArrayList<Integer>();
    }

    public ArrayList<Integer> getChildren() {
        return this.adjacencyList;
    }

    public int getLayer() {
        return this.layer;
    }

    public void addChild(int childId) {
        this.adjacencyList.add(childId);
    }

    public int getId() {
        return this.id;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void incrementLayer(int parLayer) {
        if (this.layer < parLayer + 1) {
            this.layer = parLayer + 1;
        }
    }
}

