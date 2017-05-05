import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class App extends Application {

    public int Xsize = 20;
    public int Ysize = 12;
    public int lengthEdge = 10;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(600, 250);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawShapes(gc);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void drawShapes(GraphicsContext gc) {
        ArrayList<Node2> nodes = loadGraph();
        HashMap<Integer, Node2> hash = GraphMaker.hashNode2s(nodes);
        GraphMaker.assignColumns(nodes, hash);
        for(Node2 node : nodes) {
            gc.setFill(Color.BLUE);
            gc.fillRoundRect(node.getColumnID() * (Xsize + lengthEdge) + 50, 40, Xsize, Ysize, 10, 10);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeLine(node.getColumnID() * (Xsize + Xsize) + 50,46, node.getColumnID() * (Xsize + Xsize + lengthEdge) + 50, 46);
        }
    }

    public ArrayList<Node2> loadGraph() {
        InputStream in = GfaParser.class.getClass().getResourceAsStream("/TB10.gfa");
        GfaParser parser = new GfaParser();
        parser.parse(in);
        return parser.getList();
    }
}