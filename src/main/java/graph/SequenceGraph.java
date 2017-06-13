package graph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

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

    private String partPath;

    /**
     * The constructor initializes the SequenceGraph with it's basic values.
     */
    public SequenceGraph() {
        this.nodes = new HashMap<Integer, SequenceNode>();
    }

    // upperbound in incorrect for TB10, the last node is not the highest one.

    /**
     * Function to create a subgraph.
     * @param centerNodeID The Centre node.
     * @param range The range to of the subgraph.
     * @param parentArray Array with all the parentID's.
     * @param childArray Array with all the childID's, combined with parentArray is all edges.
     * @param partPath The path of the file for reading.
     * @throws IOException for file reading.
     */
    public void createSubGraph(int centerNodeID, int range, int[] parentArray, int[] childArray, String partPath) throws IOException {
        this.partPath = partPath;
        int centerNodeIndex = findCenterNodeIndex(centerNodeID, parentArray);
        int lastNodeIndex = range + centerNodeID;
        if (centerNodeIndex + range >= parentArray.length) {
            lastNodeIndex = parentArray.length - 1;
        }
        for (int i = centerNodeIndex; i <= lastNodeIndex; i++) {
            int parentID = parentArray[i];
            int childID = childArray[i];
            if (nodes.get(parentID) == null) {
                SequenceNode node = new SequenceNode(parentID);
                String[] text = getGenomes(parentID);
                int[] genomes = splitOnStringToInt(text[0]);
                int[] offSets = splitOnStringToInt(text[1]);
                node.setGenomes(genomes);
                node.setOffSets(offSets);
                node.addChild(childID);
                nodes.put(parentID, node);
            } else {
                nodes.get(parentID).addChild(childID);
            }
            if (nodes.get(childID) == null) {
                SequenceNode node = new SequenceNode(childID);
                String[] text = getGenomes(childID);
                int[] genomes = splitOnStringToInt(text[0]);
                int[] offSets = splitOnStringToInt(text[1]);
                node.setGenomes(genomes);
                node.setOffSets(offSets);
                nodes.put(childID, node);
            }
        }

        findLongestPath();
        addDummies(parentArray, centerNodeIndex, lastNodeIndex);
        this.columns = createColumnList();
        createIndex();
        baryCenterAssignment();
    }

    private int[] splitOnStringToInt(String text) {
        String[] genomesText = text.split(";");
        int[] genomes = new int[genomesText.length];
        for (int j = 0; j < genomesText.length; j++) {
            genomes[j] = Integer.parseInt(genomesText[j]);
        }
        return genomes;
    }

    @SuppressWarnings("Since15")
    private String[] getGenomes(int node) throws IOException {
        try {
            Stream<String> lines = Files.lines(Paths.get(partPath + "genomes.txt"));
            String line = lines.skip(node - 1).findFirst().get();
            String[] text = line.split("-");
            return text;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void findLongestPath() {
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

    private void baryCenterAssignment() {
        for (int i = 1; i < columns.size(); i++) {
            ArrayList<SequenceNode> previousColumn = columns.get(i - 1);
            ArrayList<SequenceNode> currentColumn = columns.get(i);

            // set amount of incoming edges for children and increase barycentervalue
            // by index of parent.
            for (SequenceNode node: previousColumn) {
                for (int child: node.getChildren()) {
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
    private void addDummies(int[] parentArray, int centerNodeIndex, int lastNodeIndex) {

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
        SequenceNode parentNode = this.getNode(parent);
        SequenceNode targetNode = this.getNode(target);

        if (span > 1) {
            SequenceNode dummy = new SequenceNode(dummyNodeIDCounter--);
            dummy.setDummy(true);
            parentNode.removeChild(target);
            parentNode.addChild(dummy.getId());
            dummy.addChild(target);
            dummy.setColumn(parentNode.getColumn() + 1);

            if (parentNode.getGenomes().length > targetNode.getGenomes().length) {
                dummy.setGenomes(parentNode.getGenomes());
            } else {
                dummy.setGenomes(targetNode.getGenomes());
            }
            
            this.addNode(dummy);
            --span;
            addDummyHelper(span, dummy.getId(), target);
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
    private void addNode(SequenceNode node) {
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
