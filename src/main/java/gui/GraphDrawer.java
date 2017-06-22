package gui;

import graph.SequenceGraph;
import graph.SequenceNode;
import gui.sub_controllers.ColourController;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import structures.Annotation;

import java.util.*;

/**
 * Created by Jasper van Tilburg on 8-5-2017.
 * <p>
 * Class used to draw shapes on the canvas.
 */
public class GraphDrawer {

    private static GraphDrawer drawer = new GraphDrawer();

    public static final double Y_BASE = 100;
    public static final double RELATIVE_X_DISTANCE = 0.8;
    public static final double RELATIVE_Y_DISTANCE = 50;
    public static final double LINE_WIDTH_FACTOR = 0.1;
    public static final double Y_SIZE_FACTOR = 3;
    public static final double LOG_BASE = 2;
    //MUST BE THE SAME AS IN GFF PARSER
    private static final int BUCKET_SIZE = 20000;

    private Canvas canvas;
    private double zoomLevel;
    private double range;
    private double xDifference;
    private double yDifference;
    private double stepSize;
    private double[] columnWidths;
    private GraphicsContext gc;
    private ArrayList<ArrayList<SequenceNode>> columns;
    private SequenceGraph graph;
    private int highlightedNode;
    private int[] selected = null;
    private ColourController colourController;
    private HashMap<Integer, HashSet<Annotation>> allAnnotations
            = new HashMap<>();
    private SequenceNode mostLeftNode;
    private SequenceNode mostRightNode;
    private HashMap<Integer, double[]> coordinates;
    private HashMap<Integer, double[]> annotationCoordinates;

    private boolean rainbowView = true;

    public static GraphDrawer getInstance() {
        return drawer;
    }


    public void setEmptyCoordinates() {
        this.coordinates = new HashMap<>();
        this.annotationCoordinates = new HashMap<>();
    }

    public HashMap<Integer, double[]> getCoordinates() {
        return this.coordinates;
    }

    public HashMap<Integer, double[]> getAnnotationCoordinates() {
        return this.annotationCoordinates;
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
            for (SequenceNode aColumn : column) {
                if (!aColumn.isDummy()) {
                    double length = computeNodeWidth(aColumn);
                    if (length > max) {
                        max = length;
                    }
                }
            }
            columnWidths[j + 1] = columnWidths[j] + max;
        }
    }

    /**
     * Method to initialize dummyWidths.
     */
    private void initializeDummyWidths() {
        HashMap<Integer, String> genomes;
        for (int j = -1; j > graph.getDummyNodeIDCounter(); j--) {
            genomes = new HashMap<>(DrawableCanvas.getInstance().getAllGenomesReversed());
            SequenceNode node = graph.getNode(j);
            if (node.isDummy()) {
                for (SequenceNode i : columns.get(node.getColumn())) {
                    if (!i.isDummy()) {
                        for (int k : i.getGenomes()) {
                            genomes.remove(k);
                        }
                    }
                }
                int[] genome = new int[genomes.size()];
                int i = 0;
                for (Object o : genomes.entrySet()) {
                    genome[i] = (int) ((Map.Entry) o).getKey();
                    i++;
                }
                int[] parentGenomes = graph.getNodes().get(node.getParents().get(0)).getGenomes();
                int[] childList = getChildrenGenomeList(node);

                ArrayList<Integer> results = new ArrayList<>();
                for (int aResult : genome) {
                    if (contains(parentGenomes, aResult) & contains(childList, aResult)) {
                        results.add(aResult);
                    }
                }
                int[] result = results.stream().mapToInt(q -> q).toArray();
                node.setGenomes(result);
            }
        }
    }

    /**
     * A simple contains method.
     *
     * @param checkSet The set to check if it contains the genome.
     * @param genome   The genome to see if it is in the check set.
     * @return a boolean true if it is in the set or false if it is not.
     */
    private boolean contains(int[] checkSet, int genome) {
        for (int check : checkSet) {
            if (check == genome) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param child Function that returns the genomes of first child that isn't Dummy.
     * @return the genomesList of the child
     */
    private int[] getChildrenGenomeList(SequenceNode child) {
        while (child.isDummy()) {
            child = graph.getNodes().get(child.getChildren().get(0));
        }
        return child.getGenomes();
    }

    private boolean inView(double[] coordinates) {
        return coordinates[0] + coordinates[2] > 0 && coordinates[0] < gc.getCanvas().getWidth();
    }

    /**
     * Computes the coordinates for the given node.
     * [x,y,width,height]
     * @param node the node.
     * @return the coordinates.
     */
    private double[] computeCoordinates(SequenceNode node) {
        double[] coordinates = new double[4];
        double width = computeNodeWidth(node) * stepSize * RELATIVE_X_DISTANCE;
        double height = getYSize();
        double x = (columnWidths[node.getColumn()] - xDifference) * stepSize;
        double y = Y_BASE + (node.getIndex() * RELATIVE_Y_DISTANCE) - yDifference;
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



    public HashSet<Annotation> getAnnotationBuckets(SequenceNode node, int annotatedGenomeIndex) {
        HashSet<Annotation> anno = new HashSet<>();
        if (annotatedGenomeIndex == -1) {
            return anno;
        }
        int startBucket = (node.getOffsets()[annotatedGenomeIndex] / BUCKET_SIZE);
        int endBucket = ((node.getOffsets()[annotatedGenomeIndex] + node.getSequenceLength()) / BUCKET_SIZE);
        for (int i = startBucket; i <= endBucket; i++) {
            HashSet<Annotation> tempAnnotations = allAnnotations.get(i);
            if (tempAnnotations != null) {
                anno.addAll(tempAnnotations);
            }
        }
        return anno;
    }


    public int getAnnotatedGenomeIndex(SequenceNode node) {
        int indexOfGenome = colourController.containsPos(node.getGenomes(), DrawableCanvas.getInstance().getAnnotationGenome());
        if (indexOfGenome == -1) {
            return indexOfGenome;
        }
        int annotatedGenomeIndex = 0;
        if (node.getGenomes().length == node.getOffsets().length) {
            annotatedGenomeIndex = indexOfGenome;
        }
        return annotatedGenomeIndex;
    }

    private boolean isMyAnnnotation(int startXAnnotation, int endXAnnotation, int startXNode, int endXNode) {

        return !(startXAnnotation > endXNode || endXAnnotation <= startXNode);
    }

    private void drawAnnotations(SequenceNode node, double[] coordinates) {
        int annotatedGenomeIndex = getAnnotatedGenomeIndex(node);
        HashSet<Annotation> annotations = getAnnotationBuckets(node, annotatedGenomeIndex);


        double annoHeight = coordinates[3] / 2;
        double startYAnno = coordinates[1] + coordinates[3] - annoHeight;

        for (Annotation annotation : annotations) {
            if (annotation.getSelected().getValue()) {
                double startXAnno = coordinates[0];
                double annoWidth = coordinates[2];
                int startOfAnno = annotation.getStart();
                int endOfAnno = annotation.getEnd();

                int startCorNode = node.getOffsets()[annotatedGenomeIndex];
                int endCorNode = startCorNode + node.getSequenceLength();

                if (!isMyAnnnotation(startOfAnno, endOfAnno, startCorNode, endCorNode)) {
                    continue;
                }

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

                    gc.setFill(colourController.getAnnotationColor(startOfAnno, BUCKET_SIZE));
//                    //TODO Fix overalapping parent annotations
//                    if (annotation.getInfo().toLowerCase().contains("parent")) {
//                        gc.fillRect(startXAnno, coordinates[1] + coordinates[3] + annoHeight,
//                                annoWidth, annoHeight);
//                    } else {
                    startYAnno += annoHeight;

                double[] annoCoordinates = new double[4];
                annoCoordinates[0] = startXAnno;
                annoCoordinates[1] = startYAnno;
                annoCoordinates[2] = annoWidth;
                annoCoordinates[3] = annoHeight;
                this.getAnnotationCoordinates().put(annotation.getId(), annoCoordinates);

                gc.fillRect(startXAnno, startYAnno, annoWidth, annoHeight);
                    //}
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
            gc.setStroke(Color.BLACK);
            gc.strokeRect(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
        }

        colourMeBby = colourController.getNodeColours(node.getGenomes());
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

            ArrayList<Integer> allGenomesInEdge = new ArrayList<>();
            for (int genomeID : parent.getGenomes()) {
                if (contains(child.getGenomes(), genomeID)) {
                    allGenomesInEdge.add(genomeID);
                }
            }
            ArrayList<Color> colourMeBby =
                    colourController.getEdgeColours(allGenomesInEdge.stream().mapToInt(q -> q).toArray());
            if (colourMeBby.size() < 4) {
                colourMeBby.addAll(colourMeBby);
            }

            if (edgeInView(startx, endx)) {
                double tempStartX = startx;
                double tempStartY = starty;
                double dashSizeX = (endx - startx) / (double) colourMeBby.size();
                double dashSizeY = (endy - starty) / (double) colourMeBby.size();
                double tempEndX = startx + dashSizeX;
                double tempEndY = starty + dashSizeY;

                for (int i = 0; i < colourMeBby.size(); i++) {
                    gc.setStroke(colourMeBby.get(i));
                    gc.strokeLine(tempStartX, tempStartY, tempEndX, tempEndY);
                    tempStartX = tempEndX;
                    tempStartY = tempEndY;
                    tempEndX += dashSizeX;
                    tempEndY += dashSizeY;
                }
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
                    break;
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
                    int column = graph.getNode(nodeID).getColumn();
                    for (SequenceNode displayNode: graph.getColumns().get(column)) {
                        if (!displayNode.isDummy()) {
                            return displayNode.getId();
                        }
                    }
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

    public void setAllAnnotations(HashMap<Integer, HashSet<Annotation>> newAnnotations) {
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

    public HashMap<Integer, HashSet<Annotation>> getAllAnnotations() {
        return allAnnotations;
    }

    public SequenceNode getMostRightNode() {
        return mostRightNode;
    }

    public void setyDifference(double yDifference) {
        this.yDifference = yDifference;
    }

    public void setRainbowView(boolean rainbowView) {
        this.rainbowView = rainbowView;
        this.colourController = new ColourController(selected, rainbowView);
    }
}

