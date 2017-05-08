package GUI;

import graph.DummyNode;
import graph.Node;
import graph.SequenceGraph;
import graph.SequenceNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jasper van Tilburg on 8-5-2017.
 *
 * Class used to draw shapes on the canvas.
 */
public class GraphDrawer {

    public static final int X_SIZE = 50;
    public static final int Y_SIZE = 10;
    public static final int EDGE_LENGTH = 30;
    public static final int Y_BASE = 40;

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
        graph.initialize();
        graph.layerizeGraph();
    }

    /**
     * Iterates over all of the nodes in the sequence graph to visualize it in shapes.
     * @throws IOException
     */
    public void drawShapes2() throws IOException {
        HashMap<Integer, SequenceNode> nodes = graph.getNodes();

        for(int i = 1; i <= nodes.size(); i++) {
            SequenceNode node = nodes.get(i);
            gc.setFill(Color.BLUE);
            gc.fillRoundRect((node.getColumn() * (X_SIZE + EDGE_LENGTH)) + 50, Y_BASE, X_SIZE, Y_SIZE, 10, 10);
//            gc.setStroke(Color.BLACK);
//            gc.setLineWidth(1);
//            gc.strokeLine((node.getColumn() * (Xsize + lengthEdge)) + Xsize + 50,43, node.getColumn() * (Xsize + Xsize + lengthEdge) + 50, 43);
        }
    }

    public void drawShapes() throws IOException {
        ArrayList<ArrayList<Node>> columns = graph.getColumnList();
        gc.setFill(Color.BLUE);

        for(int j = 0; j < columns.size(); j++) {
            ArrayList<Node> column = columns.get(j);
            for (int i = 0; i < column.size(); i++) {
                if (column.get(i) instanceof DummyNode) {
                    gc.setFill(Color.BLACK);
                }
                gc.fillRoundRect((j * (X_SIZE + EDGE_LENGTH)) + 50, Y_BASE + (i * 50), X_SIZE, Y_SIZE, 10, 10);
                gc.setFill(Color.BLUE);
            }
        }
    }
}

