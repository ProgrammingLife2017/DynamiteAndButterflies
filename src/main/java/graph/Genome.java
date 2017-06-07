package graph;

/**
 * Created by Jip on 7-6-2017.
 */
public class Genome {

    private int id;
    private String name;
    private boolean selected;

    public Genome(int idArg, String nameArg) {
        this.id = idArg;
        this.name = nameArg;
        selected = false;
    }
}
