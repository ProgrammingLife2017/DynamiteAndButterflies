package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * The Application class.
 * This class creates and maintains the different elements of the GUI.
 */
public class App extends Application {

    /**
     * Intializes the application.
     * @param stageIn The Stage on which the application is built
     * @throws UnsupportedEncodingException If it cannot encode
     * @throws FileNotFoundException If it cannot find the file
     */
    public void start(Stage stageIn) throws UnsupportedEncodingException, FileNotFoundException {
        stage = stageIn;
        stage.setTitle("Programming Life");
        loadScene("/FXML/Menu.fxml");
    }

    private static Stage stage;
    private static AnchorPane pane;

    /**
     * This method loads the FMXL files.
     * @param path The place where the FXML file is
     * @return A FMXLLoader which contains the file that is specified in path.
     */
    public static FXMLLoader loadScene(String path) {

        try {
            // Load the anchor pane
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource(path));
            pane = loader.load();

            // Set the pane onto the scene
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
            System.out.println(path + " loaded on the stage");
            return loader;
        } catch (IOException e) {
            e.printStackTrace();
            System.out
                    .println("Something went wrong while loading the fxml file");
        }
        return null;
    }

}
