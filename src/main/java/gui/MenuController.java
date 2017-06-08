package gui;

import graph.SequenceGraph;
import graph.SequenceNode;
import gui.sub_controllers.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.mapdb.HTreeMap;
import parser.GfaParser;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Jasper van Tilburg on 1-5-2017.
 * <p>
 * Controller for the Menu scene. Used to run all functionality
 * in the main screen of the application.
 */
public class MenuController implements Observer {

    @FXML
    private MenuItem file1;
    @FXML
    private MenuItem file2;
    @FXML
    private MenuItem file3;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private MenuItem bookmark1;
    @FXML
    private MenuItem bookmark2;
    @FXML
    private MenuItem bookmark3;
    @FXML
    private Label sequenceInfo;
    @FXML
    private MenuItem saveBookmark;
    @FXML
    private TextField nodeTextField;
    @FXML
    private TextField radiusTextField;
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
    private ScrollBar scrollbar;

    private PrintStream ps;
    private GraphicsContext gc;
    private CustomProperties properties;
    private BookmarkController bookmarkController;
    private FileController fileController;
    private ZoomController zoomController;
    private InfoController infoController;
    private RecentController recentController;
    private PanningController panningController;

    private boolean[] selectedGenomes;

    private String filePath;

    /**
     * Initializes the canvas.
     */
    @FXML
    public void initialize() {
        canvas.widthProperty().bind(canvasPanel.widthProperty());
        canvas.heightProperty().bind(canvasPanel.heightProperty());
        gc = canvas.getGraphicsContext2D();
        properties = new CustomProperties();

        fileController = new FileController(new ProgressBarController(progressBar));
        infoController = new InfoController(numNodesLabel, numEdgesLabel, sequenceInfo);
        bookmarkController = new BookmarkController(bookmark1, bookmark2, bookmark3);
        recentController = new RecentController(file1, file2, file3);

        ps = new PrintStream(new Console(consoleArea));
        System.setErr(ps);
        System.setOut(ps);
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa
     * can be selected and directly be visualised on the screen.
     *
     * @throws IOException          if there is no file specified.
     * @throws InterruptedException Exception when the Thread is interrupted.
     */
    @FXML
    public void openFileClicked() throws IOException, InterruptedException {
        Stage stage = App.getStage();
        File file = fileController.chooseFile(stage);
        String filePath = file.getAbsolutePath();
        recentController.update(filePath);
        fileController.openFileClicked(gc, filePath, this);
        selectedGenomes = null;
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa
     * can be selected and directly be visualised on the screen.
     *
     * @param filePath the file that should be opened
     * @throws IOException          if there is no file specified.
     * @throws InterruptedException Exception when the Thread is interrupted.
     */
    @FXML
    private void openFileClicked(String filePath) throws IOException, InterruptedException {
        fileController.openFileClicked(gc, filePath, this);
        selectedGenomes = null;
    }

    private void displayInfo(SequenceGraph graph) {
        infoController.displayInfo(graph);
        zoomController.displayInfo();
    }

    /**
     * ZoomIn Action Handler.
     *
     * @throws IOException exception.
     */
    @FXML
    public void zoomInClicked() throws IOException {
        double xCentre = canvas.getWidth() / 2;
        zoomController.zoomIn(fileController.getDrawer().mouseLocationColumn(xCentre));
        nodeTextField.setText(fileController.getDrawer().findColumn(xCentre) + "");
    }

    /**
     * ZoomOut Action Handler.
     *
     * @throws IOException exception.
     */
    @FXML
    public void zoomOutClicked() throws IOException {
        double xCentre = canvas.getWidth() / 2;
        zoomController.zoomOut(fileController.getDrawer().mouseLocationColumn(xCentre));
        nodeTextField.setText(fileController.getDrawer().findColumn(xCentre) + "");
    }

    /**
     * Ensures the scroll bar zooms in and out.
     *
     * @param scrollEvent The scroll.
     * @throws IOException throws exception if column doesn't exist.
     */
    @FXML
    public void scrollZoom(ScrollEvent scrollEvent) throws IOException {
        int column = fileController.getDrawer().mouseLocationColumn(scrollEvent.getX());
        nodeTextField.setText(fileController.getDrawer().findColumn(scrollEvent.getX()) + "");
        if (scrollEvent.getDeltaY() > 0) {
            zoomController.zoomIn(column);
        } else {
            zoomController.zoomOut(column);
        }
    }

    /**
     * Get the X-Coordinate of the cursor on click.
     *
     * @param mouseEvent the mouse event.
     */
    @FXML
    public void clickMouse(MouseEvent mouseEvent) {
        double pressedX = mouseEvent.getX();
        double pressedY = mouseEvent.getY();
        SequenceNode clicked = fileController.getDrawer().clickNode(pressedX, pressedY);
        if (clicked != null) {
            String newString = "\nSequence: "
                    + fileController.getSequenceHashMap().get((long) clicked.getId()) + "\n";


            String childString = "Children: ";
            for (Integer i : clicked.getChildren()) {
                childString += i.toString() + "\n";
            }

            String parentString = "Parents: ";
            for (Integer i : clicked.getParents()) {
                parentString += i.toString() + "\n";
            }

            String nodeID = "Node ID: " + Integer.toString(clicked.getId()) + "\n";

            String columnString = "Column index: " + Integer.toString(clicked.getColumn()) + "\n";

            String genomeInfo = "Genomes that go through this:\t";
            for (Integer i : clicked.getGenomes()) {
                genomeInfo += i.toString() + "\t";
            }

            String concat = nodeID + columnString + parentString
                            + childString + genomeInfo + newString;
            infoController.updateSeqLabel(concat);
        }
    }

    /**
     * Adds a button to traverse the graph with.
     */
    public void traverseGraphClicked() {
        int centreNodeID = Integer.parseInt(nodeTextField.getText());
        int radius = Integer.parseInt(radiusTextField.getText());
        zoomController.traverseGraphClicked(centreNodeID, radius);
        String newString = "ID: " + centreNodeID + "\nSequence: "
                + fileController.getSequenceHashMap().get((long) centreNodeID);
        infoController.updateSeqLabel(newString);
    }

    /**
     * Adds a button to traverse the graph with.
     *
     * @param centreNode specifies the centre node to be showed
     * @param radius     specifies the radius to be showed
     */
    private void traverseGraphClicked(String centreNode, String radius) {
        int centreNodeID = Integer.parseInt(centreNode);
        int rad = Integer.parseInt(radius);

        zoomController.traverseGraphClicked(centreNodeID, rad);
        String newString = "Sequence: "
                + fileController.getSequenceHashMap().get((long) centreNodeID);
        infoController.updateSeqLabel(newString);
    }

    /**
     * Pressed the bookmark1 menuItem.
     */
    @FXML
    public void pressNewBookmark1() {
        bookmarked(bookmark1);
    }

    /**
     * Pressed the bookmark2 menuItem.
     */
    @FXML
    public void pressNewBookmark2() {
        bookmarked(bookmark2);
    }

    /**
     * Pressed the bookmark3 menuItem.
     */
    @FXML
    public void pressNewBookmark3() {
        bookmarked(bookmark3);
    }

    /**
     * Updates and saves the bookmarks.
     *
     * @throws IOException Throws expception if it can't find the fxml file.
     */
    @FXML
    public void newSaveBookmarkPress() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/bookmarkPopUp.fxml"));
        Stage stage;
        Parent root = loader.load();
        BookmarkPopUp controller = loader.<BookmarkPopUp>getController();
        controller.initialize(zoomController.getCentreNode(),
                zoomController.getRadius(), bookmarkController);

        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Add a bookmark");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    /**
     * Handles pressing the manage bookmark button.
     *
     * @throws IOException if the fxml file doesn't exist.
     */
    @FXML
    public void manageBookmarks() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/manageBookmarks.fxml"));
        Stage stage;
        Parent root = loader.load();

        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Manage bookmarks");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    /**
     * Method used to not duplicate code in working out bookmarks.
     *
     * @param bookmark the button that specifies the bookmark
     */
    private void bookmarked(MenuItem bookmark) {
        if (!bookmark.getText().equals("-")) {
            String string = bookmark.getText();
            String[] parts = string.split(" - ");
            //We skip parts[0] because that is the note.
            String centre = parts[1];
            String radius = parts[2];
            traverseGraphClicked(centre, radius);
            zoomController.setNodeTextField(centre);
            zoomController.setRadiusTextField(radius);
        }
    }

    /**
     * getter for the SequenceMap.
     *
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

                properties.updateProperties();
                properties.setProperty("file", filePath);
                properties.saveProperties();

                Platform.runLater(new Runnable() {
                    public void run() {
                        Stage stage = App.getStage();
                        String title = stage.getTitle();
                        String split = "---";
                        String[] parts = title.split(split);
                        String offTitle = parts[0];
                        stage.setTitle(offTitle + split + filePath);
                        bookmarkController.initialize(filePath);
                        panningController =
                                new PanningController(scrollbar, fileController.getDrawer());
                        zoomController = new ZoomController(fileController.getGraph(),
                                fileController.getDrawer(), panningController,
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
     *
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
     * Handles pressing the specific genome button.
     * @throws IOException when something goes wrong with IO.
     */
    @FXML
    public void chooseGenomePress() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/specGenomePopUp.fxml"));
        Stage stage;
        Parent root = loader.load();
        final SpecificGenomeController controller
                = loader.<SpecificGenomeController>getController();

        HashMap<Integer, String> hashMap;

        hashMap = fileController.getAllGenomes();
        if (hashMap == null) {
            try {
                openFileClicked();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        controller.initialize(hashMap, fileController.getDrawer().getSelected());

        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Choose a specific genome to view");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnHidden(
                new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        fileController.getDrawer().setSelected(controller.getSelectedGenomes());
                        fileController.getDrawer().redraw();
                    }
                }
        );
        stage.showAndWait();
    }
}
