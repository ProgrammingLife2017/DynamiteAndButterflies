package gui.sub_controllers;

import graph.SequenceGraph;
import graph.SequenceNode;
import gui.GraphDrawer;
import gui.MenuController;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.mapdb.HTreeMap;
import parser.GfaParser;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Controller for opening a file.
 */
public class FileController implements Observer {

    private SequenceGraph graph;
    private gui.GraphDrawer drawer;
    private HTreeMap<Long, String> sequenceHashMap;
    private File parDirectory;
    private ProgressBarController progressBarController;

    private final int renderRange = 200;
    private final int nodeId = 1;

    private Thread parseThread;

    private GraphicsContext gc;

    private GfaParser parser;

    private int[] childArray;
    private int[] parentArray;

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
    public File chooseFile(Stage stage) {
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
     * @param gc the graphicscontext we will use.
     * @param mC the MenuController so it can Observe.
     * @param filePath the filePath of the file.
     * @throws IOException exception if no file is found
     * @throws InterruptedException Exception if the Thread is interrupted.
     */
    public void openFileClicked(GraphicsContext gc, String filePath, MenuController mC)
            throws IOException, InterruptedException {
        this.gc = gc;
        if (parser != null) {
            parser.getDb().close();
        }
        parser = new GfaParser(filePath);
        parser.addObserver(this);
        parser.addObserver(mC);
        if (this.parseThread != null) {
            this.parseThread.interrupt();
        }
        this.parseThread = new Thread(parser);
        this.parseThread.start();

        progressBarController.run();
    }

    private void assignSequenceLenghts() {
        HashMap<Integer, SequenceNode> nodes = graph.getNodes();
        Iterator it = nodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SequenceNode node = (SequenceNode) pair.getValue();
            if(!node.isDummy()) {
                node.setSequenceLength(sequenceHashMap.get((long) node.getId()).length());
            }
        }
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
            try {
                childArray = parser.getChildArray(parser.getPartPath());
                parentArray = parser.getParentArray(parser.getPartPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            graph = new SequenceGraph();
            graph.createSubGraph(nodeId, renderRange, parentArray, childArray);
            sequenceHashMap = parser.getSequenceHashMap();
            assignSequenceLenghts();
            drawer = new GraphDrawer(graph, gc);
            drawer.moveShapes(0.0);
            progressBarController.done();
        }
    }



    /**
     * Gets the fileName from the filePath.
     * @param filePath The path to the file you want the name off
     * @return The name of the file.
     */
    public String fileNameFromPath(String filePath) {
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] partPaths = filePath.split(pattern);
        String fileName = partPaths[partPaths.length - 1];
        System.out.println(fileName);
        return fileName;
    }
}
