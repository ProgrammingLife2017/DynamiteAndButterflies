package gui;

import graph.SequenceGraph;
import graph.SequenceNode;
import gui.sub_controllers.ColourController;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
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

    public static final double RELATIVE_Y_DISTANCE = 50;
    private static final double Y_BASE = 150;
    private static final double RELATIVE_X_DISTANCE = 0.8;
    private static final double LINE_WIDTH_FACTOR = 0.1;
    private static final double LOG_BASE = 2;
    private static final double SNP_SIZE = 10;
    private static final double MIN_HEIGHT = 5;
    private static final int LINE_WIDTH = 5;
    private static final int X_INDEX = 0;
    private static final int Y_INDEX = 1;
    private static final int WIDTH_INDEX = 2;
    private static final int HEIGHT_INDEX = 3;
    private static final int COORDINATES = 4;
    private static final int POLYGON_POINTS = 3;
    //MUST BE THE SAME AS IN GFF PARSER
    private static final int BUCKET_SIZE = 20000;

    private MenuController menuController;
    private ColourController colourController;
    private Canvas canvas;
    private GraphicsContext gc;
    private SequenceGraph graph;
    private int highlightedNode;
    private int[] selected = null;
    private double zoomLevel;
    private double range;
    private double xDifference;
    private double yDifference;
    private double stepSize;
    private double[] columnWidths;
    private boolean rainbowView = true;
    private SequenceNode mostLeftNode;
    private SequenceNode mostRightNode;
    private ArrayList<ArrayList<SequenceNode>> columns;
    private HashMap<Integer, double[]> coordinates;
    private HashMap<Integer, HashSet<Annotation>> allAnnotations
            = new HashMap<>();

    /**
     * Getter for the singleton Graphdrawer.
     *
     * @return this graphdrawer
     */
    public static GraphDrawer getInstance() {
        return drawer;
    }

    /**
     * initialize the coordinates hashmap.
     */
    private void setEmptyCoordinates() {
        this.coordinates = new HashMap<>();
    }

    /**
     * Initialize a new graph. All values are recomputed except zoomLevel and selected.
     *
     * @param graph The new graph
     */
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
        if (mostLeftNode == null) {
            mostLeftNode = graph.getNode(graph.getLeftBoundID());
        }
        colourController = new ColourController(selected, rainbowView);
        highlightedNode = 0;
    }

    /**
     * Setter for the canvas and it's graphics context.
     *
     * @param canvas The new canvas
     */
    void setCanvas(Canvas canvas) {
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
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        this.stepSize = (canvas.getWidth() / zoomLevel);
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
            checkSNPBubble(j);
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
     * @param child Function that returns the genomes of first child that isn't Dummy.
     * @return the genomesList of the child
     */
    private int[] getChildrenGenomeList(SequenceNode child) {
        while (child.isDummy()) {
            child = graph.getNodes().get(child.getChildren().get(0));
        }
        return child.getGenomes();
    }

    /**
     * Check if the given coordinates fall on the screen.
     *
     * @param coordinates Coordinates to check
     * @return True if the coordinates fall on the screen, false otherwise
     */
    private boolean inView(double[] coordinates) {
        return coordinates[X_INDEX] + coordinates[WIDTH_INDEX] > 0 && coordinates[X_INDEX] < canvas.getWidth();
    }

    /**
     * Computes the coordinates for the given node.
     * [x,y,width,height]
     *
     * @param node the node.
     * @return the coordinates.
     */
    private double[] computeCoordinates(SequenceNode node) {
        double[] coordinates = new double[COORDINATES];
        double width = computeNodeWidth(node) * stepSize;
        if (relativeWidth(node)) {
            width *= RELATIVE_X_DISTANCE;
        }
        double height = getYSize(node);
        double x = (columnWidths[node.getColumn()] - xDifference) * stepSize;
        double y = Y_BASE + (node.getIndex() * RELATIVE_Y_DISTANCE) - yDifference - (height / 2);
        if (height > width) {
            y += (height - width) / 2;
            height = width;
        }

        coordinates[X_INDEX] = x;
        coordinates[Y_INDEX] = y;
        coordinates[WIDTH_INDEX] = width;
        coordinates[HEIGHT_INDEX] = height;

        this.coordinates.put(node.getId(), coordinates);
        return coordinates;

    }

    /**
     * Check if a node should have a width relative to the following node. The node with is relative when it is not a
     * SNP node and it's children isn't either.
     *
     * @param node Node to check the relative width
     * @return True if the width should be relative to its child, false otherwise
     */
    private boolean relativeWidth(SequenceNode node) {
        if (node.isSNP() && node.isCollapsed()) {
            return false;
        }
        if (!node.getChildren().isEmpty()) {
            SequenceNode child = graph.getNode(node.getChild(0));
            if (child.isSNP() && child.isCollapsed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gives all nodes the right coordinates on the canvas and draws them.
     */
    private void drawNodes() {
        setEmptyCoordinates();
        gc.setStroke(Color.BLACK);
        Iterator it = graph.getNodes().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SequenceNode node = (SequenceNode) pair.getValue();
            computeCoordinates(node);
            if (!node.isCollapsed()) {
                drawNode(node);
                drawEdges(node);
            } else if (node.getIndex() == 0) {
                SequenceNode neighbour = findSNPNeighbour(node);
                drawSNPBubble(node, neighbour);
            }
        }
    }

    /**
     * Draws annotations underneath the give node.
     *
     * @param node        Node to draw annotations under
     * @param coordinates Coordinates of the node
     */
    private void drawAnnotations(SequenceNode node, double[] coordinates) {
        int indexOfGenome = colourController.containsPos(node.getGenomes(), DrawableCanvas.getInstance().getAnnotationGenome());

        if (indexOfGenome != -1) {
            int placeOfAnnotatedGenome = 0;
            if (node.getGenomes().length == node.getOffsets().length) {
                placeOfAnnotatedGenome = indexOfGenome;
            }
            int startBucket = (node.getOffsets()[placeOfAnnotatedGenome] / BUCKET_SIZE);
            int endBucket = ((node.getOffsets()[placeOfAnnotatedGenome] + node.getSequenceLength()) / BUCKET_SIZE);
            HashSet<Annotation> drawThese = new HashSet<>();
            for (int i = startBucket; i <= endBucket; i++) {
                HashSet<Annotation> tempAnnotations = allAnnotations.get(i);
                if (tempAnnotations != null) {
                    drawThese.addAll(tempAnnotations);
                }
            }
            int[] bigOne = new int[2];

            double annoHeight = coordinates[HEIGHT_INDEX] / 2;
            double startYAnno = coordinates[Y_INDEX] + coordinates[HEIGHT_INDEX] - annoHeight;

            for (Annotation annotation : drawThese) {
//                double annoHeight = coordinates[HEIGHT_INDEX] / 2;
//                double startYAnno = coordinates[Y_INDEX] + coordinates[HEIGHT_INDEX];
                if (annotation.getSelected().getValue()) {
                    double startXAnno = coordinates[X_INDEX];
                    double annoWidth = coordinates[WIDTH_INDEX];
                    int startOfAnno = annotation.getStart();
                    int endOfAnno = annotation.getEnd();

                    int startCorNode = node.getOffsets()[placeOfAnnotatedGenome];
                    int endCorNode = startCorNode + node.getSequenceLength();

                    if (startOfAnno > endCorNode || endOfAnno <= startCorNode) {
                        continue;
                    }
                    if (bigOne[0] != startOfAnno && bigOne[1] != endOfAnno) {

                        double emptyAtStart = 0.0;
                        if (startOfAnno > startCorNode) {
                            emptyAtStart = startOfAnno - startCorNode;
                            annoWidth = (annoWidth * (1 - (emptyAtStart / node.getSequenceLength())));
                            startXAnno = startXAnno + (coordinates[WIDTH_INDEX] - annoWidth);
                        }
                        if (endOfAnno < endCorNode) {
                            int emptyAtEnd = endCorNode - endOfAnno;
                            annoWidth = (annoWidth
                                    * (1 - (emptyAtEnd / (node.getSequenceLength() - emptyAtStart))));
                        }
                        if (bigOne[0] == 0) {
                            bigOne[0] = startOfAnno;
                            bigOne[1] = endOfAnno;
                        }
                        gc.setFill(colourController.getAnnotationColor(startOfAnno, BUCKET_SIZE));
//                    //TODO Fix overalapping parent annotations
//                    if (annotation.getInfo().toLowerCase().contains("parent")) {
//                        gc.fillRect(startXAnno, coordinates[Y_INDEX] + coordinates[HEIGHT_INDEX] + annoHeight,
//                                annoWidth, annoHeight);
//                    } else {
                        startYAnno += annoHeight;
                        gc.fillRect(startXAnno, startYAnno, annoWidth, annoHeight);
                        //}
                    }
                }
            }
        }
    }

    /**
     * Check if a column can be collapsed into a SNP bubble. If so set the nodes isSNP attribute to true.
     *
     * @param columnID The column to check for a SNP bubble
     */
    private void checkSNPBubble(int columnID) {
        ArrayList<SequenceNode> column = columns.get(columnID);
        if (column.size() != 2) {
            return;
        }
        SequenceNode upperNode = column.get(0);
        SequenceNode lowerNode = column.get(1);
        if (upperNode.isDummy() || lowerNode.isDummy()) {
            return;
        }
        if (upperNode.getSequenceLength() != 1 || lowerNode.getSequenceLength() != 1) {
            return;
        }
        if (upperNode.getParents().size() != 1 || lowerNode.getParents().size() != 1) {
            return;
        }
        if (upperNode.getChildren().size() != 1 || lowerNode.getChildren().size() != 1) {
            return;
        }
        int upperParent = upperNode.getParents().get(0);
        int lowerParent = lowerNode.getParents().get(0);
        if (upperParent != lowerParent) {
            return;
        }
        int upperChild = upperNode.getChildren().get(0);
        int lowerChild = upperNode.getChildren().get(0);
        if (upperChild != lowerChild) {
            return;
        }
        upperNode.setSNP(true);
        upperNode.setCollapsed(true);
        lowerNode.setSNP(true);
        lowerNode.setCollapsed(true);
    }

    /**
     * Check if a node is the left most or right most node of the screen. Set the attribute to it if so.
     *
     * @param node The node to check if it is an extreme node
     */
    private void checkExtremeNode(SequenceNode node) {
        if (!node.isDummy()) {
            double[] coordinates = this.coordinates.get(node.getId());
            if (coordinates[X_INDEX] <= 0 && coordinates[X_INDEX] + coordinates[WIDTH_INDEX] / RELATIVE_X_DISTANCE > 0) {
                mostLeftNode = node;
            }
            if (coordinates[X_INDEX] < canvas.getWidth() && coordinates[X_INDEX] + coordinates[WIDTH_INDEX] / RELATIVE_X_DISTANCE >= canvas.getWidth()) {
                mostRightNode = node;
            }
        }
    }

    /**
     * Draw an individual node with possibly its annotations if it is in view of the screen. Dummies are drawn as a line.
     *
     * @param node The node to be drawn
     */
    private void drawNode(SequenceNode node) {
        double[] coordinates = this.coordinates.get(node.getId());
        checkExtremeNode(node);
        if (inView(coordinates)) {
            if (node.isDummy()) {
                this.setLineWidth(node.getGenomes().length);
                gc.strokeLine(coordinates[X_INDEX], coordinates[Y_INDEX] + coordinates[HEIGHT_INDEX] / 2,
                        coordinates[X_INDEX] + coordinates[WIDTH_INDEX], coordinates[Y_INDEX] + coordinates[HEIGHT_INDEX] / 2);
            } else {
                drawColour(node, coordinates);
                drawAnnotations(node, coordinates);
            }
        }
    }

    /**
     * Draw an individual SNP bubble. It is drawn as diamond with the upper node as upper half and lower node as lower
     * half of the bubble.
     *
     * @param upperNode Upper node in the SNP bubble
     * @param lowerNode Lower node in the SNP bubble
     */
    private void drawSNPBubble(SequenceNode upperNode, SequenceNode lowerNode) {
        double[] upperCoordinates = this.coordinates.get(upperNode.getId());
        coordinates.put(lowerNode.getId(), upperCoordinates);
        if (inView(upperCoordinates)) {
            double leftX = upperCoordinates[X_INDEX];
            double midX = leftX + upperCoordinates[WIDTH_INDEX] / 2;
            double rightX = leftX + upperCoordinates[WIDTH_INDEX];
            double midY = upperCoordinates[Y_INDEX] + upperCoordinates[HEIGHT_INDEX] / 2;
            double upY = midY - upperCoordinates[HEIGHT_INDEX];
            double downY = midY + upperCoordinates[HEIGHT_INDEX];

            if (upperNode.isHighlighted()) {
                gc.setLineWidth(LINE_WIDTH);
                gc.strokePolygon(new double[]{leftX, midX, rightX}, new double[]{midY, upY, midY}, POLYGON_POINTS);
                gc.strokePolygon(new double[]{leftX, midX, rightX}, new double[]{midY, downY, midY}, POLYGON_POINTS);
            }
            gc.setFill(Color.CHOCOLATE);
            gc.fillPolygon(new double[]{leftX, midX, rightX}, new double[]{midY, upY, midY}, POLYGON_POINTS);
            gc.setFill(Color.BROWN);
            gc.fillPolygon(new double[]{leftX, midX, rightX}, new double[]{midY, downY, midY}, POLYGON_POINTS);
        }
    }

    /**
     * Draw the individual node in the right colour.
     * @param node Node to be drawn
     * @param coordinates Coordinates of the node
     */
    private void drawColour(SequenceNode node, double[] coordinates) {
        ArrayList<Color> colourMeBby;
        if (node.isHighlighted()) {
            gc.setLineWidth(LINE_WIDTH);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(coordinates[X_INDEX], coordinates[Y_INDEX], coordinates[WIDTH_INDEX], coordinates[HEIGHT_INDEX]);
        }
        colourMeBby = colourController.getNodeColours(node.getGenomes());
        double tempCoordinate = coordinates[Y_INDEX];
        double tempHeight = coordinates[HEIGHT_INDEX] / colourMeBby.size();
        for (Color beamColour : colourMeBby) {
            gc.setFill(beamColour);
            gc.fillRect(coordinates[X_INDEX], tempCoordinate, coordinates[WIDTH_INDEX], tempHeight);
            tempCoordinate += tempHeight;
        }
    }

    /**
     * Draw the outgoing edges of the node if the node is not in a SNP bubble. The edges are coloured by the genomes
     * that pass through the edge.
     *
     * @param node Node to draw the outgoing edges of
     */
    private void drawEdges(SequenceNode node) {
        int nodeID = node.getId();
        SequenceNode parent = graph.getNode(nodeID);
        double[] coordinatesParent = this.coordinates.get(node.getId());
        for (int j = 0; j < parent.getChildren().size(); j++) {
            SequenceNode child = graph.getNode(parent.getChild(j));
            if (!node.isCollapsed() && !child.isCollapsed()) {
                double[] coordinatesChild = computeCoordinates(child);
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
    }

    /**
     * Draw the minimap on screen.
     */
    private void drawMinimap() {
        Minimap.getInstance().setValue(mostLeftNode.getId());
        Minimap.getInstance().setAmountVisible(mostRightNode.getId() - mostLeftNode.getId());
        Minimap.getInstance().draw(gc);
    }

    /**
     * Check if an edge falls on screen.
     * @param startx Start x-coordinate of the edge
     * @param endx End x-coordinate of the edge
     *
     * @return True if the edge falls on screen, false otherwise
     */
    private boolean edgeInView(double startx, double endx) {
        return startx < canvas.getWidth() && endx > 0;
    }

    /**
     * Compute the width of a node depending on its sequencelength. Dummies are as wide as the column and SNP bubbles
     * have a fixed size.
     *
     * @param node Node to compute the width of
     * @return The width of the node
     */
    private double computeNodeWidth(SequenceNode node) {
        if (node.isCollapsed()) {
            return SNP_SIZE;
        }
        if (node.isDummy()) {
            return columnWidths[node.getColumn() + 1] - columnWidths[node.getColumn()];
        }
        return Math.log(node.getSequenceLength() + (LOG_BASE - 1)) / Math.log(LOG_BASE);
    }

    /**
     * Check for each node if the click event is within its borders. If so it is highlighted. Control-click gives node
     * information in the second information pane. If a SNP bubble is clicked information of both nodes is displayed.
     * On double click a SNP bubble is collapsed/extended.
     *
     * @param xEvent The x coordinate of the click event.
     * @param yEvent The y coordinate of the click event.
     * @param mouseEvent The click event.
     */
    void clickNode(double xEvent, double yEvent, MouseEvent mouseEvent) {
        try {
            Iterator it = graph.getNodes().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                SequenceNode node = (SequenceNode) pair.getValue();
                if (checkClick(node, xEvent, yEvent)) {
                    highlight(node.getId());
                    if (node.isSNP()) {
                        SequenceNode neighbour = findSNPNeighbour(node);
                        menuController.updateSequenceInfo(node);
                        if (node.isCollapsed()) {
                            menuController.updateSequenceInfoAlt(neighbour);
                        }
                        if (mouseEvent.getClickCount() == 2) {
                            if (!node.isCollapsed()) {
                                neighbour.setCollapsed(!neighbour.isCollapsed());
                            }
                            node.setCollapsed(!node.isCollapsed());
                        }
                    } else if (mouseEvent.isControlDown()) {
                        menuController.updateSequenceInfoAlt(node);
                    } else {
                        menuController.updateSequenceInfo(node);
                        menuController.updateNodeTextField(node.getId());
                    }
                }
            }
            redraw();
        } catch (NullPointerException e) {
            System.out.println("Graph not yet initialized");
        }
    }

    /**
     * Find the neighbouring node in a SNP bubble.
     * @param node The node to find the neighbour of.
     * @return The neighbour node.
     */
    private SequenceNode findSNPNeighbour(SequenceNode node) {
        ArrayList<SequenceNode> column = columns.get(node.getColumn());
        int nodeID = node.getId();
        int columnNodeID = column.get(0).getId();
        if (nodeID == columnNodeID) {
            return column.get(1);
        } else {
            return column.get(0);
        }
    }

    /**
     * Check if a click event is within the borders of this node or SNP bubble.
     *
     * @param xEvent x coordinate of the click event
     * @param yEvent y coordinate of the click event
     * @return True if the coordinates of the click event are within borders, false otherwise.
     */
    private boolean checkClick(SequenceNode node, double xEvent, double yEvent) {
        double[] coordinates = this.coordinates.get(node.getId());
        if (node.isSNP()) {
            return (xEvent > coordinates[X_INDEX] && xEvent < coordinates[X_INDEX] + coordinates[WIDTH_INDEX]
                    && yEvent > coordinates[Y_INDEX] + coordinates[HEIGHT_INDEX] / 2 - coordinates[HEIGHT_INDEX]
                    && yEvent < coordinates[Y_INDEX] + coordinates[HEIGHT_INDEX] / 2 + coordinates[HEIGHT_INDEX]);
        } else {
            return (xEvent > coordinates[X_INDEX] && xEvent < coordinates[X_INDEX] + coordinates[WIDTH_INDEX]
                    && yEvent > coordinates[Y_INDEX] && yEvent < coordinates[Y_INDEX] + coordinates[HEIGHT_INDEX]);
        }
    }

    /**
     * Find the column corresponding to the x coordinate.
     *
     * @param xEvent x coordinate of the click event.
     * @return The column id of the column the x coordinate is in.
     */
    int findColumn(double xEvent) {
        try {
            Iterator it = graph.getNodes().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                SequenceNode node = (SequenceNode) pair.getValue();
                int nodeID = (Integer) pair.getKey();
                if (checkClickX(node, xEvent)) {
                    int column = graph.getNode(nodeID).getColumn();
                    for (SequenceNode displayNode : graph.getColumns().get(column)) {
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
     * @param node Node to check for the click event.
     * @param xEvent x coordinate of the click event.
     * @return True if the coordinates of the click event are within borders, false otherwise.
     */
    private boolean checkClickX(SequenceNode node, double xEvent) {
        double[] coordinates = this.coordinates.get(node.getId());
        return (xEvent > coordinates[X_INDEX] && xEvent < coordinates[X_INDEX] + coordinates[WIDTH_INDEX]);
    }

    /**
     * Set the height of the node depending on the level of zoom.
     */
    private double getYSize(SequenceNode node) {
        if (node.isSNP()) { return SNP_SIZE * stepSize / 2; }
        double zoomHeight = Math.log(stepSize + 1) / Math.log(LOG_BASE);
        double relativeSize = 100 * (node.getGenomes().length / (double) DrawableCanvas.getInstance().getAllGenomes().size());
        double genomeWidth = Math.log(relativeSize + 1) / Math.log(LOG_BASE);
        return Math.max(genomeWidth * zoomHeight, MIN_HEIGHT);
//        return Math.log(stepSize + 1) / Math.log(LOG_BASE) * Y_SIZE_FACTOR;
    }

    /**
     * Set the width of the line depending on the level of zoom.
     */
    private void setLineWidth(double thickness) {
        double zoomWidth = Math.log(stepSize + 1) / Math.log(LOG_BASE) * LINE_WIDTH_FACTOR;
        double relativeSize = 100 * (thickness / (double) DrawableCanvas.getInstance().getAllGenomes().size());
        double genomeWidth = Math.log(relativeSize + 1) / Math.log(LOG_BASE);
        gc.setLineWidth(genomeWidth * zoomWidth);
    }

    /**
     * Highlight the given node and lowlight the previous highlighted node.
     *
     * @param node The node that should be highlighted
     */
    public void highlight(int node) {
        if (highlightedNode != 0) {
            graph.getNode(highlightedNode).lowlight();
        }
        graph.getNode(node).highlight();
        highlightedNode = node;
    }

    /**
     * Find the zoomLevel corresponding to a given centre node and a radius.
     * @param centreNode Centre node around which the radius is set.
     * @param radius Radius that is converted to a zoomLevel.
     * @return The zoomLevel.
     */
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
     * Toggle all SNP nodes isCollapsed attributes.
     * @param collapse If true set all SNP bubbles to collapsed, set collapsed to false otherwise.
     */
    void collapse(boolean collapse) {
        Iterator it = graph.getNodes().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SequenceNode node = (SequenceNode) pair.getValue();
            if (node.isSNP()) {
                node.setCollapsed(collapse);
            }
        }
    }

    public void setRainbowView(boolean rainbowView) {
        this.rainbowView = rainbowView;
        this.colourController = new ColourController(selected, rainbowView);
    }

    void setSelected(int[] newSelection) {
        this.selected = newSelection;
        this.colourController = new ColourController(selected, rainbowView);
    }

    /**
     * Setter for the xDifference. Minimum is 0, maximum is the range of the graph minus the zoomLevel.
     * @param xDifference The new xDifference.
     */
    public void setxDifference(double xDifference) {
        if (xDifference < 0) {
            xDifference = 0;
        }
        if (xDifference + zoomLevel > range) {
            xDifference = range - zoomLevel;
        }
        this.xDifference = xDifference;
    }

    /**
     * Setter for the zoomLevel. Minimum is 1, maximum is half the range of the graph.
     * @param zoomLevel The new zoomLevel.
     */
    public void setZoomLevel(double zoomLevel) {
        if (zoomLevel < 1) {
            zoomLevel = 1;
        }
        if (zoomLevel > range / 2) {
            zoomLevel = range / 2;
        }
        this.zoomLevel = zoomLevel;
    }

    /**
     * Getter for the radius. The radius is computed by the difference between the left most node and the right most
     * node. So the radius is the number of nodes on screen.
     * @return The radius of the screen.
     */
    public int getRadius() {
        return mostRightNode.getId() - mostLeftNode.getId();
    }

    /**
     * Find the column where the mouse pointer is located on.
     * @param x The x-coordinate of the mouse event.
     * @return The column that is clicked on.
     */
    public int mouseLocationColumn(double x) {
        return (int) ((x / stepSize) + xDifference);
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    public double getxDifference() {
        return xDifference;
    }

    public double getColumnWidth(int col) {
        return columnWidths[col];
    }

    public int[] getSelected() {
        return selected;
    }

    public HashMap<Integer, HashSet<Annotation>> getAllAnnotations() {
        return allAnnotations;
    }

    public void setAllAnnotations(HashMap<Integer, HashSet<Annotation>> newAnnotations) {
        this.allAnnotations = newAnnotations;
    }

    public SequenceGraph getGraph() {
        return graph;
    }

    public SequenceNode getMostLeftNode() {
        return mostLeftNode;
    }

    public SequenceNode getMostRightNode() {
        return mostRightNode;
    }

    public void setyDifference(double yDifference) {
        this.yDifference = yDifference;
    }

    void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }
}

