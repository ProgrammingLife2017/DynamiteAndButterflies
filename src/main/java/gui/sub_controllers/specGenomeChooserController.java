package gui.sub_controllers;

import graph.Genome;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jip on 7-6-2017.
 */
public class specGenomeChooserController {

    private boolean[] selectedGenomes;
    @FXML
    private TableView<Genome> table;
    @FXML
    private TableColumn<Genome, Integer> idCol;
    @FXML
    private TableColumn<Genome, String> nameCol;
    @FXML
    private TableColumn<Genome, Boolean> highlightCol;


    public void initialize(HashMap<Integer, String> hashMap, boolean[] alreadyChosen) {
        this.selectedGenomes = alreadyChosen;

        ArrayList<Genome> realData = new ArrayList<Genome>();
        for (int i = 0; i < hashMap.size(); i++) {
            realData.add(new Genome(i, hashMap.get(i)));    //Make sure the table can read the info
        }
        final ObservableList<Genome> data = FXCollections.observableArrayList(realData);

        //Explain how the table should read the info
        idCol.setCellValueFactory(new PropertyValueFactory<Genome, Integer>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<Genome, String>("name"));
        highlightCol.setCellValueFactory(new PropertyValueFactory<Genome, Boolean>("selected"));
        highlightCol.setCellFactory(CheckBoxTableCell.forTableColumn(highlightCol));

        table.setEditable(true);
        highlightCol.setEditable(true);

        //TODO make sure this is triggered.
        //This event handler should trigger on edit
        highlightCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Genome, Boolean>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Genome, Boolean> editedBox) {
                        (editedBox.getTableView().getItems().get(
                                editedBox.getTablePosition().getRow()))
                                .setSelected(editedBox.getNewValue());
//                        selectedGenomes[genome.getId()] = genome.getSelected();
//Not sure you can incorporate this code in the event.
                    }
                }
        );

        table.getItems().setAll(data);
    }

    public boolean[] getSelectedGenomes() {
        return this.selectedGenomes;
    }

    //Save button ensure the selected genomes is updated see line 62
    @FXML
    public void saveSelected() {
        for (int i = 1; i < selectedGenomes.length; i++) {
            //All three of these should work, 2nd is the nicest but no clue why there are 0 updates.

//            boolean check = highlightCol.getCellData(i - 1);
//            selectedGenomes[i] = check;

            Genome temp = table.getItems().get(i - 1);
            selectedGenomes[i] = temp.getSelected();

//            TableColumn<Genome, Boolean> upToDate = (TableColumn<Genome, Boolean>) table.getColumns().get(2);
//            boolean check = upToDate.getCellData(i - 1);
//            selectedGenomes[i] = check;
        }
        close();
    }

    @FXML
    public void cancelClicked(ActionEvent actionEvent) {
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
