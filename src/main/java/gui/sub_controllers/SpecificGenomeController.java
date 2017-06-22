package gui.sub_controllers;

import structures.Genome;
import gui.DrawableCanvas;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jip on 7-6-2017.
 * <p>
 * This class handles choosing a specific genome and the tableView that accompanies it.
 */
public class SpecificGenomeController {

    private int[] selectedGenomes;
    @FXML
    private TableView<Genome> table;
    @FXML
    private TableColumn<Genome, Integer> idCol;
    @FXML
    private TableColumn<Genome, String> nameCol;
    @FXML
    private TableColumn highlightCol;

    /**
     * This method initializes the environment.
     * It loads the hashmap to genomes to the table.
     *
     * @param hashMap       data on the different genomes.
     * @param alreadyChosen data on what genomes want to be viewed.
     */
    public void initialize(HashMap<Integer, String> hashMap, int[] alreadyChosen) {
        selectedGenomes = alreadyChosen;

        ArrayList<Genome> realData = new ArrayList<Genome>();
        for (int i = 0; i < hashMap.size(); i++) {
            Genome genome = new Genome(i, hashMap.get(i));
            for (int hasBeenSelected : alreadyChosen) {
                if (i == hasBeenSelected) {
                    genome.setSelected(true);
                    break;
                }
            }
            realData.add(genome);
        }
        final ObservableList<Genome> data = FXCollections.observableArrayList(realData);
        table.setItems(data);

        idCol.setCellValueFactory(new PropertyValueFactory<Genome, Integer>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<Genome, String>("name"));
        highlightCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Genome,Boolean>,ObservableValue<Boolean>>()
        {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Genome, Boolean> param)
            {
                return param.getValue().selectedProperty();
            }
        });
        highlightCol.setCellFactory(CheckBoxTableCell.forTableColumn(highlightCol));

        table.setEditable(true);
        idCol.setEditable(false);
        nameCol.setEditable(false);
        highlightCol.setEditable(true);

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
}

