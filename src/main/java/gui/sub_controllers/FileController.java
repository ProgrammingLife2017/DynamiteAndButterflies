package gui.sub_controllers;

import graph.SequenceGraph;
import graph.SequenceNode;
import gui.CustomProperties;
import gui.DrawableCanvas;
import gui.GraphDrawer;
import gui.MenuController;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.mapdb.HTreeMap;
import parser.GfaParser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observer;
import java.util.Observable;
import java.util.regex.Pattern;

/**
 * Controller for opening a file.
 */
public class FileController extends Observable implements Observer {

    private SequenceGraph graph;
    private File parDirectory;
    private ProgressBarController progressBarController;

    private Thread parseThread;

    private GraphicsContext gc;


    private String partPath;

    private CustomProperties properties;

    private PopUpController popUpController;

    /**
     * Constructor of the FileController object to control the Files.
     * @param pbc The progressbar.
     */
    public FileController(ProgressBarController pbc) {
        graph = new SequenceGraph();
        parDirectory = null;
        progressBarController = pbc;

        properties = new CustomProperties();
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
        GfaParser parser;
        if (DrawableCanvas.getInstance().getParser() != null) {
            parser = DrawableCanvas.getInstance().getParser();
            parser.getDb().close();
        }
        parser = new GfaParser(filePath);
        DrawableCanvas.getInstance().setParser(parser);
        parser.addObserver(this);
        parser.addObserver(mC);
        this.addObserver(DrawableCanvas.getInstance());
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] partPaths = filePath.split(pattern);
        partPath = partPaths[partPaths.length - 1];
        properties.updateProperties();
        boolean flag = Boolean.parseBoolean(properties.getProperty(partPath, "true"));
        if (!flag) {
            popUpController = new PopUpController();
                    String message = "Database File is corrupt,"
                                + " press 'Reload' to reload the file," + "\n"
                                + "or press 'Resume' to recover the data still available.";
            popUpController.loadDbCorruptPopUp(partPath, message);
        }
        if (this.parseThread != null) {
            this.parseThread.interrupt();
        }
        this.parseThread = new Thread(parser);
        this.parseThread.start();

        progressBarController.run();
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
            if (arg instanceof Integer) {
                setChanged();
                notifyObservers(0);
                progressBarController.done();
            }
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
