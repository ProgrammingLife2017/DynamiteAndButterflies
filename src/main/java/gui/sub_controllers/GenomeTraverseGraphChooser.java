package gui.sub_controllers;

import gui.GraphDrawer;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
 * Created by Jip on 20-6-2017.
 * <p>
 * This popUp gives the user a choice on which genome to load the traverse the genome.
 */
public class GenomeTraverseGraphChooser {

    @FXML
    public TextField genomeCorField;
    private int selectedGenomeToTraverse;
    private int selectedNodeToGoTo;
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
        selectedGenomeToTraverse = suggestion;
        selectedNodeToGoTo = -1;

        ArrayList<Genome> realData = createGenomeTable(hashMap, suggestion, true);
        if (realData.size() == 0) {
            realData = createGenomeTable(hashMap, suggestion, false);
        }

        final ObservableList<Genome> data = FXCollections.observableArrayList(realData);
        table.setItems(data);

        initializeColumns();

        table.setEditable(true);
        idCol.setEditable(false);
        nameCol.setEditable(false);
        selectCol.setEditable(true);
    }

    /**
     * Initializes the columns with their correct factories.
     */
    private void initializeColumns() {
        idCol.setCellValueFactory(new PropertyValueFactory<Genome, Integer>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<Genome, String>("name"));
        selectCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Genome, Boolean>,
                ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(
                    TableColumn.CellDataFeatures<Genome, Boolean> param) {
                ObservableValue<Boolean> res = param.getValue().getSelectedProperty();
                if (res.getValue()) {
                    if (table.getItems().size() != 1) {
                        for (int i = 0; i < table.getItems().size(); i++) {
                            if (i != param.getValue().getId()) {
                                Genome genome = table.getItems().get(i);
                                genome.setSelected(false);
                            }
                        }
                    }
                }
                return res;
            }
        });
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
    }

    private ArrayList<Genome> createGenomeTable(HashMap<Integer, String> hashMap,
                                                int suggestion, boolean flag) {
        ArrayList<Genome> res = new ArrayList<Genome>();
        for (int i = 0; i < hashMap.size(); i++) {
            Genome genome = new Genome(i, hashMap.get(i));
            if (flag) {
                if (genome.getName().toLowerCase().contains("ref")) {
                    genome.setSelected(true);
                    res.add(genome);
                }
            } else {
                res.add(genome);
            }

            if (i == suggestion) {
                genome.setSelected(true);
            }
        }
        return res;
    }

    /**
     * Gets the id of the selected genome.
     * All integers in the list represent the ID of a selected genome.
     *
     * @return a int representing the genomes
     */
    public int getSelectedGenomeToTraverse() {
        return selectedGenomeToTraverse;
    }

    public int getSelectedNodeToGoTo() {
        return selectedNodeToGoTo;
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

        //TODO improve handling no genome chosen -> can be more user friendly but works.
        if (temp.isEmpty()) {
            table.getItems().get(0).setSelected(true);
            temp.add(table.getItems().get(0).getId());
        }
        selectedGenomeToTraverse = temp.get(0);

        try {
            selectedNodeToGoTo = GraphDrawer.getInstance().hongerInAfrika(
                    Integer.parseInt(genomeCorField.getText()),
                    selectedGenomeToTraverse);
        } catch (StackOverflowError e) {
            //TODO invullen.
        }
        close();
    }

    /**
     * A general function that closes the stage.
     */
    private void close() {
        Stage stage = (Stage) table.getScene().getWindow();
        stage.close();
    }

    public void cancelButClicked() {
        close();
    }
}
