
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GraphMaker extends JFrame
{

    /**
     *  Created by Jip 26-4-2017
     */
    public static void main(String[] args)
    {
        //GraphMaker frame = new GraphMaker("/test (1).gfa");
        GraphMaker frame = new GraphMaker("/TB10.gfa");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 320);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static final long serialVersionUID = -2707712944901661771L;

    public GraphMaker(String filename)
    {
        super("Programming Life");

        mxGraph graph = new mxGraph();
        InputStream in = GfaParser.class.getClass().getResourceAsStream(filename);
        GfaParser parser = new GfaParser();
        parser.parse(in);

        ArrayList<Node2> set = parser.getList();

        HashMap<Integer, Node2> allNodes = hashNode2s(set);
        assignColumns(set, allNodes);
        Node2.setAllParents(set, allNodes);
        for(int i = 0; i < set.size(); i++) {
            Node2 node = set.get(i);
            addMutations(node, allNodes);
        }

        buildGraph(graph, set);

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);
    }

    private void buildGraph(mxGraph graph, ArrayList<Node2> set) {
        HashMap<Integer, Object> hash = makeNodes(graph, set);
        createEdges(graph, hash, set);
    }

    public void createEdges(mxGraph graph, HashMap<Integer, Object> hash, ArrayList<Node2> set) {
        graph.getModel().beginUpdate();
        for (Node2 n : set) {
            Object parOfEdge = graph.getDefaultParent();
            Object par = hash.get(n.getId());
            ArrayList<Integer> children = n.getChild();

            for (Integer aChild : children) {
                Object child = hash.get(aChild);
                try {
                    graph.insertEdge(parOfEdge, null, null, par, child);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Something went wrong adding an edge");
                }
            }
        }
        graph.getModel().endUpdate();
    }

    public HashMap<Integer, Object> makeNodes(mxGraph graph, ArrayList<Node2> set) {
        graph.getModel().beginUpdate();

        Object parent = graph.getDefaultParent();
        HashMap<Integer, Object> hash = new HashMap<Integer, Object>();
        HashMap<Integer, Integer> columns = new HashMap<Integer, Integer>();

        for (Node2 node : set) {
            int columnHandler = createColumnHandler(node, columns);
            int xCo = createXCo(node);
            int yCo = createYCo(node, columnHandler);

            try {
                //Object obj = graph.insertVertex(parent, "" + node.getId(), node.getSeq(), xCo, yCo, 80, 30);
                Object obj = graph.insertVertex(parent, "" + node.getId(), "", xCo, yCo, 80, 30);
                hash.put(node.getId(), obj);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Ging iets mis met een vertex adden in de visualisation.");
            }
        }
        graph.getModel().endUpdate();
        return hash;
    }

    private int createYCo(Node2 node, int columnHandler) {
        return 200 + (50 * node.getMutationLevel()) + (columnHandler * 25);
    }

    private int createXCo(Node2 node) {
        return 100 + 20 * (node.getColumnID() * 5);
    }

    private int createColumnHandler(Node2 node, HashMap<Integer, Integer> columns) {
        int res;
        int columnId = node.getColumnID();

        if (columns.get(columnId) == null) {    //initialize hashMap if necessary
            columns.put(columnId, 0);
        }

        columns.put(columnId, columns.get(columnId) + 1);   //Update number of nodes in a column

        if((columns.get(columnId) % 2) == 1) {      // Space these nodes correctly from each other
            res = columns.get(columnId);
        } else {
            res = -1 * (columns.get(columnId) - 1);
        }

        return res;
    }

    public HashMap<Integer, Node2> hashNode2s (ArrayList<Node2> set) {
        HashMap<Integer, Node2> allNodes = new HashMap<Integer, Node2>();
        for (Node2 node : set) {
            allNodes.put(node.getId(), node);
        }
        return allNodes;
    }

    public void assignColumns (ArrayList<Node2> nodeList, HashMap<Integer, Node2> allNodes) {
        for(int i = 0; i < nodeList.size(); i++) {
            Node2 parent = nodeList.get(i);
            ArrayList<Node2> children = parent.getChildrenNodes(allNodes);
            for(int j = 0; j < children.size(); j++) {
                Node2 child = children.get(j);
                child.incrementColumn(parent.getColumnID());
            }
        }
    }

    public boolean addMutations (Node2 node, HashMap<Integer, Node2> allNodes) {
        ArrayList<Node2> parents = node.getParentNodes(allNodes);
        ArrayList<Node2> children = node.getChildrenNodes(allNodes);

        for(int i = 0; i < parents.size(); i++) {
            Node2 parent = parents.get(i);

            for (int j = 0; j < children.size(); j++) {
                Node2 child = children.get(j);
                if (child.amIYourChild(parent)) {

                    int curMutLvl = Math.max(parent.getMutationLevel(), child.getMutationLevel());
                    node.incrementMutationLevel(curMutLvl);

                    return true;
                }
            }
        }
        return false;
    }
}