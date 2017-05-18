package gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by TUDelft SID on 17-5-2017.
 */
public class DrawableNode {

    private static final int ARC_SIZE = 10;

    private static GraphicsContext gc;
    private int id;
    private double xCoordinate;
    private double yCoordinate;
    private double width;
    private double height;
    private boolean highlighted;

    public DrawableNode (int id, GraphicsContext gc) {
        this.gc = gc;
        this.id = id;
    }

    public void setCoordinates(double x, double y, double width, double height) {
        this.xCoordinate = x;
        this.yCoordinate = y;
        this.width = width;
        this.height = height;
    }

    public int getId() {
        return this.id;
    }

    public void highlight() {
        this.highlighted = true;
        draw();
    }

    public void lowlight() {
        this.highlighted = false;
        draw();
    }

    public void draw() {
        if (highlighted) {
            gc.setFill(Color.ORANGE);
        } else {
            gc.setFill(Color.BLUE);
        }
        gc.fillRoundRect(xCoordinate, yCoordinate, width, height, ARC_SIZE, ARC_SIZE);
    }

    public boolean checkClick(double xEvent, double yEvent) {
        return (xEvent > xCoordinate && xEvent < xCoordinate + width && yEvent > yCoordinate && yEvent < yCoordinate + height);
    }

}
