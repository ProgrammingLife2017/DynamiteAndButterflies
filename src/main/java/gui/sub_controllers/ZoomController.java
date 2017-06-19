package gui.sub_controllers;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import graph.SequenceGraph;
import gui.GraphDrawer;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Created by Jip on 17-5-2017.
 * The Zoom Controller controls all buttons
 * in the GUI that are the boss of zooming.
 */
public class ZoomController {

    private static final double SCROLL_ZOOM_IN_FACTOR = 0.8;
    private static final double SCROLL_ZOOM_OUT_FACTOR = 1.25;

    private final TextField nodeTextField, radiusTextField;
    private final PanningController panningController;

    /**
     * Constructor of the Zoom Controller.
     * @param panController The panningcontroller.
     * @param nodeField The textField that contains the centre node.
     * @param radField The textField that contains the radius.
     */
    public ZoomController(PanningController panController, TextField nodeField, TextField radField) {
        nodeTextField = nodeField;
        radiusTextField = radField;
        this.panningController = panController;
    }

    /**
     * Zooms in.
     * @param column the column to zoom in on.
     * @throws IOException thrown if can't find
     */
    public void zoomIn(int column) throws IOException {
        GraphDrawer.getInstance().zoom(SCROLL_ZOOM_IN_FACTOR, column);
        updateRadius();
    }

    /**
     * Zooms out.
     * @param column the column to zoom out on.
     * @throws IOException thrown if can't find
     */
    public void zoomOut(int column) throws IOException {
        GraphDrawer.getInstance().zoom(SCROLL_ZOOM_OUT_FACTOR, column);
        updateRadius();
    }

    /**
     * Traverses the graph loaded to a specified destination.
     * @param centreNode The centre node to move to
     */
    public void traverseGraphClicked(int centreNode, double zoom) {
        if (!GraphDrawer.getInstance().getGraph().getNodes().containsKey(centreNode)) {
            SequenceGraph newGraph = GraphDrawer.getInstance().getGraph().copy();
            newGraph.createSubGraph(centreNode, PanningController.RENDER_RANGE);
            GraphDrawer.getInstance().setGraph(newGraph);
            GraphDrawer.getInstance().setxDifference(0);
        }
        GraphDrawer drawer = GraphDrawer.getInstance();
        double xDiff = drawer.getColumnWidth(drawer.getGraph().getNode(centreNode).getColumn()) - drawer.getZoomLevel() / 2;
        GraphDrawer.getInstance().setZoomLevel(zoom);
        GraphDrawer.getInstance().moveShapes(xDiff);
        GraphDrawer.getInstance().highlight(centreNode);
    }

    /**
     * Displays the centre node and radius of the current view.
     */
    public void displayInfo() {
        //nodeTextField.setText(GraphDrawer.getInstance().getRealCentreNode().getId() + "");
        //radiusTextField.setText((int) Math.ceil(GraphDrawer.getInstance().getRadius()) + "");
    }

    /**
     * Getter for the centreNode.
     * @return the ID of the centre node.
     */
    public int getCentreNodeID() {
        return Integer.parseInt(nodeTextField.getText());
    }

    /**
     * Getter for the radius.
     * @return the radius.
     */
    public int getRadius() {
        return (int) Double.parseDouble(radiusTextField.getText());
    }

    /**
     * Getter for the centre node.
     * @return the centre node.
     */
    public int getCentreNode() {
        return Integer.parseInt(nodeTextField.getText());
    }

    /**
     * Updates the radius.
     */
    private void updateRadius() {
        int radius = GraphDrawer.getInstance().getMostRightNode().getId() - GraphDrawer.getInstance().getMostLeftNode().getId();
        radiusTextField.setText(radius + "");
    }

    /**
     * Setter for the Node textfield.
     * @param newCentreNode the new centre node.
     */
    public void setNodeTextField(String newCentreNode) {
        nodeTextField.setText(newCentreNode);
    }

}
