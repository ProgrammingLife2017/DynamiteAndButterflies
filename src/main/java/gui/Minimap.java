package gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Created by Jasper van Tilburg on 15-6-2017.
 * The minimap class controls a small bar in the screen the shows where in the DNA sequence you are.
 */
public class Minimap {

    private static final int MINIMAP_Y = 700;
    private static final int MINIMAP_HEIGHT = 20;
    private static final int TEXT_Y = 740;
    private static final int TEXT_END_Y = 695;
    private static final int TEXT_SIZE = 10;
    private static final int DIVISION_LINE_HEIGHT = 715;
    private static final int CHAR_WIDTH = 3;

    private static Minimap minimap = new Minimap();

    private int size;
    private int stepSize;
    private double amountVisible;
    private double width;
    private double value;

    private Minimap() {

    }

    /**
     * Getter for the singleton Minimap.
     * @return the minimap
     */
    public static Minimap getInstance() {
        return minimap;
    }

    /**
     * Initialize the minimap.
     * @param sizeVal Size of the whole graph in number of nodes
     */
    public void initialize(int sizeVal) {
        value = 0;
        amountVisible = 0;
        size = sizeVal;
        stepSize = computeDivisions();
        width = Math.log10(size) * 100;
    }

    /**
     * Draw the minimap on screen.
     * @param gc The GraphicsContext object needed to draw the minimap
     */
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.setFont(new Font("Arial", TEXT_SIZE));

        drawMapBox(gc);
        drawDivisionLines(gc);
        drawViewBox(gc);
    }

    /**
     * Draw the small box that represents the sequence.
     * @param gc The GraphicsContext object needed to draw the minimap
     */
    private void drawMapBox(GraphicsContext gc) {
        double x = gc.getCanvas().getWidth() / 2 - width / 2;
        gc.strokeRect(x, MINIMAP_Y, width, MINIMAP_HEIGHT);
        gc.strokeText("0", x, TEXT_Y);
        gc.strokeText(size + "", x + width - Integer.toString(size).length() * CHAR_WIDTH, TEXT_END_Y);
    }

    /**
     * Draw the interval lines.
     * @param gc The GraphicsContext object needed to draw the minimap
     */
    private void drawDivisionLines(GraphicsContext gc) {
        double x = gc.getCanvas().getWidth() / 2 - width / 2;
        for (int i = stepSize; i < size; i += stepSize) {
            double division = x + valueToXCoordinate(i);
            gc.strokeLine(division, DIVISION_LINE_HEIGHT, division, MINIMAP_Y + MINIMAP_HEIGHT);
            gc.strokeText(i + "", division - Integer.toString(i).length() * CHAR_WIDTH, TEXT_Y);
        }
    }

    /**
     * Draw the box that represents the part of the sequence that is in view of the screen.
     * @param gc The GraphicsContext object needed to draw the minimap
     */
    private void drawViewBox(GraphicsContext gc) {
        double x = gc.getCanvas().getWidth() / 2 - width / 2;
        gc.setStroke(Color.RED);
        gc.strokeRect(x + valueToXCoordinate(value), MINIMAP_Y, valueToXCoordinate(amountVisible), MINIMAP_HEIGHT);
    }

    /**
     * Convert a node to it's corresponding x coordinate in the minimap.
     * @param value Node id
     * @return X coordinate of the value
     */
    private double valueToXCoordinate(double value) {
        return (value / (double) size) * width;
    }

    /**
     * Compute the stepsize of the intervals.
     * @return Interval stepsize
     */
    private int computeDivisions() {
        String sizeStr = Integer.toString(size);
        int firstDigit = Integer.parseInt(sizeStr.substring(0, 1));
        int step = firstDigit >= 5 ? 1 : 5;
        int zeros = sizeStr.substring(1, sizeStr.length() - 1).length();
        return (int) (step * Math.pow(10, zeros));
    }

    /**
     * Setter for the amount of nodes visible.
     * @param amountVisible Amount of nodes visible
     */
    public void setAmountVisible(double amountVisible) {
        this.amountVisible = amountVisible;
    }

    /**
     * Setter for the value of the first node in screen, i.e. left side of the screen.
     * @param value ID of the first node
     */
    public void setValue(double value) {
        this.value = value;
    }
}
