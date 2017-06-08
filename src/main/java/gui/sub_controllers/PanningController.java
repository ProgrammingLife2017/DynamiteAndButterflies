package gui.sub_controllers;

import graph.SequenceGraph;
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
        scrollbar.setValue(scrollbar.getMax() / 2);
        scrollbar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number oldVal, Number newVal) {
                if (active) {
                    drawer.moveShapes(drawer.getxDifference()
                            + (newVal.doubleValue() - oldVal.doubleValue()));
                    System.out.println("Right bound: " + (drawer.getxDifference() + drawer.getZoomLevel()) + "Range: " + drawer.getRange());
                }
//                if (drawer.getxDifference() + drawer.getZoomLevel() > drawer.getRange()) {
//                    SequenceGraph graph = drawer.getGraph().extendGraph(100);
//                    drawer.setGraph(graph);
//                    drawer.initializeColumnWidths();
//                }

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
        scrollbar.setVisibleAmount(drawer.getZoomLevel());
        active = true;
    }

}
