import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;

import javax.swing.*;

/**
 * Created by TUDelft SID on 1-5-2017.
 */
public class MenuController {

    @FXML
    private StackPane graphPane;
    private JPanel graphPanel;

    @FXML
    public void initialize() {
        final SwingNode swingNode = new SwingNode();
        graphPanel = new JPanel();
        graphPanel.add(new GraphMaker().buildGraph("/test (1).gfa"));
        createSwingContent(swingNode);
        Group group = new Group();
        group.getChildren().add(swingNode);
        //group.setAutoSizeChildren(false);
        graphPane.getChildren().add(group);
    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                swingNode.setContent(graphPanel);
            }
        });
    }
}
