package GUI;

import graph.DummyNode;
import graph.Node;
import graph.SequenceGraph;
import graph.SequenceNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.sound.midi.Sequence;
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
    private double xSize;

    private SequenceGraph graph;
    private GraphicsContext gc;
    private ArrayList<ArrayList<Node>> columns;

    /**
     * Constructor
     * @param graph The sequencegraph to be drawn to the canvas.
     * @param gc The graphics context used to actually draw shapes.
     */
    public GraphDrawer(SequenceGraph graph, GraphicsContext gc) {
        this.graph = graph;
        this.gc = gc;
        xSize = 1;
        graph.initialize();
        graph.layerizeGraph();
        columns = graph.getColumnList();
        zoomLevel = columns.size();
    }

    public void zoomIn (double factor, int column) {
        this.zoomLevel = (int) (zoomLevel * factor);
        this.xSize += 0.1;
        moveShapes(column - zoomLevel / 2);
    }

    public void zoomOut(double factor, int column) {
        this.zoomLevel = (int) (zoomLevel * factor);
        this.xSize -= 0.1;
        moveShapes(column - zoomLevel / 2);
    }

    public int getZoomLevel() {
        return this.zoomLevel;
    }

    /**
     * Iterates over all of the nodes in the sequence graph to visualize it in shapes.
     * @throws IOException
     */
    public void drawShapes() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setFill(Color.BLUE);
        for(int j = 0; j < columns.size(); j++) {
            ArrayList<Node> column = columns.get(j);
            for (int i = 0; i < column.size(); i++) {
                if (column.get(i) instanceof DummyNode) {
                    gc.setFill(Color.BLACK);
                }
                //gc.fillRoundRect((j * (xSize + EDGE_LENGTH)) + 50, Y_BASE + (i * 50), xSize, Y_SIZE, 10, 10);
                gc.fillRoundRect(j * ((gc.getCanvas().getWidth() - 20) / zoomLevel) , Y_BASE + (i * 50), xSize, Y_SIZE, 10, 10);
                gc.setFill(Color.BLUE);
            }
        }
    }

    public void changeZoom(int newZoom, int column) {
        zoomLevel = newZoom;
        double xDifference = column;
        //double xDifference = column * (gc.getCanvas().getWidth() - 20);
        moveShapes(xDifference);
    }

    //Still needs to handle the centre node etc.
    public void moveShapes(double xDifference) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setFill(Color.BLUE);
        for(int j = 0; j < columns.size(); j++) {
            ArrayList<Node> column = columns.get(j);
            for (int i = 0; i < column.size(); i++) {
                if (column.get(i) instanceof DummyNode) {
                    gc.setFill(Color.BLACK);
                }
                gc.fillRoundRect((j - xDifference) * ((gc.getCanvas().getWidth() - 20) / zoomLevel) , Y_BASE + (i * 50), xSize, Y_SIZE, 10, 10);
                gc.setFill(Color.BLUE);
            }
        }
    }

    public ArrayList<Node> getCentreColumn() {
        return columns.get(columns.size() / 2);
    }

    public SequenceNode getRealCentreNode() {
        ArrayList<Node> set = getCentreColumn();
        for (Node test : set) {
            if (test instanceof SequenceNode) {
                return (SequenceNode) test;
            }
        }
        return null;
    }

    //TODO: Loop over the  nodes in the graph (O(n*m) > O(k))
    public int getColumnId(int nodeId){
        for(ArrayList<Node> list : columns) {
            for(Node node : list){
                if(node instanceof SequenceNode) {
                    if(((SequenceNode) node).getId() == nodeId) {
                        return node.getColumn();
                    }
                }
            }
        }
        return -1;
    }
}

