import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by TUDelft SID on 1-5-2017.
 */
public class MenuController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Canvas canvas;
    private GraphicsContext gc;

    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        gc.fillRect(10,10,20,20);
    }

    @FXML
    public void openFileClicked() {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        //fileChooser.setInitialDirectory(this.getClass().getResource("/resources").toString());
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openFile(file);
        }
    }

    public void openFile(File file) {
        //TODO
    }
}
