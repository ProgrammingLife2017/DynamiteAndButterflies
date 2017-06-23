package gui.sub_controllers;

import graph.SequenceGraph;
import gui.GraphDrawer;
import gui.MenuController;

import java.io.IOException;

/**
 * Created by Jip on 17-5-2017.
 * The Zoom Controller controls all buttons
 * in the GUI that are the boss of zooming.
 */
public class ZoomController {

    private static ZoomController zoomController = new ZoomController();
    private MenuController menuController;

    private static final double SCROLL_ZOOM_IN_FACTOR = 0.9;
    private static final double SCROLL_ZOOM_OUT_FACTOR = 1.1;

    /**
     * Constructor of the Zoom Controller.
     */
    public ZoomController() {
    }

    /**
     * Gets the singleton zoomController.
     *
     * @return the ZoomController
     */
    public static ZoomController getInstance() {
        return zoomController;
    }

    /**
     * Zooms in.
     *
     * @param column the column to zoom in on.
     */
    public void zoomIn(int column) {
        GraphDrawer.getInstance().zoom(SCROLL_ZOOM_IN_FACTOR, column);
        menuController.updateRadius();
    }

    /**
     * Zooms out.
     *
     * @param column the column to zoom out on.
     * @throws IOException thrown if can't find
     */
    public void zoomOut(int column) {
        GraphDrawer.getInstance().zoom(SCROLL_ZOOM_OUT_FACTOR, column);
        menuController.updateRadius();
    }

    /**
     * Traverses the graph loaded to a specified destination.
     *
     * @param centreNode The centre node to move to
     * @param radius     Moves to the correct place in the graph
     */
    public void traverseGraphClicked(int centreNode, int radius) {
        if (!GraphDrawer.getInstance().getGraph().getNodes().containsKey(centreNode)) {
            SequenceGraph newGraph = GraphDrawer.getInstance().getGraph().copy();
            newGraph.createSubGraph(centreNode, PanningController.RENDER_RANGE);
            GraphDrawer.getInstance().setGraph(newGraph);
            GraphDrawer.getInstance().setxDifference(0);
        }
        GraphDrawer drawer = GraphDrawer.getInstance();
        GraphDrawer.getInstance().setZoomLevel(
                GraphDrawer.getInstance().findZoomLevel(centreNode, radius));
        double xDiff = drawer.getColumnWidth(
                drawer.getGraph().getNode(centreNode).getColumn())
                - drawer.getZoomLevel() / 2;
        GraphDrawer.getInstance().highlight(centreNode);
        GraphDrawer.getInstance().moveShapes(xDiff);
    }

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }
}
