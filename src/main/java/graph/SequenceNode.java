package graph;

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

    /**
     * Constructor for the sequenceNode.
     * @param id The id of the node.
     */
    SequenceNode(int id) {
        this.id = id;
        this.index = 0;
        this.column = 0;
        this.inDegree = 0;
        this.baryCenterValue = 0;
        this.parents = new ArrayList<Integer>();
        this.children = new ArrayList<Integer>();
        this.isDummy = false;
    }

    /**
     * Setter for the location of a node.
     * @param x XLocation.
     * @param y YLocation.
     * @param width Width.
     * @param height Height.
     */
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
     * Draw the node with the color depending on it's status. Orange for highlighted nodes,
     * black for dummy nodes and blue for sequence nodes.
     * @param gc The grapicsContext of the screen.
     */
    public void draw(GraphicsContext gc) {
        gc.clearRect(xCoordinate, yCoordinate, width, height);
        if (highlighted) {
            gc.setFill(Color.ORANGE);
        } else if (isDummy) {
            gc.strokeLine(xCoordinate, yCoordinate + height / 2,
                    xCoordinate + width, yCoordinate + height / 2);
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
        return (xEvent > xCoordinate && xEvent < xCoordinate + width
                && yEvent > yCoordinate && yEvent < yCoordinate + height);
    }


    /**
     * Check if a click event is within the borders of this node.
     *
     * @param xEvent x coordinate of the click event
     * @return True if the coordinates of the click event are within borders, false otherwise.
     */
    public boolean checkClickX(double xEvent) {
        return (xEvent > xCoordinate && xEvent < xCoordinate + width);
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

    void addChild(Integer id) {
        if (!this.children.contains(id)) {
            this.children.add(id);
        }
    }

    void removeChild(Integer id) {
        this.children.remove(id);
    }

    public ArrayList<Integer> getParents() {
        return parents;
    }

    void addParent(Integer id) {
        if (!this.parents.contains(id)) {
            this.parents.add(id);
        }
    }

    boolean hasChildren() {
        return children.size() > 0;
    }

    public ArrayList<Integer> getChildren() {
        return this.children;
    }

    public boolean isDummy() {
        return isDummy;
    }

    void setDummy(boolean dummy) {
        isDummy = dummy;
    }

    public int getIndex() {
        return this.index;
    }

    void setIndex(int index) {
        this.index = index;
    }

    public int getColumn() {
        return column;
    }

    void setColumn(int col) {
        this.column = col;
    }

    void incrementColumn(int i) {
        if (this.column < i + 1) {
            column = i + 1;
        }
    }

    /**
     * method to resolve the baryCenterValue.
     * @return - returns the barycenterValue / inDegree
     */
    float getBaryCenterValue() {
        return baryCenterValue / inDegree;
    }


    void incrementBaryCenterValue(float baryCenterValue) {
        this.baryCenterValue += baryCenterValue;
    }


    void incrementInDegree() {
        this.inDegree++;
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    public void setSequenceLength(int sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    public String toString(String sequence) {
        String str = "Node ID: " + this.id + "\n"
                + "Column index: " + this.column + "\n"
                + "Children: ";
        for (Integer i : children) {
            str += i.toString() + ", ";
        }
        str = str.substring(0, str.length() - 2) + "\n" + "Parents: ";
        for (Integer i : parents) {
            str += i.toString() + ", ";
        }
        str = str.substring(0, str.length() - 2) +  "\n"
                + "SequenceLength: ";
        if (isDummy) {
            str += "-\n" + "Sequence: -";
        } else {
            str += this.sequenceLength + "\n" + "Sequence: " + sequence;
        }
        return str;
    }

}
