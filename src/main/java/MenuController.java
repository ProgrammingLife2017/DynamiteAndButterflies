import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;

/**
 * Created by TUDelft SID on 1-5-2017.
 */
public class MenuController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private StackPane graphPane;
    private JPanel graphPanel;

    @FXML
    public void initialize() {

    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                swingNode.setContent(graphPanel);
            }
        });
    }

    @FXML
    public void openFileClicked() {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        System.out.println(this.getClass().getResource("test (1).gfa"));
        //fileChooser.setInitialDirectory(this.getClass().getResource("/resources").toString());
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openFile(file);
        }
    }

    public void openFile(File file) {
        final SwingNode swingNode = new SwingNode();
        graphPanel = new JPanel();
        System.out.println(file.getName());
        graphPanel.add(new GraphMaker().buildGraph("/" + file.getName()));
        createSwingContent(swingNode);
        Group group = new Group();
        group.getChildren().add(swingNode);
        //group.setAutoSizeChildren(false);
        graphPane.getChildren().add(group);
    }
}
