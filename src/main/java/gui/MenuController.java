package gui;

import exceptions.NotInRangeException;
import graph.SequenceGraph;
import graph.SequenceNode;
import gui.sub_controllers.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.mapdb.BTreeMap;
import structures.Annotation;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;

/**
 * Created by Jasper van Tilburg on 1-5-2017.
 * <p>
 * Controller for the Menu scene. Used to run all functionality
 * in the main screen of the application.
 */
public class MenuController implements Observer {

    @FXML
    private TextField genomeCoordinate;
    @FXML
    private Button goToGenCorBut;
    @FXML
    private Button chooseGenTraverseBut;
    @FXML
    private Button annoBut;
    @FXML
    private Button chooseGenome;
    @FXML
    private CheckBox rainbowBut;
    @FXML
    private Button zoomInBut;
    @FXML
    private Button zoomOutBut;
    @FXML
    private Button goToNodeBut;
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
    private TextArea sequenceInfo;
    @FXML
    private TextArea sequenceInfoAlt;
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
    @FXML
    private CheckBox collapseSNPButton;
    @FXML
    private ScrollBar scrollBar;
    @FXML
    private Button screenshotButton;

    private PrintStream ps;
    private CustomProperties properties;
    private BookmarkController bookmarkController;
    private FileController fileController;
    private RecentController recentController;
    private RecentGenomeController recentGenomeController;
    private String filePath;

    public MenuController() {
    }

    /**
     * Initializes the canvas.
     */
    @FXML
    private void initialize() {
        canvas.widthProperty().bind(canvasPanel.widthProperty());
        canvas.heightProperty().bind(canvasPanel.heightProperty());
        GraphDrawer.getInstance().setCanvas(canvas);
        properties = new CustomProperties();

        fileController = new FileController(new ProgressBarController(progressBar));
        bookmarkController = new BookmarkController(bookmark1, bookmark2, bookmark3);
        recentController = new RecentController(file1, file2, file3);

        recentGenomeController = new RecentGenomeController(genome1, genome2, genome3);

        ps = new PrintStream(new Console(consoleArea));
        DrawableCanvas.getInstance().setMenuController(this);
        DrawableCanvas.getInstance().setRecentGenomeController(recentGenomeController);
        ScrollbarController.getInstance().setScrollBar(scrollBar);
        ZoomController.getInstance().setMenuController(this);
        Minimap.getInstance().setMenuController(this);
        GraphDrawer.getInstance().setMenuController(this);
        PanningController.getInstance().setMenuController(this);
        PanningController.getInstance().initialize(leftPannButton, rightPannButton);

        //System.setErr(ps);
        //System.setOut(ps);
    }

    /**
     * When 'open gfa file' is clicked this method opens a fileChooser from which a gfa
     * can be selected and directly be visualised on the screen.
     *
     * @throws IOException          if there is no file specified.
     * @throws InterruptedException Exception when the Thread is interrupted.
     */
    @FXML
    private void openGfaFileClicked() throws IOException, InterruptedException {
        Stage stage = App.getStage();
        File file = fileController.chooseGfaFile(stage);
        String filePath = file.getAbsolutePath();
        openGfaFileClicked(filePath);
    }

    /**
     * This method parses the gfa file specified in the filePath.
     * That file is then immediately visualised on the screen.
     *
     * @param filePath The filepath to the .gfa file
     * @throws IOException          if it can't parse/find the file
     * @throws InterruptedException if it is interrupted
     */
    private void openGfaFileClicked(String filePath) throws IOException, InterruptedException {
        GraphDrawer.getInstance().reset();
        fileController.openGfaFileClicked(filePath);
        recentController.update(filePath);
        annoBut.setDisable(true);
        PanningController.getInstance().initializeKeys(canvasPanel);
    }


    /**
     * When 'open gff file' is clicked this method opens a filechooser from which a gff
     * can be selected and directly be visualised on the screen.
     *
     * @throws IOException          if there is no file specified.
     * @throws InterruptedException Exception when the Thread is interrupted.
     */
    @FXML
    private void openGffFileClicked() throws IOException, InterruptedException {
        Stage stage = App.getStage();
        File file = fileController.chooseGffFile(stage);
        String filePath = file.getAbsolutePath();
        HashMap<Integer, TreeSet<Annotation>> annotations =
                fileController.openGffFileClicked(filePath);
        Annotation.selectAll(annotations);
        GraphDrawer.getInstance().setAllAnnotations(annotations);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/FXML/chooseGenomeForAnnotations.fxml"));
        Stage newStage;
        Parent root = loader.load();
        final GffGenomeController gffGenomeController
                = loader.<GffGenomeController>getController();

        HashMap<Integer, String> hashMap = DrawableCanvas.getInstance().getAllGenomesReversed();
        if (hashMap == null) {
            try {
                openGfaFileClicked();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        gffGenomeController.initialize(hashMap, DrawableCanvas.getInstance().getAnnotationGenome());

        newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setTitle("Choose a genome to load the annotation on");
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setOnHidden(
                new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        DrawableCanvas.getInstance().setAnnotationGenome(
                                gffGenomeController.getSelectedGenome());
                        GraphDrawer.getInstance().redraw();
                    }
                }
        );
        newStage.showAndWait();

        annoBut.setDisable(false);
    }

    /**
     * Sets the size of the graph after loading it.
     *
     * @param graph The graph with the information
     */
    private void displayInfo(SequenceGraph graph) {
        numNodesLabel.setText(graph.getFullGraphRightBoundID() + "");
        numEdgesLabel.setText(graph.totalSize() + "");
    }

    /**
     * Updates the radius.
     */
    public void updateRadius() {
        radiusTextField.setText(GraphDrawer.getInstance().getRadius() + "");
    }

    /**
     * Updates the alternative information window.
     *
     * @param node the Node that should be shown.
     */
    void updateSequenceInfoAlt(SequenceNode node) {
        String sequence = DrawableCanvas.getInstance().getParser().
                getSequenceHashMap().get((long) node.getId());
        sequenceInfoAlt.setText(node.toString(sequence));
    }

    /**
     * Updates the alternative information window.
     *
     * @param node the Node that should be shown.
     */
    void updateSequenceInfo(SequenceNode node) {
        String sequence = DrawableCanvas.getInstance().getParser().
                getSequenceHashMap().get((long) node.getId());
        sequenceInfo.setText(node.toString(sequence));
    }

    /**
     * Use this method to get a real node in the nodeTextField.
     *
     * @param xEvent Used to find the column needed.
     * @return A positive int of the real node in the column.
     */
    private int findColumnWrapper(Double xEvent) {
        int nodeID = GraphDrawer.getInstance().findColumn(xEvent);
        if (nodeID < 1) {
            nodeID = Integer.parseInt(nodeTextField.getText());
        }
        return nodeID;
    }


    /**
     * ZoomIn Action Handler.
     */
    @FXML
    public void zoomInClicked() {
        double xCentre = canvas.getWidth() / 2;
        ZoomController.getInstance().zoomIn(
                GraphDrawer.getInstance().mouseLocationColumn(xCentre));
        nodeTextField.setText(findColumnWrapper(xCentre) + "");
    }

    /**
     * ZoomOut Action Handler.
     */
    @FXML
    public void zoomOutClicked() {
        double xCentre = canvas.getWidth() / 2;
        ZoomController.getInstance().zoomOut(
                GraphDrawer.getInstance().mouseLocationColumn(xCentre));
        nodeTextField.setText(findColumnWrapper(xCentre) + "");
    }

    /**
     * Ensures the scroll bar zooms in and out.
     *
     * @param scrollEvent The scroll.
     * @throws IOException throws exception if column doesn't exist.
     */
    @FXML
    private void scrollZoom(ScrollEvent scrollEvent) throws IOException {
        int column = GraphDrawer.getInstance().mouseLocationColumn(scrollEvent.getX());
        nodeTextField.setText(findColumnWrapper(scrollEvent.getX()) + "");
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
    private void clickMouse(MouseEvent mouseEvent) {
        canvasPanel.requestFocus();
        double pressedX = mouseEvent.getX();
        double pressedY = mouseEvent.getY();
        Minimap.getInstance().clickMinimap(pressedX, pressedY);
        Object clicked = null;
        try {
            GraphDrawer.getInstance().clickOnCanvas(pressedX, pressedY, mouseEvent);
        } catch (NullPointerException e) {
            System.err.println("The graph is not yet loaded!");
            e.printStackTrace();
        }
    }

    /**
     * Updates the information fields with annotation information.
     *
     * @param controlDown boolean true iff control is down
     * @param clicked     The annotation that was clicked
     */
    void updateInfoAnnotation(boolean controlDown, Annotation clicked) {
        if (!controlDown) {
            sequenceInfo.setText(clicked.toString());
        } else {
            sequenceInfoAlt.setText(clicked.toString());
        }
    }

    /**
     * Updates the information fields with node information.
     *
     * @param controlDown boolean true iff control is down
     * @param clicked     The node that was clicked
     */
    void updateInfoSeqNode(boolean controlDown, SequenceNode clicked) {
        String sequence = DrawableCanvas.getInstance().getParser().
                getSequenceHashMap().get((long) clicked.getId());
        if (!controlDown) {
            sequenceInfo.setText(clicked.toString(sequence));
            nodeTextField.setText(clicked.getId().toString());
        } else {
            sequenceInfoAlt.setText(clicked.toString(sequence));
        }
        GraphDrawer.getInstance().redraw();
    }

    /**
     * Adds a button to traverse the graph with.
     */
    @FXML
    private void traverseGraphClicked() {
        int centreNodeID = getCentreNodeID();
        int radius = getRadius();
        if (centreNodeID != -1 && radius != -1) {
            ZoomController.getInstance().traverseGraphClicked(centreNodeID, radius);
            SequenceNode node = GraphDrawer.getInstance().getGraph().getNode(centreNodeID);
            String sequence = DrawableCanvas.getInstance().getParser()
                    .getSequenceHashMap().get((long) centreNodeID);
            GraphDrawer.getInstance().highlightNode(centreNodeID);
            nodeTextField.setText(Integer.toString(centreNodeID));
            sequenceInfo.setText(node.toString(sequence));
        }
    }

    /**
     * Pressed the bookmark1 menuItem.
     */
    @FXML
    private void pressNewBookmark1() {
        bookmarkPressed(bookmark1);
    }

    /**
     * Pressed the bookmark2 menuItem.
     */
    @FXML
    private void pressNewBookmark2() {
        bookmarkPressed(bookmark2);
    }

    /**
     * Pressed the bookmark3 menuItem.
     */
    @FXML
    private void pressNewBookmark3() {
        bookmarkPressed(bookmark3);
    }

    /**
     * Updates and saves the bookmarks.
     *
     * @throws IOException Throws expception if it can't find the fxml file.
     */
    @FXML
    private void newSaveBookmarkPress() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/bookmarkPopUp.fxml"));
        Stage stage;
        Parent root = loader.load();
        BookmarkPopUp controller = loader.<BookmarkPopUp>getController();
        controller.initialize(Integer.parseInt(nodeTextField.getText()), bookmarkController);

        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Add a bookmark");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    /**
     * Method used to not duplicate code in working out bookmarks.
     *
     * @param bookmark the button that specifies the bookmark
     */
    private void bookmarkPressed(MenuItem bookmark) {
        if (!bookmark.getText().equals("-")) {
            String string = bookmark.getText();
            String[] parts = string.split(" - ");
            //We skip parts[0] because that is the note.
            int centre = Integer.parseInt(parts[1]);
            int radius = Integer.parseInt(parts[2]);
            ZoomController.getInstance().traverseGraphClicked(centre, radius);
            GraphDrawer.getInstance().highlightNode(centre);
            nodeTextField.setText(centre + "");
        }
    }

    /**
     * Updates the centreNode.
     */
    private void updateCenterNode() {
        nodeTextField.setText(Integer.toString(
                GraphDrawer.getInstance().getGraph().getCenterNodeID()));
    }

    /**
     * getter for the SequenceMap.
     *
     * @return The sequenceMap.
     */
    BTreeMap<Long, String> getSequenceHashMap() {
        try {
            return DrawableCanvas.getInstance().getParser().getSequenceHashMap();
        } catch (NullPointerException e) {
            System.err.println("No graph was loaded so no sequenceHashMap to get");
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
                        recentGenomeController.initialize();
                        displayInfo(GraphDrawer.getInstance().getGraph());
                        updateRadius();
                        updateCenterNode();
                        enableGuiElements();
                    }
                });
            }
        }
    }

    /**
     * Enables all the buttons and textFields a user could need to view the graph.
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
        collapseSNPButton.setDisable(false);
        screenshotButton.setDisable(false);
        chooseGenTraverseBut.setDisable(false);
    }

    /**
     * Button one of the File -> Recent menu.
     */
    @FXML
    private void file1Press() {
        pressedRecent(file1);
    }

    /**
     * Button two of the File -> Recent menu.
     */
    @FXML
    private void file2Press() {
        pressedRecent(file2);
    }

    /**
     * Button three of the File -> Recent menu.
     */
    @FXML
    private void file3Press() {
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
            } catch (IOException | InterruptedException e1) {
                System.err.println("Something went wrong opening GfaFileClicked");
            }
        } else {
            try {
                openGfaFileClicked(filePath);
            } catch (IOException | InterruptedException e) {
                System.err.println("Something went wrong opening GfaFileClicked");
            }
        }
    }

    /**
     * Handles pressing the specific genome button.
     *
     * @throws IOException when something goes wrong with IO.
     */
    @FXML
    private void chooseGenomePress() throws IOException {
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
    private void genome1Click() {
        genomeBookmarkClicked(genome1);
    }

    /**
     * Pressing on the second saved genomes.
     */
    @FXML
    private void genome2Click() {
        genomeBookmarkClicked(genome2);
    }

    /**
     * Pressing on the third saved genomes.
     */
    @FXML
    private void genome3Click() {
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

            HashMap<String, Integer> genomeIds = DrawableCanvas.getInstance().getAllGenomes();

            parts = listOfIds.split(", ");
            int[] res = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                int oneSelected = genomeIds.get(parts[i]);
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
    @FXML
    private void chooseAnnoClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/AnnotationTable.fxml"));
        Stage stage;
        Parent root = loader.load();
        final AnnotationTableController annotationTableController
                = loader.<AnnotationTableController>getController();

        HashMap<Integer, TreeSet<Annotation>> allAnnotations =
                GraphDrawer.getInstance().getAllAnnotations();
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
                                setAllAnnotations(annotationTableController.getAnnotations());
                        GraphDrawer.getInstance().redraw();
                    }
                }
        );
        stage.showAndWait();
    }

    /**
     * Handles switching rainbowView on and off.
     */
    @FXML
    private void rainbowButtonClicked() {
        GraphDrawer.getInstance().setRainbowView(rainbowBut.isSelected());
        GraphDrawer.getInstance().redraw();
    }

    /**
     * Handles viewing SNPs yes or no.
     */
    @FXML
    public void collapseSNPClicked() {
        GraphDrawer.getInstance().collapse(collapseSNPButton.isSelected());
        GraphDrawer.getInstance().redraw();
    }

    @FXML
    public void saveAsPNG() {
        WritableImage image = canvas.snapshot(new SnapshotParameters(), null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.dir")).getParentFile()
        );
        File file = fileChooser.showSaveDialog(App.getStage());
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Gets the radius of the radiusTextField.
     * @return The integer in the radiusTextField
     */
    int getRadius() {
        int radius = -1;
        try {
            radius = Integer.parseInt(radiusTextField.getText());
        } catch (NumberFormatException e) {
            System.err.println("The given radius is not a number");
        }
        return radius;
    }

    /**
     * Gets the centreNode of the nodeTextField.
     * @return the integer in the nodeTextField
     */
    private int getCentreNodeID() {
        int centreNode = -1;
        try {
            int value = Integer.parseInt(nodeTextField.getText());
            if ((value < 1)
                    || (value > GraphDrawer.getInstance().getGraph().getFullGraphRightBoundID())) {
                throw new NotInRangeException();
            }
            centreNode = value;
        } catch (NumberFormatException e) {
            System.err.println("The given node ID is not a number");
        } catch (NotInRangeException e) {
            System.err.println("The given node ID is out of bounds");
        }
        return centreNode;
    }

    public void aboutUsClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/aboutUs.fxml"));
        Stage stage = new Stage();
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("About us");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void reset() {
        nodeTextField.setText("");
        radiusTextField.setText("");
        sequenceInfo.setText("");
        sequenceInfoAlt.setText("");
        genomeCoordinate.setText("");
    }

    @FXML
    public void goToGenCorClicked() {
        int nodeId = GraphDrawer.getInstance().hongerInAfrika(Integer.parseInt(genomeCoordinate.getText()),
                DrawableCanvas.getInstance().getGenomeToTraverse());
        ZoomController.getInstance().traverseGraphClicked(nodeId, getRadius());
        GraphDrawer.getInstance().highlightNode(nodeId);
    }

    @FXML
    public void chooseGenTraverseClicked() throws IOException {
        Stage stage = App.getStage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/FXML/chooseGenomeToTraverse.fxml"));
        Stage newStage;
        Parent root = loader.load();
        final GenomeTraverseGraphChooser genomeTraverseGraphChooser
                = loader.<GenomeTraverseGraphChooser>getController();

        HashMap<Integer, String> hashMap = DrawableCanvas.getInstance().getAllGenomesReversed();
        genomeTraverseGraphChooser.initialize(hashMap,
                DrawableCanvas.getInstance().getAnnotationGenome());

        newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setTitle("Choose a genome to traverse");
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setOnHidden(
                new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        DrawableCanvas.getInstance().
                                setGenomeToTraverse(genomeTraverseGraphChooser.getSelectedGenomeToTraverse());
                        goToGenCorBut.setDisable(false);
                        genomeCoordinate.setDisable(false);
                    }
                }
        );
        newStage.showAndWait();
    }
}
