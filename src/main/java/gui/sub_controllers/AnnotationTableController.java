package gui.sub_controllers;

import graph.Annotation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;


/**
 * View-Controller for the genome table.
 *
 * @author Marco Jakob -> from http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/
 * Changed to view and change annotations by Jip Rietveld
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
    public TableColumn<Annotation, String> highlightColumn;

    private ObservableList<Annotation> masterData = FXCollections.observableArrayList();

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
     *
     * Initializes the table columns and sets up sorting and filtering.
     */
    @FXML
    public void initialize(ArrayList<Annotation> annotations) {
        masterData = FXCollections.observableArrayList(annotations);

        // 0. Initialize the columns.
        idColumn.setCellValueFactory(new PropertyValueFactory<Annotation, Integer>("id"));
        startColumn.setCellValueFactory(new PropertyValueFactory<Annotation, Integer>("start"));
        endColumn.setCellValueFactory(new PropertyValueFactory<Annotation, Integer>("end"));
        infoColumn.setCellValueFactory(new PropertyValueFactory<Annotation, String>("info"));

        //TODO Change this shit up.
        highlightColumn.setCellValueFactory(new PropertyValueFactory<Annotation, String>("id"));

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

                //TODO split this info.
                if (annotation.getInfo().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Annotation> sortedData = new SortedList<Annotation>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(annotationTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        annotationTable.setItems(sortedData);
    }

//    public ArrayList<Annotation> getAllAnnotations() {
//        return allAnnotations;
//    }
}
