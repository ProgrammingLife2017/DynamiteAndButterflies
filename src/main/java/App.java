import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class App extends Application {

    private int Xsize = 10;
    private int Ysize = 6;
    private int lengthEdge = 2;
    private int yBase = 40;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(600, 250);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        try {
            drawShapes(gc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void drawShapes(GraphicsContext gc) throws IOException {

        GfaParser parser = new GfaParser();
        SequenceGraph graph = parser.parse("src/main/resources/test (1).gfa");
        graph.initialize();
        graph.layerizeGraph();
        HashMap<Integer, SequenceNode> nodes = graph.getNodes();

        for(int i = 1; i <= nodes.size(); i++) {
            SequenceNode node = nodes.get(i);
            gc.setFill(Color.BLUE);
            gc.fillRoundRect((node.getColumn() * (Xsize + lengthEdge)) + 50, yBase, Xsize, Ysize, 10, 10);
//            gc.setStroke(Color.BLACK);
//            gc.setLineWidth(1);
//            gc.strokeLine((node.getColumn() * (Xsize + lengthEdge)) + Xsize + 50,43, node.getColumn() * (Xsize + Xsize + lengthEdge) + 50, 43);
        }
    }
}