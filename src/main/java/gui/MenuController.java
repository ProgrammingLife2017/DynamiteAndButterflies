package gui;

import graph.SequenceGraph;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import parser.GfaParser;

import java.io.File;
import java.io.IOException;

/**
 * Created by Jasper van Tilburg on 1-5-2017.
 *
 * Controller for the Menu scene. Used to run all functionality
 * in the main screen of the application.
 */
public class MenuController {

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
    private gui.GraphDrawer drawer;
    private SequenceGraph graph;

    /**
     * Initializes the canvas.
     */
    @FXML
    public void initialize() {
        canvas.widthProperty().bind(canvasPanel.widthProperty());
        canvas.heightProperty().bind(canvasPanel.heightProperty());
        gc = canvas.getGraphicsContext2D();
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa
     * can be selected and directly be visualised on the screen.
     */
    @FXML
    public void openFileClicked() {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        //fileChooser.setInitialDirectory(this.getClass().getResource("/resources").toString());
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                openFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openFile(File file) throws IOException {
        GfaParser parser = new GfaParser();
        System.out.println("src/main/resources/" + file.getName());
        graph = parser.parse(file.getAbsolutePath());
        drawer = new GraphDrawer(graph, gc);
        drawer.moveShapes(0.0);
        displayInfo(graph);
    }

    private void displayInfo(SequenceGraph graph) {
        numNodesLabel.setText(graph.getNodes().size() + "");
        numEdgesLabel.setText(graph.getEdges().size() + "");
    }

    /**
     * ZoomIn Action Handler.
     * @throws IOException exception.
     */
    @FXML
    public void zoomInClicked() throws IOException {
        if (!getNodeTextField().getText().equals("")) {
            drawer.zoomIn(0.8, drawer.getColumnId(Integer.parseInt(getNodeTextField().getText())));
        } else {
            drawer.zoomIn(0.8, drawer.getRealCentreNode().getColumn());
        }

    }

    /**
     * ZoomOut Action Handler.
     * @throws IOException exception.
     */
    @FXML
    public void zoomOutClicked() throws IOException {
        if (!getNodeTextField().getText().equals("")) {
            drawer.zoomIn(1.2, drawer.getColumnId(Integer.parseInt(getNodeTextField().getText())));
        } else {
            drawer.zoomIn(1.2, drawer.getRealCentreNode().getColumn());
        }
    }

    private double pressedX;

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
        drawer.moveShapes(xDifference);
    }

    /**
     * Adds a button to traverse the graph with.
     */
    public void traverseGraphClicked() {
        int radius = Integer.parseInt(getRadiusTextField().getText());
        drawer.changeZoom(radius * 2, getStartColumn());
    }

    /**
     * Gets the start column based on the text fields.
     * @return integer representing the starting column
     */
    private int getStartColumn() {
        String text = getNodeTextField().getText();
        int centreNode = Integer.parseInt(getNodeTextField().getText());
        int radius = Integer.parseInt(getRadiusTextField().getText());

        int startNode = centreNode - radius;
        if (startNode < 1) {
            startNode = 1;
        }
        return graph.getNode(startNode).getColumn();
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
