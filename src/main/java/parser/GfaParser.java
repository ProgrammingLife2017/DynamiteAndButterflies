package parser;

import graph.Edge;
import graph.SequenceGraph;
import graph.SequenceNode;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public SequenceGraph parseGraph(String filepath) throws IOException {
        sequenceHashMap = new HashMap<Integer, byte[]>();
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
                int id = Integer.parseInt(line.split("\t")[1]);
                SequenceNode node = new SequenceNode(id);
                byte[] seq = encodeSequence(line.split("\t")[2]);
                sequenceHashMap.put(id, seq);
                sequenceGraph.addNode(node);
                count++;
                if(count%100000 == 0) {
                    System.out.println(count);
                }
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

    public byte[] encodeSequence(String seq) {
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
        if(encodedBinString.equals("")) {
            encodedBinString = "Could not read this sequence.";
        }
        byte[] sequence = new BigInteger(encodedBinString, 2).toByteArray();
        return sequence;
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
