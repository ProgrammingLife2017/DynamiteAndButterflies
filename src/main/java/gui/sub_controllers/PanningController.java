package gui.sub_controllers;

import gui.GraphDrawer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollBar;

/**
 * Created by TUDelft SID on 29-5-2017.
 */
public class PanningController {

    private ScrollBar scrollbar;
    private GraphDrawer drawer;
    private boolean active;

    public PanningController(ScrollBar scrollBar, GraphDrawer drawer) {
        this.scrollbar = scrollBar;
        this.drawer = drawer;
        initialize();
    }

    public void initialize() {
        scrollbar.setMax(drawer.getZoomLevel());
        scrollbar.setVisibleAmount(drawer.getZoomLevel());
        scrollbar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                if (active) {
                    drawer.moveShapes(drawer.getxDifference() + (new_val.doubleValue() - old_val.doubleValue()));
                }
            }
        });
    }

    public void setScrollbarSize(double factor) {
        setScrollbarSize(factor, (int) (scrollbar.getMax() / 2));
    }

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
