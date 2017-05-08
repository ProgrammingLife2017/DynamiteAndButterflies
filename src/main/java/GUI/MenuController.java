package GUI;

import graph.SequenceGraph;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
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
    private GraphicsContext gc;
    private GraphDrawer drawer;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
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
                GfaParser parser = new GfaParser();
                System.out.println("src/main/resources/" + file.getName());
                SequenceGraph graph = graph = parser.parse(file.getAbsolutePath());
                drawer = new GraphDrawer(graph, gc);
                drawer.drawShapes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
