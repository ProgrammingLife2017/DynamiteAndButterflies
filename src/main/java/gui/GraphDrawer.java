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

    private static final double RELATIVE_X_DISTANCE = 0.8;
    private static final double RELATIVE_Y_DISTANCE = 50;
    private static final double LINE_WIDTH_FACTOR = 0.2;
    private static final double Y_SIZE_FACTOR = 4;
    private static final double MIN_LINE_WIDTH = 0.01;
    private static final double MAX_LINE_WIDTH = 1;
    private static final double MAX_Y_SIZE = 20;

    private int yBase;
    private double zoomLevel;
    private double xDifference;
    private double stepSize;
    private int[] columnWidths;
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
        initializeDrawableNodes();
        graph.initialize();
        graph.layerizeGraph();
        columns = graph.getColumns();
        columnWidths = new int[columns.size() + 1];
        initializeColumnWidths();
        zoomLevel = columnWidths[columns.size()];
    }

    private void initializeDrawableNodes() {
        for (int i = 1; i <= graph.size(); i++) {
            canvasNodes.add(new DrawableNode(i, gc));
        }
    }

    /**
     * Function what to do on Zoom.
     * @param factor Zooming factor.
     * @param column The Column that has to be in the centre.
     */
    public void zoom(final double factor, final int column) {
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

    public void initializeColumnWidths() {
        for (int j = 0; j < columns.size(); j++) {
            ArrayList<SequenceNode> column = columns.get(j);
            int max = 1;
            for (int i = 0; i < column.size(); i++) {
                if (!column.get(i).isDummy()) {
                    int length = column.get(i).getSequenceLength();
                    if (length > 100) {
                        max = 100;
                    } else if (length > max) {
                        max = length;
                    }
                }
            }
            columnWidths[j + 1] = columnWidths[j] + max;
        }
    }

    private void drawNodes() {
        for (int j = 0; j < columns.size(); j++) {
            ArrayList<SequenceNode> column = columns.get(j);
            for (int i = 0; i < column.size(); i++) {
                if (column.get(i).isDummy()) {
                    double width = (columnWidths[j + 1] - columnWidths[j]) * stepSize * RELATIVE_X_DISTANCE;
                    double height = getYSize();
                    double x = (columnWidths[j] - xDifference) * stepSize;
                    double y = yBase + (i * RELATIVE_Y_DISTANCE);
                    if (height > width) {
                        y += (height - width) / 2;
                        height = width;
                    }
                    DrawableNode node = canvasNodes.get(column.get(i).getId() - 1);
                    node.setCoordinates(x, y, width, height);
                    node.draw();
                }
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
            SequenceNode node = nodes.get(i);
            DrawableNode parent = canvasNodes.get(node.getId() - 1);
            for (int j = 0; j < nodes.get(i).getChildren().size(); j++) {
                DrawableNode child = canvasNodes.get(graph.getNode(node.getChild(j)).getId() - 1);
                double startx = parent.getxCoordinate() + parent.getWidth();
                double starty = parent.getyCoordinate() + (parent.getHeight() / 2);
                double endx = child.getxCoordinate();
                double endy = child.getyCoordinate() + (child.getHeight() / 2);
                gc.strokeLine(startx, starty, endx, endy);
            }
        }
    }

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
        double size = stepSize * Y_SIZE_FACTOR;
        if (size < 1) {
            size = 1;
        }
        if (size > MAX_Y_SIZE) {
            size = MAX_Y_SIZE;
        }
        return size;
    }

    /**
     * Set the width of the line depending on the level of zoom.
     */
    private void setLineWidth() {
        double width = stepSize * LINE_WIDTH_FACTOR;
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

    public int mouseLocationColumn(double x) {
        return (int) ((x/stepSize) + xDifference);
    }
}

