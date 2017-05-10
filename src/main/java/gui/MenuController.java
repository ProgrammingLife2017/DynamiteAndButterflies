package gui;

import graph.SequenceGraph;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
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

    /**
<<<<<<< HEAD:src/main/java/gui/MenuController.java
     * Initializes the canvas.
=======
     * Initializes the controller.
>>>>>>> BasicVisualization:src/main/java/GUI/MenuController.java
     */
    @FXML
    public void initialize() {
        canvas.widthProperty().bind(canvasPanel.widthProperty());
        canvas.heightProperty().bind(canvasPanel.heightProperty());
        gc = canvas.getGraphicsContext2D();
    }

    /**
<<<<<<< HEAD:src/main/java/gui/MenuController.java
     * Implements the file chooser when choosing that option from teh file menu.
=======
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa can be selected and directly be visualised on the screen.
>>>>>>> BasicVisualization:src/main/java/GUI/MenuController.java
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
<<<<<<< HEAD:src/main/java/gui/MenuController.java
                GfaParser parser = new GfaParser();
                System.out.println("src/main/resources/" + file.getName());
                SequenceGraph graph = parser.parse(file.getAbsolutePath());
                drawer = new GraphDrawer(graph, gc);
                drawer.drawShapes();
=======
                openFile(file);
>>>>>>> BasicVisualization:src/main/java/GUI/MenuController.java
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void openFile(File file) throws IOException {
        GfaParser parser = new GfaParser();
        System.out.println("src/main/resources/" + file.getName());
        SequenceGraph graph = parser.parse(file.getAbsolutePath());
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
        drawer.drawShapes();
    }

    @FXML
    public void zoomOutClicked() throws IOException {
        drawer.zoom(2);
        drawer.drawShapes();
    }

}
