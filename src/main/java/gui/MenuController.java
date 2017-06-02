package gui;

import graph.SequenceGraph;
import graph.SequenceNode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import gui.sub_controllers.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.mapdb.HTreeMap;
import parser.GfaParser;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

/**
 * Created by Jasper van Tilburg on 1-5-2017.
 *
 * Controller for the Menu scene. Used to run all functionality
 * in the main screen of the application.
 */
public class MenuController implements Observer {

    @FXML
    private Button saveBookmark;
    @FXML
    private MenuItem file1;
    @FXML
    private MenuItem file2;
    @FXML
    private MenuItem file3;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button bookmark1;
    @FXML
    private Button bookmark2;
    @FXML
    private Label sequenceInfo;
    @FXML
    private TextField nodeTextField;
    @FXML
    private TextField radiusTextField;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Canvas canvas;
    @FXML
    private AnchorPane canvasPanel;
    @FXML
    private Label numNodesLabel;
    @FXML
    private Label numEdgesLabel;
    @FXML
    private TextArea consoleArea;
    @FXML
    private CheckBox dummyNodeCheckbox;

    private PrintStream ps;

    private GraphicsContext gc;

    private Preferences prefs;

    private BookmarkController bookmarkController;
    private FileController fileController;
    private ZoomController zoomController;
    private InfoController infoController;
    private RecentController recentController;

    private String filePath;

    /**
     * Initializes the canvas.
     */
    @FXML
    public void initialize() {
        canvas.widthProperty().bind(canvasPanel.widthProperty());
        canvas.heightProperty().bind(canvasPanel.heightProperty());
        gc = canvas.getGraphicsContext2D();
        prefs = Preferences.userRoot();

        fileController = new FileController(new ProgressBarController(progressBar));
        infoController = new InfoController(numNodesLabel, numEdgesLabel, sequenceInfo);
        bookmarkController = new BookmarkController(bookmark1, bookmark2);
        recentController = new RecentController(file1, file2, file3);

        recentController.initialize();
        ps = new PrintStream(new Console(consoleArea));
        System.setErr(ps);
        System.setOut(ps);
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa
     * can be selected and directly be visualised on the screen.
     * @throws IOException if there is no file specified.
     * @throws InterruptedException Exception when the Thread is interrupted.
     */
    @FXML
    public void openFileClicked() throws IOException, InterruptedException {
        Stage stage = App.getStage();
        File file = fileController.chooseFile(stage);
        String filePath = file.getAbsolutePath();
        recentController.update(filePath);
        fileController.openFileClicked(gc, filePath, this);
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa
     * can be selected and directly be visualised on the screen.
     * @param filePath the filePath to the file that should be opened
     * @throws IOException if there is no file specified.
     * @throws InterruptedException Exception when the Thread is interrupted.
     */
    @FXML
    public void openFileClicked(String filePath) throws IOException, InterruptedException {
        fileController.openFileClicked(gc, filePath, this);
    }


    private void displayInfo(SequenceGraph graph) {
        infoController.displayInfo(graph);
        zoomController.displayInfo();
    }

    /**
     * ZoomIn Action Handler.
     * @throws IOException exception.
     */
    @FXML
    public void zoomInClicked() throws IOException {
        zoomController.zoomIn();
    }

    /**
     * ZoomOut Action Handler.
     * @throws IOException exception.
     */
    @FXML
    public void zoomOutClicked() throws IOException {
        zoomController.zoomOut();
    }

    /**
     * Ensures the scroll bar zooms in and out.
     * @param scrollEvent The scroll.
     * @throws IOException throws exception if column doesn't exist.
     */
    @FXML
    public void scrollZoom(ScrollEvent scrollEvent) throws IOException {
        int column = fileController.getDrawer().mouseLocationColumn(scrollEvent.getX());
        if (scrollEvent.getDeltaY() > 0) {
            zoomController.zoomIn(column);
        } else {
            zoomController.zoomOut(column);
        }
    }

    /**
     * Get the X-Coordinate of the cursor on click.
     * @param mouseEvent the mouse event.
     */
    @FXML
    public void clickMouse(MouseEvent mouseEvent) {
        double pressedX = mouseEvent.getX();
        double pressedY = mouseEvent.getY();
        SequenceNode clicked = fileController.getDrawer().clickNode(pressedX, pressedY);
        if (clicked != null) {
            String newString = "ID: " + clicked.getId() + "\nSequence: "
                    + fileController.getSequenceHashMap().get((long) clicked.getId());
            infoController.updateSeqLabel(newString);
        }
    }

    /**
     * Checks if the dummynodes is selected.
     */
    @FXML
    public void checkDummynodes() {
        if (dummyNodeCheckbox.isSelected()) {

        }
    }

    /**
     * Adds a button to traverse the graph with.
     */
    public void traverseGraphClicked() {
        zoomController.traverseGraphClicked(fileController.getGraph().getNodes().size());
        int centreNodeID = zoomController.getCentreNodeID();
        String newString = "ID: " + centreNodeID + "\nSequence: "
                + fileController.getSequenceHashMap().get((long) centreNodeID);
        infoController.updateSeqLabel(newString);
    }

    /**
     * Adds a button to traverse the graph with.
     * @param centreNode specifies the centre node to be showed
     * @param radius specifies the radius to be showed
     */
    private void traverseGraphClicked(String centreNode, String radius) {
        int centreNodeID = Integer.parseInt(centreNode);
        int rad = Integer.parseInt(radius);

        zoomController.traverseGraphClicked(fileController.getGraph().getNodes().size(),
                                            centreNodeID, rad);
        String newString = "Sequence: "
                + fileController.getSequenceHashMap().get((long) centreNodeID);
        infoController.updateSeqLabel(newString);
    }

    /**
     * Display dummy nodes on checkbox checked and hide them on checkbox unchecked.
     */
    @FXML
    public void toggleDummyNodes() {
        if (dummyNodeCheckbox.isSelected()) {
            fileController.getDrawer().setShowDummyNodes(true);
        } else {
            fileController.getDrawer().setShowDummyNodes(false);
        }
        fileController.getDrawer().redraw();
    }

    /**
     * Updates and saves the bookmarks.
     */
    @FXML
    public void saveTheBookmarks() {
        bookmarkController.saving(zoomController.getCentreNode(), zoomController.getRadius());
    }

    /**
     * Pressed of the bookmark1 button.
     */
    @FXML
    public void pressBookmark1() {
        bookmarked(bookmark1);
    }

    /**
     * Pressed of the bookmark2 button.
     */
    @FXML
    public void pressBookmark2() {
        bookmarked(bookmark2);
    }

    /**
     * Method used to not duplicate code in working out bookmarks.
     * @param bookmark the button that specifies the bookmark
     */
    private void bookmarked(Button bookmark) {
        if (!bookmark.getText().equals("-")) {
            String string = bookmark.getText();
            String[] parts = string.split("-");
            String centre = parts[0];
            String radius = parts[1];
            traverseGraphClicked(centre, radius);
            zoomController.setNodeTextField(centre);
            zoomController.setRadiusTextField(radius);
        }
    }

    /**
     * getter for the SequenceMap.
     * @return The sequenceMap.
     */
    HTreeMap<Long, String> getSequenceHashMap() {
        return fileController.getSequenceHashMap();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GfaParser) {
            if (arg instanceof String) {
                filePath = (String) arg;

                String pattern = Pattern.quote(System.getProperty("file.separator"));
                String[] partPaths = filePath.split(pattern);
                final String partPath = partPaths[partPaths.length - 1];
                prefs.put("file", partPath);
                Platform.runLater(new Runnable() {
                    public void run() {
                        Stage stage = App.getStage();
                        String title = stage.getTitle();
                        String split = "---";
                        String[] parts = title.split(split);
                        String offTitle = parts[0];
                        stage.setTitle(offTitle + split + filePath);
                        bookmarkController.loadBookmarks(partPath);
                        zoomController = new ZoomController(fileController.getDrawer(),
                                nodeTextField, radiusTextField);
                        displayInfo(fileController.getGraph());
                    }
                });
            }
        }
    }

    /**
     * Button one of the File -> Recent menu.
     */
    @FXML
    public void file1Press() {
        pressedRecent(file1);
    }

    /**
     * Button two of the File -> Recent menu.
     */
    @FXML
    public void file2Press() {
        pressedRecent(file2);
    }

    /**
     * Button three of the File -> Recent menu.
     */
    @FXML
    public void file3Press() {
        pressedRecent(file3);
    }

    /**
     * Method used to not duplicate recentFile presses.
     * @param file the menuItem that has been pressed
     */
    private void pressedRecent(MenuItem file) {
        String filePath = recentController.pressedRecent(file);

        if (filePath == null) {
            try {
                openFileClicked();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                openFileClicked(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Will open tutorial when Help-Help is called.
     * @throws IOException Throws IO if fxml file can't be found.
     */
    public void helpWanted() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/tutorial.fxml"));
        Stage stage;
        Parent root = loader.load();
        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Tutorial");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.showAndWait();
    }
}
