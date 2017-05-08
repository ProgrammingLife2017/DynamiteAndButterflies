package parser;

import java.util.ArrayList;

/**
 * Created by eric on 4-5-17.
 */
public class Edge {

    private final Integer parent;
    private final Integer child;
    private boolean original;
    private ArrayList<Integer> columnSpan;

    public Edge(Integer parent, Integer child) {
        this.child = child;
        this.parent = parent;
        this.original = true;
        this.columnSpan = new ArrayList<Integer>();
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

    public void setColumnSpan(ArrayList<Integer> newSpan) {
        this.columnSpan = newSpan;
    }

    public ArrayList<Integer> getColumnSpan() {
        return columnSpan;
    }

    public void addColumn(int newColumn) {
        this.columnSpan.add(newColumn);
    }

    public void setEntireColumnSpan(int parColumn, int childColumn) {
        for(int i = parColumn + 1; i < childColumn; i++) {
            addColumn(i);
        }
    }
}
