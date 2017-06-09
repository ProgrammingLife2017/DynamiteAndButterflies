package gui.sub_controllers;

import gui.GraphDrawer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jasper van Tilburg on 29-5-2017.
 *
 * Controls the panning functionality.
 */
public class PanningController {

    public static final double PANN_FACTOR = 0.01;

    private GraphDrawer drawer;
    private Button rightPannButton;
    private Button leftPannButton;
    private Timeline timelineRight;
    private Timeline timelineLeft;

    public PanningController(GraphDrawer drawer, Button leftPannButton, Button rightPannButton) {
        this.drawer = drawer;
        this.leftPannButton = leftPannButton;
        this.rightPannButton = rightPannButton;
        initializeTimer();
        initializeButtons();
    }

    public void initializeTimer() {
        timelineRight = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pannRight();
                System.out.println("test");
            }
        }));
        timelineRight.setCycleCount(Animation.INDEFINITE);

        timelineLeft = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pannLeft();
            }
        }));
        timelineLeft.setCycleCount(Animation.INDEFINITE);
    }

    public void initializeButtons() {
        rightPannButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                timelineRight.play();
            }
        });
        rightPannButton.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                timelineRight.pause();
            }
        });

        leftPannButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                timelineLeft.play();
            }
        });
        leftPannButton.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                timelineLeft.pause();
            }
        });
    }

    public void pannRight() {
        if (drawer.getxDifference() + drawer.getZoomLevel() > drawer.getRange()) {
            drawer.getGraph().createSubGraph(1, drawer.getGraph().getEndNodeIndex() + 100, drawer.getGraph().getPartPath());
            drawer.initGraph();
        }
        //TODO check for left bound
        drawer.moveShapes(drawer.getxDifference() + drawer.getZoomLevel() * PANN_FACTOR);
    }

    public void pannLeft() {
        drawer.moveShapes(drawer.getxDifference() - drawer.getZoomLevel() * PANN_FACTOR);
    }

}
