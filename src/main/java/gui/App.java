package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.FileWriter;

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
    public void start(Stage stageIn) throws UnsupportedEncodingException, FileNotFoundException {
        stage = stageIn;
        stage.setTitle("Programming Life");
        loadScene("/FXML/Menu.fxml");
    }

    private static Stage stage;
    private static AnchorPane pane;
    private static CustomProperties properties;
    private static FXMLLoader loader;

    /**
     * This method is able to load FXML files onto the stage.
     * @param path Path of the FXML file to be loaded on the screen
     * @return The FXMLLoader
     */
    public static FXMLLoader loadScene(String path) {

        try {
            setUpProperties();

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

    private static void setUpProperties() throws IOException {
        properties = new CustomProperties();
        try {
            FileReader fileReader = new FileReader("properties.txt");
            properties.load(fileReader);
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Making the properties.txt file");
            FileWriter fileWriter = new FileWriter("properties.txt");
            properties.store(fileWriter, "Property files for Dynamite and Butterflies");
            fileWriter.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void stop() {
        properties.updateProperties();

        String stringOfFile = properties.getProperty("file", "def");
        int numOfBookmarks = Integer.parseInt(
                        properties.getProperty("bookmarkNum" + stringOfFile, "-1"));
        properties.setProperty("bookmarkNum" + stringOfFile, Integer.toString(numOfBookmarks));

        properties.saveProperties();

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
