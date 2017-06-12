package gui.sub_controllers;

import graph.SequenceGraph;
import gui.GraphDrawer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;


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
    private boolean updating;

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
        if (!updating) {
            if (drawer.getGraph().getEndNodeIndex() < drawer.getGraph().getRightBoundID()) {
                if (drawer.getxDifference() + drawer.getZoomLevel() + 2000 > drawer.getRange()) {
                    new Thread(new Task<Integer>() {
                        @Override
                        protected Integer call() throws Exception {
                            updating = true;
                            System.out.println("OLD: getEndNodeIndex: " + drawer.getGraph().getEndNodeIndex() + ", getRightBoundID: " + drawer.getGraph().getRightBoundID());
                            SequenceGraph newGraph = drawer.getGraph().copy();
                            newGraph.createSubGraph(1,//drawer.getGraph().getStartNodeIndex() + Math.min(1000, drawer.getGraph().getRightBoundID() - drawer.getGraph().getEndNodeIndex()),
                                    drawer.getGraph().getEndNodeIndex() + Math.min(1000, drawer.getGraph().getRightBoundID() - drawer.getGraph().getEndNodeIndex()),
                                    drawer.getGraph().getPartPath());
                            drawer.setGraph(newGraph);
                            drawer.initGraph();
                            System.out.println("NEW: getEndNodeIndex: " + drawer.getGraph().getEndNodeIndex() + ", getRightBoundID: " + drawer.getGraph().getRightBoundID());
                            updating = false;
                            return null;
                        }
                    }).start();
                }
            }
        }
        if (drawer.getGraph().getNodes().containsKey(drawer.getGraph().getRightBoundID())) {
            if (drawer.getxDifference() + drawer.getZoomLevel() > drawer.getColumnWidth(drawer.getGraph().getColumns().size())) {
                return;
            }
        }
        drawer.moveShapes(drawer.getxDifference() + drawer.getZoomLevel() * PANN_FACTOR);
    }

    public void pannLeft() {
        drawer.moveShapes(drawer.getxDifference() - drawer.getZoomLevel() * PANN_FACTOR);
    }

}
