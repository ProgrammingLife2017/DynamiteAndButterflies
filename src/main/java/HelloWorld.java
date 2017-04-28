
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class HelloWorld extends JFrame
{

    /**
     *
     */
    private static final long serialVersionUID = -2707712944901661771L;

    public HelloWorld()
    {
        super("Hello, World!");

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        InputStream in = GfaParser.class.getClass().getResourceAsStream("/test (1).gfa");
        GfaParser parser = new GfaParser();
        parser.parse(in);
        HashMap<Integer, Object> hash = parser.makeNodes(graph);
        ArrayList<Node2> set = parser.getList();

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

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);
    }

    public static void main(String[] args)
    {
        HelloWorld frame = new HelloWorld();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 320);
        frame.setVisible(true);
    }

}