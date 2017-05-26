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
    public void zoomIn() throws IOException {
        if (!nodeTextField.getText().equals("")) {
            drawer.zoom(0.8, drawer.getColumnId(Integer.parseInt(nodeTextField.getText())));
        } else {
            drawer.zoom(0.8, drawer.getRealCentreNode().getColumn());
        }
        updateRadius((int) Math.ceil(drawer.getRadius()) + "");
    }

    public void zoomIn(int column) throws IOException {
        drawer.zoom(0.9, column);
        updateRadius((int) Math.ceil(drawer.getRadius()) + "");
    }

    /**
     * ZoomOut.
     * @throws IOException exception.
     */
    public void zoomOut() throws IOException {
        if (!nodeTextField.getText().equals("")) {
            drawer.zoom(1.25, drawer.getColumnId(Integer.parseInt(nodeTextField.getText())));
        } else {
            drawer.zoom(1.25, drawer.getRealCentreNode().getColumn());
        }
        updateRadius((int) Math.ceil(drawer.getRadius()) + "");
    }

    public void zoomOut(int column) throws IOException {
        drawer.zoom(1.1, column);
        updateRadius((int) Math.ceil(drawer.getRadius()) + "");
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
        drawer.highlight(centreNodeID);
    }

    public void traverseGraphClicked(int graphSize, int centreNode, int radius) {
        int startColumn = getStartColumn(centreNode, radius);
        int endColumn = getEndColumn(graphSize, centreNode, radius);
        drawer.changeZoom(endColumn - startColumn, drawer.getColumnId(centreNode));
        drawer.highlight(centreNode);
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
