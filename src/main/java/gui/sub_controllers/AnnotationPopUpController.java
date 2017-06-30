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

public class AnnotationPopUpController {

    @FXML
    private Label message;
    @FXML
    private Button Ok;


    private Stage stage;

    /**
     * Constructor of the PopUp controller  that controls pop-ups on errors.
     */
    public AnnotationPopUpController() {
    }

    public void loadNoAnnotationFound() {
        try {
            popUp();
            Ok.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            stage.close();
                        }
                    });
            setMessage("Sorry, can't find this annotation.");
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void popUp() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/annoPopUp.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Annotation not found.");
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    @FXML
    private void setMessage(String messageArg) {
        message.setText(messageArg);
    }
}
