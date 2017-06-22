package gui.sub_controllers;

import graph.SequenceGraph;
import gui.GraphDrawer;
import gui.MenuController;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import java.util.Observable;

/**
 * Created by Jasper van Tilburg on 29-5-2017.
 * <p>
 * Controls the panning functionality.
 */
public class PanningController extends Observable {

    /**
     * The amount of nodes to render.
     */
    public static final int RENDER_RANGE = 2000;

    /**
     * The speed at which to pan.
     */
    private static final double PANN_FACTOR = 0.03;

    /**
     * The the threshold to update the subGraph.
     */
    private static final int RENDER_THRESHOLD = 500;

    /**
     * The amount of nodes to shift.
     */
    private static final int RENDER_SHIFT = 1000;

    private static PanningController panningController = new PanningController();

    private MenuController menuController;
    private Button rightPannButton;
    private Button leftPannButton;
    private boolean updating;

    /**
     * Constructor for the panning controller.
     */
    private PanningController() {
    }

    /**
     * Return the singleton instance of panning controller.
     *
     * @return The panning controller
     */
    public static PanningController getInstance() {
        return panningController;
    }

    /**
     * Initialize the panningcontroller.
     *
     * @param leftPannButton  the pan left button.
     * @param rightPannButton the pan right button,
     */
    public void initialize(Button leftPannButton, Button rightPannButton) {
        this.leftPannButton = leftPannButton;
        this.rightPannButton = rightPannButton;
        initializeButtons();
    }

    /**
     * initialize function for the pan buttons.
     */
    private void initializeButtons() {
        rightPannButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> panRight());
        leftPannButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> panLeft());
    }

    /**
     * listener for key presses.
     *
     * @param canvasPanel - the canvas which to apply the listener to,
     */
    public void initializeKeys(Node canvasPanel) {
        canvasPanel.requestFocus();
        canvasPanel.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                panRight();
            } else if (event.getCode() == KeyCode.LEFT) {
                panLeft();
            } else if (event.getCode() == KeyCode.UP) {
                menuController.zoomInClicked();
            } else if (event.getCode() == KeyCode.DOWN) {
                menuController.zoomOutClicked();
            }
            event.consume();
        });
    }

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
        GraphDrawer.getInstance().moveShapes(GraphDrawer.getInstance().getxDifference() + GraphDrawer.getInstance().getZoomLevel() * PANN_FACTOR);
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
        GraphDrawer.getInstance().moveShapes(GraphDrawer.getInstance().getxDifference() - GraphDrawer.getInstance().getZoomLevel() * PANN_FACTOR);
    }

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }
}
