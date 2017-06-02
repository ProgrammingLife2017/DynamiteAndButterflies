package gui.sub_controllers;

import javafx.scene.control.Button;
import java.util.prefs.Preferences;

/**
 * Created by Jip on 17-5-2017.
 * A BookmarkController class moving some logic
 * from the MenuController into a different class.
 */
public class BookmarkController {

    private static Preferences prefs = Preferences.userRoot();
    private String stringFile;
    private final Button bookmark1, bookmark2, saveBookmark;
    private static final String BOOKMARK_SAVE = "bookmarkNum";

    //As a user,
    // When viewing a file with bookmarks,
    // And choosing a new file to view
    // And that file has no bookmarks
    // I want to not see the old bookmarks.
    // Initializing this number as -2 ensures the above user story.
    private static final int loopStart = -2;

    /**
     * Constructor of the bookmark controller to handle the bookmarks.
     * @param bm1 The button with the first bookmark.
     * @param bm2 The button with the second bookmark.
     * @param save the button that saves the boomarks.
     */
    public BookmarkController(Button bm1, Button bm2, Button save) {
        bookmark1 = bm1;
        bookmark2 = bm2;
        saveBookmark = save;

        stringFile = "";
    }

    /**
     * Loads the bookmarks of the specific file stringFile.
     * @param stringOfFile The file that is being
     *                     loaded whose bookmarks should be loaded.
     */
    public void loadBookmarks(String stringOfFile) {
        stringFile = stringOfFile;

        if (prefs.getInt(BOOKMARK_SAVE + stringFile, -1) == -1) {
            prefs.putInt(BOOKMARK_SAVE + stringFile, 0);
        }

        int largestIndex = prefs.getInt(BOOKMARK_SAVE + stringFile, -1);
        for (int i = loopStart; i <= largestIndex; i++) {
            String realBM = prefs.get(stringFile + i, "-");
            updateBookmarks(realBM);
        }
    }

    /**
     * Saves the bookmarks when the user presses save.
     * @param nodes The centre node
     * @param radius The radius of nodes we should save/show
     */
    public void saving(int nodes, int radius) {

        String stringFile = prefs.get("file", "def");
        int newIndex = prefs.getInt(BOOKMARK_SAVE + stringFile, -1);
        newIndex++;
        prefs.put(stringFile + newIndex, nodes + "-" + radius);
        prefs.putInt(BOOKMARK_SAVE + stringFile, newIndex);

        updateBookmarks(nodes + "-" + radius);
    }

    /**
     * Uodates all bookmarks.
     * @param newBookmark The string that should be added.
     */
    private void updateBookmarks(String newBookmark) {
        //TODO Add more visuals to this update

        bookmark2.setText(bookmark1.getText());
        bookmark1.setText(newBookmark);
    }

    /**
     * Sets all buttons to visible so they can be used.
     */
    public void graphLoaded() {
        bookmark1.setVisible(true);
        bookmark2.setVisible(true);
        saveBookmark.setVisible(true);
    }
}
