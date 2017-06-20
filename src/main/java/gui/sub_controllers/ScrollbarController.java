package gui.sub_controllers;

import gui.GraphDrawer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollBar;

/**
 * Created by TUDelft SID on 20-6-2017.
 */
public class ScrollbarController {

    public static ScrollbarController scrollbarController = new ScrollbarController();


    private ScrollBar scrollBar;

    public static ScrollbarController getInstance() {
        return scrollbarController;
    }

    public ScrollbarController() {
    }

    public void initialize(int maxColumnSize) {
        scrollBar.setMax(maxColumnSize);
        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                GraphDrawer.getInstance().setyDifference(scrollBar.getValue() * GraphDrawer.RELATIVE_Y_DISTANCE);
                GraphDrawer.getInstance().redraw();
            }
        });

    }

    public void scroll() {

    }

    public void setScrollBar(ScrollBar scrollBar) {
        this.scrollBar = scrollBar;
    }
}
