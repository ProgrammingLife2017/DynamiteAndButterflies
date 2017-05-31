package gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by Jasper van Tilburg on 17-5-2017.
 * <p>
 * A drawable version of SequenceNode. This class includes all attributes needed for displaying a node including
 * coordinates, height, width, whether it is a dummy node and whether it is a highlighted node.
 */
public class DrawableNode {

    private static final int ARC_SIZE = 10;
    private static final double RELATIVE_X_DISTANCE = 0.8;

    private static GraphicsContext gc;
    private int id;
    private double xCoordinate;
    private double yCoordinate;
    private double width;
    private double height;
    private boolean highlighted;
    private boolean isDummy;

    public DrawableNode(int id, GraphicsContext gc) {
        this.gc = gc;
        this.id = id;
    }

    public DrawableNode(int id, GraphicsContext gc, boolean isDummy) {
        this.gc = gc;
        this.id = id;
        this.isDummy = isDummy;
    }

    /**
     * Set the coordinates, width and height of the node.
     *
     * @param x      x coordinate
     * @param y      y coordinate
     * @param width  Width
     * @param height Height
     */
    public void setCoordinates(double x, double y, double width, double height) {
        this.xCoordinate = x;
        this.yCoordinate = y;
        this.width = width * RELATIVE_X_DISTANCE;
        this.height = height;
    }

    public int getId() {
        return this.id;
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

    public boolean isDummy() {
        return isDummy;
    }

    public void setDummy(boolean dummy) {
        isDummy = dummy;
    }

    /**
     * Draw the node highlighted.
     */
    public void highlight() {
        this.highlighted = true;
        draw();
    }

    /**
     * Draw the node lowlighted.
     */
    public void lowlight() {
        this.highlighted = false;
        draw();
    }

    /**
     * Draw the node with the color depending on it's status. Orange for highlighted nodes, black for dummy nodes and
     * blue for sequence nodes.
     */
    public void draw() {
        if (highlighted) {
            gc.setFill(Color.ORANGE);
        } else if (isDummy) {
            gc.setFill(Color.BLACK);
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

    public boolean checkClick(double xEvent) {
        return (xEvent > xCoordinate && xEvent < xCoordinate + (width / RELATIVE_X_DISTANCE));
    }

}
