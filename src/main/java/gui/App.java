package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.prefs.Preferences;

/**
 * Created by Jasper van Tilburg on 1-5-2017.
 *
 * The Application class.
 * This class creates and maintains the different elements of the gui.
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
     * Launches the application with Menu.fxml as default scene.
     * @param stageIn The stage to load the app.
     * @throws UnsupportedEncodingException Needs a certain encoding
     * @throws FileNotFoundException Needs a certain file
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public void start(Stage stageIn) throws UnsupportedEncodingException, FileNotFoundException {
        stage = stageIn;
        stage.setTitle("Programming Life");
        loadScene("/FXML/Menu.fxml");
    }

    private static Stage stage;
    private static AnchorPane pane;
    private static Preferences prefs = Preferences.userRoot();
    private static FXMLLoader loader;

    /**
     * This method is able to load FXML files onto the stage.
     * @param path Path of the FXML file to be loaded on the screen
     * @return The FXMLLoader
     */
    public static FXMLLoader loadScene(String path) {

        try {
            // Load the anchor pane
            loader = new FXMLLoader();
            loader.setLocation(App.class.getResource(path));
            pane = loader.load();

            // Set the pane onto the scene
            Scene scene = new Scene(pane);
            stage.setTitle("Wow!! DynamiteAndButterflies genome visualiser\t");
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

    @Override
    public void stop() {
        String stringOfFile = prefs.get("file", "def");
        int numOfBookmarks = prefs.getInt("bookmarkNum" + stringOfFile, -1);
        prefs.putInt("bookmarkNum" + stringOfFile, numOfBookmarks);
        MenuController controller = loader.getController();
        if (controller.getSequenceHashMap() != null) {
            controller.getSequenceHashMap().close();
        }
    }

    /**
     * Getter for the stage on which the application is loaded.
     * @return The Stage on which the application is loaded.
     */
    public static Stage getStage() {
        return stage;
    }
}
