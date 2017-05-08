package GUI;

import graph.SequenceGraph;
import parser.SequenceNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by TUDelft SID on 8-5-2017.
 */
public class GraphDrawer {

    public static final int X_SIZE = 10;
    public static final int Y_SIZE = 6;
    public static final int EDGE_LENGTH = 2;
    public static final int Y_BASE = 40;

    private SequenceGraph graph;
    private GraphicsContext gc;

    public GraphDrawer(SequenceGraph graph, GraphicsContext gc) {
        this.graph = graph;
        this.gc = gc;
        graph.initialize();
        graph.layerizeGraph();
    }

    public void drawShapes() throws IOException {
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
}

