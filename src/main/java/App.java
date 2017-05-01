import javafx.application.Application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Created by TUDelft SID on 1-5-2017.
 */
public class App extends Application {

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage stageIn) throws UnsupportedEncodingException, FileNotFoundException {
        stage = stageIn;
        stage.setTitle("Programming Life");
        loadScene("/FXML/Menu.fxml");
    }

    private static Stage stage;
    private static AnchorPane pane;

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
