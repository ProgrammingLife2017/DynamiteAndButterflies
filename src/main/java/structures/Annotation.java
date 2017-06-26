package structures;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by lex_b on 13/06/2017.
 *
 * Saves the annotations in an annotation object.
 * Uses this setUp to ensure that it can be loaded into a tableView.
 */
public class Annotation {
    private SimpleIntegerProperty start = new SimpleIntegerProperty();
    private SimpleIntegerProperty end = new SimpleIntegerProperty();
    private SimpleStringProperty info = new SimpleStringProperty();
    private SimpleBooleanProperty selected  = new SimpleBooleanProperty();
    private int identifier;
    /**
     * Constructor of the object.
     * @param identifier the ID of the annotation
     * @param startArg the start coördinate of the annotation
     * @param endArg the end coördinate of the annotation
     * @param infoArg the information with the annotation
     */
    public Annotation(int identifier, int startArg, int endArg, String infoArg) {
        this.identifier = identifier;
        start.set(startArg);
        end.set(endArg);
        info.set(infoArg);
        selected.set(false);
    }

    public int getId() {
        return this.identifier;
    }

    public int getStart() {
        return start.get();
    }

    public int getEnd() {
        return end.get();
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

    /**
     * Selects all the annotations.
     * @param allAnnotations Selects all these annotations
     */
    public static void selectAll(HashMap<Integer, HashSet<Annotation>> allAnnotations) {
        HashSet<Annotation> selectThese = new HashSet<>();
        for (int i = 0; i <= allAnnotations.size(); i++) {
            HashSet<Annotation> tempAnnotations = allAnnotations.get(i);
            if (tempAnnotations != null) {
                selectThese.addAll(tempAnnotations);
            }
        }
        for (Annotation annotation : selectThese) {
            annotation.setSelected(true);
        }
    }

    @Override
    public String toString() {
        String res = getInfo();
        res = res.replace("\t", "\n");
        res = res.replace("=", ":\t");
        return res;
    }
}