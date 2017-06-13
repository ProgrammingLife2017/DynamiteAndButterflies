package gui.sub_controllers;

import gui.GraphDrawer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollBar;

import java.util.Observable;

/**
 * Created by Jasper van Tilburg on 29-5-2017.
 *
 * Controls the panning functionality.
 */
public class PanningController extends Observable {

    public static final int EXTEND_LEFT = 0;
    public static final int EXTEND_RIGHT = 1;

    private final ScrollBar scrollbar;

    private boolean active;

    /**
     * Constructor.
     * @param scrollBar Scrollbar.
     */
    public PanningController(ScrollBar scrollBar) {
        this.scrollbar = scrollBar;
        initialize();
    }

    /**
     * Initializes the scrollbar and adds a listener to it.
     * The listener is only active when it is manually changed, not by zooming.
     */
    private void initialize() {
        scrollbar.setMax(GraphDrawer.getInstance().getZoomLevel());
        scrollbar.setVisibleAmount(GraphDrawer.getInstance().getZoomLevel());
        scrollbar.setValue(scrollbar.getMax() / 2);
        scrollbar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number oldVal, Number newVal) {
                if (active) {
                    GraphDrawer.getInstance().moveShapes(GraphDrawer.getInstance().getxDifference()
                            + (newVal.doubleValue() - oldVal.doubleValue()));
                }
                if (GraphDrawer.getInstance().getLeftbound() < 0) {
                    setChanged();
                    notifyObservers(EXTEND_LEFT);
                } else if (GraphDrawer.getInstance().getRightbound() > GraphDrawer.getInstance().getRange()) {
                    setChanged();
                    notifyObservers(EXTEND_RIGHT);
                }
            }
        });
    }

    /**
     * Change the scrollbar value and visible amount when zooming in by scrolling.
     * @param column Column that is the centre of the zooming.
     */
    public void setScrollbarSize(double column) {
        active = false;
        scrollbar.setValue(column);
        scrollbar.setVisibleAmount(GraphDrawer.getInstance().getZoomLevel());
        active = true;
    }

}
