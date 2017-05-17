package gui;

import graph.Edge;
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
public final class GraphDrawer {

    /**
     * Height of drawn nodes.
     */
    private static final int Y_SIZE = 5;

    /**
     * Base line for Nodes.
     */
    private static final int Y_BASE = 100;

    private static final double RELATIVE_DISTANCE = 0.8;

    /**
     * The number of Columns to be displayed in the Canvas.
     */
    private int zoomLevel;

    /**
     * The size of the nodes.
     */
    private double xSize;

    /**
     * The graphics context used to actually draw shapes.
     */
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
        xSize = 1;
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
        this.xSize += 0.1;
        moveShapes(column - zoomLevel / 2);
    }

    /**
     * Function what to do on ZoomOut.
     * @param factor Zooming factor.
     * @param column The column that has to be in the centre.
     */
    public void zoomOut(final double factor, final int column) {
        this.zoomLevel = (int) (zoomLevel * factor);
        this.xSize -= 0.1;
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
                double stepSize = ((gc.getCanvas().getWidth() - 20) / zoomLevel);
                gc.fillRoundRect((j - xDifference) * stepSize,
                                Y_BASE + (i * 50), RELATIVE_DISTANCE * stepSize, Y_SIZE,
                                10, 10);

                gc.setFill(Color.BLUE);
            }
        }
        drawEdges(xDifference);
    }

    public void drawEdges(double xDifference) {
        HashMap<Integer, SequenceNode> nodes = graph.getNodes();
        for (int i = 1; i <= nodes.size(); i++) {
            SequenceNode parent = nodes.get(i);
            for (int j = 0; j < parent.getChildren().size(); j++) {
                SequenceNode child = graph.getNode(parent.getChild(j));
                double stepSize = ((gc.getCanvas().getWidth() - 20) / zoomLevel);
                double startx = (parent.getColumn() - xDifference) * stepSize + RELATIVE_DISTANCE * stepSize;
                double starty = Y_BASE + (parent.getIndex() * 50);
                double endx = (child.getColumn() - xDifference) * stepSize;
                double endy = Y_BASE + (child.getIndex() * 50);
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
    public int getZoomLevel(){
        return zoomLevel;
    }
}

