package graph;

/**
 * Class to determine the boundary of a sub-graph.
 */
public class Boundary {
    private int centerNodeID;
    private int range;

    private int leftBoundIndex;
    private int leftBoundID;
    private int rightBoundIndex;
    private int rightBoundID;

    /**
     * Boundary constructor, calls setBoundary.
     * @param centerNodeID - the center-node.
     * @param range - the range in amount of nodes as range.
     * @param parentArray - the parent edges.
     * @param childArray - the child edges.
     */
    Boundary(int centerNodeID, int range, int[] parentArray, int[] childArray) {
        this.centerNodeID = centerNodeID;
        this.range = range;
        setBoundaries(parentArray, childArray);
    }


    /**
     * sets the boundary's.
     * @param parentArray - the parent edges.
     * @param childArray - the child edges.
     */
    private void setBoundaries(int[] parentArray, int[] childArray) {
        leftBoundIndex = 0;
        leftBoundID = parentArray[leftBoundIndex];

        if (centerNodeID - range > 0) {
            leftBoundID = centerNodeID - range;
            leftBoundIndex = findLeftBoundIndex(leftBoundID, parentArray);
        }

        rightBoundIndex = childArray.length - 1;
        rightBoundID = childArray[childArray.length - 1];

        if (centerNodeID + range < childArray[childArray.length - 1]) {
            rightBoundID = centerNodeID + range;
            rightBoundIndex = findRightBoundIndex(rightBoundID, parentArray);
        }
    }

    /**
     * Method to determine the right bound.
     * @param rightBoundID - the node of which we want the parentArray index.
     * @param parentArray - the array in which to lookup the rightBound node.
     * @return returns the index of the rightBoundNode.
     */
    private int findRightBoundIndex(int rightBoundID, int[] parentArray) {
        for (int i = parentArray.length-1; i >= 0; i--) {
            if (parentArray[i] == rightBoundID) {
                if (parentArray[i + 1] != rightBoundID) {
                    return i;
                }
            }
        }
        throw new IllegalArgumentException();
    }


    /**
     * Method to determine the right bound.
     * @param leftBoundID - the node of which we want the parentArray index.
     * @param parentArray - the array in which to lookup the rightBound node.
     * @return returns the index of the rightBoundNode.
     */
    private int findLeftBoundIndex(int leftBoundID, int[] parentArray) {
        for (int i = 0; i < parentArray.length; i++) {
            if (parentArray[i] == leftBoundID) {
                return i;
            }
        }
        throw new IllegalArgumentException();
    }


    /**
     * Getter for right bound index.
     * @return the right bound index.
     */
    int getRightBoundIndex() {
        return rightBoundIndex;
    }

    /**
     * Getter for right bound index.
     * @return the right bound index.
     */
    int getLeftBoundIndex() {
        return leftBoundIndex;
    }

    /**
     * Getter for leftBoundID.
     * @return the leftBoundID..
     */
    int getLeftBoundID() {
        return leftBoundID;
    }

    /**
     * Getter for rightBoundID.
     * @return the rightBoundID..
     */
    int getRightBoundID() {
        return rightBoundID;
    }

    public int getRange() {
        return range;
    }
}
