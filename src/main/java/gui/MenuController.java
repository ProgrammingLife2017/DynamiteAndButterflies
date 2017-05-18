package gui;

import graph.SequenceGraph;
import gui.subControllers.BookmarkController;
import gui.subControllers.FileController;
import gui.subControllers.InfoController;
import gui.subControllers.ZoomController;
import graph.SequenceNode;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.mapdb.HTreeMap;

import java.io.IOException;
import java.util.prefs.Preferences;

/**
 * Created by Jasper van Tilburg on 1-5-2017.
 *
 * Controller for the Menu scene. Used to run all functionality
 * in the main screen of the application.
 */
public class MenuController {

    @FXML
    public Button saveBookmark;
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
    private GraphicsContext gc;

    private double pressedX;
    private Preferences prefs;

    private BookmarkController bookmarkController;
    private FileController fileController;
    private ZoomController zoomController;
    private InfoController infoController;

    /**
     * Initializes the canvas.
     */
    @FXML
    public void initialize() {
        canvas.widthProperty().bind(canvasPanel.widthProperty());
        canvas.heightProperty().bind(canvasPanel.heightProperty());
        gc = canvas.getGraphicsContext2D();

        prefs = Preferences.userRoot();
        fileController = new FileController();
        infoController = new InfoController(numNodesLabel, numEdgesLabel, sequenceInfo);
        bookmarkController = new BookmarkController(bookmark1, bookmark2);
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa
     * can be selected and directly be visualised on the screen.
     * @throws IOException if there is no file specified.
     */
    @FXML
    public void openFileClicked() throws IOException {
        String fileString = fileController.openFileClicked(anchorPane, gc);
        prefs.put("file", fileString);
        bookmarkController.loadBookmarks(fileString);
        zoomController = new ZoomController(fileController.getDrawer(),
                                nodeTextField, radiusTextField);

        displayInfo(fileController.getGraph());
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
        zoomController.zoomInClicked();
    }

    /**
     * ZoomOut Action Handler.
     * @throws IOException exception.
     */
    @FXML
    public void zoomOutClicked() throws IOException {
        zoomController.zoomOutClicked();
    }

    /**
     * Get the X-Coordinate of the cursor on click.
     * @param mouseEvent the mouse event.
     */
    @FXML
    public void clickMouse(MouseEvent mouseEvent) {
        pressedX = mouseEvent.getX();
        double y = mouseEvent.getY();
        SequenceNode clicked = fileController.getDrawer().clickNode(pressedX, y);
        if (clicked != null) {
            System.out.println(clicked.getId());
        }
    }

    /**
     *  The eventHandler for dragging the mouse.
     * @param mouseEvent The MouseEvent for dragging.
     */
    @FXML
    public void dragMouse(MouseEvent mouseEvent) {
        double xDifference = pressedX - mouseEvent.getX() / 2;
        fileController.getDrawer().moveShapes(xDifference);
    }

    /**
     * Adds a button to traverse the graph with.
     */
    public void traverseGraphClicked() {
        zoomController.traverseGraphClicked(fileController.getGraph().getNodes().size());
        int centreNodeID = zoomController.getCentreNodeID();
        String newString = "Sequence: "
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
        }
    }

    HTreeMap<Long, String> getSequenceHashMap() {
        return fileController.getSequenceHashMap();
    }
}
