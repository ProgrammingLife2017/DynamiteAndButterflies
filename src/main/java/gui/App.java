package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;


/**
 * Created by Jasper van Tilburg on 1-5-2017.
 * <p>
 * The Application class.
 * This class creates and maintains the different elements of the gui.
 */
public class App extends Application {

    /**
     * A main method that launches the application.
     *
     * @param args arguments that might be neccesary later
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Launches the application with Menu.fxml as default scene.
     *
     * @param stageIn The stage to load the app.
     * @throws UnsupportedEncodingException Needs a certain encoding
     * @throws FileNotFoundException        Needs a certain file
     */
    public void start(Stage stageIn) throws UnsupportedEncodingException, FileNotFoundException {
        stage = stageIn;
        stage.setTitle("Programming Life");
        loadScene();
    }

    private static Stage stage;
    private static CustomProperties properties;
    private static FXMLLoader loader;

    /**
     * This method is able to load FXML files onto the stage.
     */
    private static void loadScene() {

        try {
            setUpProperties();

            // Load the anchor pane
            loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/FXML/Menu.fxml"));
            AnchorPane pane = loader.load();

            // Set the pane onto the scene
            Scene scene = new Scene(pane);
            scene.getStylesheets().add(App.class.getResource("/CSS/screenshotCSS").toExternalForm());
            stage.setTitle("Multiple Genome Visualiser - DynamiteAndButterflies  - TU Delft");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.getIcons().add(new Image("http://i.imgur.com/CjIn1cR.png"));
            stage.show();
            // stage.setMaximized(true); -> Mac fails to load a setMaximized(true) stage.
        } catch (IOException e) {
            System.err
                    .println("Something went wrong while loading the fxml file");
        }
    }

    /**
     * Initializes the properties file.
     * @throws IOException Throws IOException if writing the file goes wrong.
     */
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
        MenuController controller = loader.getController();
        if (controller.getSequenceHashMap() != null) {
            controller.getSequenceHashMap().close();
        }
    }

    /**
     * Getter for the stage on which the application is loaded.
     *
     * @return The Stage on which the application is loaded.
     */
    static Stage getStage() {
        return stage;
    }
}
