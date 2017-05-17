package gui;

import graph.SequenceGraph;
import gui.subControllers.FileController;
import gui.subControllers.InfoController;
import gui.subControllers.ZoomController;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Created by Jasper van Tilburg on 1-5-2017.
 *
 * Controller for the Menu scene. Used to run all functionality
 * in the main screen of the application.
 */
public class MenuController {


    @FXML
    private Label sequenceInfo;
    @FXML
    private TextField nodeTextField;
    @FXML
    private TextField radiusTextField;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Canvas canvas;
    @FXML
    private AnchorPane canvasPanel;
    @FXML
    private Label numNodesLabel;
    @FXML
    private Label numEdgesLabel;
    private GraphicsContext gc;
    private double pressedX;

    private FileController fileController;
    private ZoomController zoomController;
    private InfoController infoController;

    /**
     * Initializes the canvas.
     */
    @FXML
    public void initialize() {
        canvas.widthProperty().bind(canvasPanel.widthProperty());
        canvas.heightProperty().bind(canvasPanel.heightProperty());
        gc = canvas.getGraphicsContext2D();
        fileController = new FileController();
        infoController = new InfoController(numNodesLabel, numEdgesLabel, sequenceInfo);
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa
     * can be selected and directly be visualised on the screen.
     */
    @FXML
    public void openFileClicked() throws IOException {
        fileController.openFileClicked(anchorPane, gc);


        zoomController = new ZoomController(fileController.getDrawer(), nodeTextField, radiusTextField);

        displayInfo(fileController.getGraph());
    }

    private void displayInfo(SequenceGraph graph) {
        infoController.displayInfo(graph);
        zoomController.displayInfo();
    }

    /**
     * ZoomIn Action Handler.
     * @throws IOException exception.
     */
    @FXML
    public void zoomInClicked() throws IOException {
        zoomController.zoomInClicked();
    }

    /**
     * ZoomOut Action Handler.
     * @throws IOException exception.
     */
    @FXML
    public void zoomOutClicked() throws IOException {
        zoomController.zoomOutClicked();
    }

    /**
     * Get the X-Coordinate of the cursor on click.
     * @param mouseEvent the mouse event.
     */
    @FXML
    public void clickMouse(MouseEvent mouseEvent) {
        pressedX = mouseEvent.getX();
    }

    /**
     *  The eventHandler for dragging the mouse.
     * @param mouseEvent The MouseEvent for dragging.
     */
    @FXML
    public void dragMouse(MouseEvent mouseEvent) {
        double xDifference = pressedX - mouseEvent.getX() / 2;
        fileController.getDrawer().moveShapes(xDifference);
    }

    /**
     * Adds a button to traverse the graph with.
     */
    public void traverseGraphClicked() {
        zoomController.traverseGraphClicked(fileController.getGraph().getNodes().size());
        int centreNodeID = zoomController.getCentreNodeID();
        String newString = "Sequence: "
                            + fileController.getSequenceHashMap().get(centreNodeID);
        infoController.updateSeqLabel(newString);
    }
}
