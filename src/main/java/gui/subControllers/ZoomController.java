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
            radiusTextField.setText(drawer.getZoomLevel() + "");
        } else {
            drawer.zoomIn(0.8, drawer.getRealCentreNode().getColumn());
            radiusTextField.setText(drawer.getZoomLevel() + "");
        }
    }

    /**
     * ZoomOut.
     * @throws IOException exception.
     */
    public void zoomOutClicked() throws IOException {
        if (!nodeTextField.getText().equals("")) {
            drawer.zoomOut(1.2, drawer.getColumnId(Integer.parseInt(nodeTextField.getText())));
            radiusTextField.setText(drawer.getZoomLevel() + "");
        } else {
            drawer.zoomOut(1.2, drawer.getRealCentreNode().getColumn());
            radiusTextField.setText(drawer.getZoomLevel() + "");
        }
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
    private int getRadius() {
        return Integer.parseInt(radiusTextField.getText());
    }

    /**
     * Gets the start column based on the text fields.
     * @return integer representing the starting column
     */
    private int getStartColumn() {
        int centreNode = getCentreNodeID();
        int radius = getRadius();

        int startNode = centreNode - radius;
        if (startNode < 1) {
            startNode = 1;
        }
        return drawer.getColumnId(startNode);
    }

    /**
     * Gets the end column based on the text fields.
     * @return integer representing the end column
     */
    private int getEndColumn(int graphSize) {
        int centreNode = getCentreNodeID();
        int radius = getRadius();

        int endNode = centreNode + radius;
        if (endNode > graphSize) {
            endNode = graphSize;
        }
        return drawer.getColumnId(endNode);
    }
}
