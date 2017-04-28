
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
        GraphMaker frame = new GraphMaker("/test (1).gfa");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 320);
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
                    graph.insertEdge(parOfEdge, null, "Edge", par, child);
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

        for (int i = 0; i < set.size(); i++) {
            Node2 node = set.get(i);
            try {
                Object obj = graph.insertVertex(parent, "" + node.getId(), node.getSeq(), 20 * (i * 5), 20 * (i * 5), 80, 30);
                hash.put(node.getId(), obj);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Ging iets mis met een vertex adden in de visualisation.");
            }
        }
        graph.getModel().endUpdate();
        return hash;
    }

}