package graph;

import java.util.ArrayList;


public class DummyNode extends AbstractNode {

    private int id;
    private ArrayList<Integer> adjacencyList;
    private ArrayList<Integer> parents;
    private int index;
    private int layer;
    private float baryCenterValue;
    private int xCoordinate;
    private int yCoordinate;

    DummyNode(int id, int layer) {
        this.layer = layer;
        this.id = id;
        this.adjacencyList = new ArrayList<Integer>();
        this.parents = new ArrayList<Integer>();
    }

    void addParent(int id) {
        this.parents.add(id);
    }

    ArrayList<Integer> getParents() {
        return this.parents;
    }

    void setParents(ArrayList<Integer> parents) {
        this.parents = parents;
    }

    int getIndex() {
        return this.index;
    }

    void setIndex(int index) {
        this.index = index;
    }

    float getBaryCenterValue() {
        return this.baryCenterValue;
    }

    void setBaryCenterValue(float value) {
        this.baryCenterValue = value;
    }

    public void updateBaryCenterValue(int value) {
        this.baryCenterValue += value;
    }

    public ArrayList<Integer> getChildren() {
        return this.adjacencyList;
    }

    public int getLayer() {
        return this.layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void addChild(int childId) {
        this.adjacencyList.add(childId);
    }

    public int getId() {
        return this.id;
    }

    public void incrementLayer(int parLayer) {
        if (this.layer < parLayer + 1) {
            this.layer = parLayer + 1;
        }
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(int x) {
        this.xCoordinate = x;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(int y) {
        this.yCoordinate = y;
    }
}

