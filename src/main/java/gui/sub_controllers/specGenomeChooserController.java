package gui.sub_controllers;

import graph.Genome;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.HashMap;

/**
 * Created by Jip on 7-6-2017.
 */
public class specGenomeChooserController {
    public TableView table;


    public void initialize(HashMap<Integer, String> hash) {


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

        ObservableList<Genome> data = FXCollections.observableArrayList();

        for(int i = 0; i < hash.size(); i++) {
            data.add(new Genome(i, hash.get(i)));
        }

        table.setItems(data);
    }
}
