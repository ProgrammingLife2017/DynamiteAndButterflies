package graph;

import sun.awt.image.ImageWatched;

import java.lang.reflect.Array;
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

    private int dummyNodeIDCounter;

    /**
     * The constructor initializes the SequenceGraph with it's basic values.
     */
    public SequenceGraph() {
        this.nodes = new HashMap<Integer, SequenceNode>();
    }

    public int size() {
        return nodes.size();
    }

    // upperbound in incorrect for TB10, the last node is not the highest one.
    public void createSubGraph(int centerNodeID, int range, int[] parentArray, int[] childArray) {
        int centerNodeIndex = findCenterNodeIndex(centerNodeID, parentArray);
        int lastNodeIndex = range + centerNodeID;
        if (centerNodeIndex + range >= parentArray.length) {
            lastNodeIndex = parentArray.length-1;
        }


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
                nodes.put(childID, node);
            }
        }

        // order: longest path, column, dummy's,
        // layerizeGraph(lowerBoundID);

        this.getNode(centerNodeID).setColumn(0);
        Queue<Integer> queue = createQueue();
        findLongestPath(queue);
        //initColumns2();


        //initColumns(centerNodeIndex, lastNodeIndex, parentArray);
        addDummies(parentArray, childArray, centerNodeIndex, lastNodeIndex);
        this.columns = createColumnList();
        createIndex();
        baryCenterAssignment();
    }

    private void findLongestPath(Queue<Integer> queue) {
        while (!queue.isEmpty()) {
            SequenceNode currentNode = this.getNode(queue.poll());
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

    private void baryCenterAssignment() {
        for(int i = 1; i < columns.size(); i++) {
            ArrayList<SequenceNode> previousColumn = columns.get(i - 1);
            ArrayList<SequenceNode> currentColumn = columns.get(i);

            // set amount of incoming edges for children and increase barycentervalue by index of parent
            for (SequenceNode node: previousColumn) {
                for(int child: node.getChildren()) {
                    this.nodes.get(child).incrementInDegree();
                    this.nodes.get(child).incrementBaryCenterValue(node.getIndex()+1);
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

            for(int j = 0; j < currentColumn.size(); j++) {
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

    //TODO: weird node is null? and dummy nodes at 0 column?
    // remove the null check.
    private void initColumns(int centerNodeIndex, int lastNodeIndex, int[] parentArray) {
        for (int i = centerNodeIndex; i <= lastNodeIndex; i++) {
            int parentColumn = this.getNode(parentArray[i]).getColumn();
            for (int id : this.getNode(parentArray[i]).getChildren()) {
                if (this.getNode(id) != null && this.getNode(id).getColumn() == 0) {
                    this.getNode(id).setColumn(parentColumn + 1);
                }
            }
        }
    }
    private Queue<Integer> createQueue() {
        Queue<Integer> queue = new PriorityQueue<Integer>();
        Iterator it = this.getNodes().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SequenceNode node = (SequenceNode) pair.getValue();
            int nodeID = (Integer) pair.getKey();
            queue.add(nodeID);
        }
        return queue;
    }


    /**
     * Adds dummy nodes to the graph for visualisation purposes.
     */
    private void addDummies(int[] parentArray,int[] childArray,int centerNodeIndex,int lastNodeIndex) {
//        Iterator it = this.getNodes().entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry) it.next();
//            SequenceNode node = (SequenceNode) pair.getValue();
//            int nodeID = (Integer) pair.getKey();
//            for(int child: node.getChildren()) {
//                int span = this.getNode(child).getColumn() - node.getColumn();
//                for(int i = 1; i < span; i++) {
//                    SequenceNode dummy = new SequenceNode(dummyNodeIDCounter--);
//                }
//            }
//        }
        dummyNodeIDCounter = -1;
        for (int i = centerNodeIndex; i <= lastNodeIndex; i++) {
            SequenceNode parent = this.getNode(parentArray[i]);
            int size = parent.getChildren().size();
            for (int j = 0; j < size; j++) {
                int childId = parent.getChildren().get(j);
                int span = getNode(childId).getColumn() - parent.getColumn();
                if (span > 1) {
                    addDummyHelper(span, parent, getNode(childId));
                }
            }
        }
    }

    /**
     * Helper function for addDummy()
     *
     * @param span   - the difference in layer level of parent and child
     * @param parent - the parent node
     * @param target - the target node
     */
    private void addDummyHelper(int span, SequenceNode parent, SequenceNode target) {
        if (span > 1) {
            SequenceNode dummy = new SequenceNode(dummyNodeIDCounter--);
            dummy.setDummy(true);
            int size = parent.getChildren().size();
            for (int i = 0; i < size; i++) {
                if (this.getNode(parent.getId()).getChildren().get(i) == target.getId()) {
                    this.getNode(parent.getId()).getChildren().remove(i);
                    this.getNode(parent.getId()).addChild(dummy.getId());
                    break;
                }
            }
            dummy.addParent(parent.getId());
            dummy.setColumn(parent.getColumn() + 1);
            this.addNode(dummy);
            --span;
            addDummyHelper(span, dummy, target);
        }
    }

    /**
     * Getter for the column list.
     * @return the column arraylist with an arraylist with nodes.
     */
    public ArrayList<ArrayList<SequenceNode>> getColumns() {
        return this.columns;
    }


    /**
     * Add a node to the ArrayList of Nodes.
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


    private ArrayList<ArrayList<SequenceNode>> createColumnList() {
        ArrayList<ArrayList<SequenceNode>> columns = new ArrayList<ArrayList<SequenceNode>>();

        for (Object o : nodes.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            SequenceNode node = (SequenceNode) pair.getValue();
            while (columns.size() <= node.getColumn()) {
                columns.add(new ArrayList<SequenceNode>());
            }
            columns.get(node.getColumn()).add(node);
        }
        return columns;
    }

    /**
     * assigns indices to all nodes in the column list.
     */
    private void createIndex() {
        for (ArrayList<SequenceNode> column : columns) {
            for (int j = 0; j < column.size(); j++) {
                column.get(j).setIndex(j);
            }
        }

    }

}
