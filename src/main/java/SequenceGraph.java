import java.util.ArrayList;
import java.util.HashMap;

public class SequenceGraph   {


    private Integer size;
    private HashMap<Integer, SequenceNode> nodes;
    private boolean initialized;
    private ArrayList<Edge> edges;

    public SequenceGraph() {
        this.size = 0;
        this.nodes = new HashMap<Integer, SequenceNode>();
        this.initialized = false;
        this.edges = new ArrayList<Edge>();

    }

    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    public void addNode(SequenceNode node) {
        this.nodes.put(node.getId(), node);
        this.size++;
    }

    public SequenceNode getNode(Integer id) {
        return nodes.get(id);
    }

    public HashMap<Integer, SequenceNode> getNodes() {
        return this.nodes;
    }

    public void initialize() {

        for (Edge edge : getEdges()) {
            // aan elke parent de child toevoegen
            this.getNode(edge.getParent()).addChild(this.getNode(edge.getChild()));
        }
    }

    public void layerizeGraph() {
//        Start at first node
//        Get all children
//        Assign layer
    }

}
