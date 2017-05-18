package gui.subControllers;

import gui.GraphDrawer;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Created by Jip on 17-5-2017.
 * The Zoom Controller controls all buttons in the GUI that aare the boss of zooming.
 */
public class ZoomController {

    private gui.GraphDrawer drawer;
    private TextField nodeTextField, radiusTextField;

    /**
     * Constructor of the Zoom Controller.
     * @param drwr A GraphDrawer that can draw the graph for us.
     * @param nodeField The textField that contains the centre node.
     * @param radField The textField that contains the radius.
     */
    public ZoomController(GraphDrawer drwr, TextField nodeField, TextField radField) {
        drawer = drwr;
        nodeTextField = nodeField;
        radiusTextField = radField;
    }

    /**
     * ZoomIn.
     * @throws IOException exception.
     */
    public void zoomInClicked() throws IOException {
        if (!nodeTextField.getText().equals("")) {
            drawer.zoomIn(0.8, drawer.getColumnId(Integer.parseInt(nodeTextField.getText())));
        } else {
            drawer.zoomIn(0.8, drawer.getRealCentreNode().getColumn());
        }
        updateRadius(drawer.getZoomLevel() + "");
    }

    /**
     * ZoomOut.
     * @throws IOException exception.
     */
    public void zoomOutClicked() throws IOException {
        if (!nodeTextField.getText().equals("")) {
            drawer.zoomOut(1.2, drawer.getColumnId(Integer.parseInt(nodeTextField.getText())));
        } else {
            drawer.zoomOut(1.2, drawer.getRealCentreNode().getColumn());
        }
        updateRadius(drawer.getZoomLevel() + "");
    }

    /**
     * Adds a button to traverse the graph with.
     * @param graphSize the size of the graph to determine the end node.
     */
    public void traverseGraphClicked(int graphSize) {
        int startColumn = getStartColumn();
        int endColumn = getEndColumn(graphSize);
        int centreNodeID = Integer.parseInt(nodeTextField.getText());
        drawer.changeZoom(endColumn - startColumn, drawer.getColumnId(centreNodeID));
    }

    public void traverseGraphClicked(int graphSize, int centreNode, int radius) {
        int startColumn = getStartColumn(centreNode, radius);
        int endColumn = getEndColumn(graphSize, centreNode, radius);
        drawer.changeZoom(endColumn - startColumn, drawer.getColumnId(centreNode));
    }

    /**
     * Displays the centre node and radius of the current view.
     */
    public void displayInfo() {
        nodeTextField.setText(drawer.getRealCentreNode().getId() + "");
        radiusTextField.setText(drawer.getZoomLevel() + "");
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
        return Integer.parseInt(radiusTextField.getText());
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
     * Gets the start column based on the given fields.
     * @param centre the integer representing the centre node.
     * @param rad the integer representing the radius.
     * @return integer representing the starting column
     */
    private int getStartColumn(int centre, int rad) {

        int startNode = centre - rad;
        if (startNode < 1) {
            startNode = 1;
        }
        return drawer.getColumnId(startNode);
    }

    /**
     * Gets the start column based on the text fields.
     * @return integer representing the starting column
     */
    private int getStartColumn() {
        int centreNode = getCentreNodeID();
        int radius = getRadius();

        return getStartColumn(centreNode, radius);
    }

    /**
     * Gets the end column based on the text fields.
     * @param graphSize the size of the graph.
     * @return integer representing the end column
     */
    private int getEndColumn(int graphSize) {
        int centreNode = getCentreNodeID();
        int radius = getRadius();

        return getEndColumn(graphSize, centreNode, radius);
    }

    /**
     * Gets the end column based on the text fields.
     * @param graphSize the size of the graph.
     * @param centre the integer representing the centre node.
     * @param rad the integer representing the radius.
     * @return integer representing the end column
     */
    private int getEndColumn(int graphSize, int centre, int rad) {
        int endNode = centre + rad;
        if (endNode > graphSize) {
            endNode = graphSize;
        }
        return drawer.getColumnId(endNode);
    }

    /**
     * Getter for the Node textfield.
     * @return The text in the textfield.
     */
    private TextField getNodeTextField() {
        return nodeTextField;
    }

    /**
     * Getter for the radius textfield.
     * @return The text in the textfield.
     */
    private TextField getRadiusTextField() {
        return radiusTextField;
    }

}
