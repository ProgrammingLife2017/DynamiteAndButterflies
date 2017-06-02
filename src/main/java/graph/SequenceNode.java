package graph;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Class Node2, which represents sequences of DNA. A sequence is a part of a genome.
 * The sequence is a String consisting of A, C T and G.
 */
public class SequenceNode {

    private static final int ARC_SIZE = 10;

    private int id;
    private int index;
    private int column;
    private int sequenceLength;
    private double xCoordinate;
    private double yCoordinate;
    private double width;
    private double height;
    private boolean highlighted;
    private boolean isDummy;
    private float baryCenterValue;
    private int inDegree;


    private ArrayList<Integer> children;
    private ArrayList<Integer> parents;
    private GraphicsContext gc;

    public SequenceNode(int id) {
        this.id = id;
        this.index = 0;
        this.column = Integer.MIN_VALUE;
        this.inDegree = 0;
        this.baryCenterValue = 0;
        this.parents = new ArrayList<Integer>();
        this.children = new ArrayList<Integer>();
        this.isDummy = false;
    }

    public void setCoordinates(double x, double y, double width, double height) {
        this.xCoordinate = x;
        this.yCoordinate = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Draw the node highlighted.
     */
    public void highlight() {
        this.highlighted = true;
    }

    /**
     * Draw the node lowlighted.
     */
    public void lowlight() {
        this.highlighted = false;
    }

    /**
     * Draw the node with the color depending on it's status. Orange for highlighted nodes, black for dummy nodes and
     * blue for sequence nodes.
     */
    public void draw(GraphicsContext gc) {
        gc.clearRect(xCoordinate, yCoordinate, width, height);
        if (highlighted) {
            gc.setFill(Color.ORANGE);
        } else if (isDummy) {
            gc.strokeLine(xCoordinate, yCoordinate + height / 2, xCoordinate + width, yCoordinate + height / 2);
            return;
        } else {
            gc.setFill(Color.BLUE);
        }
        gc.fillRoundRect(xCoordinate, yCoordinate, width, height, ARC_SIZE, ARC_SIZE);
    }

    /**
     * Check if a click event is within the borders of this node.
     *
     * @param xEvent x coordinate of the click event
     * @param yEvent y coordinate of the click event
     * @return True if the coordinates of the click event are within borders, false otherwise.
     */
    public boolean checkClick(double xEvent, double yEvent) {
        return (xEvent > xCoordinate && xEvent < xCoordinate + width && yEvent > yCoordinate && yEvent < yCoordinate + height);
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Integer getId() {
        return id;
    }

    public Integer getChild(int id) {
        return children.get(id);
    }

    public void addChild(Integer id) {
        if(!this.children.contains(id))
            this.children.add(id);
    }

    public void removeChild(Integer id) {
        this.children.remove(id);
    }

    public ArrayList<Integer> getParents() {
        return parents;
    }

    public void addParent(Integer id) {
        if(!this.parents.contains(id)) {
            this.parents.add(id);
        }
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public ArrayList<Integer> getChildren() {
        return this.children;
    }

    public boolean isDummy() {
        return isDummy;
    }

    public void setDummy(boolean dummy) {
        isDummy = dummy;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int col) {
        this.column = col;
    }

    public void incrementColumn(int i) {
        if (this.column < i + 1) {
            column = i + 1;
        }
    }

    /**
     * method to resolve the baryCenterValue.
     * @return - returns the barycenterValue / inDegree
     */
    public float getBaryCenterValue() {
        return baryCenterValue / inDegree;
    }

    public void setBaryCenterValue(float baryCenterValue) {
        this.baryCenterValue = baryCenterValue;
    }

    public void incrementBaryCenterValue(float baryCenterValue) {
        this.baryCenterValue += baryCenterValue;
    }

    public int getInDegree() {
        return inDegree;
    }

    public void setInDegree(int inDegree) {
        this.inDegree = inDegree;
    }

    public void incrementInDegree() {
        this.inDegree++;
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    public void setSequenceLength(int sequenceLength) {
        this.sequenceLength = sequenceLength;
    }


    public void incrementLayer(int parLayer) {
        if (this.column < parLayer + 1) {
            this.column = parLayer + 1;
        }
    }
}
