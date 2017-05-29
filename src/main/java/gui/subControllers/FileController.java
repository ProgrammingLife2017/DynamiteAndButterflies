package gui.subControllers;

import graph.SequenceGraph;
import gui.GraphDrawer;
import gui.MenuController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.mapdb.HTreeMap;
import parser.GfaParser;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Jip on 17-5-2017.
 */
public class FileController implements Observer {

    private SequenceGraph graph;
    private gui.GraphDrawer drawer;
    private HTreeMap<Long, String> sequenceHashMap;
    private File parDirectory;
    private ProgressBarController progressBarController;

    private final int renderRange = 5000;
    private final int nodeId = 1;

    private Thread parseThread;

    private GraphicsContext gc;

    private GfaParser parser;

    /**
     * Constructor of the FileController object to control the Files.
     * @param pbc The progressbar.
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
     * @param gc the graphicscontext we will use.
     * @param mC the MenuController so it can Observe.
     * @throws IOException exception if no file is found
     * @throws InterruptedException Exception if the Thread is interrupted.
     */
    public void openFileClicked(AnchorPane anchorPane, GraphicsContext gc, MenuController mC)
            throws IOException, InterruptedException {
        this.gc = gc;
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        File file = chooseFile(stage);
        parser = new GfaParser(file.getAbsolutePath());
        parser.addObserver(this);
        parser.addObserver(mC);
        if (this.parseThread != null) {
            this.parseThread.interrupt();
        }
        this.parseThread = new Thread(parser);
        this.parseThread.start();

        progressBarController.run();
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

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GfaParser) {
            if (arg instanceof HTreeMap) {
                HTreeMap<Long, int[]> adjacencyMap = (HTreeMap) arg;
                progressBarController.done();
                //adjacencyMap = parser.getAdjacencyHMap();
                graph = new SequenceGraph();
                graph.createSubGraph(nodeId, renderRange, adjacencyMap);
                sequenceHashMap = parser.getSequenceHashMap();
                drawer = new GraphDrawer(graph, gc);
                drawer.moveShapes(0.0);
            }
        }
    }
}
