package gui.sub_controllers;

import graph.SequenceGraph;
import gui.GraphDrawer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;


import java.util.Observable;

/**
 * Created by Jasper van Tilburg on 29-5-2017.
 *
 * Controls the panning functionality.
 */
public class PanningController extends Observable {

    /**
     * The speed at which to pan.
     */
    private static final double PAN_FACTOR = 0.01;

    /**
     * The the threshold to update the subGraph.
     */
    private static final int RENDER_THRESHOLD = 500;

    /**
     * The amount of nodes to render.
     */
    public static final int RENDER_RANGE = 2000;

    /**
     * The amount of nodes to shift.
     */
    private static final int RENDER_SHIFT = 1000;


    /**
     * The amount of delay in which it is updated
     */
    private static final int ANIMATION_TIMER = 10;

    private Button rightPanButton;
    private Button leftPanButton;
    private Timeline timelineRight;
    private Timeline timelineLeft;
    private boolean updating;

    /**
     * Constructor for the panning controller.
]     * @param leftPanButton - the pan left button.
     * @param rightPanButton - the pan right button,
     */
    public PanningController(Button leftPanButton, Button rightPanButton) {
        this.leftPanButton = leftPanButton;
        this.rightPanButton = rightPanButton;
        initializeTimer();
        initializeButtons();
    }

    /**
     * Timer for panning.
     */
    private void initializeTimer() {
        timelineRight = new Timeline(new KeyFrame(Duration.millis(ANIMATION_TIMER), event -> panRight()));
        timelineRight.setCycleCount(Animation.INDEFINITE);

        timelineLeft = new Timeline(new KeyFrame(Duration.millis(ANIMATION_TIMER), event -> panLeft()));
        timelineLeft.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * initialize function for the pan buttons.
     */
    private void initializeButtons() {
        rightPanButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> timelineRight.play());
        rightPanButton.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> timelineRight.pause());

        leftPanButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> timelineLeft.play());
        leftPanButton.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> timelineLeft.pause());
    }

    /**
     * listener for key presses.
     * @param canvasPanel - the canvas which to apply the listener to,
     */

    public void initializeKeys(Node canvasPanel) {
        canvasPanel.requestFocus();
        canvasPanel.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                timelineRight.play();
            } else if (event.getCode() == KeyCode.LEFT) {
                timelineLeft.play();
            }
            event.consume();
        });
        canvasPanel.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                timelineRight.stop();
            } else if (event.getCode() == KeyCode.LEFT) {
                timelineLeft.stop();
            }
            event.consume();
        });
    }


    //TODO: Clean up this code.
    /**
     * Pan right method.
     */
    private void panRight() {
        if (!updating) {
            if (GraphDrawer.getInstance().getGraph().getRightBoundIndex() < GraphDrawer.getInstance().getGraph().getFullGraphRightBoundIndex()) {
                if (GraphDrawer.getInstance().getMostRightNode().getId() + RENDER_THRESHOLD > GraphDrawer.getInstance().getGraph().getRightBoundID()) {
                    updating = true;
                    new Thread(new Task<Integer>() {
                        @Override
                        protected Integer call() throws Exception {
                            //System.out.println("OLD: getRightBoundID: " + drawer.getGraph().getRightBoundID() + ", getFullGraphRightBoundID: " + drawer.getGraph().getFullGraphRightBoundID() + ", getCentreNodeID: " + drawer.getGraph().getCenterNodeID());
                            long start = System.currentTimeMillis();
                            SequenceGraph newGraph = GraphDrawer.getInstance().getGraph().copy();
                            newGraph.createSubGraph(GraphDrawer.getInstance().getGraph().getCenterNodeID() + RENDER_SHIFT, RENDER_RANGE);
                            int leftMostID = GraphDrawer.getInstance().getMostLeftNode().getId();
                            GraphDrawer.getInstance().setGraph(newGraph);
                            GraphDrawer.getInstance().setxDifference(GraphDrawer.getInstance().getColumnWidth(GraphDrawer.getInstance().getGraph().getNode(leftMostID).getColumn()));
                            long end = System.currentTimeMillis();
                            System.out.println("Runtime: " + (end - start));
                            //System.out.println("NEW: getRightBoundID: " + drawer.getGraph().getRightBoundID() + ", getFullGraphRightBoundID: " + drawer.getGraph().getFullGraphRightBoundID() + ", getCentreNodeID: " + drawer.getGraph().getCenterNodeID());
                            updating = false;
                            return null;
                        }
                    }).start();
                }
            }
        }
        if (GraphDrawer.getInstance().getGraph().getNodes().containsKey(GraphDrawer.getInstance().getGraph().getFullGraphRightBoundID())) {
            if (GraphDrawer.getInstance().getxDifference() + GraphDrawer.getInstance().getZoomLevel() > GraphDrawer.getInstance().getColumnWidth(GraphDrawer.getInstance().getGraph().getColumns().size())) {
                return;
            }
        }
        GraphDrawer.getInstance().moveShapes(GraphDrawer.getInstance().getxDifference() + GraphDrawer.getInstance().getZoomLevel() * PAN_FACTOR);
    }

    /**
     * Pan left method.
     */
    private void panLeft() {
        if (!updating) {
            if (GraphDrawer.getInstance().getGraph().getLeftBoundIndex() > GraphDrawer.getInstance().getGraph().getFullGraphLeftBoundIndex()) {
                if (GraphDrawer.getInstance().getxDifference() - RENDER_THRESHOLD < 0) {
                    updating = true;
                    new Thread(new Task<Integer>() {
                        @Override
                        protected Integer call() throws Exception {
                            //System.out.println("OLD: getLeftBoundID: " + drawer.getGraph().getLeftBoundID() + ", getFullGraphLeftBoundID: " + drawer.getGraph().getFullGraphLeftBoundID() + ", getCentreNodeID: " + drawer.getGraph().getCenterNodeID());
                            long start = System.currentTimeMillis();
                            SequenceGraph newGraph = GraphDrawer.getInstance().getGraph().copy();
                            newGraph.createSubGraph(GraphDrawer.getInstance().getGraph().getCenterNodeID() - RENDER_SHIFT, RENDER_RANGE);
                            int leftMostID = GraphDrawer.getInstance().getMostLeftNode().getId();
                            GraphDrawer.getInstance().setGraph(newGraph);
                            GraphDrawer.getInstance().setxDifference(GraphDrawer.getInstance().getColumnWidth(GraphDrawer.getInstance().getGraph().getNode(leftMostID).getColumn()));
                            long end = System.currentTimeMillis();
                            System.out.println("Runtime: " + (end - start));
                            //System.out.println("NEW: getLeftBoundID: " + drawer.getGraph().getLeftBoundID() + ", getFullGraphLeftBoundID: " + drawer.getGraph().getFullGraphLeftBoundID() + ", getCentreNodeID: " + drawer.getGraph().getCenterNodeID());
                            updating = false;
                            return null;
                        }
                    }).start();
                }
            }
        }
        if (GraphDrawer.getInstance().getGraph().getNodes().containsKey(GraphDrawer.getInstance().getGraph().getFullGraphLeftBoundID())) {
            if (GraphDrawer.getInstance().getxDifference() < 0) {
                return;
            }
        }
        GraphDrawer.getInstance().moveShapes(GraphDrawer.getInstance().getxDifference() - GraphDrawer.getInstance().getZoomLevel() * PAN_FACTOR);
    }

}
