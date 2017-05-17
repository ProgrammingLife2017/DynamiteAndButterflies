package gui;

import graph.SequenceGraph;
import graph.SequenceNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jasper van Tilburg on 8-5-2017.
 *
 * Class used to draw shapes on the canvas.
 */
public class GraphDrawer {

    private static final int Y_SIZE = 5;
    private static final int Y_BASE = 100;
    private static final int ARC_SIZE = 10;
    private static final int X_OFFSET = 20;
    private static final double RELATIVE_X_DISTANCE = 0.8;
    private static final double RELATIVE_Y_DISTANCE = 50;

    private int zoomLevel;
    private GraphicsContext gc;
    private ArrayList<ArrayList<SequenceNode>> columns;
    private SequenceGraph graph;

    /**
     * Constructor.
     * @param graph The sequencegraph to be drawn to the canvas.
     * @param gc The graphics context used to actually draw shapes.
     */
    GraphDrawer(final SequenceGraph graph, final GraphicsContext gc) {
        this.gc = gc;
        this.graph = graph;
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
    void zoomIn(final double factor, final int column) {
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
    void changeZoom(final int newZoom, final int column) {
        zoomLevel = newZoom;
        moveShapes(column - zoomLevel / 2);
    }

    /**
     * Draws the Graph.
     * @param xDifference Variable to determine which column should be in the centre.
     */
    void moveShapes(final double xDifference) {
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
                double y = Y_BASE + (i * RELATIVE_Y_DISTANCE);
                gc.fillRoundRect(x, y, RELATIVE_X_DISTANCE * stepSize, Y_SIZE, ARC_SIZE, ARC_SIZE);
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
        HashMap<Integer, SequenceNode> nodes = graph.getNodes();
        for (int i = 1; i <= nodes.size(); i++) {
            SequenceNode parent = nodes.get(i);
            for (int j = 0; j < parent.getChildren().size(); j++) {
                SequenceNode child = graph.getNode(parent.getChild(j));
                double stepSize = ((gc.getCanvas().getWidth() - X_OFFSET) / zoomLevel);
                double startx = stepSize * ((parent.getColumn() - xDifference) + RELATIVE_X_DISTANCE);
                double starty = Y_BASE + (parent.getIndex() * RELATIVE_Y_DISTANCE);
                double endx = (child.getColumn() - xDifference) * stepSize;
                double endy = Y_BASE + (child.getIndex() * RELATIVE_Y_DISTANCE);
                gc.strokeLine(startx, starty, endx, endy);
            }
        }
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
    SequenceNode getRealCentreNode() {
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
    int getColumnId(final int nodeId) {
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

