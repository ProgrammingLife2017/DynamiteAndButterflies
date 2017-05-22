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
 * Created by lex_b on 22/05/2017.
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
    public void loadPopUp(final String partPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/popUp.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(".db File corrupted");
            stage.initModality(Modality.APPLICATION_MODAL);
            popUpOK.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                        public void handle(MouseEvent e) {
                            stage.close();
                        }
            });
            openLoc.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                        public void handle(MouseEvent e) {
                            /*File seq = new File(partPath + ".sequence.db");
                            File adj = new File(partPath + ".adjacency.db");
                            adj.delete();
                            seq.delete();*/
                            try {
                                Runtime.getRuntime().exec("explorer.exe /select," + partPath + ".sequence.db");
                                Runtime.getRuntime().exit(0);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
            });
            stage.show();
        } catch (Exception e) {
            System.out
                    .println("Something went wrong while loading the fxml file");
            e.getCause();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        if(event.getSource() == popUpOK) {
            stage.close();
        }
    }

    @FXML
    public void setMessage(String messageArg) {
        message.setText(messageArg);
    }


}
