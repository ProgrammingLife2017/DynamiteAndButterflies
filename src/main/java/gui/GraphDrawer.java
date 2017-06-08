package gui;

import graph.SequenceGraph;
import graph.SequenceNode;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jasper van Tilburg on 8-5-2017.
 * <p>
 * Class used to draw shapes on the canvas.
 */
public class GraphDrawer {

    private static final double RELATIVE_X_DISTANCE = 0.8;
    private static final double RELATIVE_Y_DISTANCE = 50;
    private static final double LINE_WIDTH_FACTOR = 4;
    private static final double Y_SIZE_FACTOR = 3;
    private static final double LOG_BASE = 2;

    private int yBase;
    private double zoomLevel;
    private double radius;
    private double xDifference;
    private double stepSize;
    private double[] columnWidths;
    private GraphicsContext gc;
    private ArrayList<ArrayList<SequenceNode>> columns;
    private SequenceGraph graph;
    private int highlightedNode;
    private int[] selected;

    /**
     * Constructor.
     *
     * @param graph The sequencegraph to be drawn to the canvas.
     * @param gc    The graphics context used to actually draw shapes.
     */
    public GraphDrawer(final SequenceGraph graph, final GraphicsContext gc) {
        this.gc = gc;
        this.graph = graph;
        this.yBase = (int) (gc.getCanvas().getHeight() / 4); //TODO explain magic number
        columns = graph.getColumns();
        columnWidths = new double[columns.size() + 1];
        initializeColumnWidths();
        zoomLevel = columnWidths[columns.size()];
        radius = columns.size();
        selected = new int[0];
    }

    /**
     * Function what to do on Zoom.
     *
     * @param factor Zooming factor.
     * @param column The Column that has to be in the centre.
     */
    public void zoom(final double factor, final int column) {
        if ((factor < 1 && radius < 1) || (factor > 1 && radius >= columns.size())) {
            return;
        }
        this.zoomLevel = zoomLevel * factor;
        this.radius = radius * factor;
        moveShapes(column - ((column - xDifference) * factor));
    }

    /**
     * Change the zoom (invoked by user by clicking on "Go to this Node".
     *
     * @param radius The new radius.
     * @param column  The new Column to be in the centre.
     */
    public void changeZoom(int column, int radius) {
        this.radius = radius + radius + 1;
        zoomLevel = columnWidths[column + radius + 1] - columnWidths[column - radius];
        moveShapes(columnWidths[column - radius]);
    }

    /**
     * Redraw all nodes with the same coordinates.
     */
    public void redraw() {
        moveShapes(xDifference);
    }

    /**
     * Draws the Graph.
     *
     * @param xDifference Variable to determine which column should be in the centre.
     */
    public void moveShapes(double xDifference) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        this.xDifference = xDifference;
        this.stepSize = (gc.getCanvas().getWidth() / zoomLevel);
        setLineWidth();
        drawNodes();
        drawEdges();
    }

    /**
     * Gives all nodes the right coordinates on the canvas and draw them.
     * It depends on whether the dummy nodes checkbox
     * is checked dummy nodes are either drawn or skipped.
     */
    private void drawNodes() {
        Iterator it = graph.getNodes().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SequenceNode node = (SequenceNode) pair.getValue();
            double width = computeNodeWidth(node) * stepSize * RELATIVE_X_DISTANCE;
            double height = getYSize();
            double x = (columnWidths[node.getColumn()] - xDifference) * stepSize;
            double y = yBase + (node.getIndex() * RELATIVE_Y_DISTANCE);
            if (height > width) {
                y += (height - width) / 2;
                height = width;
            }
            node.setCoordinates(x, y, width, height);
            node.draw(gc, selected);
        }
    }

    /**
     * Initializes the widths of each column.
     * Using the widest node of each column.
     */
    public void initializeColumnWidths() {
        for (int j = 0; j < columns.size(); j++) {
            ArrayList<SequenceNode> column = columns.get(j);
            double max = 1;
            for (int i = 0; i < column.size(); i++) {
                if (!column.get(i).isDummy()) {
                    double length = computeNodeWidth(column.get(i));
                    if (length > max) {
                        max = length;
                    }
                }
            }
            columnWidths[j + 1] = columnWidths[j] + max;
        }
    }

    private void drawEdges() {
        Iterator it = graph.getNodes().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int nodeID = (Integer) pair.getKey();
            SequenceNode parent = graph.getNode(nodeID);
            for (int j = 0; j < parent.getChildren().size(); j++) {
                SequenceNode child = graph.getNode(parent.getChild(j));
                double startx = parent.getxCoordinate() + parent.getWidth();
                double starty = parent.getyCoordinate() + (parent.getHeight() / 2);
                double endx = child.getxCoordinate();
                double endy = child.getyCoordinate() + (child.getHeight() / 2);
                gc.setLineWidth(Math.log(child.getGenomes().length)
                                / Math.log(LOG_BASE + 1.3));
                gc.strokeLine(startx, starty, endx, endy);
            }
        }
    }

    public double computeNodeWidth(SequenceNode node) {
        if (node.isDummy()) {
            return columnWidths[node.getColumn() + 1] - columnWidths[node.getColumn()];
        }
        return Math.log(node.getSequenceLength() + (LOG_BASE - 1)) / Math.log(LOG_BASE);
    }

    /**
     * Check for each node if the click event is within its borders.
     * If so highlight the node and return it. Also all other nodes are lowlighted.
     *
     * @param xEvent The x coordinate of the click event.
     * @param yEvent The y coordinate of the click event.
     * @return The sequencenode that has been clicked or null if nothing was clicked.
     */
    public SequenceNode clickNode(double xEvent, double yEvent) {
        SequenceNode click = null;
        Iterator it = graph.getNodes().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SequenceNode node = (SequenceNode) pair.getValue();
            if (node.checkClick(xEvent, yEvent)) {
                click = graph.getNode(node.getId());
                highlight(node.getId());
            }
        }
        return click;
    }

    /**
     * Find the column corresponding to the x coordinate.
     * @param xEvent x coordinate of the click event.
     * @return The column id of the column the x coordinate is in.
     */
    public int findColumn(double xEvent) {
        Iterator it = graph.getNodes().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SequenceNode node = (SequenceNode) pair.getValue();
            int nodeID = (Integer) pair.getKey();
            if (graph.getNode(nodeID).checkClickX(xEvent)) {
                return graph.getNode(nodeID).getId();
            }
        }
        return -1;
    }

    /**
     * Set the height of the node depending on the level of zoom.
     */
    private double getYSize() {
        return Math.log(stepSize + 1) / Math.log(LOG_BASE) * Y_SIZE_FACTOR;
    }

    /**
     * Set the width of the line depending on the level of zoom.
     */
    private void setLineWidth() {
        double width = (Math.log(stepSize + 1) / Math.log(LOG_BASE)) / LINE_WIDTH_FACTOR;
        gc.setLineWidth(width);
    }

    /**
     * Gets the real Centre Column.
     *
     * @return the Centre Column.
     */
    private ArrayList<SequenceNode> getCentreColumn() {
        return columns.get(columns.size() / 2);
    }

    /**
     * Returns the First SequenceNode (not Dummy) Object from the centre Column.
     *
     * @return The Centre Node.
     */
    public SequenceNode getRealCentreNode() {
        ArrayList<SequenceNode> set = getCentreColumn();
        for (SequenceNode test : set) {
            return test;
        }
        return null;
    }

    /**
     * Highlights the centre node.
     *
     * @param node The node that should be highlighted
     */
    public void highlight(int node) {
        if (highlightedNode != 0) {
            graph.getNode(highlightedNode).lowlight();
            graph.getNode(highlightedNode).draw(gc, selected);
        }
        graph.getNode(node).highlight();
        graph.getNode(node).draw(gc, selected);
        highlightedNode = node;
    }

    //TODO: Loop over the  nodes in the graph (O(n*m) > O(k))

    /**
     * Returns the ColumnId of a Node at the users Choice.
     *
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
     *
     * @return the Zoom level.
     */
    public double getZoomLevel() {
        return zoomLevel;
    }

    /**
     * Get function for the radius.
     *
     * @return the double representing the radius.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Get function for x difference.
     * @return The x difference.
     */
    public double getxDifference() {
        return xDifference;
    }

    public double getColumnWidth(int col) {
        return columnWidths[col];
    }

    /**
     * Return the column the mouse click is in.
     *
     * @param x The x coordinate of the mouse click event
     * @return The id of the column that the mouse click is in.
     */
    public int mouseLocationColumn(double x) {
        return (int) ((x / stepSize) + xDifference);
    }

    public void setSelected(int[] newSelection) {
        this.selected = newSelection;
    }

    public int[] getSelected() {
        return selected;
    }
}

