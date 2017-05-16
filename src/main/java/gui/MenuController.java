package gui;

import graph.SequenceGraph;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import parser.GfaParser;

import java.io.File;
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
    private Button saveBookmark;
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
    private gui.GraphDrawer drawer;
    private SequenceGraph graph;

    static Preferences prefs = Preferences.userRoot();

    /**
     * Initializes the canvas.
     */
    @FXML
    public void initialize() {
        canvas.widthProperty().bind(canvasPanel.widthProperty());
        canvas.heightProperty().bind(canvasPanel.heightProperty());
        gc = canvas.getGraphicsContext2D();
        if (prefs.get("numOfBookmarks", "def").equals("def")) {
            prefs.put("numOfBookmarks", "0");
        }
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa
     * can be selected and directly be visualised on the screen.
     */
    @FXML
    public void openFileClicked() {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        //fileChooser.setInitialDirectory(this.getClass().getResource("/resources").toString());
        File file = fileChooser.showOpenDialog(stage);
        prefs.put("file", file.toString());

        if (file != null) {
            try {
                openFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int largestIndex = Integer.parseInt(prefs.get("numOfBookmarks", "def"));
        int i = 0;
        String stringFile = file.toString();

        while (i <= largestIndex) {
            int newIndex = i;
            String realBM = prefs.get(stringFile + newIndex, "-");
            updateBookmarks(realBM);
            i++;
        }
    }

    private void openFile(File file) throws IOException {
        GfaParser parser = new GfaParser();
        System.out.println("src/main/resources/" + file.getName());
        graph = parser.parse(file.getAbsolutePath());
        drawer = new GraphDrawer(graph, gc);
        drawer.moveShapes(0.0);
        displayInfo(graph);
    }

    private void displayInfo(SequenceGraph graph) {
        numNodesLabel.setText(graph.getNodes().size() + "");
        numEdgesLabel.setText(graph.getEdges().size() + "");
        nodeTextField.setText(drawer.getRealCentreNode().getId() + "");
        radiusTextField.setText(drawer.getZoomLevel() + "");
    }

    /**
     * ZoomIn Action Handler.
     * @throws IOException exception.
     */
    @FXML
    public void zoomInClicked() throws IOException {
        if (!getNodeTextField().getText().equals("")) {
            drawer.zoomIn(0.8, drawer.getColumnId(Integer.parseInt(getNodeTextField().getText())));
            radiusTextField.setText(drawer.getZoomLevel() + "");
        } else {
            drawer.zoomIn(0.8, drawer.getRealCentreNode().getColumn());
            radiusTextField.setText(drawer.getZoomLevel() + "");
        }

    }

    /**
     * ZoomOut Action Handler.
     * @throws IOException exception.
     */
    @FXML
    public void zoomOutClicked() throws IOException {
        if (!getNodeTextField().getText().equals("")) {
            drawer.zoomOut(1.2, drawer.getColumnId(Integer.parseInt(getNodeTextField().getText())));
            radiusTextField.setText(drawer.getZoomLevel() + "");
        } else {
            drawer.zoomOut(1.2, drawer.getRealCentreNode().getColumn());
            radiusTextField.setText(drawer.getZoomLevel() + "");
        }
    }

    private double pressedX;

    /**
     * Get the X-Coordinate of the cursor on click.
     * @param mouseEvent the mouse event.
     */
    @FXML
    public void clickMouse(MouseEvent mouseEvent) {
        pressedX = mouseEvent.getX();
    }

    /**
     *  The eventHandler for dragging the mouse.
     * @param mouseEvent The MouseEvent for dragging.
     */
    @FXML
    public void dragMouse(MouseEvent mouseEvent) {
        double xDifference = pressedX - mouseEvent.getX() / 2;
        drawer.moveShapes(xDifference);
    }

    /**
     * Adds a button to traverse the graph with.
     */
    public void traverseGraphClicked() {
        int centreNodeID = Integer.parseInt(getNodeTextField().getText());
        drawer.changeZoom(getEndColumn() - getStartColumn(), drawer.getColumnId(centreNodeID));
        sequenceInfo.setText("Sequence: " + graph.getNode(centreNodeID).getSequence());
    }

    /**
     * Adds a button to traverse the graph with.
     * @param centreNode specifies the centre node to be showed
     * @param radius specifies the radius to be showed
     */
    private void traverseGraphClicked(String centreNode, String radius) {
        int centreNodeID = Integer.parseInt(centreNode);
        drawer.changeZoom(getEndColumn(centreNode, radius) - getStartColumn(centreNode, radius), drawer.getColumnId(centreNodeID));
        sequenceInfo.setText("Sequence: " + graph.getNode(centreNodeID).getSequence());
    }

    /**
     * Gets the start column based on the text fields.
     * @return integer representing the starting column
     */
    private int getStartColumn() {
        return getStartColumn(getNodeTextField().getText(), getRadiusTextField().getText());
    }

    private int getStartColumn(String centre, String rad) {
        int centreNode = Integer.parseInt(centre);
        int radius = Integer.parseInt(rad);

        int startNode = centreNode - radius;
        if (startNode < 1) {
            startNode = 1;
        }
        return drawer.getColumnId(startNode);
    }

    private int getEndColumn() {
        return getEndColumn(getNodeTextField().getText(), getRadiusTextField().getText());
    }

    private int getEndColumn(String centre, String rad) {
        int centreNode = Integer.parseInt(centre);
        int radius = Integer.parseInt(rad);

        int endNode = centreNode + radius;
        if (endNode > graph.getNodes().size()) {
            endNode = graph.getNodes().size();
        }
        return drawer.getColumnId(endNode);
    }

    /**
     * Getter for the Node textfield.
     * @return The text in the textfield.
     */
    private TextField getNodeTextField() {
        return nodeTextField;
    }

    /**
     * Getter for the radius textfield.
     * @return The text in the textfield.
     */
    private TextField getRadiusTextField() {
        return radiusTextField;
    }

    /**
     * Updates and saves the bookmarks.
     */
    @FXML
    public void saveTheBookmarks() {
        TextField nodes = getNodeTextField();
        TextField radius = getRadiusTextField();

        String numOfBookmarks = "";
        int newIndex = Integer.parseInt(prefs.get("numOfBookmarks", numOfBookmarks));
        newIndex++;
        String file = prefs.get("file", "def");
        prefs.put(file + newIndex, nodes.getText() + "-" + radius.getText());
        prefs.put("numOfBookmarks", "" + newIndex);

        updateBookmarks(nodes.getText() + "-" + radius.getText());
    }

    private void updateBookmarks(String newBookmark) {
        bookmark2.setText(bookmark1.getText());
        bookmark1.setText(newBookmark);
    }

    /**
     * Pressed of the bookmark1 button.
     */
    @FXML
    public void pressBookmark1() {
        if (!bookmark1.getText().equals("-")) {
            bookmarked(bookmark1);
        }
    }

    /**
     * Pressed of the bookmark2 button.
     */
    @FXML
    public void pressBookmark2() {
        if (!bookmark2.getText().equals("-")) {
            bookmarked(bookmark2);
        }
    }

    /**
     * Method used to not duplicate code in working out bookmarks.
     * @param bookmark the button that specifies the bookmark
     */
    private void bookmarked(Button bookmark) {
        String string = bookmark.getText();
        String[] parts = string.split("-");
        String centre = parts[0];
        String radius = parts[1];
        traverseGraphClicked(centre, radius);
    }
}
