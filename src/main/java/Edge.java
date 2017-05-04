/**
 * Created by eric on 4-5-17.
 */
public class Edge {

    private final Integer parent;
    private final Integer child;
    private boolean original;

    public Edge(Integer parent, Integer child) {
        this.child = child;
        this.parent = parent;
        this.original = true;
    }

    public void setDummy() {
        this.original = false;
    }

    public Integer getChild() {
        return child;
    }

    public Integer getParent() {
        return parent;
    }
}
