package parser;

/**
 * Created by marc on 22-5-17.
 */
public class Tuple {
    public final int parentID;
    public final int childID;
    public Tuple(int parentID, int childID) {
        this.parentID = parentID;
        this.childID = childID;
    }
}
