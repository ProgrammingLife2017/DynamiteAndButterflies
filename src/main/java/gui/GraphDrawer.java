package gui;

import graph.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by Jasper van Tilburg on 8-5-2017.
 *
 * Class used to draw shapes on the canvas.
 */
public class GraphDrawer {

    public static final int MIN_X_SIZE = 1;
    public static final int Y_SIZE = 5;
    public static final int EDGE_LENGTH = 30;
    public static final int Y_BASE = 100;

    private int zoomLevel;
    private int xSize;

    private SequenceGraph graph;
    private GraphicsContext gc;

    /**
     * Constructor of the graphDrawer.
     * @param graph Needs a graph to draw
     * @param gc and a GraphicsContext to draw on
     * Constructor
     * @param graph The sequencegraph to be drawn to the canvas.
     * @param gc The graphics context used to actually draw shapes.
     */
    public GraphDrawer(SequenceGraph graph, GraphicsContext gc) {
        this.graph = graph;
        this.gc = gc;
    }

    public void drawShapes() {
        drawNodes();
        drawEdges();
    }

    private void drawNodes() {
        for (int i = 0; i < graph.getNodes().size(); i++) {
            if (graph.getNode(i + 1) instanceof SequenceNode) {
                SequenceNode node = (SequenceNode) graph.getNode(i + 1);
                gc.setFill(Color.BLUE);
                gc.fillRect(node.getxCoordinate(), node.getyCoordinate(), 50, 10);
            } else {
                DummyNode node = (DummyNode) graph.getNode(i + 1);
                gc.setFill(Color.BLACK);
                gc.fillRect(node.getxCoordinate(), node.getyCoordinate(), 50, 10);
            }
        }
    }

    private void drawEdges() {
        for (int i = 0; i < graph.getEdges().size(); i++) {
            Edge edge = graph.getEdges().get(i);
            gc.setFill(Color.BLACK);
            AbstractNode node = graph.getNode(edge.getParent() + 1);
            int startx = 0;
            int starty = 0;
            int endx = 0;
            int endy = 0;
            if (node instanceof SequenceNode) {
                SequenceNode parent = (SequenceNode) node;
                startx = parent.getxCoordinate();
                starty = parent.getyCoordinate();
            } else {
                DummyNode parent = (DummyNode) node;
                startx = parent.getxCoordinate();
                starty = parent.getyCoordinate();
            }
            node = graph.getNode(edge.getChild() + 1);
            if (node instanceof SequenceNode) {
                SequenceNode child = (SequenceNode) node;
                endx = child.getxCoordinate();
                endy = child.getyCoordinate();
            } else {
                DummyNode child = (DummyNode) node;
                endx = child.getxCoordinate();
                endy = child.getyCoordinate();
            }
            gc.strokeLine(startx, starty, endx, endy);
        }
    }
}

