import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.ArrayList;

public class App extends Application {

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

        int counter = 10;

        ArrayList<Node2> nodes = loadGraph();
        for(Node2 node : nodes) {
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(10);
            gc.strokeLine(counter,40, counter + node.getSeq().length() + 20, 40);
            counter += node.getSeq().length() + 25;
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeLine(counter,40, counter + 5, 40);
            counter += 10;
        }



    }

    public ArrayList loadGraph() {
        InputStream in = GfaParser.class.getClass().getResourceAsStream("/TB10.gfa");
        GfaParser parser = new GfaParser();
        parser.parse(in);
        return parser.getList();
    }
}