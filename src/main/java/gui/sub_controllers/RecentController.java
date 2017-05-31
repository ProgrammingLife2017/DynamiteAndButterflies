package gui.sub_controllers;

import javafx.scene.control.MenuItem;

import java.util.prefs.Preferences;

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

    private Preferences prefs;
    private static final String empty = "<No recent file>";

    /**
     * Constructor of the recentController that controls the File->Recent tab.
     * @param filebut1 MenuItem one
     * @param filebut2 MenuItem two
     * @param filebut3 MenuItem three
     */
    public RecentController(MenuItem filebut1, MenuItem filebut2, MenuItem filebut3) {
        file1 = filebut1;
        file2 = filebut2;
        file3 = filebut3;

        prefs = Preferences.userRoot();
    }

    /**
     * Method handles pressing one of the MenuItems.
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
     * @param filePath The new path to be added.
     */
    public void update(String filePath) {
        if (!isDuplicate(filePath)) {
            prefs.put(filePref3, file2.getText());
            prefs.put(filePref2, file1.getText());
            prefs.put(filePref1, filePath);

            file3.setText(file2.getText());
            file2.setText(file1.getText());
            file1.setText(filePath);
        }
    }

    private boolean isDuplicate(String filePath) {
        boolean res = false;
        if (prefs.get(filePref1, empty).equals(filePath)) {
            res = true;
        } else if (prefs.get(filePref2, empty).equals(filePath)) {
            res = true;
        } else if (prefs.get(filePref3, empty).equals(filePath)) {
            res = true;
        }
        return res;
    }

    /**
     * Initializes all the recent files from Preferences.
     */
    public void initialize() {
        prefs.put(filePref3, prefs.get(filePref3, empty));
        prefs.put(filePref2, prefs.get(filePref2, empty));
        prefs.put(filePref1, prefs.get(filePref1, empty));

        file1.setText(prefs.get(filePref1, empty));
        file2.setText(prefs.get(filePref2, empty));
        file3.setText(prefs.get(filePref3, empty));
    }
}
