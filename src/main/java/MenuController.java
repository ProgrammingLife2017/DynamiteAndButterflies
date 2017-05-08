import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Created by TUDelft SID on 1-5-2017.
 */
public class MenuController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Canvas canvas;
    private GraphicsContext gc;
    private GraphDrawer drawer;

    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
    }

    @FXML
    public void openFileClicked() {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        //fileChooser.setInitialDirectory(this.getClass().getResource("/resources").toString());
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                GfaParser parser = new GfaParser();
                System.out.println("src/main/resources/" + file.getName());
                SequenceGraph graph = graph = parser.parse("src/main/resources/" + file.getName());
                drawer = new GraphDrawer(graph, gc);
                drawer.drawShapes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
