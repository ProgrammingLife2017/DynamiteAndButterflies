package gui.sub_controllers;

import graph.Genome;
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
 * Created by Jip on 20-6-2017.
 */
public class GffGenomeController {

    private int selectedGenome;
    @FXML
    private TableView<Genome> table;
    @FXML
    private TableColumn<Genome, Integer> idCol;
    @FXML
    private TableColumn<Genome, String> nameCol;
    @FXML
    private TableColumn selectCol;

    /**
     * This method initializes the environment.
     * It loads the hashmap to genomes to the table.
     *
     * @param hashMap    data on the different genomes.
     * @param suggestion data on what genomes want to be viewed.
     */
    public void initialize(HashMap<Integer, String> hashMap, int suggestion) {
        selectedGenome = suggestion;

        ArrayList<Genome> realData = new ArrayList<Genome>();
        for (int i = 0; i < hashMap.size(); i++) {
            Genome genome = new Genome(i, hashMap.get(i));
            realData.add(genome);
            if (i == suggestion) {
                genome.setSelected(true);
            }
        }
        final ObservableList<Genome> data = FXCollections.observableArrayList(realData);
        table.setItems(data);

        idCol.setCellValueFactory(new PropertyValueFactory<Genome, Integer>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<Genome, String>("name"));
        selectCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Genome, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Genome, Boolean> param) {
                //TODO Make sure this works.
                ObservableValue<Boolean> res = param.getValue().selectedProperty();
                for (int i = 0; i < table.getItems().size(); i++) {
                    if (i != param.getValue().getId()) {
                        Genome genome = table.getItems().get(i);
                        if (genome.isSelected()) {
                            genome.setSelected(false);
                        }
                    }
                }
                return res;
            }
        });
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));

        table.setEditable(true);
        idCol.setEditable(false);
        nameCol.setEditable(false);
        selectCol.setEditable(true);
    }

    /**
     * Gets the id of the selected genome.
     * All integers in the list represent the ID of a selected genome.
     *
     * @return a int representing the genomes
     */
    public int getSelectedGenome() {
        return selectedGenome;
    }

    /**
     * Handles pressing the save button.
     * Updates the selectedgenomes with what is in the table.
     */
    @FXML
    public void continueClicked() {
        ArrayList<Integer> temp = new ArrayList<Integer>();

        for (int i = 0; i < table.getItems().size(); i++) {
            Genome genome = table.getItems().get(i);
            if (genome.isSelected()) {
                temp.add(genome.getId());
            }
        }

        DrawableCanvas.getInstance().setAnnotationGenome(temp.get(0));
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
