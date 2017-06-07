package gui.sub_controllers;

import gui.JipProps;
import javafx.scene.control.MenuItem;

import java.util.regex.Pattern;

/**
 * Created by Jip on 17-5-2017.
 * A BookmarkController class moving some logic
 * from the MenuController into a different class.
 */
public class BookmarkController {

    private static JipProps properties;
    private String stringFile;
    private final MenuItem bookmark1, bookmark2, bookmark3;
    private static final String BOOKMARK_SAVE = "bookmarkNum";
    private static final String BOOKMARK_REGEX = "\\w.*\\s-\\s\\d+\\s+-\\s\\d+";

    /**
     * Constructor of the bookmark controller to handle the bookmarks.
     *
     * @param bm1 The menuItem with the first bookmark.
     * @param bm2 The menuItem with the second bookmark.
     * @param bm3 The menuItem with the third bookmark.
     */
    public BookmarkController(MenuItem bm1, MenuItem bm2, MenuItem bm3) {
        bookmark1 = bm1;
        bookmark2 = bm2;
        bookmark3 = bm3;

        stringFile = "";
        properties = new JipProps();
    }

    /**
     * Loads the bookmarks of the specific file stringFile.
     *
     * @param filePath The file that is being
     *                     loaded whose bookmarks should be loaded.
     */
    public void loadBookmarks(String filePath) {
        properties.updateProperties();
        stringFile = filePath;

        if (properties.getProperty(BOOKMARK_SAVE + stringFile, "non").equals("non")) {
            properties.setProperty(BOOKMARK_SAVE + stringFile, "0");
            properties.saveProperties();
        }

        int largestIndex = Integer.parseInt(properties.getProperty(BOOKMARK_SAVE + stringFile, "-1"));

        //As a user,
        // When viewing a file with bookmarks,
        // And choosing a new file to view
        // And that file has no bookmarks
        // I want to not see the old bookmarks.
        // Initializing this number as -2 ensures the above user story.
        for (int i = -2; i <= largestIndex; i++) { //TODO magic number
            String realBM = properties.getProperty(stringFile + i, "-");
            String checkedString = checkBookmark(realBM);
            updateBookmarks(checkedString);
        }
    }

    /**
     * Checks the string if it has the right syntax.
     * @param realBM A string that is returned if it has the correct syntax.
     * @return A string or faulty string depending on the check
     */
    private String checkBookmark(String realBM) {
        if (Pattern.matches(BOOKMARK_REGEX, realBM)) {
            return realBM;
        }
        return " - ";
    }

    /**
     * Saves the bookmarks when the user presses save.
     *
     * @param note   The note for the bookmark
     * @param nodes  The centre node
     * @param radius The radius of nodes we should save/show
     */
    public void saving(String note, String nodes, String radius) {
        properties.updateProperties();

        String filePath = properties.getProperty("file", "def");
        String bookmark = note + " - " + nodes + " - " + radius;

        int newIndex = Integer.parseInt(properties.getProperty(BOOKMARK_SAVE + filePath, "-1"));
        newIndex++;

        properties.setProperty(filePath + newIndex, bookmark);
        properties.setProperty(BOOKMARK_SAVE + filePath, Integer.toString(newIndex));

        properties.saveProperties();

        assert (bookmark.equals(checkBookmark(bookmark)));
        updateBookmarks(bookmark);
    }

    /**
     * Uodates all bookmarks.
     *
     * @param newBookmark The string that should be added.
     */
    private void updateBookmarks(String newBookmark) {
        //TODO Add more visuals to this update
        //Is this still neccesary? Maybe a confirmation message.

        bookmark3.setText(bookmark2.getText());
        bookmark2.setText(bookmark1.getText());
        bookmark1.setText(newBookmark);
    }
}
