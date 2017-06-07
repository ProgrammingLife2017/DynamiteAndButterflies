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

    private final SequenceGraph graph;
    private final GraphDrawer drawer;
    private final TextField nodeTextField, radiusTextField;
    private final PanningController panningController;

    /**
     * Constructor of the Zoom Controller.
     * @param drwr A GraphDrawer that can draw the graph for us.
     * @param panningController The panningcontroller.
     * @param nodeField The textField that contains the centre node.
     * @param radField The textField that contains the radius.
     */
    public ZoomController(SequenceGraph graph, GraphDrawer drwr, PanningController panningController,
                          TextField nodeField, TextField radField) {
        this.graph = graph;
        drawer = drwr;
        nodeTextField = nodeField;
        radiusTextField = radField;
        this.panningController = panningController;
    }

    /**
     * Zooms in.
     * @param column the column to zoom in on.
     * @throws IOException thrown if can't find
     */
    public void zoomIn(int column) throws IOException {
        drawer.zoom(SCROLL_ZOOM_IN_FACTOR, column);
        panningController.setScrollbarSize(column);
        updateRadius((int) Math.ceil(drawer.getRadius()) + "");
    }

    /**
     * Zooms out.
     * @param column the column to zoom out on.
     * @throws IOException thrown if can't find
     */
    public void zoomOut(int column) throws IOException {
        drawer.zoom(SCROLL_ZOOM_OUT_FACTOR, column);
        panningController.setScrollbarSize(column);
        updateRadius((int) Math.ceil(drawer.getRadius()) + "");
    }

    /**
     * Traverses the graph loaded to a specified destination.
     * @param centreNode The centre node to move to
     * @param radius The radius to be viewed
     */
    public void traverseGraphClicked(int centreNode, int radius) {
        int column = graph.getNode(centreNode).getColumn();
        drawer.changeZoom(column, radius);
        drawer.highlight(centreNode);
        panningController.setScrollbarSize(drawer.getColumnWidth(column));
    }

    /**
     * Displays the centre node and radius of the current view.
     */
    public void displayInfo() {
        nodeTextField.setText(drawer.getRealCentreNode().getId() + "");
        radiusTextField.setText((int) Math.ceil(drawer.getRadius()) + "");
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
