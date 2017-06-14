package gui.sub_controllers;

import graph.SequenceGraph;
import gui.GraphDrawer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;


/**
 * Created by Jasper van Tilburg on 29-5-2017.
 *
 * Controls the panning functionality.
 */
public class PanningController {

    /**
     * The speed at which to pan.
     */
    private static final double PANN_FACTOR = 0.005;

    /**
     * The the threshold to update the subGraph.
     */
    private static final int RENDER_THRESHOLD = 100;

    /**
     * The amount of nodes to render.
     */
    static final int RENDER_RANGE = 200;

    /**
     * The amount of nodes to shift.
     */
    private static final int RENDER_SHIFT = 100;

    private GraphDrawer drawer;
    private Button rightPannButton;
    private Button leftPannButton;
    private Timeline timelineRight;
    private Timeline timelineLeft;
    private boolean updating;

    /**
     * Constructor for the panning controller.
     * @param drawer - the drawer.
     * @param leftPannButton - the pan left button.
     * @param rightPannButton - the pan right button,
     */
    public PanningController(GraphDrawer drawer, Button leftPannButton, Button rightPannButton) {
        this.drawer = drawer;
        this.leftPannButton = leftPannButton;
        this.rightPannButton = rightPannButton;
        initializeTimer();
        initializeButtons();
    }

    /**
     * Timer for panning.
     */
    private void initializeTimer() {
        timelineRight = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                panRight();
            }
        }));
        timelineRight.setCycleCount(Animation.INDEFINITE);

        timelineLeft = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                panLeft();
            }
        }));
        timelineLeft.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * initialize function for the pan buttons.
     */
    private void initializeButtons() {
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

    /**
     * listener for key presses.
     * @param canvasPanel - the canvas which to apply the listener to,
     */
    public void initializeKeys(Node canvasPanel) {
        canvasPanel.requestFocus();
        canvasPanel.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.RIGHT) {
                    timelineRight.play();
                } else if (event.getCode() == KeyCode.LEFT) {
                    timelineLeft.play();
                }
                event.consume();
            }
        });
        canvasPanel.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.RIGHT) {
                    timelineRight.stop();
                } else if (event.getCode() == KeyCode.LEFT) {
                    timelineLeft.stop();
                }
                event.consume();
            }
        });
    }

    /**
     * Pan right method.
     */
    private void panRight() {
        if (!updating) {
            if (drawer.getGraph().getRightBoundIndex() < drawer.getGraph().getFullGraphRightBoundIndex()) {
                if (drawer.getxDifference() + drawer.getZoomLevel() + RENDER_THRESHOLD > drawer.getRange()) {
                    updating = true;
                    new Thread(new Task<Integer>() {
                        @Override
                        protected Integer call() throws Exception {
                            System.out.println("OLD: getRightBoundID: " + drawer.getGraph().getRightBoundID() + ", getFullGraphRightBoundID: " + drawer.getGraph().getFullGraphRightBoundID() + ", getCentreNodeID: " + drawer.getGraph().getCenterNodeID());
                            SequenceGraph newGraph = drawer.getGraph().copy();
                            newGraph.createSubGraph(drawer.getGraph().getCenterNodeID() + RENDER_SHIFT, RENDER_RANGE, drawer.getGraph().getPartPath());
                            int leftMostID = drawer.getMostLeftNode().getId();
                            drawer.setGraph(newGraph);
                            drawer.initGraph();
                            //drawer.setxDifference(drawer.getColumnWidth(drawer.getGraph().getNode(leftMostID).getColumn()));
                            drawer.moveShapes(drawer.getColumnWidth(drawer.getGraph().getNode(leftMostID).getColumn()));
                            System.out.println("NEW: getRightBoundID: " + drawer.getGraph().getRightBoundID() + ", getFullGraphRightBoundID: " + drawer.getGraph().getFullGraphRightBoundID() + ", getCentreNodeID: " + drawer.getGraph().getCenterNodeID());
                            updating = false;
                            return null;
                        }
                    }).start();
                }
            }
        }
        if (drawer.getGraph().getNodes().containsKey(drawer.getGraph().getFullGraphRightBoundID())) {
            if (drawer.getxDifference() + drawer.getZoomLevel() > drawer.getColumnWidth(drawer.getGraph().getColumns().size())) {
                return;
            }
        }
        drawer.moveShapes(drawer.getxDifference() + drawer.getZoomLevel() * PANN_FACTOR);
    }

    /**
     * Pan left method.
     */
    private void panLeft() {
        if (!updating) {
            if (drawer.getGraph().getLeftBoundIndex() > drawer.getGraph().getFullGraphLeftBoundIndex()) {
                if (drawer.getxDifference() - RENDER_THRESHOLD < 0) {
                    updating = true;
                    new Thread(new Task<Integer>() {
                        @Override
                        protected Integer call() throws Exception {
                            System.out.println("OLD: getLeftBoundID: " + drawer.getGraph().getLeftBoundID() + ", getFullGraphLeftBoundID: " + drawer.getGraph().getFullGraphLeftBoundID() + ", getCentreNodeID: " + drawer.getGraph().getCenterNodeID());
                            SequenceGraph newGraph = drawer.getGraph().copy();
                            newGraph.createSubGraph(drawer.getGraph().getCenterNodeID() - RENDER_SHIFT, RENDER_RANGE, drawer.getGraph().getPartPath());
                            int leftMostID = drawer.getMostLeftNode().getId();
                            drawer.setGraph(newGraph);
                            drawer.initGraph();
                            //drawer.setxDifference(drawer.getColumnWidth(drawer.getGraph().getNode(leftMostID).getColumn()));
                            drawer.moveShapes(drawer.getColumnWidth(drawer.getGraph().getNode(leftMostID).getColumn()));
                            System.out.println("NEW: getLeftBoundID: " + drawer.getGraph().getLeftBoundID() + ", getFullGraphLeftBoundID: " + drawer.getGraph().getFullGraphLeftBoundID() + ", getCentreNodeID: " + drawer.getGraph().getCenterNodeID());
                            updating = false;
                            return null;
                        }
                    }).start();
                }
            }
        }
        if (drawer.getGraph().getNodes().containsKey(drawer.getGraph().getFullGraphLeftBoundID())) {
            if (drawer.getxDifference() < 0) {
                return;
            }
        }
        drawer.moveShapes(drawer.getxDifference() - drawer.getZoomLevel() * PANN_FACTOR);
    }

}
