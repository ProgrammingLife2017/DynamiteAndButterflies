package graph;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Jip on 7-6-2017.
 */
public class Genome {

    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleBooleanProperty selected = new SimpleBooleanProperty();

    public Genome() {
        this(-1, null);
    }

    public Genome(int idArg, String nameArg) {
        id.set(idArg);
        name.set(nameArg);
        selected.set(false);
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public boolean getSelected() {
        return selected.get();
    }
}
