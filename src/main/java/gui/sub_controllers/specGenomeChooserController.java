package gui.sub_controllers;

import graph.Genome;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jip on 7-6-2017.
 */
public class specGenomeChooserController {
    @FXML
    public TableView table;


    public void initialize(HashMap<Integer, String> hash) {

        HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
        hashMap.put(1, "Jip");
        hashMap.put(2, "Jappie");
        hashMap.put(3, "Marc dikke lulz");



        ArrayList<Genome> realData = new ArrayList<Genome>();
        for (int i = 0; i < hashMap.size(); i++) {
            realData.add(new Genome(i, hashMap.get(i)));
        }
        ObservableList<Genome> data = FXCollections.observableArrayList(realData);
        table.setItems(data);

        ObservableList<TableColumn> test = FXCollections.observableArrayList();
        test = table.getColumns();

        final TableColumn<Genome, Integer> IDCol = test.get(0);
        final TableColumn<Genome, String> nameCol = test.get(1);
        final TableColumn<Genome, Boolean> highlightCol = test.get(2);

        IDCol.setCellValueFactory(new PropertyValueFactory<Genome, Integer>("ID"));
        nameCol.setCellValueFactory(new PropertyValueFactory<Genome, String>("Name"));
        highlightCol.setCellValueFactory(new PropertyValueFactory<Genome, Boolean>("Highlight"));
        highlightCol.setCellFactory(CheckBoxTableCell.forTableColumn(highlightCol));

        highlightCol.setEditable(true);
        table.setEditable(true);
    }
}
