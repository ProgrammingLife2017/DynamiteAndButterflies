package gui.sub_controllers;

import gui.CustomProperties;
import javafx.scene.control.MenuItem;

/**
 * Created by Jip on 23-5-2017.
 */
public class RecentController {

    private final MenuItem file1;
    private final MenuItem file2;
    private final MenuItem file3;

    private String filePref1 = "file1";
    private String filePref2 = "file2";
    private String filePref3 = "file3";

    private CustomProperties properties;
    private static final String EMPTY = "<No recent file>";

    /**
     * Constructor of the recentController that controls the File->Recent tab.
     *
     * @param filebut1 MenuItem one
     * @param filebut2 MenuItem two
     * @param filebut3 MenuItem three
     */
    public RecentController(MenuItem filebut1, MenuItem filebut2, MenuItem filebut3) {
        file1 = filebut1;
        file2 = filebut2;
        file3 = filebut3;

        properties = new CustomProperties();
        this.initialize();
        properties.saveProperties();
    }

    /**
     * Method handles pressing one of the MenuItems.
     *
     * @param file the MenuItem that was pressed
     * @return the string that the MenuItem contains.
     */
    public String pressedRecent(MenuItem file) {
        String filePath = file.getText();
        if (!"<No recent file>".equals(filePath)) {
            return filePath;
        }
        return null;
    }

    /**
     * Updates all the recent files.
     *
     * @param filePath The new path to be added.
     */
    public void update(String filePath) {
        properties.updateProperties();
        //updateProperties has to be before the isDuplicate method call.
        int check = isDuplicate(filePath);
        switch (check) {
            case -1:
                properties.setProperty(filePref3, file2.getText());
                properties.setProperty(filePref2, file1.getText());
                properties.setProperty(filePref1, filePath);

                file3.setText(file2.getText());
                file2.setText(file1.getText());
                file1.setText(filePath);
                break;
            case 1:
                break;
            case 2:
                String temp = properties.getProperty(filePref2, EMPTY);
                properties.setProperty(filePref2, properties.getProperty(filePref1, EMPTY));
                properties.setProperty(filePref1, temp);

                file1.setText(temp);
                file2.setText(properties.getProperty(filePref2, EMPTY));
                break;
            default: throw new UnsupportedOperationException();
        }
        properties.saveProperties();
    }

    /**
     * Will check if the string filepath is already a recent file.
     *
     * @param filePath The string to checked
     * @return a boolean if it is a duplicate yay or nay.
     */
    private int isDuplicate(String filePath) {
        int res = -1;

        if (properties.getProperty(filePref1, EMPTY).equals(filePath)) {
            res = 1;
        } else if (properties.getProperty(filePref2, EMPTY).equals(filePath)) {
            res = 2;
        } else if (properties.getProperty(filePref3, EMPTY).equals(filePath)) {
            res = -1;    //The last recent file is the same behaviour as adding the new filepath
            // Get rid of the last one and add the new one at the top.
        }
        return res;
    }

    /**
     * Initializes all the recent files from Preferences.
     */
    public void initialize() {
        properties.updateProperties();
        properties.setProperty(filePref3, properties.getProperty(filePref3, EMPTY));
        properties.setProperty(filePref2, properties.getProperty(filePref2, EMPTY));
        properties.setProperty(filePref1, properties.getProperty(filePref1, EMPTY));

        file1.setText(properties.getProperty(filePref1, EMPTY));
        file2.setText(properties.getProperty(filePref2, EMPTY));
        file3.setText(properties.getProperty(filePref3, EMPTY));
    }
}
