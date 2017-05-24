package gui.subControllers;

import gui.App;
import javafx.event.ActionEvent;
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
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Pattern;


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
                            adj.delete();
                            seq.delete();
                            stage.close();
                        }
            });
            setMessage(message);
            stage.showAndWait();
        } catch (Exception e) {
            System.out
                    .println("Something went wrong while loading the fxml file");
            e.getCause();
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
