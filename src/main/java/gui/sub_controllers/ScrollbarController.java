package gui.sub_controllers;

import gui.GraphDrawer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollBar;

/**
 * Created by Jasper van Tilburg SID on 20-6-2017.
 * <p>
 * This controller handles the scrollbar.
 */
public final class ScrollbarController {

    private static ScrollbarController scrollbarController = new ScrollbarController();

    private ScrollBar scrollBar;

    private ScrollbarController() {
    }

    /**
     * Getter for the singleton ScrollbarController.
     *
     * @return the scrollbar controller
     */
    public static ScrollbarController getInstance() {
        return scrollbarController;
    }

    /**
     * Initialize the scrollbar controller.
     *
     * @param maxColumnSize How far down the graph goes
     */
    public void initialize(int maxColumnSize) {
        scrollBar.setMax(maxColumnSize);
        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number oldValue, Number newValue) {
                GraphDrawer.getInstance().setyDifference(
                        scrollBar.getValue() * GraphDrawer.RELATIVE_Y_DISTANCE);
                GraphDrawer.getInstance().redraw();
            }
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
