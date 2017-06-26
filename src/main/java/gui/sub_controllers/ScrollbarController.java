package gui.sub_controllers;

import gui.GraphDrawer;
import javafx.scene.control.ScrollBar;

/**
 * This controller handles the scrollbar.
 * Created by Jasper van Tilburg on 20-6-2017.
 */
public final class ScrollbarController {

    private static final ScrollbarController SCROLLBAR_CONTROLLER = new ScrollbarController();

    private ScrollBar scrollBar;

    private ScrollbarController() {
    }

    /**
     * Getter for the singleton ScrollbarController.
     *
     * @return the scrollbar controller
     */
    public static ScrollbarController getInstance() {
        return SCROLLBAR_CONTROLLER;
    }

    /**
     * Initialize the scrollbar controller.
     *
     * @param maxColumnSize How far down the graph goes
     */
    public void initialize(int maxColumnSize) {
        scrollBar.setMax(maxColumnSize);
        scrollBar.valueProperty().addListener((ov, oldValue, newValue) -> {
            GraphDrawer.getInstance().setyDifference(
                    scrollBar.getValue() * GraphDrawer.RELATIVE_Y_DISTANCE);
            GraphDrawer.getInstance().redraw();
        });

    }

    /**
     * Setter for the scrollbar.
     *
     * @param scrollBar The scrollbar
     */
    public void setScrollBar(ScrollBar scrollBar) {
        this.scrollBar = scrollBar;
    }
}
