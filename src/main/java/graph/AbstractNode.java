package graph;

import java.util.ArrayList;

public abstract class AbstractNode {

    abstract ArrayList<Integer> getChildren();
    abstract int getLayer();

    abstract void setLayer(int layer);

    abstract void addChild(int child);

    abstract int getId();

    abstract void incrementLayer(int Parentlayer);
    abstract void setxCoordinate(int x);
    abstract void setyCoordinate(int y);

    abstract void addParent(int id);

    abstract ArrayList<Integer> getParents();

    abstract void setParents(ArrayList<Integer> parents);

    abstract int getIndex();

    abstract void setIndex(int index);

    abstract float getBaryCenterValue();

    abstract void setBaryCenterValue(float value);

    abstract void updateBaryCenterValue(int value);
}
