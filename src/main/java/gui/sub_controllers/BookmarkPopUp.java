package gui.sub_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
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

    public void initialize(int centreNode, int radius, BookmarkController bookmarkControllerArg) {
        centreNodeBook.setText(Integer.toString(centreNode));
        radiusBook.setText(Integer.toString(radius));

        bookmarkController = bookmarkControllerArg;
    }

    @FXML
    public void confirmNewBookmark() {
        bookmarkController.saving(noteBook.getText(), centreNodeBook.getText(), radiusBook.getText());
        close();
    }

    @FXML
    public void cancelButtonPress() {
        close();
    }

    private void close() {
        Stage stage = (Stage) radiusBook.getScene().getWindow();
        stage.close();
    }
}
