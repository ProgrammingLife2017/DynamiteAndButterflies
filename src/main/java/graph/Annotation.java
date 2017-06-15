package graph;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by lex_b on 13/06/2017.
 */
public class Annotation {
    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleIntegerProperty start = new SimpleIntegerProperty();
    private SimpleIntegerProperty end = new SimpleIntegerProperty();
    private SimpleStringProperty info = new SimpleStringProperty();
    private SimpleBooleanProperty selected  = new SimpleBooleanProperty();


    public Annotation(int idArg, int startArg, int endArg, String infoArg) {
        id.set(idArg);
        start.set(startArg);
        end.set(endArg);
        info.set(infoArg);
        selected.set(false);
    }

    public int getStart() {
        return start.get();
    }

    public int getEnd() {
        return end.get();
    }

    public int getId() {
        return id.get();
    }

    public String getInfo() {
        return info.get();
    }

    public SimpleBooleanProperty getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}