package gui.sub_controllers;

import gui.GraphDrawer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import structures.Annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;


/**
 * View-Controller for the genome table.
 *
 * @author Marco Jakob -> from http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/
 *         Changed to view and change annotations by Jip Rietveld
 */
public class AnnotationTableController {

    @FXML
    private TextField filterField;
    @FXML
    private TableView<Annotation> annotationTable;
    @FXML
    private TableColumn<Annotation, Integer> startColumn;
    @FXML
    private TableColumn<Annotation, Integer> endColumn;
    @FXML
    private TableColumn<Annotation, String> infoColumn;
    @FXML
    private TableColumn<Annotation, Boolean> highlightColumn;

    private SortedList<Annotation> sortedData;
    private HashMap<Integer, TreeSet<Annotation>> annotations;
    private HashMap<Integer, TreeSet<Annotation>> updatedAnnotations;
    private boolean allSelected;

    /**
     * Just add some sample data in the constructor.
     */
    public AnnotationTableController() {
    }

    /**
     * Initializes the controller class.
     * Needs to be called manually to get the data.
     * Initializes the table columns and sets up sorting and filtering.
     *
     * @param annotationsArg the annotations to load into the table.
     */
    @FXML
    @SuppressWarnings("MethodLength") //It is only 2 too long and the comments ensure clarity.
    public void initialize(HashMap<Integer, TreeSet<Annotation>> annotationsArg) {
        this.annotations = annotationsArg;
        this.updatedAnnotations = this.annotations;
        allSelected = false;

        ObservableList<Annotation> masterData =
                FXCollections.observableArrayList(new ArrayList<>(bucketsToTreeSet()));

        // 0. Initialize the columns.
        initializeColumns();

        // 0.1 setRight editable columns
        setEditable();

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Annotation> filteredData = new FilteredList<>(masterData, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue)
                -> filteredData.setPredicate(annotation -> {
            // If filter text is empty, display all annotations.
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();

            //Check the info, but also co-ordinates.
            return annotation.getInfo().toLowerCase().contains(lowerCaseFilter)
                    || Integer.toString(annotation.getStart()).contains(lowerCaseFilter)
                    || Integer.toString(annotation.getEnd()).contains(lowerCaseFilter);
        }));

        // 3. Wrap the FilteredList in a SortedList.
        sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(annotationTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        annotationTable.setItems(sortedData);
    }

    /**
     * Sets the hashMap annotations to a HashSet.
     *
     * @return a hash set of the buckets of annotations
     */
    @NotNull
    private TreeSet<Annotation> bucketsToTreeSet() {
        return bucketsToTreeSet(this.annotations);
    }

    /**
     * Converts the hashMap to a hashSet.
     *
     * @param hashMap The hashMap to be converted.
     * @return a hashSet of the hashMap.
     */
    private TreeSet<Annotation> bucketsToTreeSet(HashMap<Integer, TreeSet<Annotation>> hashMap) {
        if (hashMap == null) {
            return null;
        }
        TreeSet<Annotation> drawThese = new TreeSet<>();
        for (int i = 0; i <= hashMap.size(); i++) {
            TreeSet<Annotation> tempAnnotations = hashMap.get(i);
            if (tempAnnotations != null) {
                drawThese.addAll(tempAnnotations);
            }
        }
        return drawThese;
    }

    /**
     * Method that sets the columns and table to the correct editable state.
     */
    private void setEditable() {
        annotationTable.setEditable(true);
        startColumn.setEditable(false);
        endColumn.setEditable(false);
        infoColumn.setEditable(false);
        highlightColumn.setEditable(true);
    }

    /**
     * Method that initializes the columns with the right factories.
     */
    private void initializeColumns() {
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        infoColumn.setCellValueFactory(new PropertyValueFactory<>("info"));
        highlightColumn.setCellValueFactory(
                param -> param.getValue().getSelected());
        highlightColumn.setCellFactory(CheckBoxTableCell.forTableColumn(highlightColumn));
        annotationTable.setRowFactory(tv -> {
            TableRow<Annotation> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Annotation annotation = row.getItem();
                    goToAnnotation(annotation);
                    close();
                }
            });
            return row;
        });
    }

    /**
     * Method that goes to the annotation and highlights it.
     *
     * @param annotation the Annotation to go to.
     */
    private void goToAnnotation(Annotation annotation) {
        try {
            int startNodeID = GraphDrawer.getInstance().hongerInAfrika(annotation.getStart());
            int endNodeID = GraphDrawer.getInstance().hongerInAfrika(annotation.getEnd());
            int soortVanRadius = (int) ((endNodeID - startNodeID) * 1.2);
            if (soortVanRadius > 4000) {
                ZoomController.getInstance().traverseGraphClicked(startNodeID, 4000);
            } else {
                ZoomController.getInstance().traverseGraphClicked(((endNodeID + startNodeID) / 2),
                        Math.max(soortVanRadius, (int) Math.sqrt(49)));
            }
            GraphDrawer.getInstance().highlightAnnotation(annotation);
        } catch (StackOverflowError e) {
            AnnotationPopUpController popUp = new AnnotationPopUpController();
            popUp.loadNoAnnotationFound("Sorry, can't find this annotation.");

            System.err.println("Sorry, too many nodes without ref to hold in memory.");
        }
    }

    /**
     * Handles pressing the save button.
     */
    @FXML
    public void saveButtonClicked() {
        updatedAnnotations = annotations;
        Annotation annotation = annotationTable.getSelectionModel().getSelectedItem();
        if (annotation != null) {
            goToAnnotation(annotation);
        }
        close();
    }

    /**
     * Handles pressing the cancel button.
     */
    public void cancelButtonClicked() {
        close();
    }

    /**
     * A general function that closes the stage.
     */
    private void close() {
        Stage stage = (Stage) annotationTable.getScene().getWindow();
        stage.close();
    }

    /**
     * Can select/deselect the entire sortedData at the same time.
     */
    @FXML
    public void selectAllFiltered() {
        for (Annotation annotation : sortedData) {
            if (allSelected) {
                annotation.setSelected(false);
            } else {
                annotation.setSelected(true);
            }
        }
        annotationTable.setItems(sortedData);
        allSelected = !allSelected;
    }

    public HashMap<Integer, TreeSet<Annotation>> getAnnotations() {
        return updatedAnnotations;
    }
}
