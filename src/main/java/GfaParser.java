import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * Created by marc on 25-4-17.
 */
public class GfaParser {


    public static void main(String[] args) throws Exception {




        long startTime = System.currentTimeMillis();
        GfaParser parser = new GfaParser();
        InputStream in = GfaParser.class.getClass().getResourceAsStream("/chr19.hg38.w115.gfa");
        SequenceGraph graph = parser.parse(in);
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) );


        long sT = System.currentTimeMillis();
        graph.initialize();
        long eT = System.currentTimeMillis();
        System.out.println("Total execution time: " + (eT - sT) );

    }

    public SequenceGraph parse(InputStream in) {
        SequenceGraph sequenceGraph = new SequenceGraph();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            String header1 = line.split("H")[1];
            line = br.readLine();
            String header2 = line.split("H")[1];


            while ((line = br.readLine()) != null) {

                if(line.startsWith("S")) {
                    String[] data = line.split("\t");
                    SequenceNode node = new SequenceNode(Integer.parseInt(data[1]), data[2]);
                    sequenceGraph.addNode(node);
                }
                else if(line.startsWith("L")){
                    String[] edgeDataString = line.split("\t");
                    int parentId = (Integer.parseInt(edgeDataString[1]));
                    int childId = Integer.parseInt(edgeDataString[3]);
                    Edge edge = new Edge(parentId, childId);
                    sequenceGraph.getEdges().add(edge);
                }
            }

        }
        catch(IOException e) {
                e.printStackTrace();
            }
            return sequenceGraph;

    }

}
