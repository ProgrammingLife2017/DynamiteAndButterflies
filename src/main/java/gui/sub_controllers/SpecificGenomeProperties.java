package gui.sub_controllers;

import gui.CustomProperties;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;


/**
 * Created by Jip on 9-6-2017.
 *
 * This class handles the menu part of saving bookmarks about the genome views.
 */
public class SpecificGenomeProperties {

    private Button saveButton;

    private MenuItem genomes1;
    private MenuItem genomes2;
    private MenuItem genomes3;
    private CustomProperties properties;

    private static final String PROP_ONE = "firstGenomes";
    private static final String PROP_TWO = "secondGenomes";
    private static final String PROP_THREE = "thirdGenomes";
    private static final String EMPTY = "-";

    private String filePath;

    /**
     * Constructor of the genomeproperties.
     * @param saveButtonArg The save button.
     * @param g1 The first menui=Item
     * @param g2 the second menuItem
     * @param g3 the third menuItem
     */
    public SpecificGenomeProperties(Button saveButtonArg, MenuItem g1, MenuItem g2, MenuItem g3) {
        saveButton = saveButtonArg;
        genomes1 = g1;
        genomes2 = g2;
        genomes3 = g3;
        properties = new CustomProperties();

    }

    /**
     * Updates the list of bookmarked genomes.
     * @param genomes The string to add.
     */
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

    /**
     * Initializes the genome bookmarks.
     */
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

    /**
     * Shows the save button.
     */
    public void showSave() {
        saveButton.setVisible(true);
    }

    /**
     * Hides the save button.
     */
    public void hideSave() {
        saveButton.setVisible(false);
    }

    /**
     * Handles the save button and parsing data to a string.
     * @param selectedGenomes The selectedGenomes.
     */
    public void saving(int[] selectedGenomes) {
        String save = "Genomes" + " - ";

        for (int addSelectedGen : selectedGenomes) {
            save += Integer.toString(addSelectedGen) + ", ";
        }
        save = save.replaceAll(", $", "");
        update(save);
    }
}
