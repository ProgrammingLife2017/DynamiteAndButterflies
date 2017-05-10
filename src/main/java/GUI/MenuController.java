package GUI;

import graph.SequenceGraph;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import parser.GfaParser;

import java.io.File;
import java.io.IOException;

/**
 * Created by Jasper van Tilburg on 1-5-2017.
 *
 * Controller for the Menu scene. Used to run all functionality in the main screen of the application.
 */
public class MenuController {

    public TextField nodeTextField;
    public TextField radiusTextField;
    private boolean flagView;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Canvas canvas;
    @FXML
    private Pane canvasPanel;
    @FXML
    private Label numNodesLabel;
    @FXML
    private Label numEdgesLabel;
    private GraphicsContext gc;
    private GraphDrawer drawer;
    private SequenceGraph graph;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        canvas.widthProperty().bind(canvasPanel.widthProperty());
        canvas.heightProperty().bind(canvasPanel.heightProperty());
        gc = canvas.getGraphicsContext2D();
        flagView = false;
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa can be selected and directly be visualised on the screen.
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

    public void openFile(File file) throws IOException {
        GfaParser parser = new GfaParser();
        System.out.println("src/main/resources/" + file.getName());
        graph = parser.parse(file.getAbsolutePath());
        drawer = new GraphDrawer(graph, gc);
        drawer.drawShapes();
        displayInfo(graph);
    }

    public void displayInfo(SequenceGraph graph) {
        numNodesLabel.setText(graph.getNodes().size() + "");
        numEdgesLabel.setText(graph.getEdges().size() + "");
    }

    @FXML
    public void zoomInClicked() throws IOException {
        drawer.zoom(0.5);
        if (flagView) {
            drawer.drawShapes(getStartColumn(), getEndColumn());
        } else {
            drawer.drawShapes();
        }
    }

    @FXML
    public void zoomOutClicked() throws IOException {
        drawer.zoom(2);
        drawer.drawShapes();
    }

    private double pressedX;

    @FXML
    public void clickMouse(MouseEvent mouseEvent) {
        pressedX = mouseEvent.getX();
    }

    @FXML
    public void dragMouse(MouseEvent mouseEvent) {
        double xDifference = pressedX - mouseEvent.getX();
        drawer.reShape(xDifference);
    }

    /**
     * Adds a button to traverse the graph with.
     */
    public void traverseGraphClicked() {
        flagView = true;

        drawer.drawShapes(getStartColumn(), getEndColumn());
    }

    /**
     * Gets the start column based on the text fields.
     * @return integer representing the starting column
     */
    private int getStartColumn() {
        String text = nodeTextField.getText();
        int centreNode = Integer.parseInt(nodeTextField.getText());
        int radius = Integer.parseInt(radiusTextField.getText());

        int startNode = centreNode - radius;
        if (startNode < 1) {
            startNode = 1;
        }
        return graph.getNode(startNode).getColumn();
    }

    /**
     * Gets the end column based on the text fields.
     * @return a integer representing the last column
     */
    private int getEndColumn() {
        int centreNode = Integer.parseInt(nodeTextField.getText());
        int radius = Integer.parseInt(radiusTextField.getText());

        int endNode = centreNode + radius;

        return graph.getNode(endNode).getColumn();
    }
}
