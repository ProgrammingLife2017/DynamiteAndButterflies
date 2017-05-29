package gui.sub_controllers;

import gui.GraphDrawer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollBar;

/**
 * Created by Jasper van Tilburg on 29-5-2017.
 *
 * Controls the panning functionality.
 */
public class PanningController {

    private final ScrollBar scrollbar;
    private final GraphDrawer drawer;
    private boolean active;

    /**
     * Constructor.
     * @param scrollBar Scrollbar.
     * @param drawer The graphdrawer.
     */
    public PanningController(ScrollBar scrollBar, GraphDrawer drawer) {
        this.scrollbar = scrollBar;
        this.drawer = drawer;
        initialize();
    }

    /**
     * Initializes the scrollbar and adds a listener to it.
     * The listener is only active when it is manually changed, not by zooming.
     */
    private void initialize() {
        scrollbar.setMax(drawer.getZoomLevel());
        scrollbar.setVisibleAmount(drawer.getZoomLevel());
        scrollbar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number oldVal, Number newVal) {
                if (active) {
                    drawer.moveShapes(drawer.getxDifference()
                            + (newVal.doubleValue() - oldVal.doubleValue()));
                }
            }
        });
    }

    /**
     * Change the scrollbar value and visible amount when zooming with the buttons.
     * @param factor Changing factor of the visible amount.
     */
    public void setScrollbarSize(double factor) {
        setScrollbarSize(factor, (int) (scrollbar.getMax() / 2));
    }

    /**
     * Change the scrollbar value and visible amount when zooming in by scrolling.
     * @param factor Changing factor of the visible amount
     * @param column Column that is the centre of the zooming.
     */
    public void setScrollbarSize(double factor, int column) {
        active = false;
        double max = scrollbar.getMax();
        double amount = scrollbar.getVisibleAmount();
        if ((factor < 1 && amount < 1) || (factor > 1 && amount >= max)) {
            return;
        }
        amount = scrollbar.getVisibleAmount() * factor;
        scrollbar.setValue(column);
        scrollbar.setVisibleAmount(amount);
        active = true;
    }

}
