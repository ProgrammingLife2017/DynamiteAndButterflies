package gui.sub_controllers;

import gui.DrawableCanvas;
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
import structures.Genome;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jip on 7-6-2017.
 * <p>
 * This class handles choosing a specific genome and the tableView that accompanies it.
 */
public class SpecificGenomeController {

    @FXML
    private TextField filterField;
    @FXML
    private TableView<Genome> table;
    @FXML
    private TableColumn<Genome, Integer> idCol;
    @FXML
    private TableColumn<Genome, String> nameCol;
    @FXML
    private TableColumn highlightCol;
    private SortedList<Genome> sortedData;
    private int[] selectedGenomes;
    private boolean allSelected;

    /**
     * This method initializes the environment.
     * It loads the hashmap to genomes to the table.
     *
     * @param hashMap       data on the different genomes.
     * @param alreadyChosen data on what genomes want to be viewed.
     */
    public void initialize(HashMap<Integer, String> hashMap, int[] alreadyChosen) {
        selectedGenomes = alreadyChosen;
        allSelected = false;

        ArrayList<Genome> realData = createData(hashMap, alreadyChosen);
        final ObservableList<Genome> data = FXCollections.observableArrayList(realData);

        initializeColumns();
        setEditable();

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Genome> filteredData = new FilteredList<>(data, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(genome -> {
                // If filter text is empty, display all annotations.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                //Check the info, but also co-ordinates.
                return genome.getName().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        sortedData = new SortedList<Genome>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        table.setItems(sortedData);
    }

    /**
     * Translates the hasMap into dataSet that can be used in the table.
     *
     * @param hashMap       The hashMap of data
     * @param alreadyChosen The array of already chosen genomes.
     * @return ArrayList of Genomes, usable in the tableView.
     */
    private ArrayList<Genome> createData(HashMap<Integer, String> hashMap, int[] alreadyChosen) {
        ArrayList<Genome> res = new ArrayList<>();
        for (int i = 0; i < hashMap.size(); i++) {
            Genome genome = new Genome(i, hashMap.get(i));
            for (int hasBeenSelected : alreadyChosen) {
                if (i == hasBeenSelected) {
                    genome.setSelected(true);
                    break;
                }
            }
            res.add(genome);
        }
        return res;
    }

    private void setEditable() {
        table.setEditable(true);
        idCol.setEditable(false);
        nameCol.setEditable(false);
        highlightCol.setEditable(true);
    }

    private void initializeColumns() {
        idCol.setCellValueFactory(new PropertyValueFactory<Genome, Integer>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<Genome, String>("name"));
        highlightCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Genome, Boolean>,
                        ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(
                            TableColumn.CellDataFeatures<Genome, Boolean> param) {
                        return param.getValue().selectedProperty();
                    }
                });
        highlightCol.setCellFactory(CheckBoxTableCell.forTableColumn(highlightCol));
    }

    /**
     * Gets the int[] of selected genomes.
     * All integers in the list represent the ID of a selected genome.
     *
     * @return a int[] representing the genomes
     */
    public int[] getSelectedGenomes() {
        return selectedGenomes;
    }

    /**
     * Handles pressing the save button.
     * Updates the selectedgenomes with what is in the table.
     */
    @FXML
    public void saveSelected() {
        ArrayList<Integer> temp = new ArrayList<Integer>();

        for (int i = 0; i < table.getItems().size(); i++) {
            Genome genome = table.getItems().get(i);
            if (genome.isSelected()) {
                temp.add(genome.getId());
            }
        }

        selectedGenomes = new int[temp.size()];
        for (int i = 0; i < selectedGenomes.length; i++) {
            selectedGenomes[i] = temp.get(i);
        }

        close();
        DrawableCanvas.getInstance().getSpecificGenomeProperties().saving(selectedGenomes);
    }

    /**
     * Closes the pop up.
     */
    @FXML
    public void cancelClicked() {
        close();
    }

    /**
     * A general function that closes the stage.
     */
    private void close() {
        Stage stage = (Stage) table.getScene().getWindow();
        stage.close();
    }

    /**
     * Can select/deselect the entire sortedData at the same time.
     */
    public void selectAllFiltered() {
        for (Genome genome : sortedData) {
            if (allSelected) {
                genome.setSelected(false);
            } else {
                genome.setSelected(true);
            }
        }
        table.setItems(sortedData);
        allSelected = !allSelected;
    }
}

