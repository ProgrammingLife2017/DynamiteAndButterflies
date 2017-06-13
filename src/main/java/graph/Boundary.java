package graph;

public class Boundary {
    private int centerNodeID;
    private int range;

    private int leftBoundIndex;
    private int leftBoundID;
    private int rightBoundIndex;
    private int rightBoundID;

    public Boundary(int centerNodeID, int range, int[] parentArray, int[] childArray) {
        this.centerNodeID = centerNodeID;
        this.range = range;
        setBoundaries(parentArray, childArray);
    }

    private void setBoundaries(int[] parentArray, int[] childArray) {
        leftBoundIndex = 0;
        leftBoundID = parentArray[leftBoundIndex];

        if(centerNodeID - range > 0) {
            leftBoundID = centerNodeID - range;
            leftBoundIndex = findLeftBoundIndex(leftBoundID, parentArray);
        }

        rightBoundIndex = childArray.length-1;
        rightBoundID = childArray[childArray.length-1];

        if(centerNodeID + range < childArray[childArray.length-1]) {
            rightBoundID = centerNodeID + range;
            rightBoundIndex = findRightBoundIndex(rightBoundID, parentArray);
        }
    }

    private int findRightBoundIndex(int rightBoundID, int[] parentArray) {
        for (int i = 0; i < parentArray.length; i++) {
            if (parentArray[i] == rightBoundID) {
                if(parentArray[i+1] != rightBoundID)
                    return i;
            }
        }
        throw new IllegalArgumentException();
    }


    /**
     * Finds the centerNode index (for in the hashmap).
     * @param leftBoundID - the node to lookup.
     * @param parentArray - the array in which to look.
     * @return - index of centerNode.
     */
    private int findLeftBoundIndex(int leftBoundID, int[] parentArray) {
        for (int i = 0; i < parentArray.length; i++) {
            if (parentArray[i] == leftBoundID) {
                return i;
            }
        }
        throw new IllegalArgumentException();
    }


    public int getRightBoundIndex() {
        return rightBoundIndex;
    }

    public int getLeftBoundIndex() {
        return leftBoundIndex;
    }

    public int getLeftBoundID() {
        return leftBoundID;
    }

    public int getRightBoundID() {
        return rightBoundID;
    }
}
