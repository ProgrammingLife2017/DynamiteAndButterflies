package graph;
import java.util.ArrayList;

/**
 * Class Node2, which represents sequences of DNA. A sequence is a part of a genome.
 * The sequence is a String consisting of A, C T and G.
 */
public class SequenceNode extends AbstractNode {
    private int id;
    private String seq;
    private ArrayList<Integer> adjacencyList;
    private ArrayList<Integer> parents;
    private ArrayList<Integer> ids;
    private int index;
    private int layer;
    private float baryCenterValue;
    private int xCoordinate;
    private int yCoordinate;


    public SequenceNode(int id, String seq){
        this.id = id;
        this.seq = seq;
        this.adjacencyList = new ArrayList<Integer>();
        this.parents = new ArrayList<Integer>();
        this.ids = new ArrayList<Integer>();
        this.layer = -1;
        this.baryCenterValue = 0;
    }

    public int getLayer() {
        return this.layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
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
//    public ArrayList<Edge> getEdges() {
//        ArrayList<Edge> edges = new ArrayList<Edge>(this.adjacencyList.size());
//        for(int id: this.adjacencyList) {
//            Edge edge = new Edge(this.id, id);
//            edges.add(edge);
//        }
//        return edges;
//    }

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

    public int getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public void addParent(int id) {
        this.parents.add(id);
    }

    public ArrayList<Integer> getParents() {
        return this.parents;
    }

    public void setParents(ArrayList<Integer> parents) {
        this.parents = parents;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public float getBaryCenterValue() {
        return baryCenterValue;
    }

    public void setBaryCenterValue(float value) {
        this.baryCenterValue = value;
    }

    public void updateBaryCenterValue(int value) {
        this.baryCenterValue += value;
    }
}