package gui.sub_controllers;

import gui.GraphDrawer;
import structures.Annotation;
import gui.CustomProperties;
import gui.DrawableCanvas;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import parser.GfaParser;
import parser.GffParser;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Controller for opening a file.
 */
public class FileController extends Observable implements Observer {

    private File gfaParDirectory;
    private File gffParDirectory;
    private final ProgressBarController progressBarController;
    private Thread parseThread;
    private final CustomProperties properties;

    /**
     * Constructor of the FileController object to control the Files.
     *
     * @param pbc The progressbar.
     */
    public FileController(ProgressBarController pbc) {
        gfaParDirectory = null;
        gffParDirectory = null;
        progressBarController = pbc;

        properties = new CustomProperties();
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa.
     * can be selected and directly be visualised on the screen.
     *
     * @param stage The stage on which the fileFinder is shown.
     * @return returns the file that can be loaded.
     */
    public File chooseGfaFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        if (gfaParDirectory == null) {
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.dir")).getParentFile()
            );
        } else {
            fileChooser.setInitialDirectory(
                    gfaParDirectory
            );
        }

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("GFA", "*.gfa")
        );
        File res = fileChooser.showOpenDialog(stage);
        gfaParDirectory = res.getParentFile();
        return res;
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa.
     * can be selected and directly be visualised on the screen.
     *
     * @param stage The stage on which the fileFinder is shown.
     * @return returns the file that can be loaded.
     */
    public File chooseGffFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Annotation File");

        if (gffParDirectory == null) {
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.dir")).getParentFile()
            );
        } else {
            fileChooser.setInitialDirectory(
                    gffParDirectory
            );
        }

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("GFF", "*.gff")
        );
        File res = fileChooser.showOpenDialog(stage);
        gffParDirectory = res.getParentFile();
        return res;
    }


    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa.
     * can be selected and directly be visualised on the screen.
     *
     * @param filePath the filePath of the file.
     */
    public void openGfaFileClicked(String filePath) {
        if (DrawableCanvas.getInstance().getParser() != null) {
            DrawableCanvas.getInstance().getParser().getDb().close();
        }
        GfaParser parser = new GfaParser(filePath);
        DrawableCanvas.getInstance().setParser(parser);
        parser.addObserver(this);
        this.addObserver(DrawableCanvas.getInstance());
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] partPaths = filePath.split(pattern);
        String partPath = partPaths[partPaths.length - 1];
        properties.updateProperties();
        GraphDrawer.getInstance().setyDifference(0);
        boolean flag = Boolean.parseBoolean(properties.getProperty(partPath, "true"));
        if (!flag) {
            PopUpController popUpController = new PopUpController();
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
     * Opens a gff file.
     *
     * @param filePath The filepath where we should open it
     * @return A bucketList of all the annotations.
     * @throws IOException If the filepath does not exist.
     */
    public HashMap<Integer, TreeSet<Annotation>>
    openGffFileClicked(String filePath) throws IOException {
        GffParser parser = new GffParser(filePath);
        return parser.parseGff();
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
}
