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
import java.util.regex.Pattern;

import static java.lang.Math.toIntExact;

/**
 * This class contains a parser to parse a .gfa file into our data structure.
 */
public class GfaParser {
    private String header1;
    private String header2;
    private HTreeMap<Long, String> sequenceMap;

    private int[] parentArray;
    private int[] childArray;

    public DB db;


    /**
     * This method parses the file specified in filepath into a sequence graph.
     * @param filePath A string specifying where the file is stored.
     * @return Returns a sequenceGraph
     * @throws IOException For instance when the file is not found
     */
    @SuppressWarnings("Since15")
    public void parseGraph(String filePath) throws IOException {
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] partPaths = filePath.split(pattern);
        String partPath = partPaths[partPaths.length-1];
        System.out.println(partPath);


        db = DBMaker.fileDB(partPath +  ".sequence.db").fileMmapEnable().fileMmapPreclearDisable().cleanerHackEnable().make();
        if (db.get(partPath+ ".sequence.db") != null) {
            sequenceMap = db.hashMap(partPath + ".sequence.db").keySerializer(Serializer.LONG).valueSerializer(Serializer.STRING).createOrOpen();
            parseSpecific(filePath, true);
        } else {
            sequenceMap = db.hashMap(partPath + ".sequence.db").keySerializer(Serializer.LONG).valueSerializer(Serializer.STRING).createOrOpen();
            parseSpecific(filePath, false);
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
    private void parseSpecific(String filePath, Boolean exists) throws IOException {
        parentArray = new int[17367708];
        childArray = new int[17367708];
        InputStream in = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = br.readLine();
        header1 = line.split("H")[1];
        line = br.readLine();
        header2 = line.split("H")[1];
        int counter = 0;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("S")) {
                String[] data = line.split(("\t"));
                int id = Integer.parseInt(data[1]);
                if (!exists) {
                    sequenceMap.put((long) (id), data[2]);
                }
            } else if (line.startsWith("L")) {
                String[] edgeDataString = line.split("\t");
                int parentId = (Integer.parseInt(edgeDataString[1]));
                int childId = Integer.parseInt(edgeDataString[3]);
                parentArray[counter] = parentId;
                childArray[counter] = childId;
                counter++;
            }
        }
        in.close();
        br.close();
        db.commit();
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

    /**
     * Converts an List<Integer> to int[]
     * @param integers list with integers
     * @return int[]
     */
    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

    public int[] getParentArray() {
        return this.parentArray;
    }

    public int[] getChildArray() {
        return this.parentArray;
    }
}
