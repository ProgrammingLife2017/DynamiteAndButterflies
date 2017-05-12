package gui;

import graph.DummyNode;
import graph.Node;
import graph.SequenceGraph;
import graph.SequenceNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

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

    private ArrayList<ArrayList<Node>> columns;

    /**
     * Constructor.
     * @param graph The sequencegraph to be drawn to the canvas.
     * @param gc The graphics context used to actually draw shapes.
     */
    GraphDrawer(final SequenceGraph graph, final GraphicsContext gc) {
        this.gc = gc;
        xSize = 1;
        graph.initialize();
        graph.layerizeGraph();
        columns = graph.getColumnList();
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
            ArrayList<Node> column = columns.get(j);
            for (int i = 0; i < column.size(); i++) {
                if (column.get(i) instanceof DummyNode) {
                    gc.setFill(Color.BLACK);
                }
                gc.fillRoundRect((j - xDifference) * ((gc.getCanvas().getWidth() - 20) / zoomLevel),
                                Y_BASE + (i * 50), 0.9 * ((gc.getCanvas().getWidth() - 20) / zoomLevel), Y_SIZE,
                                10, 10);

                gc.setFill(Color.BLUE);
            }
        }
    }

    /**
     * Gets the real Centre Column.
     * @return the Centre Column.
     */
    private ArrayList<Node> getCentreColumn() {
        return columns.get(columns.size() / 2);
    }

    /**
     * Returns the First SequenceNode (not Dummy) Object from the centre Column.
     * @return The Centre Node.
     */
    SequenceNode getRealCentreNode() {
        ArrayList<Node> set = getCentreColumn();
        for (Node test : set) {
            if (test instanceof SequenceNode) {
                return (SequenceNode) test;
            }
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
        for (ArrayList<Node> list : columns) {
            for (Node node : list) {
                if (node instanceof SequenceNode) {
                    if (((SequenceNode) node).getId() == nodeId) {
                        return node.getColumn();
                    }
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

