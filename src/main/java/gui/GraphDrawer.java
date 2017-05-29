package gui;

import graph.SequenceGraph;
import graph.SequenceNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jasper van Tilburg on 8-5-2017.
 *
 * Class used to draw shapes on the canvas.
 */
public class GraphDrawer {

    private static final int X_OFFSET = 20;
    private static final double RELATIVE_X_DISTANCE = 0.8;
    private static final double RELATIVE_Y_DISTANCE = 50;
    private static final double LINE_WIDTH_FACTOR = 0.01;
    private static final double MIN_LINE_WIDTH = 0.01;
    private static final double MAX_LINE_WIDTH = 1;

    private int yBase;
    private double zoomLevel;
    private double xDifference;
    private double stepSize;
    private boolean showDummyNodes;
    private GraphicsContext gc;
    private ArrayList<ArrayList<SequenceNode>> columns;
    private SequenceGraph graph;
    private ArrayList<DrawableNode> canvasNodes;

    /**
     * Constructor.
     * @param graph The sequencegraph to be drawn to the canvas.
     * @param gc The graphics context used to actually draw shapes.
     */
    public GraphDrawer(final SequenceGraph graph, final GraphicsContext gc) {
        this.gc = gc;
        this.graph = graph;
        this.yBase = (int) (gc.getCanvas().getHeight() / 4);
        canvasNodes = new ArrayList<DrawableNode>();
        graph.layerizeGraph(1);
        columns = graph.getColumns();
        zoomLevel = columns.size();
        initializeDrawableNodes();
    }

    private void initializeDrawableNodes() {
        for (int i = 0; i < columns.size(); i++) {
            ArrayList<SequenceNode> column = columns.get(i);
            for (int j = 0; j < column.size(); j++) {
                SequenceNode sNode = column.get(j);
                DrawableNode dNode = new DrawableNode(sNode.getId(), gc);
                if (sNode.isDummy()) {
                    dNode.setDummy(true);
                }
                canvasNodes.add(dNode);
            }
        }
    }

    /**
     * Function what to do on Zoom.
     * @param factor Zooming factor.
     * @param column The Column that has to be in the centre.
     */
    public void zoom(final double factor, final int column) {
        if ((factor < 1 && zoomLevel < 2) || (factor > 1 && zoomLevel > columns.size())) {
            return;
        }
        this.zoomLevel = zoomLevel * factor;
        moveShapes(column - ((column - xDifference) * factor));
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
     * Redraw all nodes with the same coordinates.
     */
    public void redraw() {
        moveShapes(xDifference);
    }

    /**
     * Draws the Graph.
     * @param xDifference Variable to determine which column should be in the centre.
     */
    public void moveShapes(double xDifference) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setFill(Color.BLUE);
        this.xDifference = xDifference;
        this.stepSize = (gc.getCanvas().getWidth() / zoomLevel);
        drawNodes();
        drawEdges();
    }

    /**
     * Gives all nodes the right coordinates on the canvas and draw them. Depending on whether the dummy nodes checkbox
     * is checked dummy nodes are either drawn or skipped.
     */
    private void drawNodes() {
        int counter = 0;
        for (int j = 0; j < columns.size(); j++) {
            ArrayList<SequenceNode> column = columns.get(j);
            for (int i = 0; i < column.size(); i++) {
                if (column.get(i).isDummy() && !showDummyNodes) {
                    counter++;
                    continue;
                }
                double x = (j - xDifference) * stepSize;
                double y = yBase + (i * RELATIVE_Y_DISTANCE);
                double width = RELATIVE_X_DISTANCE * stepSize;
                double height = getYSize();
                DrawableNode node = canvasNodes.get(counter);
                node.setCoordinates(x, y, width, height);
                node.draw();
                counter++;
            }
        }
    }

    /**
     * Draws the edges.
     */
    private void drawEdges() {
        setLineWidth();
        HashMap<Integer, SequenceNode> nodes = graph.getNodes();
        for (int i = 1; i <= nodes.size(); i++) {
            SequenceNode parent = nodes.get(i);
            for (int j = 0; j < parent.getChildren().size(); j++) {
                SequenceNode child = graph.getNode(parent.getChild(j));
                double startx = stepSize * ((parent.getColumn() - xDifference) + RELATIVE_X_DISTANCE);
                double starty = yBase + (parent.getIndex() * RELATIVE_Y_DISTANCE) + getYSize() / 2;
                double endx = (child.getColumn() - xDifference) * stepSize;
                double endy = yBase + (child.getIndex() * RELATIVE_Y_DISTANCE) + getYSize() / 2;
                gc.strokeLine(startx, starty, endx, endy);
            }
        }
    }

    /**
     * Check for each node if the click event is within its borders. If so highlight the node and return it. Also all
     * other nodes are lowlighted.
     * @param xEvent The x coordinate of the click event.
     * @param yEvent The y coordinate of the click event.
     * @return The sequencenode that has been clicked or null if nothing was clicked.
     */
    public SequenceNode clickNode(double xEvent, double yEvent) {
        SequenceNode click = null;
        for (int i = 0; i < canvasNodes.size(); i++) {
            DrawableNode node = canvasNodes.get(i);
            node.lowlight();
            if (node.checkClick(xEvent, yEvent)) {
                click = graph.getNode(node.getId());
                node.highlight();
            }
        }
        return click;
    }

    /**
     * Set the height of the node depending on the level of zoom.
     */
    private double getYSize() {
        double size = ((gc.getCanvas().getWidth() - X_OFFSET) / zoomLevel) * 0.3;
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
        if (width == 0) {
            width = MIN_LINE_WIDTH;
        }
        if (width > 1) {
            width = MAX_LINE_WIDTH;
        }
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
    public double getZoomLevel() {
        return zoomLevel;
    }

    public void setShowDummyNodes(boolean showDummyNodes) {
        this.showDummyNodes = showDummyNodes;
    }

    /**
     * Return the column the mouse click is in.
     * @param x The x coordinate of the mouse click event
     * @return The id of the column that the mouse click is in.
     */
    public int mouseLocationColumn(double x) {
        return (int) ((x/stepSize) + xDifference);
    }
}

