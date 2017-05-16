package parser;

import graph.Edge;
import graph.SequenceGraph;
import graph.SequenceNode;
import org.mapdb.*;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.toIntExact;

/**
 * This class contains a parser to parse a .gfa file into our data structure.
 */
public class GfaParser {
    HashMap<Integer, byte[]> sequenceHashMap;
    private String header1;
    private String header2;
    private Boolean duplicate = false;

    /**
     * This method parses the file specified in filepath into a sequence graph.
     * @param filepath A string specifying where the file is stored
     * @return Returns a sequenceGraph
     * @throws IOException For instance when the file is not found
     */
    @SuppressWarnings("Since15")
    public SequenceGraph parseGraph(String filepath) throws IOException {
        DB db = DBMaker.fileDB("fileDB").fileMmapEnable().fileMmapPreclearDisable().cleanerHackEnable().make();
        BTreeMap<Long, String> sequenceMap = db.treeMap("sequenceMap").keySerializer(Serializer.LONG).valueSerializer(Serializer.STRING).createOrOpen();
        SequenceGraph sequenceGraph = new SequenceGraph();
        InputStream in = new FileInputStream(filepath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = br.readLine();
        header1 = line.split("H")[1];
        line = br.readLine();
        header2 = line.split("H")[1];
        int count = 0;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("S")) {
                String[] data = line.split(("\t"));
                int id = Integer.parseInt(data[1]);
                SequenceNode node = new SequenceNode(toIntExact(id));
                //String seq = encodeSequence(data[2]);
                sequenceMap.put((long) (id), data[2]);
                sequenceGraph.addNode(node);
                count++;
                if(count%100000 == 0) {
                    System.out.println(count);
                }
            } /*else if (line.startsWith("L")) {
                String[] edgeDataString = line.split("\t");
                int parentId = (Integer.parseInt(edgeDataString[1]));
                int childId = Integer.parseInt(edgeDataString[3]);
                Edge edge = new Edge(parentId, childId);
                sequenceGraph.getEdges().add(edge);
            }*/
        }
        db.close();
        in.close();
        br.close();
        return sequenceGraph;
    }


    public HashMap<Integer, byte[]> getSequenceHashMap() {
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

    public String encodeSequence(String seq) {
        List<String> binString = new ArrayList<String>();
        for (int i = 0; i < seq.length(); i += 4) {
            binString.add(convert(seq.substring(i, Math.min(i + 4, seq.length()))));
        }
        duplicate = false;
        String encodedBinString = binString.get(0);
        for (int i = 1; i < binString.size(); i++) {
            if (binString.get(i).equals(binString.get(i - 1))) {
                if (!duplicate) {
                    encodedBinString = encodedBinString.concat("1");
                    duplicate = true;
                } else {
                    encodedBinString = encodedBinString.concat(binString.get(i));
                    duplicate = false;
                }
            } else {
                encodedBinString = encodedBinString.concat("0");
                encodedBinString = encodedBinString.concat(binString.get(i));
                duplicate = false;
            }
        }
        return encodedBinString;
    }

    public String decodeSequence(Byte[] encodedBinString) {
        String result = "";
        System.out.println(result);
        return result;
    }

    private String convert(String seq) {
        String result = "";
        for (int i = 0; i < seq.length(); i++) {
            switch (seq.charAt(i)) {
                case 'A': result = result.concat("00"); break;
                case 'C': result = result.concat("01"); break;
                case 'G': result = result.concat("10"); break;
                case 'T': result = result.concat("11"); break;
                case 'N': result = result.concat("0"); break;
            }
        }
        return result;
    }
}
