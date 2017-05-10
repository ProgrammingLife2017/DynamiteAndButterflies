package graph;

import java.util.ArrayList;

/**
 * Created by eric on 10-5-17.
 */
public abstract class AbstractNode {

    abstract ArrayList<Integer> getChildren();
    abstract int getLayer();
    abstract void addChild(int child);
    abstract int getId();
    abstract void setLayer(int layer);
    abstract void incrementLayer(int Parentlayer);
}
