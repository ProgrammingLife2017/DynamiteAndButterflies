package graph;

import java.util.*;

/**
 * Our own Graph Class.
 * This class is the SequenceGraph.
 * A Graph handling a Directed-Acyclic-Graph.
 * This is our own data structure we will use to draw the eventual graph.
 */
public class SequenceGraph {


    private HashMap<Integer, SequenceNode> nodes;
    private ArrayList<ArrayList<SequenceNode>> columns;
    private int startNodeIndex;
    private int endNodeIndex;
    private int[] parentArray;
    private int[] childArray;

    private int dummyNodeIDCounter;

    /**
     * The constructor initializes the SequenceGraph with it's basic values.
     */
    public SequenceGraph(int[] parentArray, int[] childArray) {
        this.parentArray = parentArray;
        this.childArray = childArray;

    }

    public int size() {
        return nodes.size();
    }

    // upperbound in incorrect for TB10, the last node is not the highest one.
    public void createSubGraph(int centerNodeID, int range) {
        this.nodes = new HashMap<Integer, SequenceNode>();
        this.columns = new ArrayList<ArrayList<SequenceNode>>();

        startNodeIndex = findCenterNodeIndex(centerNodeID, parentArray);
        endNodeIndex = range + centerNodeID;
        if (startNodeIndex + range >= parentArray.length) {
            endNodeIndex = parentArray.length - 1;
        }
        initNodes(startNodeIndex, endNodeIndex);
        findLongestPath(centerNodeID);
        addDummies(startNodeIndex, endNodeIndex);
        this.columns = initColumns();
    }

    private void initNodes(int centerNodeIndex, int lastNodeIndex) {
        for (int i = centerNodeIndex; i <= lastNodeIndex; i++) {
            int parentID = parentArray[i];
            int childID = childArray[i];
            if (nodes.get(parentID) == null) {
                SequenceNode node = new SequenceNode(parentID);
                node.addChild(childID);
                nodes.put(parentID, node);
            } else {
                nodes.get(parentID).addChild(childID);
            }
            if (nodes.get(childID) == null) {
                SequenceNode node = new SequenceNode(childID);
                node.addParent(parentID);
                nodes.put(childID, node);
            }
        }

    }

    private void findLongestPath(int centerNodeID) {
        this.getNode(centerNodeID).setColumn(0);
        for (Object o : this.getNodes().entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            SequenceNode currentNode = (SequenceNode) pair.getValue();
            if (currentNode.getColumn() != Integer.MIN_VALUE) {
                for (int child : currentNode.getChildren()) {
                    if (this.getNode(child).getColumn() < currentNode.getColumn() + 1) {
                        this.getNode(child).addParent(currentNode.getId());
                        this.getNode(child).setColumn(currentNode.getColumn() + 1);
                    }
                }
            }
        }
    }

    private void minimiseEdgeCrossings(ArrayList<ArrayList<SequenceNode>> columns) {
        for (int i = 1; i < columns.size(); i++) {
            ArrayList<SequenceNode> previousColumn = columns.get(i - 1);
            ArrayList<SequenceNode> currentColumn = columns.get(i);

            // set amount of incoming edges for children and increase barycentervalue by index of parent
            for (SequenceNode node : previousColumn) {
                for (int child : node.getChildren()) {
                    this.nodes.get(child).incrementInDegree();
                    this.nodes.get(child).incrementBaryCenterValue(node.getIndex() + 1);
                }
            }

            // sort childlayer based on barycenter values.
            Collections.sort(currentColumn, new Comparator<SequenceNode>() {
                public int compare(SequenceNode o1, SequenceNode o2) {
                    float baryVal1 = nodes.get(o1.getId()).getBaryCenterValue();
                    float baryVal2 = nodes.get(o2.getId()).getBaryCenterValue();
                    if (baryVal1 > baryVal2) {
                        return 1;
                    } else if (baryVal1 < baryVal2) {
                        return -1;
                    } else if (baryVal1 == baryVal2) {
                        if (nodes.get(o1.getId()).isDummy()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                    return 0;
                }
            });

            for (int j = 0; j < currentColumn.size(); j++) {
                currentColumn.get(j).setIndex(j);
                this.nodes.get(currentColumn.get(j).getId()).setIndex(j);
            }
        }
    }

    private int findCenterNodeIndex(int centerNodeID, int[] parentArray) {
        for (int i = 0; i < parentArray.length; i++) {
            if (parentArray[i] == centerNodeID) {
                return i;
            }
        }
        throw new IllegalArgumentException();

    }


    /**
     * Adds dummy nodes to the graph for visualisation purposes.
     */
    private void addDummies(int centerNodeIndex, int lastNodeIndex) {
        dummyNodeIDCounter = -1;
        for (int i = centerNodeIndex; i <= lastNodeIndex; i++) {
            SequenceNode parent = this.getNode(parentArray[i]);
            int size = parent.getChildren().size();
            for (int j = 0; j < size; j++) {
                int childId = parent.getChildren().get(j);
                int span = getNode(childId).getColumn() - parent.getColumn();
                if (span > 1) {
                    //parent.removeChild(childId);
                    addDummyHelper(span, parent.getId(), childId);
                }
            }
        }
    }

    /**
     * Helper function for addDummy().
     *
     * @param span   - the difference in layer level of parent and child
     * @param parent - the parent node
     * @param target - the target node
     */
    private void addDummyHelper(int span, int parent, int target) {
        if (span > 1) {
            SequenceNode dummy = new SequenceNode(dummyNodeIDCounter--);
            dummy.setDummy(true);
            this.getNode(parent).removeChild(target);
            this.getNode(parent).addChild(dummy.getId());
            dummy.addChild(target);
            dummy.setColumn(this.getNode(parent).getColumn() + 1);
            this.addNode(dummy);
            --span;
            addDummyHelper(span, dummy.getId(), target);
        }
    }

    /**
     * Getter for the column list.
     *
     * @return the column arraylist with an arraylist with nodes.
     */
    public ArrayList<ArrayList<SequenceNode>> getColumns() {
        return this.columns;
    }


    /**
     * Add a node to the ArrayList of Nodes.
     *
     * @param node The node to be added.
     */
    public void addNode(SequenceNode node) {
        this.nodes.put(node.getId(), node);
    }

    /**
     * Get a specific Node.
     *
     * @param id The Id of the Node to get.
     * @return The Node with the given Id.
     */
    public SequenceNode getNode(Integer id) {
        return nodes.get(id);
    }

    /**
     * Returns all nodes contained in the graph.
     *
     * @return A HashMap of all nodes and their IDs contained in the graph.
     */
    public HashMap<Integer, SequenceNode> getNodes() {
        return this.nodes;
    }

    public void setNodes(HashMap<Integer, SequenceNode> hash) {
        this.nodes = hash;
    }


    private ArrayList<ArrayList<SequenceNode>> initColumns() {
        ArrayList<ArrayList<SequenceNode>> columns = new ArrayList<ArrayList<SequenceNode>>();

        for (Object o : nodes.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            SequenceNode node = (SequenceNode) pair.getValue();
            while (columns.size() <= node.getColumn()) {
                columns.add(new ArrayList<SequenceNode>());
            }
            columns.get(node.getColumn()).add(node);
        }
        createIndex(columns);
        minimiseEdgeCrossings(columns);
        return columns;
    }

    /**
     * assigns indices to all nodes in the column list.
     */
    private void createIndex(ArrayList<ArrayList<SequenceNode>> columns) {
        for (ArrayList<SequenceNode> column : columns) {
            for (int j = 0; j < column.size(); j++) {
                column.get(j).setIndex(j);
            }
        }

    }


    public SequenceGraph extendGraph(int range) {
        int currentDummyNodeIndexCounter = this.getDummyNodeIDCounter();
        int endNodeIndex = this.getEndNodeIndex();

        SequenceGraph graphExtension = new SequenceGraph(this.parentArray, this.childArray);
        graphExtension.setDummyNodeIDCounter(currentDummyNodeIndexCounter);
        graphExtension.createSubGraph(endNodeIndex, range);


        HashMap<Integer, SequenceNode> mapExtension = graphExtension.getNodes();
        mergeNodeMaps(this, mapExtension);

        int baseGraphHighestColumn = this.getColumns().size();

        ArrayList<ArrayList<SequenceNode>> columnExtension = graphExtension.getColumns();
        for (int i = 0; i < columnExtension.size(); i++) {
            for (int j = 0; j < columnExtension.get(i).size(); j++) {
                columnExtension.get(i).get(j).setColumn(baseGraphHighestColumn + i);
            }
        }

        ArrayList<ArrayList<SequenceNode>> newColumnList = new ArrayList<ArrayList<SequenceNode>>(columns);
        newColumnList.addAll(graphExtension.getColumns());
        this.setColumns(newColumnList);

        return this;
    }

    private void mergeNodeMaps(SequenceGraph graph, HashMap<Integer, SequenceNode> mapExtension) {
        Iterator it = mapExtension.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SequenceNode node = (SequenceNode) pair.getValue();
            graph.addNode(node);
        }
    }

    public int getDummyNodeIDCounter() {
        return dummyNodeIDCounter;
    }

    public void setDummyNodeIDCounter(int dummyNodeIDCounter) {
        this.dummyNodeIDCounter = dummyNodeIDCounter;
    }

    public void setColumns(ArrayList<ArrayList<SequenceNode>> columns) {
        this.columns = columns;
    }

    public int[] getParentArray() {
        return parentArray;
    }

    public int[] getChildArray() {
        return childArray;
    }

    public int getStartNodeIndex() {
        return startNodeIndex;
    }

    public int getEndNodeIndex() {
        return endNodeIndex;
    }

}
