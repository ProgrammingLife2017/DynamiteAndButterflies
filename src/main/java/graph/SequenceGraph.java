package graph;

import org.mapdb.HTreeMap;

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
    private int startNodeIndex;
    private int endNodeIndex;
    private int[] parentArray;
    private int[] childArray;
    private HTreeMap<Long, String> sequenceHashMap;

    private int dummyNodeIDCounter = -1;



    private String partPath;

    /**
     * The constructor initializes the SequenceGraph with it's basic values.
     * @param parentArray - the parent array for edges.
     * @param childArray - the child array for edges.
     */
    public SequenceGraph(final int[] parentArray, final int[] childArray, HTreeMap<Long, String> sequenceHashMap) {
        this.sequenceHashMap = sequenceHashMap;
        this.parentArray = parentArray;
        this.childArray = childArray;
    }

    /**
     * size method for nodes.
     * @return the size of the HashMap
     */
    public int size() {
        return nodes.size();
    }

    public int totalSize() { return parentArray.length; }

    /**
     * Creates a subgraph.
     * @param centerNodeID - the node to start rendering at.
     * @param range - the amount of edges to add to the graph
     */
    public void createSubGraph(int centerNodeID, int range, String partPath) {
        this.partPath = partPath;
        this.nodes = new HashMap<Integer, SequenceNode>();
        this.columns = new ArrayList<ArrayList<SequenceNode>>();

        startNodeIndex = findCenterNodeIndex(centerNodeID, parentArray);
        endNodeIndex = findEndNodeIndex(centerNodeID, range);
        initNodes();
        findLongestPath();
        addDummies();
        this.columns = initColumns();
        assignSequenceLenghts();

    }

    private void assignSequenceLenghts() {
        Iterator it = nodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SequenceNode node = (SequenceNode) pair.getValue();
            if (!node.isDummy()) {
                node.setSequenceLength(sequenceHashMap.get((long) node.getId()).length());
            }
        }
    }

    private int findEndNodeIndex(int centerNodeID, int range) {
        endNodeIndex = range + centerNodeID;
        if (startNodeIndex + range >= parentArray.length) {
            endNodeIndex = parentArray[parentArray.length - 1];
        }
        return endNodeIndex;
    }

    /**
     * Add nodes with children to the nodes hashmap.
     */
    private void initNodes() {
        for (int i = startNodeIndex; i <= endNodeIndex; i++) {
            int parentID = parentArray[i];
            int childID = childArray[i];
            if (nodes.get(parentID) == null) {
                SequenceNode node = new SequenceNode(parentID);
                int[] genomes = new int[0];
                try {
                    genomes = getGenomes(parentID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                node.setGenomes(genomes);
                node.addChild(childID);
                nodes.put(parentID, node);
            } else {
                nodes.get(parentID).addChild(childID);
            }
            if (nodes.get(childID) == null) {
                SequenceNode node = new SequenceNode(childID);
                int[] genomes = new int[0];
                try {
                    genomes = getGenomes(childID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                node.setGenomes(genomes);
                nodes.put(childID, node);
            }
        }

    }

    /**
     * Finds the longest path of the graph and sets columns accordingly.
     */
    @SuppressWarnings("Since15")
    private int[] getGenomes(int node) throws IOException {
        try {
            Stream<String> lines = Files.lines(Paths.get(partPath + "genomes.txt"));
            String line = lines.skip(node - 1).findFirst().get();
            String[] text = line.split(";");
            int[] genomes = new int[text.length];
            for (int i = 0; i < text.length; i++) {
                genomes[i] = Integer.parseInt(text[i]);
            }
            return genomes;
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

    /**
     * Uses barycenter heuristics to approach edge crossing reduction.
     * @param columns - the columns with nodes, on which the algorithem is applied.
     */
    private void minimiseEdgeCrossings(ArrayList<ArrayList<SequenceNode>> columns) {
        for (int i = 1; i < columns.size(); i++) {
            ArrayList<SequenceNode> previousColumn = columns.get(i - 1);
            ArrayList<SequenceNode> currentColumn = columns.get(i);

            setBarycenterValues(previousColumn);
            sortColumns(currentColumn);

            for (int j = 0; j < currentColumn.size(); j++) {
                currentColumn.get(j).setIndex(j);
                this.nodes.get(currentColumn.get(j).getId()).setIndex(j);
            }
        }
    }

    /**
     * set amount of incoming edges for children and increase barycenter value by index of parent.
     * @param previousColumn - the column on which to base the barycenter values.
     */
    private void setBarycenterValues(ArrayList<SequenceNode> previousColumn) {
        for (SequenceNode node : previousColumn) {
            for (int child : node.getChildren()) {
                this.nodes.get(child).incrementInDegree();
                this.nodes.get(child).incrementBaryCenterValue(node.getIndex() + 1);
            }
        }
    }

    /**
     * Sorts the columns using barycenter points.
     * @param currentColumn - the column which to sort.
     */
    private void sortColumns(ArrayList<SequenceNode> currentColumn) {
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
    }

    /**
     * Finds the centerNode index (for in the hashmap).
     * @param centerNodeID - the node to lookup.
     * @param parentArray - the array in which to look.
     * @return - index of centerNode.
     */
    private int findCenterNodeIndex(int centerNodeID, int[] parentArray) {
        for (int i = 0; i < parentArray.length; i++) {
            if (parentArray[i] == centerNodeID) {
                return i;
            }
        }
        throw new IllegalArgumentException();
    }


    /**
     * Adds dummy's so that the span is always 1.
     *
     */
    private void addDummies() {
        for (int i = startNodeIndex; i <= endNodeIndex; i++) {
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

    /**
     * Creates a column list for easier crossing reduction.
     * @return - the column list with solved edge crossings.
     */
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

    /**
     * Extend the current graph with a given range. (range is amount of edges)
     * @param range - the amount of edges to add.
     * @return the extended graph
     */
    public SequenceGraph extendGraph(int range) {
        SequenceGraph graphExtension = new SequenceGraph(this.parentArray, this.childArray, this.sequenceHashMap);
        graphExtension.setDummyNodeIDCounter(this.getDummyNodeIDCounter());
        graphExtension.createSubGraph(this.getEndNodeIndex(), range, this.partPath);


        HashMap<Integer, SequenceNode> mapExtension = graphExtension.getNodes();
        mergeNodeMaps(this, mapExtension);

        int baseGraphHighestColumn = this.getColumns().size();

        ArrayList<ArrayList<SequenceNode>> columnExtension = graphExtension.getColumns();
        for (int i = 0; i < columnExtension.size(); i++) {
            for (int j = 0; j < columnExtension.get(i).size(); j++) {
                columnExtension.get(i).get(j).setColumn(baseGraphHighestColumn + i);
            }
        }

        ArrayList<ArrayList<SequenceNode>> newColumnList =
                new ArrayList<ArrayList<SequenceNode>>(columns);
        newColumnList.addAll(graphExtension.getColumns());
        this.setColumns(newColumnList);

        return this;
    }

    /**
     * Method to merge a node hashmmap into the current hashmap.
     * @param graph - the graph
     * @param mapExtension - the hashmap Extension.
     */
    private void mergeNodeMaps(SequenceGraph graph, HashMap<Integer, SequenceNode> mapExtension) {
        for (Object o : mapExtension.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            SequenceNode node = (SequenceNode) pair.getValue();
            graph.addNode(node);
        }
    }

    /**
     * Getter for dummyNodeIDCounter.
     * @return - the dummyNodeIDCounter.
     */
    private int getDummyNodeIDCounter() {
        return dummyNodeIDCounter;
    }

    /**
     * Setter for dummyNodeIDCounter.
     * @param dummyNodeIDCounter - value which to set.
     */
    private void setDummyNodeIDCounter(int dummyNodeIDCounter) {
        this.dummyNodeIDCounter = dummyNodeIDCounter;
    }

    /**
     * Setter for columns.
     * @param columns - the colums which to set.
     */
    private void setColumns(ArrayList<ArrayList<SequenceNode>> columns) {
        this.columns = columns;
    }

    /**
     * getter for EndNodeIndex.
     * @return - the endNodeIndex.
     */
    public int getEndNodeIndex() {
        return endNodeIndex;
    }

    public int getStartNodeIndex() {
        return startNodeIndex;
    }

    public String getPartPath() {
        return partPath;
    }

    public int getLeftBoundID() {
        return parentArray[0];
    }

    public int getRightBoundID() {
        return childArray[childArray.length - 1];
    }

    public SequenceGraph copy() {
        return new SequenceGraph(parentArray, childArray, sequenceHashMap);
    }

}
