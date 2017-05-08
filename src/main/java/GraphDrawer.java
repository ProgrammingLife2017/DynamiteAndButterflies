import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by TUDelft SID on 8-5-2017.
 */
public class GraphDrawer {

    private int Xsize = 10;
    private int Ysize = 6;
    private int lengthEdge = 2;
    private int yBase = 40;

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
            gc.fillRoundRect((node.getColumn() * (Xsize + lengthEdge)) + 50, yBase, Xsize, Ysize, 10, 10);
//            gc.setStroke(Color.BLACK);
//            gc.setLineWidth(1);
//            gc.strokeLine((node.getColumn() * (Xsize + lengthEdge)) + Xsize + 50,43, node.getColumn() * (Xsize + Xsize + lengthEdge) + 50, 43);
        }
    }
}

