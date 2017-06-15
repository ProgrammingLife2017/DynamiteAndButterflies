package graph;

import java.util.ArrayList;

/**
 * Created by lex_b on 13/06/2017.
 */
public class Annotation {
    private String id;
    private int start;
    private int end;
    private ArrayList<String> info;


    public Annotation(String id, int start, int end, ArrayList<String> info) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.info = info;
    }
}