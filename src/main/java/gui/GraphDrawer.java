package gui;

import graph.SequenceGraph;
import graph.SequenceNode;
import gui.sub_controllers.ColourController;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import structures.Annotation;

import java.util.ArrayList;
import java.util.HashMap;
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
    private ArrayList<Annotation> allAnnotations = new ArrayList<Annotation>();
    private ArrayList<Annotation> selectedAnnotations = new ArrayList<Annotation>();
    private SequenceNode mostLeftNode;
    private SequenceNode mostRightNode;
    private HashMap<Integer, double[]> coordinates;
    private boolean rainbowView = true;
    private HashMap<Integer, ArrayList<Annotation>> smartAnnotations = new HashMap<>();

    public static GraphDrawer getInstance() {
        return drawer;
    }


    public void setEmptyCoordinates() {
        this.coordinates = new HashMap<>();
    }

    public HashMap<Integer, double[]> getCoordinates() {
        return this.coordinates;
    }

    public void setGraph(SequenceGraph graph) {
        this.graph = graph;
        columns = graph.getColumns();
        columnWidths = new double[columns.size() + 1];
        initializeColumnWidths();
        initializeDummyWidths();
        range = columnWidths[columns.size()];
        if (zoomLevel == 0) {
            setZoomLevel(columnWidths[columns.size()]);
        }
        if (selected == null) {
            selected = new int[0];
        }
        if (mostRightNode == null) {
            mostRightNode = graph.getNode(graph.getRightBoundID());
        }
        colourController = new ColourController(selected, rainbowView);
        highlightedNode = 0;
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
        if (zoomLevel != range / 2) {
            moveShapes(column - ((column - xDifference) * factor));
        }
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
        colourController = new ColourController(selected, rainbowView);
        drawNodes();
        drawMinimap();
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

    public void initializeDummyWidths() {
        HashMap<Integer, String> genomes;
        Iterator it = graph.getNodes().entrySet().iterator();
        while (it.hasNext()) {
            genomes = new HashMap<>(DrawableCanvas.getInstance().getAllGenomesReversed());
            Map.Entry pair = (Map.Entry) it.next();
            SequenceNode node = (SequenceNode) pair.getValue();
            if (node.isDummy()) {
                for (SequenceNode i : columns.get(node.getColumn())) {
                    if (!i.isDummy()) {
                        for (int j : i.getGenomes()) {
                            genomes.remove(j);
                        }
                    }
                }
                int[] result = new int[genomes.size()];
                int i = 0;
                Iterator itt = genomes.entrySet().iterator();
                while (itt.hasNext()) {
                    result[i] = (int) ((Map.Entry) itt.next()).getKey();
                    i++;
                }
                node.setGenomes(result);
            }
        }
    }

    public boolean inView(double[] coordinates) {
        return coordinates[0] + coordinates[2] > 0 && coordinates[0] < gc.getCanvas().getWidth();
    }

    /**
     * Computes the coordinates for the given node
     * [x,y,width,height]
     *
     * @param node
     * @return
     */
    private double[] computeCoordinates(SequenceNode node) {
        double[] coordinates = new double[4];
        yBase = (int) GraphDrawer.getInstance().canvas.getHeight() / 4;
        double width = computeNodeWidth(node) * stepSize * RELATIVE_X_DISTANCE;
        double height = getYSize();
        double x = (columnWidths[node.getColumn()] - xDifference) * stepSize;
        double y = yBase + (node.getIndex() * RELATIVE_Y_DISTANCE);
        if (height > width) {
            y += (height - width) / 2;
            height = width;
        }

        coordinates[0] = x;
        coordinates[1] = y;
        coordinates[2] = width;
        coordinates[3] = height;

        return coordinates;

    }

    /**
     * Gives all nodes the right coordinates on the canvas and draw them.
     * It depends on whether the dummy nodes checkbox
     * is checked dummy nodes are either drawn or skipped.
     */
    private void drawNodes() {
        setEmptyCoordinates();
        gc.setStroke(Color.BLACK);
        Iterator it = graph.getNodes().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SequenceNode node = (SequenceNode) pair.getValue();
            checkExtremeNode(node);
            drawNode(node);
            drawEdges(node);
        }
    }

    private void drawAnnotations(SequenceNode node, double[] coordinates) {
        ArrayList<Annotation> drawThese = smartAnnotations.get(node.getId());
        if (drawThese != null) {
            double annoHeight = coordinates[3] / 2;
            double startYAnno = coordinates[1] + coordinates[3] - annoHeight;
            for (int i = 0; i < drawThese.size(); i++) {
                Annotation annotation = drawThese.get(i);
                double startXAnno = coordinates[0];
                double annoWidth = coordinates[2];
                int indexOfGenome = colourController.containsPos(node.getGenomes(), DrawableCanvas.getInstance().getAnnotationGenome());
                int startOfAnno = annotation.getStart();
                int endOfAnno = annotation.getEnd();
                int placeOfAnnotatedGenome = 0;

                if (node.getGenomes().length == node.getOffsets().length) {
                    placeOfAnnotatedGenome = indexOfGenome;
                }
                int startCorNode = node.getOffsets()[placeOfAnnotatedGenome];
                int endCorNode = startCorNode + node.getSequenceLength();

                double emptyAtStart = 0.0;
                if (startOfAnno > startCorNode) {
                    emptyAtStart = startOfAnno - startCorNode;
                    annoWidth = (annoWidth * (1 - (emptyAtStart / node.getSequenceLength())));
                    startXAnno = startXAnno + (coordinates[2] - annoWidth);
                }
                if (endOfAnno < endCorNode) {
                    int emptyAtEnd = endCorNode - endOfAnno;
                    annoWidth = (annoWidth
                            * (1 - (emptyAtEnd / (node.getSequenceLength() - emptyAtStart))));
                }
                gc.setFill(colourController.getAnnotationColor(i));
                startYAnno += annoHeight;
                gc.fillRect(startXAnno, startYAnno, annoWidth, annoHeight);
            }
        }
    }

    private void checkExtremeNode(SequenceNode node) {
        double[] coordinates = getCoordinates(node);
        if (coordinates[0] <= 0 && coordinates[0] + coordinates[2] > 0) {
            mostLeftNode = node;
        }
        if (coordinates[0] < gc.getCanvas().getWidth() && coordinates[0] + coordinates[2] >= gc.getCanvas().getWidth()) {
            mostRightNode = node;
        }
    }

    private void drawNode(SequenceNode node) {
        double[] coordinates = this.getCoordinates().get(node.getId());
        if (coordinates[0] <= 0 && coordinates[0] + coordinates[2] / RELATIVE_X_DISTANCE > 0) {
            mostLeftNode = node;
        }
        if (coordinates[0] <= gc.getCanvas().getWidth() && coordinates[0] + coordinates[2] / RELATIVE_X_DISTANCE >= gc.getCanvas().getWidth()) {
            mostRightNode = node;
        }

        if (inView(coordinates)) {
            if (node.isDummy()) {
                this.setLineWidth(node.getGenomes().length);
                gc.strokeLine(coordinates[0], coordinates[1] + coordinates[3] / 2,
                        coordinates[0] + coordinates[2], coordinates[1] + coordinates[3] / 2);
            } else {
                drawColour(node, coordinates);
                drawAnnotations(node, coordinates);
            }
        }
    }

    private void drawColour(SequenceNode node, double[] coordinates) {
        ArrayList<Color> colourMeBby;
        if (node.isHighlighted()) {
            gc.setLineWidth(6);
            gc.strokeRect(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
        }

        colourMeBby = colourController.getColors(node.getGenomes());
        double tempCoordinate = coordinates[1];
        double tempHeight = coordinates[3] / colourMeBby.size();
        for (Color beamColour : colourMeBby) {
            gc.setFill(beamColour);
            gc.fillRect(coordinates[0], tempCoordinate, coordinates[2], tempHeight);
            tempCoordinate += tempHeight;
        }
    }

    private double[] getCoordinates(SequenceNode node) {
        if (this.getCoordinates().containsKey(node.getId())) {
            return this.getCoordinates().get(node.getId());
        } else {
            this.getCoordinates().put(node.getId(), computeCoordinates(node));
            return this.getCoordinates().get(node.getId());
        }
    }

    private void drawEdges(SequenceNode node) {
        int nodeID = node.getId();
        SequenceNode parent = graph.getNode(nodeID);
        double[] coordinatesParent = getCoordinates(node);
        for (int j = 0; j < parent.getChildren().size(); j++) {
            SequenceNode child = graph.getNode(parent.getChild(j));
            double[] coordinatesChild = getCoordinates(child);
            double startx = coordinatesParent[0] + coordinatesParent[2];
            double starty = coordinatesParent[1] + (coordinatesParent[3] / 2);
            double endx = coordinatesChild[0];
            double endy = coordinatesChild[1] + (coordinatesChild[3] / 2);
            setLineWidth(Math.min(child.getGenomes().length, parent.getGenomes().length));
            if (edgeInView(startx, endx)) {
                gc.strokeLine(startx, starty, endx, endy);
            }
        }
    }

    public void drawMinimap() {
        Minimap.getInstance().setValue(mostLeftNode.getId());
        Minimap.getInstance().setAmountVisible(mostRightNode.getId() - mostLeftNode.getId());
        Minimap.getInstance().draw(gc);
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

        try {
            Iterator it = graph.getNodes().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                SequenceNode node = (SequenceNode) pair.getValue();
                if (checkClick(node, xEvent, yEvent)) {
                    click = graph.getNode(node.getId());
                    highlight(node.getId());
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Graph not yet intialized");
        }
        return click;
    }

    /**
     * Check if a click event is within the borders of this node.
     *
     * @param xEvent x coordinate of the click event
     * @param yEvent y coordinate of the click event
     * @return True if the coordinates of the click event are within borders, false otherwise.
     */
    public boolean checkClick(SequenceNode node, double xEvent, double yEvent) {
        double[] coordinates = getCoordinates(node);
        return (xEvent > coordinates[0] && xEvent < coordinates[0] + coordinates[2] && yEvent > coordinates[1] && yEvent < coordinates[1] + coordinates[3]);
    }

    /**
     * Find the column corresponding to the x coordinate.
     *
     * @param xEvent x coordinate of the click event.
     * @return The column id of the column the x coordinate is in.
     */
    public int findColumn(double xEvent) {

        try {
            Iterator it = graph.getNodes().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                SequenceNode node = (SequenceNode) pair.getValue();
                int nodeID = (Integer) pair.getKey();
                if (checkClickX(node, xEvent)) {
                    return graph.getNode(nodeID).getId();
                }
            }
        } catch (NullPointerException e) {
            System.out.println("No graph has been loaded.");
        }
        return -1;
    }

    /**
     * Check if a click event is within the borders of this node.
     *
     * @param xEvent x coordinate of the click event
     * @return True if the coordinates of the click event are within borders, false otherwise.
     */
    public boolean checkClickX(SequenceNode node, double xEvent) {
        double[] coordinates = getCoordinates(node);
        return (xEvent > coordinates[0] && xEvent < coordinates[0] + coordinates[2]);
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
        }
        graph.getNode(node).highlight();
        highlightedNode = node;
        redraw();
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

    public double findZoomLevel(int centreNode, int radius) {
        int rightNodeID = (int) (centreNode + (double) radius / 2.0);
        int leftNodeID = (int) (centreNode - (double) radius / 2.0);
        if (rightNodeID > graph.getFullGraphRightBoundID()) {
            rightNodeID = graph.getFullGraphRightBoundID();
            mostRightNode = graph.getNode(graph.getFullGraphRightBoundID());
        }
        if (leftNodeID < graph.getFullGraphLeftBoundID()) {
            leftNodeID = graph.getFullGraphLeftBoundID();
            mostLeftNode = graph.getNode(graph.getFullGraphLeftBoundID());
        }
        int rightColumn = graph.getNode(rightNodeID).getColumn();
        int leftColumn = graph.getNode(leftNodeID).getColumn();
        return columnWidths[rightColumn] - columnWidths[leftColumn];
    }

    /**
     * Get function for zoom level.
     *
     * @return the Zoom level.
     */
    public double getZoomLevel() {
        return zoomLevel;
    }

    public double getRange() {
        return range;
    }

    /**
     * Get function for x difference.
     *
     * @return The x difference.
     */
    public double getxDifference() {
        return xDifference;
    }

    public double getColumnWidth(int col) {
        return columnWidths[col];
    }

    public double getRightbound() {
        return xDifference + zoomLevel;
    }

    public double getLeftbound() {
        return xDifference;
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
        this.colourController = new ColourController(selected, rainbowView);
    }

    public int[] getSelected() {
        return selected;
    }

    public void setSelectedAnnotations(ArrayList<Annotation> newAnnotations) {
        this.selectedAnnotations = newAnnotations;
        this.smartAnnotations = intializeAnnotationsOnNodes();
    }

    public void setAllAnnotations(ArrayList<Annotation> newAnnotations) {
        this.allAnnotations = newAnnotations;
    }

    public void setxDifference(double xDifference) {
        if (xDifference < 0) {
            xDifference = 0;
        }
        if (xDifference + zoomLevel > range) {
            xDifference = range - zoomLevel;
        }
        this.xDifference = xDifference;
    }

    public void setZoomLevel(double zoomLevel) {
        if (zoomLevel < 1) {
            zoomLevel = 1;
        }
        if (zoomLevel > range / 2) {
            zoomLevel = range / 2;
        }
        this.zoomLevel = zoomLevel;
    }

    public int getRadius() {
        return mostRightNode.getId() - mostLeftNode.getId();
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

    public void setRainbowView(boolean rainbowView) {
        this.rainbowView = rainbowView;
        this.colourController = new ColourController(selected, rainbowView);
    }

    public HashMap<Integer, ArrayList<Annotation>> intializeAnnotationsOnNodes() {
        HashMap<Integer, ArrayList<Annotation>> res = new HashMap<>();

        Iterator it = graph.getNodes().entrySet().iterator();
        while (it.hasNext()) {
            ArrayList<Annotation> annotationsOnThisNode = new ArrayList<Annotation>();
            Map.Entry pair = (Map.Entry) it.next();
            SequenceNode node = (SequenceNode) pair.getValue();

            if (!node.isDummy()) {
                for (int i = 0; i < selectedAnnotations.size(); i++) {
                    Annotation annotation = selectedAnnotations.get(i);
                    int annoID = DrawableCanvas.getInstance().getAnnotationGenome();
                    int indexOfGenome = colourController.containsPos(node.getGenomes(), annoID);
                    if (indexOfGenome != -1) {
                        int startOfAnno = annotation.getStart();
                        int endOfAnno = annotation.getEnd();
                        int placeOfAnnotatedGenome = 0;

                        if (node.getGenomes().length == node.getOffsets().length) {
                            placeOfAnnotatedGenome = indexOfGenome;
                        }

                        int startCorNode = node.getOffsets()[placeOfAnnotatedGenome];
                        int endCorNode = startCorNode + node.getSequenceLength();

                        if (startOfAnno > endCorNode || endOfAnno <= startCorNode) {
                            continue;
                        }

                        if (startOfAnno >= startCorNode || (startOfAnno < startCorNode && endOfAnno > endCorNode)) {
                            annotationsOnThisNode.add(annotation);
                        }

                        if (endOfAnno < endCorNode) {
                            annotationsOnThisNode.add(annotation);
                            selectedAnnotations.remove(annotation);
                        }
                    }
                }
                if (annotationsOnThisNode.size() != 0) {
                    res.put(node.getId(), annotationsOnThisNode);
                }
            }
        }
        return res;
    }
}

