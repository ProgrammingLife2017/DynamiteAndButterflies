package gui.sub_controllers;

import gui.CustomProperties;
import javafx.scene.control.MenuItem;


/**
 * Created by Jip on 9-6-2017.
 * <p>
 * This class handles the menu part of saving bookmarks about the genome views.
 */
public class RecentGenomeController {

    private final MenuItem genomes1;
    private final MenuItem genomes2;
    private final MenuItem genomes3;
    private final CustomProperties properties;

    private static final String PROP_ONE = "firstGenomes";
    private static final String PROP_TWO = "secondGenomes";
    private static final String PROP_THREE = "thirdGenomes";
    private static final String EMPTY = "-";

    private String filePath;

    /**
     * Constructor of the genomeproperties.
     *
     * @param g1 The first menui=Item
     * @param g2 the second menuItem
     * @param g3 the third menuItem
     */
    public RecentGenomeController(MenuItem g1, MenuItem g2, MenuItem g3) {
        genomes1 = g1;
        genomes2 = g2;
        genomes3 = g3;
        properties = new CustomProperties();
    }

    /**
     * Updates the list of bookmarked genomes.
     *
     * @param genomes The string to add.
     */
    private void update(String genomes) {
        properties.updateProperties();

        int check = isDuplicate(genomes);
        switch (check) {
            case -1:
                properties.setProperty(PROP_THREE + filePath, genomes2.getText());
                properties.setProperty(PROP_TWO + filePath, genomes1.getText());
                properties.setProperty(PROP_ONE + filePath, genomes);

                genomes3.setText(genomes2.getText());
                genomes2.setText(genomes1.getText());
                genomes1.setText(genomes);
                break;
            case 1:
                break;
            case 2:
                String temp = properties.getProperty(PROP_TWO, EMPTY);
                properties.setProperty(PROP_TWO, properties.getProperty(PROP_ONE, EMPTY));
                properties.setProperty(PROP_ONE, temp);

                genomes1.setText(temp);
                genomes2.setText(properties.getProperty(PROP_TWO, EMPTY));
                break;
            default:
                throw new UnsupportedOperationException();
        }
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
     * Handles the save button and parsing data to a string.
     *
     * @param selectedGenomes The selectedGenomes.
     */
    void saving(int[] selectedGenomes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Genomes - ");

        for (int addSelectedGen : selectedGenomes) {
            stringBuilder.append(addSelectedGen).append(", ");
        }
        String res = stringBuilder.toString();
        res = res.replaceAll(", $", "");
        update(res);
    }

    /**
     * Will check if the new genome selection is already a recent selection.
     *
     * @param selection The selection to be checked
     * @return a boolean if it is a duplicate yay or nay.
     */
    private int isDuplicate(String selection) {
        int res = -1;

        String what = properties.getProperty(PROP_ONE, EMPTY);
        if (properties.getProperty(PROP_ONE + filePath, EMPTY).equals(selection)) {
            res = 1;
        } else if (properties.getProperty(PROP_TWO + filePath, EMPTY).equals(selection)) {
            res = 2;
        } else if (properties.getProperty(PROP_THREE + filePath, EMPTY).equals(selection)) {
            res = -1;
            //The last recent selection is the same behaviour as adding a new selection.
            // Get rid of the last one and add the new one at the top.
        }
        return res;
    }
}
