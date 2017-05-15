package parser;

import graph.Edge;
import graph.SequenceGraph;
import graph.SequenceNode;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class contains a parser to parse a .gfa file into our data structure.
 */
public class GfaParser {
    HashMap<Integer, String> sequenceHashMap;
    private String header1;
    private String header2;

    /**
     * This method parses the file specified in filepath into a sequence graph.
     * @param filepath A string specifying where the file is stored
     * @return Returns a sequenceGraph
     * @throws IOException For instance when the file is not found
     */
    public SequenceGraph parseGraph(String filepath) throws IOException {
        sequenceHashMap = new HashMap<Integer, String>();
        SequenceGraph sequenceGraph = new SequenceGraph();
        InputStream in = new FileInputStream(filepath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = br.readLine();
        header1 = line.split("H")[1];
        line = br.readLine();
        header2 = line.split("H")[1];
        while ((line = br.readLine()) != null) {
            if (line.startsWith("S")) {
                String[] data = line.split("\t");
                SequenceNode node = new SequenceNode(Integer.parseInt(data[1]));
                sequenceHashMap.put(Integer.parseInt(data[1]), data[2]);
                sequenceGraph.addNode(node);
            }
            else if (line.startsWith("L")) {
                String[] edgeDataString = line.split("\t");
                int parentId = (Integer.parseInt(edgeDataString[1]));
                int childId = Integer.parseInt(edgeDataString[3]);
                Edge edge = new Edge(parentId, childId);
                sequenceGraph.getEdges().add(edge);
            }
        }
        return sequenceGraph;
    }


    public HashMap<Integer, String> getSequenceHashMap() {
        return sequenceHashMap;
    }


    /**
     * Cretes an ArrayList of Strings specifying headers.
     * @return an arrayList containing all headers
     */
    public ArrayList<String> getHeaders() {
        ArrayList<String> headers = new ArrayList<String>();
        headers.add(header1);
        headers.add(header2);
        return headers;
    }
}
