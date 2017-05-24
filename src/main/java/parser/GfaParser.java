package parser;

import graph.SequenceNode;
import gui.subControllers.PopUpController;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import static java.lang.Math.toIntExact;

/**
 * This class contains a parser to parse a .gfa file into our data structure.
 */
public class GfaParser {
    private String header1;
    private String header2;
    private HTreeMap<Long, String> sequenceMap;
    private HTreeMap<Long, int[]> adjacencyHMap;
    private DB db;
    private DB db2;
    private String partPath;
    private Preferences prefs;

    /**
     * This method parses the file specified in filepath into a sequence graph.
     * @param filePath A string specifying where the file is stored.
     * @return Returns a sequenceGraph
     * @throws IOException For instance when the file is not found
     */
    public HTreeMap<Long, int[]> parseGraph(String filePath) throws IOException{
        prefs = Preferences.userRoot();
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] partPaths = filePath.split(pattern);
        partPath = partPaths[partPaths.length - 1];
        System.out.println(partPath);

        if(!prefs.getBoolean(partPath, true)) {
            PopUpController controller = new PopUpController();
            controller.loadPopUp(partPath);
        }

        db = DBMaker.fileDB(partPath + ".sequence.db").fileMmapEnable().fileMmapPreclearDisable().cleanerHackEnable().closeOnJvmShutdown().checksumHeaderBypass().make();
        db2 = DBMaker.fileDB(partPath + ".adjacency.db").fileMmapEnable().fileMmapPreclearDisable().cleanerHackEnable().closeOnJvmShutdown().checksumHeaderBypass().make();
        if (db.get(partPath + ".sequence.db") != null) {
            sequenceMap = db.hashMap(partPath + ".sequence.db").keySerializer(Serializer.LONG).valueSerializer(Serializer.STRING).createOrOpen();
            adjacencyHMap = db2.hashMap(partPath + ".adjacency.db").keySerializer(Serializer.LONG).valueSerializer(Serializer.INT_ARRAY).createOrOpen();
            return adjacencyHMap;
        } else {
            prefs.putBoolean(partPath, false);
            sequenceMap = db.hashMap(partPath + ".sequence.db").keySerializer(Serializer.LONG).valueSerializer(Serializer.STRING).createOrOpen();
            adjacencyHMap = db2.hashMap(partPath + ".adjacency.db").keySerializer(Serializer.LONG).valueSerializer(Serializer.INT_ARRAY).createOrOpen();
            return parseSpecific(filePath);
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
     * Getter for the AdjacencyHMap.
     * @return The HashMap.
     */
    public HTreeMap<Long, int[]> getAdjacencyHMap() {
        return this.adjacencyHMap;
    }

    /**
     * Parses the file with a boolean whether to create a db file or not. Creates the Graph
     * @param filePath The file to parse/
     * @return The sequenceGraph
     * @throws IOException Reader.
     */
    @SuppressWarnings("Since15")
    private HTreeMap<Long, int[]> parseSpecific(String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = br.readLine();
        header1 = line.split("H")[1];
        line = br.readLine();
        header2 = line.split("H")[1];
        ArrayList<Integer> temp = new ArrayList<Integer>();
        int parentId = 0;
        while ((line = br.readLine()) != null) {
            int childId;
            if (line.startsWith("S")) {
                if(parentId > 0) {
                    adjacencyHMap.put((long) parentId, convertIntegers(temp));
                    temp = new ArrayList<Integer>();
                }
                String[] data = line.split(("\t"));
                int id = Integer.parseInt(data[1]);
                SequenceNode node = new SequenceNode(toIntExact(id));
                sequenceMap.put((long) (id), data[2]);
            } else if (line.startsWith("L")) {
                String[] edgeDataString = line.split("\t");
                parentId = (Integer.parseInt(edgeDataString[1]));
                childId = Integer.parseInt(edgeDataString[3]);
                temp.add(childId);
            }
        }
        prefs.putBoolean(partPath, true);
        in.close();
        br.close();
        db.commit();
        return adjacencyHMap;
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
    private static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i);
        }
        return ret;
    }
}
