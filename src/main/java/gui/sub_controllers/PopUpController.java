package gui.sub_controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


/**
 * Created by Lex Boleij on 22/05/2017.
 * Class that creates a popUp (for now only necessary on db file corruption).
 */

public class PopUpController {

    @FXML
    private Label message;
    @FXML
    private Button popUpOK;
    @FXML
    private Button openLoc;

    private Stage stage;

    /**
     * Constructor of the PopUp controller  that controls pop-ups on errors.
     */
    public PopUpController() {
    }

    /**
     * The popup for a corrupt database file.
     *
     * @param partPath The path to the file
     * @param message  The message to display.
     */
    @FXML
    public void loadDbCorruptPopUp(final String partPath, String message) {
        try {
            popUp(".db File corrupted");
            popUpOK.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                        public void handle(MouseEvent e) {
                            stage.close();
                        }
                    });
            openLoc.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                        public void handle(MouseEvent e) {
                            File seq = new File(partPath + ".sequence.db");
                            File adj = new File(partPath + ".adjacency.db");
                            boolean success = adj.delete();
                            assert success;
                            success = seq.delete();
                            assert success;
                            stage.close();
                        }
                    });
            setMessage(message);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void popUp(String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/popUp.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    @FXML
    private void setMessage(String messageArg) {
        message.setText(messageArg);
    }
}
