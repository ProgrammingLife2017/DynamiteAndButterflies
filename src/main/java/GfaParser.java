import java.io.*;
import java.util.ArrayList;



/**
 * Created by marc on 25-4-17.
 */
public class GfaParser {

    private ArrayList<Node> nodes;

    public static void main(String[] args) throws Exception {
        GfaParser parser = new GfaParser();
        parser.parse(new File("/home/marc/IdeaProjects/DynamiteAndButterflies/TB10.gfa"));
        System.out.println("hey");
    }

    private void parse(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
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
                    line = br.readLine();
                    nodes.add(node);
                    while (line.startsWith("L")) {
                        String[] edgeDataString = line.split("\t");
                        int childNode = Integer.parseInt(edgeDataString[3]);
                        line = br.readLine();
                        node.addChild(childNode);
                    }


                }

            }
        }
        catch(IOException e) {
                e.printStackTrace();
            }
    }
}
