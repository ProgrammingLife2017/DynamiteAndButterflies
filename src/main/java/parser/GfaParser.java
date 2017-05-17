package parser;

import graph.Edge;
import graph.SequenceGraph;
import graph.SequenceNode;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.toIntExact;

/**
 * This class contains a parser to parse a .gfa file into our data structure.
 */
public class GfaParser {
    private String header1;
    private String header2;
    private HTreeMap<Long, String> sequenceMap;
    public DB db;

    /**
     * This method parses the file specified in filepath into a sequence graph.
     * @param filePath A string specifying where the file is stored.
     * @return Returns a sequenceGraph
     * @throws IOException For instance when the file is not found
     */
    @SuppressWarnings("Since15")
    public SequenceGraph parseGraph(String filePath) throws IOException {
        String[] partPaths = filePath.split("\\\\");
        String partPath = partPaths[partPaths.length-1];
        db = DBMaker.fileDB(partPath).fileMmapEnable().fileMmapPreclearDisable().cleanerHackEnable().make();
        if (db.get(partPath) != null) {
            sequenceMap = db.hashMap(partPath).keySerializer(Serializer.LONG).valueSerializer(Serializer.STRING).createOrOpen();
            return parseSpecific(filePath, true);
        } else {
            sequenceMap = db.hashMap(partPath).keySerializer(Serializer.LONG).valueSerializer(Serializer.STRING).createOrOpen();
            return parseSpecific(filePath, false);
        }
    }

    /**
     * Getter for the sequenceHashMap.
     * @return The HashMap.
     */
    public HTreeMap<Long, String> getSequenceHashMap() {
        return sequenceMap;
    }

    /**
     * Parses the file with a boolean whether to create a db file or not. Creates the Graph
     * @param filePath The file to parse/
     * @param exists Does the db file already exist?
     * @return The sequenceGraph
     * @throws IOException Reader.
     */
    @SuppressWarnings("Since15")
    private SequenceGraph parseSpecific(String filePath, Boolean exists) throws IOException {
        SequenceGraph sequenceGraph = new SequenceGraph();
        InputStream in = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = br.readLine();
        header1 = line.split("H")[1];
        line = br.readLine();
        header2 = line.split("H")[1];
        while ((line = br.readLine()) != null) {
            if (line.startsWith("S")) {
                String[] data = line.split(("\t"));
                int id = Integer.parseInt(data[1]);
                SequenceNode node = new SequenceNode(toIntExact(id));
                if (!exists) {
                    sequenceMap.put((long) (id), data[2]);
                }
                sequenceGraph.addNode(node);
            } else if (line.startsWith("L")) {
                String[] edgeDataString = line.split("\t");
                int parentId = (Integer.parseInt(edgeDataString[1]));
                int childId = Integer.parseInt(edgeDataString[3]);
                Edge edge = new Edge(parentId, childId);
                sequenceGraph.getEdges().add(edge);
            }
        }
        in.close();
        br.close();
        db.commit();
        return sequenceGraph;
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
