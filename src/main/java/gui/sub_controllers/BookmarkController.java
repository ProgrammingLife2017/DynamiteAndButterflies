package gui.sub_controllers;

import gui.CustomProperties;
import javafx.scene.control.MenuItem;

import java.util.regex.Pattern;

/**
 * Created by Jip on 17-5-2017.
 * A BookmarkController class moving some logic
 * from the MenuController into a different class.
 */
public class BookmarkController {

    private final CustomProperties properties;
    private final MenuItem bookmark1, bookmark2, bookmark3;
    private static final String BOOKMARK_REGEX = "\\w.*\\s-\\s\\d+\\s+-\\s\\d+";
    private static final String EMPTY = "-";
    private static final String FILE_BOOKMARK3 = "thirdBookmarkOf";
    private static final String FILE_BOOKMARK2 = "secondBookmarkOf";
    private static final String FILE_BOOKMARK1 = "firstBookmarkOf ";

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

        properties = new CustomProperties();
    }

    /**
     * Checks the string if it has the right syntax.
     *
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
    void saving(String note, String nodes, String radius) {
        properties.updateProperties();

        String filePath = properties.getProperty("file", "def");
        String bookmark = note + " - " + nodes + " - " + radius;

        assert (bookmark.equals(checkBookmark(bookmark)));
        updateBookmarks(bookmark, filePath);
    }

    /**
     * Uodates all bookmarks.
     *
     * @param newBookmark The string that should be added.
     */
    private void updateBookmarks(String newBookmark, String filePath) {
        properties.updateProperties();

        properties.setProperty(FILE_BOOKMARK3 + filePath, bookmark2.getText());
        properties.setProperty(FILE_BOOKMARK2 + filePath, bookmark1.getText());
        properties.setProperty(FILE_BOOKMARK1 + filePath, newBookmark);

        properties.saveProperties();

        bookmark3.setText(bookmark2.getText());
        bookmark2.setText(bookmark1.getText());
        bookmark1.setText(newBookmark);
    }

    /**
     * Loads the bookmarks of the specific file filePath.
     *
     * @param filePath The file that is being
     *                 loaded whose bookmarks should be loaded.
     */
    public void initialize(String filePath) {
        properties.updateProperties();
        properties.setProperty(FILE_BOOKMARK3 + filePath,
                properties.getProperty(FILE_BOOKMARK3 + filePath, EMPTY));
        properties.setProperty(FILE_BOOKMARK2 + filePath,
                properties.getProperty(FILE_BOOKMARK2 + filePath, EMPTY));
        properties.setProperty(FILE_BOOKMARK1 + filePath,
                properties.getProperty(FILE_BOOKMARK1 + filePath, EMPTY));
        properties.saveProperties();

        bookmark3.setText(properties.getProperty(FILE_BOOKMARK3 + filePath, EMPTY));
        bookmark2.setText(properties.getProperty(FILE_BOOKMARK2 + filePath, EMPTY));
        bookmark1.setText(properties.getProperty(FILE_BOOKMARK1 + filePath, EMPTY));

    }
}
