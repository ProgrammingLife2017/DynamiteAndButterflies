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

    public RecentController(MenuItem f1, MenuItem f2, MenuItem f3) {
        file1 = f1;
        file2 = f2;
        file3 = f3;
    }

    public String pressedRecent(MenuItem file) {
        String filePath = file.getText();
        if (!filePath.equals("<No recent file>")) {
            return filePath;
        }
        return null;
    }

    public void update(String filePath, Preferences prefs) {
        prefs.put("file3", file2.getText());
        prefs.put("file2", file1.getText());
        prefs.put("file1", filePath);

        file3.setText(file2.getText());
        file2.setText(file1.getText());
        file1.setText(filePath);
    }

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
