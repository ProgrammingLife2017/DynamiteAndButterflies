package gui.subControllers;

import graph.SequenceGraph;
import gui.GraphDrawer;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.mapdb.HTreeMap;
import parser.GfaParser;

import java.io.File;
import java.io.IOException;

/**
 * Created by Jip on 17-5-2017.
 */
public class FileController {

    private SequenceGraph graph;
    private gui.GraphDrawer drawer;
    private HTreeMap<Long, String> sequenceHashMap;

    /**
     * Constructor of the FileController object to control the Files.
     */
    public FileController() {
        graph = new SequenceGraph();
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa.
     * can be selected and directly be visualised on the screen.
     * @param stage The stage on which the fileFinder is shown.
     * @return returns the file that can be loaded.
     */
    private File chooseFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        return fileChooser.showOpenDialog(stage);
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa.
     * can be selected and directly be visualised on the screen.
     * @param anchorPane the pane where we will be drawing
     * @param gc the graphicscontext we will use
     * @return The strign of the file that has just been loaded.
     * @throws IOException exception if no file is found
     */
    @FXML
    public String openFileClicked(AnchorPane anchorPane, GraphicsContext gc) throws IOException {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        File file = chooseFile(stage);
        GfaParser parser = new GfaParser();
        System.out.println("src/main/resources/" + file.getName());
        graph = parser.parseGraph(file.getAbsolutePath());
        sequenceHashMap = parser.getSequenceHashMap();

        drawer = new GraphDrawer(graph, gc);
        drawer.moveShapes(0.0);

        return file.toString();
    }

    /**
     * Gets the sequenceHashMap.
     * @return the sequenceHashMap with all the sequences
     */
    public HTreeMap<Long, String> getSequenceHashMap() {
        return sequenceHashMap;
    }

    /**
     * Gets the GraphDrawer.
     * @return the graphDrawer.
     */
    public GraphDrawer getDrawer() {
        return drawer;
    }

    /**
     * Gets the graph.
     * @return the graph.
     */
    public SequenceGraph getGraph() {
        return graph;
    }
}
