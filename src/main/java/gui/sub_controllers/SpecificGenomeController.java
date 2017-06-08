package gui.sub_controllers;

import graph.Genome;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

    private boolean[] selectedGenomes;
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
    public void initialize(HashMap<Integer, String> hashMap, boolean[] alreadyChosen) {
        System.arraycopy(alreadyChosen, 0, selectedGenomes, 0, alreadyChosen.length);
        ArrayList<Genome> realData = new ArrayList<Genome>();
        for (int i = 0; i < hashMap.size(); i++) {
            Genome genome = new Genome(i, hashMap.get(i));
            genome.setSelected(Boolean.toString(alreadyChosen[i]));
            realData.add(genome);
        }
        final ObservableList<Genome> data = FXCollections.observableArrayList(realData);

        Callback<TableColumn, TableCell> cellFactory =
                new Callback<TableColumn, TableCell>() {
                    public TableCell call(TableColumn p) {
                        return new EditingCell();
                    }
                };

        idCol.setCellValueFactory(new PropertyValueFactory<Genome, Integer>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<Genome, String>("name"));
        highlightCol.setCellValueFactory(new PropertyValueFactory<Genome, String>("selected"));
        highlightCol.setCellFactory(cellFactory);

        table.setEditable(true);
        idCol.setEditable(false);
        nameCol.setEditable(false);
        highlightCol.setEditable(true);

        highlightCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Genome, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Genome, String> t) {
                        ((Genome) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setSelected(t.getNewValue());
                    }
                }
        );
        table.getItems().setAll(data);
    }

    /**
     * Gets the bool[] of selected genomes.
     * The key is the id.
     * The value is the value.
     *
     * @return a boll[] representing the genomes
     */
    public boolean[] getSelectedGenomes() {
        return selectedGenomes;
    }

    /**
     * Handles pressing the save button.
     * Updates the selectedgenomes with what is in the table.
     */
    @FXML
    public void saveSelected() {
        for (int i = 0; i < selectedGenomes.length; i++) {
            Genome temp = table.getItems().get(i);
            selectedGenomes[i] = Boolean.parseBoolean(temp.getSelected());
        }
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

/**
 * A class taken from.
 * http://docs.oracle.com/javafx/2/ui_controls/table-view.htm
 * Handles editing a cell
 */
class EditingCell extends TableCell<Genome, String> {

    private TextField textField;

    /**
     * Constructor.
     */
    EditingCell() {
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText((String) getItem());
        setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0,
                                Boolean arg1, Boolean arg2) {
                if (!arg2) {
                    commitEdit(textField.getText());
                }
            }
        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem();
    }
}
