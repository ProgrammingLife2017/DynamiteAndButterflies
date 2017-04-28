import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


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
}
