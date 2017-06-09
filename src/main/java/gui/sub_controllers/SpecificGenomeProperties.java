package gui.sub_controllers;

import gui.CustomProperties;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;


/**
 * Created by Jip on 9-6-2017.
 */
public class SpecificGenomeProperties {

    private Button saveButton;

    private MenuItem genomes1;
    private MenuItem genomes2;
    private MenuItem genomes3;
    private MenuItem addGenomes;
    private CustomProperties properties;

    private static final String PROP_ONE = "firstGenomes";
    private static final String PROP_TWO = "secondGenomes";
    private static final String PROP_THREE = "thirdGenomes";
    private static final String EMPTY = "-";

    private String filePath;

    public SpecificGenomeProperties(Button saveButtonArg, MenuItem g1, MenuItem g2, MenuItem g3, MenuItem addArg) {
        saveButton = saveButtonArg;
        genomes1 = g1;
        genomes2 = g2;
        genomes3 = g3;
        addGenomes = addArg;
        properties = new CustomProperties();

    }

    public void update(String genomes) {
        properties.updateProperties();
        properties.setProperty(PROP_THREE + filePath, genomes2.getText());
        properties.setProperty(PROP_TWO + filePath, genomes1.getText());
        properties.setProperty(PROP_ONE + filePath, genomes);

        genomes3.setText(genomes2.getText());
        genomes2.setText(genomes1.getText());
        genomes1.setText(genomes);
        properties.saveProperties();
    }

    public void initialize() {
        properties.updateProperties();

        this.filePath = properties.getProperty("file", "def");

        properties.setProperty(PROP_ONE + filePath,
                properties.getProperty(PROP_ONE + filePath, EMPTY));
        properties.setProperty(PROP_TWO + filePath,
                properties.getProperty(PROP_TWO + filePath, EMPTY));
        properties.setProperty(PROP_THREE + filePath,
                properties.getProperty(PROP_THREE + filePath, EMPTY));

        genomes1.setText(properties.getProperty(PROP_ONE + filePath, EMPTY));
        genomes2.setText(properties.getProperty(PROP_TWO + filePath, EMPTY));
        genomes3.setText(properties.getProperty(PROP_THREE + filePath, EMPTY));
        properties.saveProperties();
    }

    public void showSave() {
        saveButton.setVisible(true);
    }

    public void hideSave() {
        saveButton.setVisible(false);
    }

    public void saving(int[] selectedGenomes) {
        String save = "Genomes" + " - ";

        for (int addSelectedGen : selectedGenomes) {
            save += Integer.toString(addSelectedGen) + ", ";
        }
        save = save.replaceAll(", $", "");
        update(save);
    }
}
