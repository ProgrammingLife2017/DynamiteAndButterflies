package graph;
import com.rits.cloning.Cloner;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;

public class SequenceGraph {

    private final int ROOTNODEKEY = 1;

    private Integer size;
    private HashMap<Integer, AbstractNode> nodes;
    private boolean initialized;
    private ArrayList<Edge> edges;
    private LinkedList<Integer> list;

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
        this.initialized = true;
    }

    /** Creates a topological ordering of the graph and returns this ordering in the form of a LinkedList
     *
     * @return list - LinkedList<Integer>
     */
    public LinkedList<Integer> createTopologicalOrder() {
//        LinkedList<graph.SequenceNode> list = new Stack<graph.SequenceNode>();
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

    /** Creates a deep copy of the topological ordered list.
     *
     * @return list
     */
    public LinkedList<Integer> getDeepCopyTopologicalOrderedList() {
        Cloner cloner = new Cloner();
        return cloner.deepClone(this.list);
    }

    /** Assigns each node with a layer using the longest path algorithm.
     *
     */
    public void createLayers() {
        LinkedList<Integer> list = getDeepCopyTopologicalOrderedList();


        this.getNode(list.peek()).setLayer(1);

        while (!list.isEmpty()) {
            AbstractNode current = this.getNode(list.poll());
            for (int i : current.getChildren()) {
                this.getNode(i).incrementLayer(current.getLayer());
            }
        }

        for (int i = 1; i < this.nodes.size(); i++) {
            System.out.println("Node: " + this.getNode(i).getId() + " in layer: " + this.getNode(i).getLayer());

        }
    }

    /** Adds dummy nodes to the graph for visualisation purposes.
     *
     */
    public void addDummies() {
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
    public void addDummyHelper(int span, AbstractNode parent, AbstractNode target) {
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


}
