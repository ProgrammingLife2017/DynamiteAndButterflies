package gui.sub_controllers;

import graph.Annotation;
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
    public AnnotationTableController() {
//        masterData.add(new Person("Hans", "Muster"));
//        masterData.add(new Person("Ruth", "Mueller"));
//        masterData.add(new Person("Heinz", "Kurz"));
//        masterData.add(new Person("Cornelia", "Meier"));
//        masterData.add(new Person("Werner", "Meyer"));
//        masterData.add(new Person("Lydia", "Kunz"));
//        masterData.add(new Person("Anna", "Best"));
//        masterData.add(new Person("Stefan", "Meier"));
//        masterData.add(new Person("Martin", "Mueller"));
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     * <p>
     * Initializes the table columns and sets up sorting and filtering.
     */
    @FXML
    public void initialize(ArrayList<Annotation> annotations) {
        allSelected = false;
        selection = new ArrayList<Annotation>();
        masterData = FXCollections.observableArrayList(annotations);

        // 0. Initialize the columns.
        idColumn.setCellValueFactory(new PropertyValueFactory<Annotation, Integer>("id"));
        startColumn.setCellValueFactory(new PropertyValueFactory<Annotation, Integer>("start"));
        endColumn.setCellValueFactory(new PropertyValueFactory<Annotation, Integer>("end"));
        infoColumn.setCellValueFactory(new PropertyValueFactory<Annotation, String>("info"));
        highlightColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Annotation, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Annotation, Boolean> param) {
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
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                return annotation.getInfo().toLowerCase().contains(lowerCaseFilter);
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
