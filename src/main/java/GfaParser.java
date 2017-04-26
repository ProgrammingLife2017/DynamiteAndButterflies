import java.io.*;
import java.util.ArrayList;



/**
 * Created by marc on 25-4-17.
 */
public class GfaParser {

    private ArrayList<Node> nodes;

    public static void main(String[] args) throws Exception {
        GfaParser parser = new GfaParser();
        InputStream in = GfaParser.class.getClass().getResourceAsStream("/TB10.gfa");
        parser.parse(in);
        System.out.println("hey");
    }

    private void parse(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            String header1 = line.split("H")[1];
            line = br.readLine();
            String header2 = line.split("H")[1];

            nodes = new ArrayList<Node>();
            while ((line = br.readLine()) != null) {
                Node node;
                if(line.startsWith("S")) {
                    String[] data = line.split("\t");
                    node = new Node(Integer.parseInt(data[1]), data[2]);
                    nodes.add(node);
                }
                else if(line.startsWith("L")){
                    String[] edgeDataString = line.split("\t");
                    int childNode = Integer.parseInt(edgeDataString[3]);
                    nodes.get(Integer.parseInt(edgeDataString[1]) - 1).addChild(childNode);
                }
            }
        }
        catch(IOException e) {
                e.printStackTrace();
            }
    }
}
