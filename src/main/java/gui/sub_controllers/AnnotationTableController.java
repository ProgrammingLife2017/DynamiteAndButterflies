package gui.sub_controllers;

import structures.Annotation;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;


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
    private TableColumn<Annotation, Integer> idColumn;
    @FXML
    public TableColumn<Annotation, Integer> startColumn;
    @FXML
    public TableColumn<Annotation, Integer> endColumn;
    @FXML
    public TableColumn<Annotation, String> infoColumn;
    @FXML
    public TableColumn<Annotation, Boolean> highlightColumn;

    private ObservableList<Annotation> masterData = FXCollections.observableArrayList();
    private SortedList<Annotation> sortedData;
    private ArrayList<Annotation> selection;
    private boolean allSelected;

    /**
     * Just add some sample data in the constructor.
     */
    public AnnotationTableController() { }

    /**
     * Initializes the controller class.
     * Needs to be called manually to get the data.
     * Initializes the table columns and sets up sorting and filtering.
     * @param annotations the annotations to load into the table.
     */
    @FXML
    public void initialize(ArrayList<Annotation> annotations) {
        allSelected = false;
        selection = new ArrayList<Annotation>();
        for (Annotation annotation : annotations) {
            if (annotation.getSelected().getValue()) {
                selection.add(annotation);
            }
        }
        masterData = FXCollections.observableArrayList(annotations);

        // 0. Initialize the columns.
        idColumn.setCellValueFactory(new PropertyValueFactory<Annotation, Integer>("id"));
        startColumn.setCellValueFactory(new PropertyValueFactory<Annotation, Integer>("start"));
        endColumn.setCellValueFactory(new PropertyValueFactory<Annotation, Integer>("end"));
        infoColumn.setCellValueFactory(new PropertyValueFactory<Annotation, String>("info"));
        highlightColumn.setCellValueFactory(
                new Callback<TableColumn.
                        CellDataFeatures<Annotation, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(
                    TableColumn.CellDataFeatures<Annotation, Boolean> param) {
                return param.getValue().getSelected();
            }
        });
        highlightColumn.setCellFactory(CheckBoxTableCell.forTableColumn(highlightColumn));

        annotationTable.setEditable(true);
        idColumn.setEditable(false);
        startColumn.setEditable(false);
        endColumn.setEditable(false);
        infoColumn.setEditable(false);
        highlightColumn.setEditable(true);

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Annotation> filteredData = new FilteredList<>(masterData, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(annotation -> {
                // If filter text is empty, display all annotations.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                //Check the info, but also co-ordinates.
                return annotation.getInfo().toLowerCase().contains(lowerCaseFilter)
                        || Integer.toString(annotation.getStart()).contains(lowerCaseFilter)
                        || Integer.toString(annotation.getEnd()).contains(lowerCaseFilter);
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        sortedData = new SortedList<Annotation>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(annotationTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        annotationTable.setItems(sortedData);
    }

    public ArrayList<Annotation> getSelection() {
        return selection;
    }

    /**
     * Handles pressing the save button.
     */
    @FXML
    public void saveButtonClicked() {
        ArrayList<Annotation> res = new ArrayList<Annotation>();

        for (int i = 0; i < annotationTable.getItems().size(); i++) {
            Annotation annotation = annotationTable.getItems().get(i);
            if (annotation.getSelected().get()) {
                res.add(annotation);
            }
        }
        selection = res;
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
}
