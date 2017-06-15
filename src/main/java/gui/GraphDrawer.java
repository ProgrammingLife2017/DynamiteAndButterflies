package gui;

import graph.Annotation;
import graph.SequenceGraph;
import graph.SequenceNode;
import gui.sub_controllers.ColourController;
import javafx.scene.canvas.Canvas;
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

    private static GraphDrawer drawer = new GraphDrawer();

    private static final double RELATIVE_X_DISTANCE = 0.8;
    private static final double RELATIVE_Y_DISTANCE = 50;
    private static final double LINE_WIDTH_FACTOR = 0.1;
    private static final double Y_SIZE_FACTOR = 3;
    private static final double LOG_BASE = 2;

    private Canvas canvas;
    private int yBase;
    private double zoomLevel;
    private double radius;
    private double range;
    private double xDifference;
    private double stepSize;
    private double[] columnWidths;
    private GraphicsContext gc;
    private ArrayList<ArrayList<SequenceNode>> columns;
    private SequenceGraph graph;
    private int highlightedNode;
    private int[] selected = null;
    private ColourController colourController;
    private ArrayList<Annotation> allAnnotations;
    private ArrayList<Annotation> selectedAnnotations;
    private SequenceNode mostLeftNode;
    private SequenceNode mostRightNode;

    public static GraphDrawer getInstance(){
        return drawer;
    }

    public void setGraph(SequenceGraph graph) {
        this.graph = graph;
        columns = graph.getColumns();
        columnWidths = new double[columns.size() + 1];
        initializeColumnWidths();
        range = columnWidths[columns.size()];
        radius = columns.size();
        if (zoomLevel == 0) {
            setZoomLevel(columnWidths[columns.size()]);
        }
        if (selected == null) {
            selected = new int[0];
        }
        if (mostRightNode == null) {
            mostRightNode = graph.getNode(graph.getRightBoundID());
        }
        colourController = new ColourController(selected);
        highlightedNode = 0;

        allAnnotations = new ArrayList<Annotation>();
        selectedAnnotations = new ArrayList<Annotation>();
    }


    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

//        MAGIC NUMBER
        this.yBase = (int) (canvas.getHeight() / 4);

    }

    /**
     * Function what to do on Zoom.
     *
     * @param factor Zooming factor.
     * @param column The Column that has to be in the centre.
     */
    public void zoom(final double factor, final int column) {
        setZoomLevel(zoomLevel * factor);
        setRadius(radius * factor);
        if (zoomLevel != range / 2) {
            moveShapes(column - ((column - xDifference) * factor));
        }
    }

    /**
     * Change the zoom (invoked by user by clicking on "Go to this Node".
     *
     * @param radius The new radius.
     * @param column  The new Column to be in the centre.
     */
    public void changeZoom(int column, int radius) {
        setRadius(radius);
        int widthRight = column + radius + 1;
        int widthLeft = column - radius;

        if (column + radius + 1 > columnWidths.length-1 ) {
            widthRight = columnWidths.length - 1;
        }
        if (column - radius < 0) {
            widthLeft = 0;
        }

        setZoomLevel(columnWidths[widthRight] - columnWidths[widthLeft]);
        moveShapes(columnWidths[widthLeft]);
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
        this.stepSize = (gc.getCanvas().getWidth() / zoomLevel);
        setxDifference(xDifference);
        colourController = new ColourController(selected);
        drawNodes();
        drawEdges();
    }

    /**
     * Initializes the widths of each column.
     * Using the widest node of each column.
     */

    private void initializeColumnWidths() {
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

            yBase = (int) GraphDrawer.getInstance().canvas.getHeight() / 4;
            double y = yBase + (node.getIndex() * RELATIVE_Y_DISTANCE);
            if (height > width) {
                y += (height - width) / 2;
                height = width;
            }
            node.setCoordinates(x, y, width, height);
            setExtremeNodes(node);
            node.draw(gc, colourController, selectedAnnotations);
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
                setLineWidth(Math.min(child.getGenomes().length, parent.getGenomes().length));
                if (edgeInView(startx, endx)) {
                    gc.strokeLine(startx, starty, endx, endy);
                }
            }
        }
    }

    public void setExtremeNodes(SequenceNode node) {
        if (node.getxCoordinate() <= 0 && node.getxCoordinate() + node.getWidth() > 0) { mostLeftNode = node; }
        if (node.getxCoordinate() < gc.getCanvas().getWidth() && node.getxCoordinate() + node.getWidth() >= gc.getCanvas().getWidth()) { mostRightNode = node; }
    }

    public boolean edgeInView(double startx, double endx) {
        return startx < gc.getCanvas().getWidth() && endx > 0;
    }

    private double computeNodeWidth(SequenceNode node) {
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
    public void setLineWidth(double thickness) {
        double zoomWidth = Math.log(stepSize + 1) / Math.log(LOG_BASE) * LINE_WIDTH_FACTOR;
        double relativeSize = 100 * (thickness / (double) DrawableCanvas.getInstance().getAllGenomes().size());
        double genomeWidth = Math.log(relativeSize + 1) / Math.log(LOG_BASE);
        gc.setLineWidth(genomeWidth * zoomWidth);
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
            graph.getNode(highlightedNode).draw(gc, colourController, selectedAnnotations);
        }
        graph.getNode(node).highlight();
        graph.getNode(node).draw(gc, colourController, selectedAnnotations);
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

    public double getRange() {
        return range;
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

    public double getRightbound() { return xDifference + zoomLevel; }

    public double getLeftbound() { return xDifference; }

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
        this.colourController = new ColourController(selected);
    }

    public int[] getSelected() {
        return selected;
    }

    public void setSelectedAnnotations(ArrayList<Annotation> newAnnotations) {
        this.selectedAnnotations = newAnnotations;
    }

    public void setAllAnnotations(ArrayList<Annotation> newAnnotations) {
        this.allAnnotations = newAnnotations;
    }

    public void setxDifference(double xDifference) {
        if (xDifference < 0) { xDifference = 0; }
        if (xDifference + zoomLevel > range) { xDifference = range - zoomLevel; }
        this.xDifference = xDifference;
    }

    public void setRadius(double radius) {
        if (radius < 1) { radius = 1; }
        if (radius > range) { radius = range; }
        this.radius = radius;
    }

    public void setZoomLevel(double zoomLevel) {
        if (zoomLevel < 1) { zoomLevel = 1; }
        if (zoomLevel > range / 2) { zoomLevel = range / 2; }
        this.zoomLevel = zoomLevel;
    }

    public SequenceGraph getGraph() {
        return graph;
    }


    public SequenceNode getMostLeftNode() {
        return mostLeftNode;
    }

    public ArrayList<Annotation> getAllAnnotations() {
        return allAnnotations;
    }

    public ArrayList<Annotation> getSelectedAnnotations() {
        return selectedAnnotations;
    }

    public SequenceNode getMostRightNode() {
        return mostRightNode;
    }
}

