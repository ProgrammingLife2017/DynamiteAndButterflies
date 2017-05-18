package gui;

import graph.SequenceNode;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import graph.SequenceGraph;
import javafx.scene.paint.Color;

import java.util.HashMap;

/**
 * Created by Jasper van Tilburg on 8-5-2017.
 *
 * Class used to draw shapes on the canvas.
 */
public class GraphDrawer {

    private static final int ARC_SIZE = 10;
    private static final int X_OFFSET = 20;
    private static final double RELATIVE_X_DISTANCE = 0.8;
    private static final double RELATIVE_Y_DISTANCE = 50;
    private static final double LINE_WIDTH_FACTOR = 0.01;
    private static final double MIN_LINE_WIDTH = 0.01;
    private static final double MAX_LINE_WIDTH = 1;

    private int yBase;
    private int zoomLevel;
    private GraphicsContext gc;
    private ArrayList<ArrayList<SequenceNode>> columns;
    private SequenceGraph graph;

    /**
     * Constructor.
     * @param graph The sequencegraph to be drawn to the canvas.
     * @param gc The graphics context used to actually draw shapes.
     */
    public GraphDrawer(final SequenceGraph graph, final GraphicsContext gc) {
        this.gc = gc;
        this.graph = graph;
        this.yBase = (int) (gc.getCanvas().getHeight() / 4);
        graph.initialize();
        graph.layerizeGraph();
        columns = graph.getColumns();
        zoomLevel = columns.size();
    }

    /**
     * Function what to do on ZoomIn.
     * @param factor Zooming factor.
     * @param column The Column that has to be in the centre.
     */
    public void zoomIn(final double factor, final int column) {
        this.zoomLevel = (int) (zoomLevel * factor);
        moveShapes(column - zoomLevel / 2);
    }

    /**
     * Function what to do on ZoomOut.
     * @param factor Zooming factor.
     * @param column The column that has to be in the centre.
     */
    public void zoomOut(final double factor, final int column) {
        this.zoomLevel = (int) (zoomLevel * factor);
        moveShapes(column - zoomLevel / 2);
    }

    /**
     * Change the zoom (invoked by user by clicking on "Go to this Node".
     * @param newZoom The new radius.
     * @param column The new Column to be in the centre.
     */
    public void changeZoom(final int newZoom, final int column) {
        zoomLevel = newZoom;
        moveShapes(column - zoomLevel / 2);
    }

    /**
     * Draws the Graph.
     * @param xDifference Variable to determine which column should be in the centre.
     */
    public void moveShapes(final double xDifference) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setFill(Color.BLUE);

        for (int j = 0; j < columns.size(); j++) {
            ArrayList<SequenceNode> column = columns.get(j);
            for (int i = 0; i < column.size(); i++) {
                if (column.get(i).isDummy()) {
                    gc.setFill(Color.BLACK);
                }
                double stepSize = ((gc.getCanvas().getWidth() - X_OFFSET) / zoomLevel);
                double x = (j - xDifference) * stepSize;
                double y = yBase + (i * RELATIVE_Y_DISTANCE);
                gc.fillRoundRect(x, y, RELATIVE_X_DISTANCE * stepSize, getYSize(), ARC_SIZE, ARC_SIZE);
                gc.setFill(Color.BLUE);
            }
        }
        drawEdges(xDifference);
    }

    /**
     * Draws the edges.
     * @param xDifference Variable to determine which column should be in the centre.
     */
    private void drawEdges(double xDifference) {
        setLineWidth();
        HashMap<Integer, SequenceNode> nodes = graph.getNodes();
        for (int i = 1; i <= nodes.size(); i++) {
            SequenceNode parent = nodes.get(i);
            for (int j = 0; j < parent.getChildren().size(); j++) {
                SequenceNode child = graph.getNode(parent.getChild(j));
                double stepSize = ((gc.getCanvas().getWidth() - X_OFFSET) / zoomLevel);
                double startx = stepSize * ((parent.getColumn() - xDifference) + RELATIVE_X_DISTANCE);
                double starty = yBase + (parent.getIndex() * RELATIVE_Y_DISTANCE) + getYSize() / 2;
                double endx = (child.getColumn() - xDifference) * stepSize;
                double endy = yBase + (child.getIndex() * RELATIVE_Y_DISTANCE) + getYSize() / 2;
                gc.strokeLine(startx, starty, endx, endy);
            }
        }
    }

    /**
     * Set the height of the node depending on the level of zoom.
     */
    private double getYSize() {
        double size = ((gc.getCanvas().getWidth() - X_OFFSET) / zoomLevel) * 0.2;
        if (size < 1) {
            size = 1;
        }
        if (size > 20) {
            size = 20;
        }
        return size;
    }

    /**
     * Set the width of the line depending on the level of zoom.
     */
    private void setLineWidth() {
        double width = ((gc.getCanvas().getWidth() - X_OFFSET) / zoomLevel) * LINE_WIDTH_FACTOR;
        if (width == 0) { width = MIN_LINE_WIDTH; }
        if (width > 1) { width = MAX_LINE_WIDTH; }
        gc.setLineWidth(width);
    }

    /**
     * Gets the real Centre Column.
     * @return the Centre Column.
     */
    private ArrayList<SequenceNode> getCentreColumn() {
        return columns.get(columns.size() / 2);
    }

    /**
     * Returns the First SequenceNode (not Dummy) Object from the centre Column.
     * @return The Centre Node.
     */
    public SequenceNode getRealCentreNode() {
        ArrayList<SequenceNode> set = getCentreColumn();
        for (SequenceNode test : set) {
            return test;
        }
        return null;
    }

    //TODO: Loop over the  nodes in the graph (O(n*m) > O(k))
    /**
     * Returns the ColumnId of a Node at the users Choice.
     * @param nodeId The Id of the Node you want to find the Column of.
     * @return The ColumnId
     */
    public int getColumnId(final int nodeId) {
        for (ArrayList<SequenceNode> list : columns) {
            for (SequenceNode node : list) {
                if (node.getId() == nodeId) {
                    return node.getColumn();
                }
            }
        }
        return -1;
    }

    /**
     * Get function for zoom level.
     * @return the Zoom level.
     */
    public int getZoomLevel() {
        return zoomLevel;
    }
}

