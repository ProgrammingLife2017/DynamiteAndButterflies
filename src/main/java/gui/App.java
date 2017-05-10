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
<<<<<<< HEAD:src/main/java/gui/App.java
 * The Application class.
 * This class creates and maintains the different elements of the GUI.
=======
 * Created by Jasper van Tilburg on 1-5-2017.
 *
 * Class to startup the application.
>>>>>>> BasicVisualization:src/main/java/GUI/App.java
 */
public class App extends Application {

    /**
     * A main method that launches the application.
     * @param args arguments that might be neccesary later
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
<<<<<<< HEAD:src/main/java/gui/App.java
     * Intializes the application.
     * @param stageIn The Stage on which the application is built
     * @throws UnsupportedEncodingException If it cannot encode
     * @throws FileNotFoundException If it cannot find the file
=======
     * Launches the application with Menu.fxml as default scene.
     * @param stageIn
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
>>>>>>> BasicVisualization:src/main/java/GUI/App.java
     */
    public void start(Stage stageIn) throws UnsupportedEncodingException, FileNotFoundException {
        stage = stageIn;
        stage.setTitle("Programming Life");
        loadScene("/FXML/Menu.fxml");
    }

    private static Stage stage;
    private static AnchorPane pane;

    /**
<<<<<<< HEAD:src/main/java/gui/App.java
     * This method loads the FMXL files.
     * @param path The place where the FXML file is
     * @return A FMXLLoader which contains the file that is specified in path.
=======
     * This method is able to load FXML files onto the stage.
     * @param path Path of the FXML file to be loaded on the screen
     * @return The FXMLLoader
>>>>>>> BasicVisualization:src/main/java/GUI/App.java
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
            stage.setMaximized(true);
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
