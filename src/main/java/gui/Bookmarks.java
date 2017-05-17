package gui;

import java.util.prefs.Preferences;

/**
 * Created by Jip on 17-5-2017.
 * A Bookmarks class moving some logic from the MenuController into a different class.
 */
class Bookmarks {

    /**
     * The preferences of the user are saved in this built-in API.
     */
    static Preferences prefs = Preferences.userRoot();

    /**
     * Loads the bookmarks of the specific file stringFile.
     * @param stringFile The file that is being loaded whose bookmarks should be loaded.
     */
    static void loadBookmarks(String stringFile) {
        if (prefs.getInt("bookmarkNum" + stringFile, -1) == -1) {
            prefs.putInt("bookmarkNum" + stringFile, 0);
        }

        int largestIndex = prefs.getInt("bookmarkNum" + stringFile, -1);
        int i = 0;

        while (i <= largestIndex) {
            int newIndex = i;
            String realBM = prefs.get(stringFile + newIndex, "-");
            MenuController.updateBookmarks(realBM);
            i++;
        }
    }

    /**
     * Saves the bookmarks when the user presses save.
     * @param nodes The centre node
     * @param radius The radius of nodes we should save/show
     */
    static void saving(String nodes, String radius) {

        String stringFile = prefs.get("file", "def");
        int newIndex = prefs.getInt("bookmarkNum" + stringFile, -1);
        newIndex++;
        prefs.put(stringFile + newIndex, nodes + "-" + radius);
        prefs.putInt("bookmarkNum" + stringFile, newIndex);

        MenuController.updateBookmarks(nodes + "-" + radius);
    }
}
