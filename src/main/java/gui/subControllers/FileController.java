package gui.subControllers;

import graph.SequenceGraph;
import gui.GraphDrawer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.mapdb.HTreeMap;
import parser.GfaParser;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by Jip on 17-5-2017.
 */
public class FileController {

    private SequenceGraph graph;
    private gui.GraphDrawer drawer;
    private HTreeMap<Long, String> sequenceHashMap;
    HTreeMap<Long, int[]> adjacencyMap;
    private File parDirectory;
    private ProgressBarController progressBarController;

    private final int RENDER_RANGE = 5000;
    private final int NODE_ID = 1;

    /**
     * Constructor of the FileController object to control the Files.
     */
    public FileController(ProgressBarController pbc) {
        graph = new SequenceGraph();
        parDirectory = null;
        progressBarController = pbc;
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

        if (parDirectory == null) {
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.dir")).getParentFile()
            );
        } else {
            fileChooser.setInitialDirectory(
                    parDirectory
            );
        }

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("GFA", "*.gfa")
        );
        File res = fileChooser.showOpenDialog(stage);
        parDirectory = res.getParentFile();
        return res;
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa.
     * can be selected and directly be visualised on the screen.
     * @param anchorPane the pane where we will be drawing
     * @param gc the graphicscontext we will use
     * @return The strign of the file that has just been loaded.
     * @throws IOException exception if no file is found
     */
    public String openFileClicked(AnchorPane anchorPane, GraphicsContext gc) throws IOException {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        progressBarController.run();
        File file = chooseFile(stage);
        GfaParser parser = new GfaParser(file.getAbsolutePath());
        System.out.println("src/main/resources/" + file.getName());
        parser.start();

        adjacencyMap = parser.getAdjacencyHMap();
        graph = new SequenceGraph();
        graph.createSubGraph(NODE_ID, RENDER_RANGE, adjacencyMap);
        sequenceHashMap = parser.getSequenceHashMap();
        drawer = new GraphDrawer(graph, gc);
        drawer.moveShapes(0.0);

        String filePath = file.getAbsolutePath();
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] partPaths = filePath.split(pattern);
        String fileName = partPaths[partPaths.length - 1];
        System.out.println(fileName);

        return fileName;
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

//    public Task fileReadTask() {
//        return new Task() {
//            @Override
//            protected Object call() throws Exception {
//
//                GfaParser parser = new GfaParser();
//                System.out.println("src/main/resources/" + file.getName());
//
//                adjacencyMap = parser.parseGraph(file.getAbsolutePath());
//                progressBarController.done();
//                sequenceHashMap = parser.getSequenceHashMap();
//
//                return file.getName();
//            }
//        };
//    };
}
