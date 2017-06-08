package graph;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Jip on 7-6-2017.
 */
public class Genome {

    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty selected = new SimpleStringProperty();

    /**
     * Basic constructor I think I needed to make.
     */
    public Genome() {
        this(-1, null);
    }

    /**
     * Real constructor of Genome object.
     * @param idArg the id of the genome
     * @param nameArg the name of the genome
     */
    public Genome(int idArg, String nameArg) {
        id.set(idArg);
        name.set(nameArg);
        selected.set("false");
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setSelected(String selected) {
        if (selected.startsWith("t")) {
            this.selected.set("true");
        } else if (selected.startsWith("f")) {
            this.selected.set("false");
        } else {
            this.selected.set(null);
        }
    }

    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getSelected() {
        return selected.get();
    }
}
