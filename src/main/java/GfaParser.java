import com.mxgraph.view.mxGraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by marc on 25-4-17.
 */
public class GfaParser {

    private ArrayList<Node2> node2s;

    public static void main(String[] args) throws Exception {
        GfaParser parser = new GfaParser();
        InputStream in = GfaParser.class.getClass().getResourceAsStream("/TB10.gfa");
        parser.parse(in);
        System.out.println("hey");
    }

    public void parse(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            String header1 = line.split("H")[1];
            line = br.readLine();
            String header2 = line.split("H")[1];

            node2s = new ArrayList<Node2>();
            while ((line = br.readLine()) != null) {
                Node2 node2;
                if(line.startsWith("S")) {
                    String[] data = line.split("\t");
                    node2 = new Node2(Integer.parseInt(data[1]), data[2]);
                    node2s.add(node2);
                }
                else if(line.startsWith("L")){
                    String[] edgeDataString = line.split("\t");
                    int childNode = Integer.parseInt(edgeDataString[3]);
                    node2s.get(Integer.parseInt(edgeDataString[1]) - 1).addChild(childNode);
                }
            }
        }
        catch(IOException e) {
                e.printStackTrace();
            }
    }

    public ArrayList<Node2> getList() {
        return node2s;
    }

    public HashMap<Integer, Object> makeNodes(mxGraph graph) {
        Object parent = graph.getDefaultParent();
        HashMap<Integer, Object> hash = new HashMap<Integer, Object>();
        for (int i = 0; i < node2s.size(); i++) {
            Node2 node = node2s.get(i);
            try {
                Object obj = graph.insertVertex(parent, "" + node.getId(), node.getSeq(), 20 * (i * 10), 20 * (i * 10), 80, 30);
                hash.put(node.getId(), obj);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Ging iets mis met een vertex adden in de visualisation.");
            }
        }
        return hash;
    }
}
