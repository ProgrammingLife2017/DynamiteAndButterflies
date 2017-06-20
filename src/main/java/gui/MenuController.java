package gui;

import graph.Annotation;
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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
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
    public Button annoBut;
    @FXML
    public Button chooseGenome;
    @FXML
    public CheckBox rainbowBut;
    @FXML
    public Button zoomInBut;
    @FXML
    public Button zoomOutBut;
    @FXML
    public Button goToNodeBut;
    @FXML
    private MenuItem genome1;
    @FXML
    private MenuItem genome2;
    @FXML
    private MenuItem genome3;
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
    private MenuItem gffItem;
    @FXML
    private Button rightPannButton;
    @FXML
    private Button leftPannButton;

    private PrintStream ps;
    private GraphicsContext gc;
    private CustomProperties properties;
    private BookmarkController bookmarkController;
    private FileController fileController;
    private RecentController recentController;
    private PanningController panningController;
    private SpecificGenomeProperties specificGenomeProperties;
    private String filePath;

    /**
     * Initializes the canvas.
     */
    @FXML
    public void initialize() {
        canvas.widthProperty().bind(canvasPanel.widthProperty());
        canvas.heightProperty().bind(canvasPanel.heightProperty());
        GraphDrawer.getInstance().setCanvas(canvas);
        gc = canvas.getGraphicsContext2D();
        properties = new CustomProperties();

        fileController = new FileController(new ProgressBarController(progressBar));
        bookmarkController = new BookmarkController(bookmark1, bookmark2, bookmark3);
        recentController = new RecentController(file1, file2, file3);

        specificGenomeProperties = new SpecificGenomeProperties(genome1, genome2, genome3);

        ps = new PrintStream(new Console(consoleArea));
        DrawableCanvas.getInstance().setMenuController(this);
        DrawableCanvas.getInstance().setSpecificGenomeProperties(specificGenomeProperties);
        ZoomController.getInstance().setMenuController(this);
        Minimap.getInstance().setMenuController(this);

        //System.setErr(ps);
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
    public void openGfaFileClicked() throws IOException, InterruptedException {
        Stage stage = App.getStage();
        File file = fileController.chooseGfaFile(stage);
        String filePath = file.getAbsolutePath();
        openGfaFileClicked(filePath);
    }

    private void openGfaFileClicked(String filePath) throws IOException, InterruptedException {
        fileController.openGfaFileClicked(filePath);
        recentController.update(filePath);
    }


    /**
     * When 'open gff file' is clicked this method opens a filechooser from which a gff
     * can be selected and directly be visualised on the screen.
     *
     * @throws IOException          if there is no file specified.
     * @throws InterruptedException Exception when the Thread is interrupted.
     */
    @FXML
    public void openGffFileClicked() throws IOException, InterruptedException {
        Stage stage = App.getStage();
        File file = fileController.chooseGffFile(stage);
        String filePath = file.getAbsolutePath();
        //TODO: do something with this return value.
        GraphDrawer.getInstance().setAllAnnotations(fileController.openGffFileClicked(filePath));
        annoBut.setDisable(false);
    }

    private void displayInfo(SequenceGraph graph) {
        numNodesLabel.setText(graph.getFullGraphRightBoundID() + "");
        numEdgesLabel.setText(graph.totalSize() + "");
    }

    public void updateRadius() {
        radiusTextField.setText(GraphDrawer.getInstance().getRadius() + "");
    }

    /**
     * ZoomIn Action Handler.
     *
     * @throws IOException exception.
     */
    @FXML
    public void zoomInClicked() throws IOException {
        double xCentre = canvas.getWidth() / 2;
        ZoomController.getInstance().zoomIn(GraphDrawer.getInstance().mouseLocationColumn(xCentre));
        nodeTextField.setText(GraphDrawer.getInstance().findColumn(xCentre) + "");
    }

    /**
     * ZoomOut Action Handler.
     *
     * @throws IOException exception.
     */
    @FXML
    public void zoomOutClicked() throws IOException {
        double xCentre = canvas.getWidth() / 2;
        ZoomController.getInstance().zoomOut(GraphDrawer.getInstance().mouseLocationColumn(xCentre));
        nodeTextField.setText(GraphDrawer.getInstance().findColumn(xCentre) + "");
    }

    /**
     * Ensures the scroll bar zooms in and out.
     *
     * @param scrollEvent The scroll.
     * @throws IOException throws exception if column doesn't exist.
     */
    @FXML
    public void scrollZoom(ScrollEvent scrollEvent) throws IOException {
        int column = GraphDrawer.getInstance().mouseLocationColumn(scrollEvent.getX());
        nodeTextField.setText(GraphDrawer.getInstance().findColumn(scrollEvent.getX()) + "");
        if (scrollEvent.getDeltaY() > 0) {
            ZoomController.getInstance().zoomIn(column);
        } else {
            ZoomController.getInstance().zoomOut(column);
        }
    }

    /**
     * Get the X-Coordinate of the cursor on click.
     *
     * @param mouseEvent the mouse event.
     */
    @FXML
    public void clickMouse(MouseEvent mouseEvent) {
        System.out.println("Clickec on: " + mouseEvent.getX());
        canvasPanel.requestFocus();
        double pressedX = mouseEvent.getX();
        double pressedY = mouseEvent.getY();
        Minimap.getInstance().clickMinimap(pressedX, pressedY);
        SequenceNode clicked = null;
        try {
            clicked = GraphDrawer.getInstance().clickNode(pressedX, pressedY);
        } catch (NullPointerException e) {
            System.out.println("The graph is not yet loaded!");
            e.printStackTrace();
        }
        if (clicked != null) {
            String sequence = DrawableCanvas.getInstance().getParser().getSequenceHashMap().get((long) clicked.getId());
            sequenceInfo.setText(clicked.toString(sequence));
            nodeTextField.setText(clicked.getId().toString());
        }
    }

    /**
     * Adds a button to traverse the graph with.
     */
    @FXML
    public void traverseGraphClicked() {
        if (!nodeTextField.getText().equals("")) {
            int centreNodeID = Integer.parseInt(nodeTextField.getText());
            ZoomController.getInstance().traverseGraphClicked(centreNodeID, GraphDrawer.getInstance().getZoomLevel());
            SequenceNode node = GraphDrawer.getInstance().getGraph().getNode(centreNodeID);
            String sequence = DrawableCanvas.getInstance().getParser().getSequenceHashMap().get((long) centreNodeID);
            sequenceInfo.setText(node.toString(sequence));
        }
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
        controller.initialize(Integer.parseInt(nodeTextField.getText()),
                GraphDrawer.getInstance().getZoomLevel(), bookmarkController);

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
            int centre = Integer.parseInt(parts[1]);
            int radius = Integer.parseInt(parts[2]);
            double zoomLevel = GraphDrawer.getInstance().findZoomLevel(centre, radius);
            ZoomController.getInstance().traverseGraphClicked(centre, zoomLevel);
            nodeTextField.setText(centre + "");
        }
    }

    /**
     * getter for the SequenceMap.
     *
     * @return The sequenceMap.
     */
    HTreeMap<Long, String> getSequenceHashMap() {
        try {
            return DrawableCanvas.getInstance().getParser().getSequenceHashMap();
        } catch (NullPointerException e) {
            System.out.println("No graph was loaded so no sequenceHashMap to get");
        }
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof DrawableCanvas) {
            if (arg instanceof String) {
                filePath = (String) arg;

                properties.updateProperties();
                properties.setProperty("file", filePath);
                properties.saveProperties();

                gffItem.setDisable(false);

                Platform.runLater(new Runnable() {
                    public void run() {
                        Stage stage = App.getStage();
                        String title = stage.getTitle();
                        String split = "---";
                        String[] parts = title.split(split);
                        String offTitle = parts[0];
                        stage.setTitle(offTitle + split + filePath);
                        bookmarkController.initialize(filePath);
                        specificGenomeProperties.initialize();
                        panningController =
                                new PanningController(leftPannButton, rightPannButton);
                        panningController.initializeKeys(canvasPanel);
                        displayInfo(GraphDrawer.getInstance().getGraph());
                        updateRadius();
                        enableGuiElements();
                    }
                });
            }
        }
    }

    /**
     * Enables all the buttons and textfields a user could need to view the graph.
     */
    private void enableGuiElements() {
        zoomInBut.setDisable(false);
        zoomOutBut.setDisable(false);
        leftPannButton.setDisable(false);
        rightPannButton.setDisable(false);
        nodeTextField.setDisable(false);
        radiusTextField.setDisable(false);
        goToNodeBut.setDisable(false);
        chooseGenome.setDisable(false);
        rainbowBut.setDisable(false);
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
                openGfaFileClicked();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                openGfaFileClicked(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles pressing the specific genome button.
     *
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

        hashMap = DrawableCanvas.getInstance().getAllGenomesReversed();
        if (hashMap == null) {
            try {
                openGfaFileClicked();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        controller.initialize(hashMap, GraphDrawer.getInstance().getSelected());

        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Choose a specific genome to view");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnHidden(
                new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        GraphDrawer.getInstance().setSelected(controller.getSelectedGenomes());
                        GraphDrawer.getInstance().redraw();
                    }
                }
        );
        stage.showAndWait();
    }

    /**
     * Pressing on the first saved genomes.
     */
    @FXML
    public void genome1Click() {
        genomeBookmarkClicked(genome1);
    }

    /**
     * Pressing on the second saved genomes.
     */
    @FXML
    public void genome2Click() {
        genomeBookmarkClicked(genome2);
    }

    /**
     * Pressing on the third saved genomes.
     */
    @FXML
    public void genome3Click() {
        genomeBookmarkClicked(genome3);
    }

    /**
     * Generic genome bookmark function to not duplicate code.
     *
     * @param bookmark The MenuItem that was pressed.
     */
    private void genomeBookmarkClicked(MenuItem bookmark) {
        if (!bookmark.getText().equals("-")) {
            String string = bookmark.getText();
            String[] parts = string.split(" - ");
            //We skip parts[0] because that is "Genomes".
            String listOfIds = parts[1];

            parts = listOfIds.split(", ");
            int[] res = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                int oneSelected = Integer.parseInt(parts[i]);
                res[i] = oneSelected;
            }

            GraphDrawer.getInstance().setSelected(res);
            GraphDrawer.getInstance().redraw();
        }
    }

    /**
     * Handles choosing Annotations.
     *
     * @throws IOException if something goes wrong.
     */
    public void chooseAnnoClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/AnnotationTable.fxml"));
        Stage stage;
        Parent root = loader.load();
        final AnnotationTableController annotationTableController
                = loader.<AnnotationTableController>getController();

        ArrayList<Annotation> allAnnotations;
        allAnnotations = GraphDrawer.getInstance().getAllAnnotations();
        if (allAnnotations.size() == 0) {
            try {
                openGffFileClicked();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        annotationTableController.initialize(allAnnotations);

        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Search for annotations to view");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnHidden(
                new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        GraphDrawer.getInstance().
                                setSelectedAnnotations(annotationTableController.getSelection());
                        GraphDrawer.getInstance().redraw();
                    }
                }
        );
        stage.showAndWait();
    }

    public void rainbowButtonClicked() {
        GraphDrawer.getInstance().setRainbowView(rainbowBut.isSelected());
        GraphDrawer.getInstance().redraw();
    }

}
