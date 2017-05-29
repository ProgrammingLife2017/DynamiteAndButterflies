package gui.subControllers;

import javafx.scene.control.MenuItem;

import java.util.prefs.Preferences;

/**
 * Created by Jip on 23-5-2017.
 */
public class RecentController {

    private MenuItem file1;
    private MenuItem file2;
    private MenuItem file3;

    /**
     * Constructor of the recentController that controls the File->Recent tab.
     * @param f1 MenuItem one
     * @param f2 MenuItem two
     * @param f3 MenuItem three
     */
    public RecentController(MenuItem f1, MenuItem f2, MenuItem f3) {
        file1 = f1;
        file2 = f2;
        file3 = f3;
    }

    /**
     * Method handles pressing one of the MenuItems.
     * @param file the MenuItem that was pressed
     * @return the string that the MenuItem contains.
     */
    public String pressedRecent(MenuItem file) {
        String filePath = file.getText();
        if (!filePath.equals("<No recent file>")) {
            return filePath;
        }
        return null;
    }

    /**
     * Updates all the recent files.
     * @param filePath The new path to be added.
     * @param prefs The preferences of the user.
     */
    public void update(String filePath, Preferences prefs) {
        prefs.put("file3", file2.getText());
        prefs.put("file2", file1.getText());
        prefs.put("file1", filePath);

        file3.setText(file2.getText());
        file2.setText(file1.getText());
        file1.setText(filePath);
    }

    /**
     * Initializes all the recent files from Preferences.
     * @param prefs the users preferences.
     */
    public void initialize(Preferences prefs) {
        String empty = "<No recent file>";
        prefs.put("file3", prefs.get("file3", empty));
        prefs.put("file2", prefs.get("file2", empty));
        prefs.put("file1", prefs.get("file1", empty));

        file1.setText(prefs.get("file1", empty));
        file2.setText(prefs.get("file2", empty));
        file3.setText(prefs.get("file3", empty));
    }
}
