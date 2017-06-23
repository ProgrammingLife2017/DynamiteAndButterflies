package gui.sub_controllers;

import gui.GraphDrawer;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * This class handles adding a bookmark.
 * <p>
 * Created by Jip on 1-6-2017.
 */
public class BookmarkPopUp {

    @FXML
    private TextField noteBook;
    @FXML
    private TextField radiusBook;
    @FXML
    private TextField centreNodeBook;

    private BookmarkController bookmarkController;

    /**
     * Intializes the pop up.
     *
     * @param centreNode            The centre node already filled in in the main application
     * @param zoomLevel             The radius already filled in in the main application
     * @param bookmarkControllerArg The bookmark controller linked to the main application.
     */
    public void initialize(int centreNode, double zoomLevel,
                           BookmarkController bookmarkControllerArg) {
        centreNodeBook.setText(Integer.toString(centreNode));
        radiusBook.setText(Integer.toString(GraphDrawer.getInstance().getRadius()));

        bookmarkController = bookmarkControllerArg;
    }

    /**
     * Pressing confirm in the popup saves the bookmark.
     * And closes the pop up.
     */
    @FXML
    public void confirmNewBookmark() {
        bookmarkController.saving(noteBook.getText(),
                centreNodeBook.getText(), radiusBook.getText());
        close();
    }

    /**
     * Closes the pop up.
     */
    @FXML
    public void cancelButtonPress() {
        close();
    }

    /**
     * A general function that closes the stage.
     */
    private void close() {
        Stage stage = (Stage) radiusBook.getScene().getWindow();
        stage.close();
    }
}
