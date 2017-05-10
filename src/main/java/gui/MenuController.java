package gui;

import com.sun.xml.internal.bind.v2.TODO;
import graph.AbstractNode;
import graph.DummyNode;
import graph.SequenceGraph;
import graph.SequenceNode;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import parser.GfaParser;

import javax.sound.midi.Sequence;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

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
     * Initializes the canvas.
     */
    @FXML
    public void initialize() {
        canvas.widthProperty().bind(canvasPanel.widthProperty());
        canvas.heightProperty().bind(canvasPanel.heightProperty());
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
                openFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void openFile(File file) throws IOException {
        GfaParser parser = new GfaParser();
        System.out.println("src/main/resources/" + file.getName());
        SequenceGraph graph = parser.parse(file.getAbsolutePath());
        graph.initialize();
        assignCoordinates(graph);
        drawer = new GraphDrawer(graph, gc);
        drawer.drawShapes();
    }

    public void assignCoordinates(SequenceGraph graph) {
        int[] layers = new int[graph.getNodes().size()];
        for (int i = 0; i < graph.getNodes().size(); i++) {
            if (graph.getNode(i + 1) instanceof SequenceNode) {
                SequenceNode node = (SequenceNode) graph.getNode(i + 1);
                int x = (int) (node.getLayer() * (canvas.getWidth() / graph.getNodes().size()));
                node.setxCoordinate(x);
                int y = layers[node.getLayer()] * 100 + 100;
                layers[node.getLayer()]++;
                node.setyCoordinate(y);
            } else {
                DummyNode node = (DummyNode) graph.getNode(i + 1);
                int x = (int) (node.getLayer() * (canvas.getWidth() / graph.getNodes().size()));
                node.setxCoordinate(x);
                int y = layers[node.getLayer()] * 100 + 100;
                layers[node.getLayer()]++;
                node.setyCoordinate(y);
            }
        }
    }

    @FXML
    public void zoomInClicked() throws IOException {
        //TODO
    }

    @FXML
    public void zoomOutClicked() throws IOException {
        //TODO
    }
}
