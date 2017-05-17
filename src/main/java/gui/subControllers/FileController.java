package gui.subControllers;

import graph.SequenceGraph;
import gui.GraphDrawer;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import parser.GfaParser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Jip on 17-5-2017.
 */
public class FileController {

    SequenceGraph graph;
    private gui.GraphDrawer drawer;
    private HashMap<Integer, String> sequenceHashMap;

    public FileController() {
        graph = new SequenceGraph();
        sequenceHashMap = new HashMap<Integer, String>();
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa.
     * can be selected and directly be visualised on the screen.
     * @param stage The stage on which the fileFinder is shown.
     * @return returns the file that can be loaded.
     */
    public File chooseFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        return fileChooser.showOpenDialog(stage);
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa
     * can be selected and directly be visualised on the screen.
     */
    @FXML
    public SequenceGraph openFileClicked(AnchorPane anchorPane, GraphicsContext gc) throws IOException {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        File file = chooseFile(stage);
        GfaParser parser = new GfaParser();
        System.out.println("src/main/resources/" + file.getName());
        graph = parser.parseGraph(file.getAbsolutePath());
        sequenceHashMap = parser.getSequenceHashMap();

        drawer = new GraphDrawer(graph, gc);
        drawer.moveShapes(0.0);

        return graph;
    }

    public HashMap<Integer, String> getSequenceHashMap() {
        return sequenceHashMap;
    }

    public GraphDrawer getDrawer() {
        return drawer;
    }

    public SequenceGraph getGraph() {
        return graph;
    }
}
