package parser;

import graph.Edge;
import graph.SequenceGraph;
import graph.SequenceNode;

import java.io.*;
import java.util.ArrayList;


public class GfaParser {
    private String header1;
    private String header2;

    /**
    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        parser.GfaParser parser = new parser.GfaParser();
        graph.SequenceGraph graph = parser.parse("src/main/resources/TB10.gfa");
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) );
        long sT = System.currentTimeMillis();
        graph.initialize();
        long eT = System.currentTimeMillis();
        System.out.println("Total execution time: " + (eT - sT) );
    }**/

    public SequenceGraph parse(String filepath) throws IOException {
        SequenceGraph sequenceGraph = new SequenceGraph();
        InputStream in = new FileInputStream(filepath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = br.readLine();
        header1 = line.split("H")[1];
        line = br.readLine();
        header2 = line.split("H")[1];
        while ((line = br.readLine()) != null) {
            if(line.startsWith("S")) {
                String[] data = line.split("\t");
                SequenceNode node = new SequenceNode(Integer.parseInt(data[1]), data[2]);
                sequenceGraph.addNode(node);
            }
            else if(line.startsWith("L")) {
                String[] edgeDataString = line.split("\t");
                int parentId = (Integer.parseInt(edgeDataString[1]));
                int childId = Integer.parseInt(edgeDataString[3]);
                Edge edge = new Edge(parentId, childId);
                sequenceGraph.getEdges().add(edge);
            }
        }
        return sequenceGraph;
    }

    public ArrayList<String> getHeaders(){
        ArrayList<String> headers = new ArrayList<String>();
        headers.add(header1);
        headers.add(header2);
        return headers;
    }
}
