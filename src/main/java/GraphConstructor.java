//import org.graphstream.graph.Graph;
//import org.graphstream.graph.Node;
//import org.graphstream.graph.implementations.SingleGraph;
//
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Iterator;
//
//public class GraphConstructor {
//    public static void main(String args[]) {
//        new GraphConstructor(new GfaParser());
//    }
//
//    public GraphConstructor(GfaParser parser) {
//        Graph graph = new SingleGraph("tutorial 1");
//
//        graph.addAttribute("ui.stylesheet", styleSheet);
//        graph.setAutoCreate(true);
//        graph.setStrict(false);
//        graph.display();
//
//        InputStream in = GfaParser.class.getClass().getResourceAsStream("/test (1).gfa");
//        parser.parse(in);
//        ArrayList<Node2> set = parser.getList();
//        parser.makeNodes(graph);
//
//        for(int j = 0; j < set.size(); j++) {
//            Node2 n = set.get(j);
//
//            Node tempNode = graph.getNode("" + n.getId());
//            tempNode.setAttribute("seq", n.getSeq());
//
//            ArrayList<Integer> children = n.getChild();
//            for(int i = 0; i < children.size(); i++) {
//                int child = children.get(i);
//                graph.addEdge("" + i, "" + n.getId(), "" + child);
//            }
//
//        }
//
////        for (Node node : graph) {
////            node.addAttribute("ui.label", node.getId());
////        }
//
//        explore(graph.getNode("1"));
//    }
//
//    public void explore(Node source) {
//        Iterator<? extends Node> k = source.getBreadthFirstIterator();
//
//        while (k.hasNext()) {
//            Node next = k.next();
//            next.setAttribute("ui.class", "marked");
//            sleep();
//        }
//    }
//
//    protected void sleep() {
//        try { Thread.sleep(1000); } catch (Exception e) {}
//    }
//
//    protected String styleSheet =
//            "node {" +
//                    "	fill-color: red;" +
//                    "}" +
//                    "node.marked {" +
//                    "	fill-color: black;" +
//                    "}";
//}