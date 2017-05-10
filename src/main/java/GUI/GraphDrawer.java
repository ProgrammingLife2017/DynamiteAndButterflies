package GUI;

import graph.DummyNode;
import graph.Node;
import graph.SequenceGraph;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;

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
     * Constructor
     * @param graph The sequencegraph to be drawn to the canvas.
     * @param gc The graphics context used to actually draw shapes.
     */
    public GraphDrawer(SequenceGraph graph, GraphicsContext gc) {
        this.graph = graph;
        this.gc = gc;
        zoomLevel = 2985;
        xSize = 1;
        graph.initialize();
        graph.layerizeGraph();
    }

    public void zoom(double factor) {
        this.zoomLevel = (int) (zoomLevel * factor);
        this.xSize = (int) (xSize / factor);
    }

    /**
     * Iterates over all of the nodes in the sequence graph to visualize it in shapes.
     * @throws IOException
     */
    public void drawShapes() throws IOException {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        ArrayList<ArrayList<Node>> columns = graph.getColumnList();
        gc.setFill(Color.BLUE);
        for(int j = 0; j < columns.size(); j++) {
            ArrayList<Node> column = columns.get(j);
            for (int i = 0; i < column.size(); i++) {
                if (column.get(i) instanceof DummyNode) {
                    gc.setFill(Color.BLACK);
                }
                //gc.fillRoundRect((j * (xSize + EDGE_LENGTH)) + 50, Y_BASE + (i * 50), xSize, Y_SIZE, 10, 10);
                gc.fillRoundRect(j * (((gc.getCanvas().getWidth() - 20) / zoomLevel) + xSize), Y_BASE + (i * 50), xSize, Y_SIZE, 10, 10);
                gc.setFill(Color.BLUE);
            }
        }
    }

    public void reShape(double xDifference) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        ArrayList<ArrayList<Node>> columns = graph.getColumnList();
        gc.setFill(Color.BLUE);
        for(int j = 0; j < columns.size(); j++) {
            ArrayList<Node> column = columns.get(j);
            for (int i = 0; i < column.size(); i++) {
                if (column.get(i) instanceof DummyNode) {
                    gc.setFill(Color.BLACK);
                }
                //gc.fillRoundRect((j * (xSize + EDGE_LENGTH)) + 50, Y_BASE + (i * 50), xSize, Y_SIZE, 10, 10);
                gc.fillRoundRect(
                        j * (((gc.getCanvas().getWidth() - 20) / zoomLevel) + xSize) - xDifference,
                        Y_BASE + (i * 50), xSize, Y_SIZE,
                        10, 10);
                gc.setFill(Color.BLUE);
            }
        }
    }
}

