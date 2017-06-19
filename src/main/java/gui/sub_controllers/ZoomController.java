package gui.sub_controllers;

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

    private static final double BUTTON_ZOOM_IN_FACTOR = 0.8;
    private static final double BUTTON_ZOOM_OUT_FACTOR = 1.25;
    private static final double SCROLL_ZOOM_IN_FACTOR = 0.9;
    private static final double SCROLL_ZOOM_OUT_FACTOR = 1.1;

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
        updateRadius((int) Math.ceil(GraphDrawer.getInstance().getRadius()) + "");
    }

    /**
     * Zooms out.
     * @param column the column to zoom out on.
     * @throws IOException thrown if can't find
     */
    public void zoomOut(int column) throws IOException {
        GraphDrawer.getInstance().zoom(SCROLL_ZOOM_OUT_FACTOR, column);
        updateRadius((int) Math.ceil(GraphDrawer.getInstance().getRadius()) + "");
    }

    /**
     * Traverses the graph loaded to a specified destination.
     * @param centreNode The centre node to move to
     * @param radius The radius to be viewed
     */
    public void traverseGraphClicked(int centreNode, int radius) {
        if (!GraphDrawer.getInstance().getGraph().getNodes().containsKey(centreNode)) {
            SequenceGraph newGraph = GraphDrawer.getInstance().getGraph().copy();
            newGraph.createSubGraph(centreNode, radius);
            GraphDrawer.getInstance().setGraph(newGraph);
            GraphDrawer.getInstance().setxDifference(0);
        }
        int column = GraphDrawer.getInstance().getGraph().getNode(centreNode).getColumn();
        GraphDrawer.getInstance().changeZoom(column, radius);
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
     * @param newRadius the new radius.
     */
    private void updateRadius(String newRadius) {
        radiusTextField.setText(newRadius);
    }

    /**
     * Setter for the Node textfield.
     * @param newCentreNode the new centre node.
     */
    public void setNodeTextField(String newCentreNode) {
        nodeTextField.setText(newCentreNode);
    }

    /**
     * Setter for the radius textfield.
     * @param newRadius the new radius.
     */
    public void setRadiusTextField(String newRadius) {
        radiusTextField.setText(newRadius);
    }

}
