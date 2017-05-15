package graph;

import java.util.ArrayList;

/**
 * Created by Eric on 4-5-17.
 * This file specifies the edges in a graph.
 */
public class Edge {

    private final Integer parent;
    private final Integer child;
    private ArrayList<Integer> columnSpan;

    /**
     * The constructor of the class.
     * @param parent The parent of the edge.
     * @param child The child of the edge.
     */
    public Edge(Integer parent, Integer child) {
        this.child = child;
        this.parent = parent;
        this.columnSpan = new ArrayList<Integer>();
    }

    /**
     * This method returns the child of the edge.
     * @return Returns the child of the edge.
     */
    public Integer getChild() {
        return child;
    }

    /**
     * This method returns the parent of the edge.
     * @return Returns the parent of the edge.
     */
    public Integer getParent() {
        return parent;
    }

    /**
     * Can set all the columns an edge spans where there are no nodes for it.
     * @param newSpan The new ArrayList of integers specifying the columsn the edge spans
     */
    public void setColumnSpan(ArrayList<Integer> newSpan) {
        this.columnSpan = newSpan;
    }

    /**
     * Returns the columns the edge spans where there is no node it goes through.
     * @return An ArrayList of Integers specifying the nodeless comments.
     */
    public ArrayList<Integer> getColumnSpan() {
        return columnSpan;
    }

    /**
     * This adds a column to the column span of the edge.
     * @param newColumn The new column to be added
     */
    public void addColumn(int newColumn) {
        this.columnSpan.add(newColumn);
    }

    /**
     * Sets the column span from parent column to child column.
     * @param parColumn the column of your parent
     * @param childColumn the column of your child
     */
    public void setEntireColumnSpan(int parColumn, int childColumn) {
        for (int i = parColumn + 1; i < childColumn; i++) {
            addColumn(i);
        }
    }

    //TODO Add a setEntireColumnSpan method without parameters.
}
