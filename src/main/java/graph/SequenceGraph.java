package graph;
import com.rits.cloning.Cloner;

import java.util.*;

public class SequenceGraph {

    private final int ROOTNODEKEY = 1;
    private HashMap<Integer, List<Integer>> layoutHashMap = new HashMap<Integer, List<Integer>>();
    private Integer size;
    private HashMap<Integer, AbstractNode> nodes;
    private boolean initialized;
    private ArrayList<Edge> edges;
    private LinkedList<Integer> list;
    private int maxDepth = Integer.MIN_VALUE;

    public SequenceGraph() {
        this.size = 0;
        this.nodes = new HashMap<Integer, AbstractNode>();
        this.initialized = false;
        this.edges = new ArrayList<Edge>();
    }

    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    /** Adds a node to the Hashmap and increments the size of the Graph
     *
     * @param node - the node to be added
     */
    public void addNode(AbstractNode node) {
        this.nodes.put(node.getId(), node);
        this.size++;
    }


    public AbstractNode getNode(Integer id) {
        return nodes.get(id);
    }

    public HashMap<Integer, AbstractNode> getNodes() {
        return nodes;
    }


    /** Initializer function that calls the setup functions for the graph
     *
     */
    public void initialize() {

        for (Edge edge : getEdges()) {
            // aan elke parent de child toevoegen
            this.getNode(edge.getParent()).addChild(edge.getChild());
        }
        this.list = this.createTopologicalOrder();
        this.createLayers();
        this.addDummies();
        this.initializeParents();
        this.createLayoutHashMap();
        this.assignIndex();
        this.baryCenterAssignment();
        this.initialized = true;
    }

    /** Creates a topological ordering of the graph and returns this ordering in the form of a LinkedList
     *
     * @return list - LinkedList<Integer>
     */
    private LinkedList<Integer> createTopologicalOrder() {
        LinkedList<Integer> queue = new LinkedList<Integer>();
        LinkedList<Integer> list = new LinkedList<Integer>();
        AbstractNode root = this.getNode(ROOTNODEKEY);
        boolean[] visited = new boolean[this.size+1];
        queue.add(ROOTNODEKEY);
        visited[ROOTNODEKEY] = true;
        while (queue.size() != 0) {
            AbstractNode current = this.getNode(queue.poll());
            list.add(current.getId());
            for (int i: current.getChildren()) {
                if (!visited[i])
                {
                    visited[i] = true;
                    queue.add(i);
                }
            }
        }
//        createTopologicalOrderHelper(list, root, visited);
        return list;
    }

    /**
     * Creates a deep copy of the topological ordered list.
     * @return list
     */
    private LinkedList<Integer> getDeepCopyTopologicalOrderedList() {
        Cloner cloner = new Cloner();
        return cloner.deepClone(this.list);
    }

    /**
     * Assigns each node with a layer using the longest path algorithm.
     */
    private void createLayers() {
        LinkedList<Integer> list = getDeepCopyTopologicalOrderedList();

        AbstractNode firstNode = this.getNode(list.peek());
        firstNode.setLayer(1);
        firstNode.setIndex(1);

        while (!list.isEmpty()) {
            AbstractNode current = this.getNode(list.poll());
            for (int i : current.getChildren()) {
                this.getNode(i).incrementLayer(current.getLayer());
                if (this.getNode(i).getLayer() > maxDepth)
                    maxDepth = this.getNode(i).getLayer();
            }
        }
    }


    /**
     * Creates a hashmap with key = int layer and value = List<Integer> ids
     */
    private void createLayoutHashMap() {
        for (int i = 1; i < this.nodes.size(); i++) {
            int currentNodeId = this.getNode(i).getId();
            int currentNodeLayer = this.getNode(i).getLayer();
            addToList(currentNodeLayer, currentNodeId);
        }
    }

    /**
     * Adds dummy nodes to the graph for visualisation purposes.
     */
    private void addDummies() {
        LinkedList<Integer> list = getDeepCopyTopologicalOrderedList();
        while (!list.isEmpty()){
            AbstractNode parent = this.getNode(list.poll());
            int size = parent.getChildren().size();
            for (int i = 0; i < size; i++) {
                int childId = parent.getChildren().get(i);
                int span = getNode(childId).getLayer() - parent.getLayer();
                if (span > 1) {
                    addDummyHelper(span, parent, getNode(childId));
                }
            }
        }
    }


    /** Helper function for addDummy()
     *
     * @param span - the difference in layer level of parent and child
     * @param parent - the parent node
     * @param target - the target node
     */
    private void addDummyHelper(int span, AbstractNode parent, AbstractNode target) {
        if (span > 1) {
            DummyNode dummy = new DummyNode(this.size+1, parent.getLayer() + 1);
            int size = parent.getChildren().size();
            for (int i = 0; i < size; i++) {
                if (parent.getChildren().get(i) == target.getId()) {
                    parent.getChildren().remove(i);
                    parent.addChild(dummy.getId());
                    break;
                }
            }
            dummy.addChild(target.getId());
            this.addNode(dummy);
            span --;
            addDummyHelper(span, dummy, target);
        }
    }

    /**
     * Assigns all nodes with an index.
     */
    private void assignIndex() {
        for (int i = 1; i <= maxDepth; i++) {
            List<Integer> layer = layoutHashMap.get(i);
            for (int j = 0; j < layer.size(); j++) {
                int currentNodeID = layer.get(j);
                this.getNode(currentNodeID).setIndex(j + 1);
            }
        }
    }

    /**
     * Sorts the layers in the layoutHashMap using barycenter heuristics.
     * Which reduces the number of crossing edges.
     */
    private void baryCenterAssignment() {
        // loop over layers
        for (int i = 1; i < maxDepth; i++) {
            List<Integer> parentLayer = layoutHashMap.get(i);
            List<Integer> childLayer = layoutHashMap.get(i + 1);

            // loop over childlayer
            for (int id : childLayer) {
                AbstractNode currentNode = this.getNode(id);
                currentNode.setBaryCenterValue(getBaryCenterValue(currentNode));
            }

            // sort childlayer based on barycenter values.
            Collections.sort(childLayer, new Comparator<Integer>() {
                public int compare(Integer o1, Integer o2) {
                    float baryVal1 = nodes.get(o1).getBaryCenterValue();
                    float baryVal2 = nodes.get(o2).getBaryCenterValue();
                    if (baryVal1 < baryVal2) {
                        return 1;
                    } else if (baryVal1 > baryVal2) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });

            // reassign index
            for (int j = 0; j < childLayer.size(); j++) {
                AbstractNode currentNode = this.getNode(childLayer.get(j));
                currentNode.setIndex(j + 1);
            }

            layoutHashMap.put(i + 1, childLayer);
        }
    }

    /**
     * Calculates the barycentric value for a node.
     *
     * @param node - the node to calculate the value for.
     * @return the barycenter value
     */
    private float getBaryCenterValue(AbstractNode node) {
        for (int id : node.getParents()) {
            node.updateBaryCenterValue(this.getNode(id).getIndex());
        }
        return (node.getBaryCenterValue() / node.getParents().size());
    }

    /**
     * Creates parents for each node.
     */
    private void initializeParents() {
        for (AbstractNode node : this.nodes.values()) {
            for (int id : node.getChildren()) {
                this.getNode(id).addParent(node.getId());
            }
        }
    }

    /**
     * Adder for Hashmap<Integer, List<Integer>)
     *
     * @param mapKey - the key in which to add a nodeID
     * @param nodeID - the nodeID to be added
     */
    private synchronized void addToList(Integer mapKey, Integer nodeID) {
        List<Integer> idList = layoutHashMap.get(mapKey);

        // if list does not exist create it
        if (idList == null) {
            idList = new ArrayList<Integer>();
            idList.add(nodeID);
            layoutHashMap.put(mapKey, idList);
        } else {
            // add if item is not already in list
            if (!idList.contains(nodeID)) idList.add(nodeID);
        }
    }

}
