package parser;

import gui.sub_controllers.PopUpController;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import static java.lang.Math.toIntExact;

/**
 * This class contains a parser to parse a .gfa file into our data structure.
 */
public class GfaParser extends Observable implements Runnable {
    private String header1;
    private String header2;
    private HTreeMap<Long, String> sequenceMap;
    private String filePath;
    private String partPath;
    private Preferences prefs;
    private int startNode = Integer.MAX_VALUE;
    private int endNode = Integer.MIN_VALUE;

    private int counter;
    private int maxID;

    private DB db;

    /**
     * Constructor.
     * @param absolutePath The path location of the file.
     */
    public GfaParser(String absolutePath) {
        filePath = absolutePath;
    }

    @Override
    public void run() {
        try {
            parseGraph(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * getter for db of the sequencemap.
     * @return db.
     */
    public DB getDb() {
        return db;
    }


    /**
     * This method parses the file specified in filepath into a sequence graph.
     * @param filePath A string specifying where the file is stored.
     * @throws IOException For instance when the file is not found
     */
    @SuppressWarnings("Since15")
    private synchronized void parseGraph(String filePath) throws IOException {
        prefs = Preferences.userRoot();
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] partPaths = filePath.split(pattern);
        partPath = partPaths[partPaths.length - 1];
        if (!prefs.getBoolean(partPath, true)) {
            PopUpController controller = new PopUpController();
            String message = "Database File is corrupt, press 'Reload' to reload the file," + "\n"
                    + "or press 'Resume' to recover the data still available.";
            controller.loadDbCorruptPopUp(partPath, message);
        }
        db = DBMaker.fileDB(partPath + ".sequence.db").fileMmapEnable().
                                                fileMmapPreclearDisable().cleanerHackEnable().
                                                closeOnJvmShutdown().checksumHeaderBypass().make();
        if (db.get(partPath + ".sequence.db") != null) {
            sequenceMap = db.hashMap(partPath + ".sequence.db").
                            keySerializer(Serializer.LONG).
                            valueSerializer(Serializer.STRING).createOrOpen();
        } else {
            prefs.putBoolean(partPath, false);
            sequenceMap = db.hashMap(partPath + ".sequence.db").
                                    keySerializer(Serializer.LONG).
                                    valueSerializer(Serializer.STRING).createOrOpen();
            parseSpecific(filePath);
        }
        this.setChanged();
        this.notifyObservers(filePath);
    }

    /**
     * Getter for the sequenceHashMap.
     * @return The HashMap.
     */
    public synchronized HTreeMap<Long, String> getSequenceHashMap() {
        return sequenceMap;
    }

    /**
     * Parses the file with a boolean whether to create a db file or not. Creates the Graph
     * @param filePath The file to parse/
     * @throws IOException Reader.
     */
    @SuppressWarnings("Since15")
    private synchronized void parseSpecific(String filePath) throws IOException {
        LinkedList<Integer> parentList = new LinkedList<Integer>();
        LinkedList<Integer> childList = new LinkedList<Integer>();
        InputStream in = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = br.readLine();
        if (line == null) {
            in.close();
            br.close();
        }
        header1 = line.split("H")[1];
        line = br.readLine();

        if (line == null) {
            in.close();
            br.close();
        }
        header2 = line.split("H")[1];
        while ((line = br.readLine()) != null) {
            if (line.startsWith("S")) {
                String[] data = line.split(("\t"));
                int id = Integer.parseInt(data[1]);
                sequenceMap.put((long) (id), data[2]);
            } else if (line.startsWith("L")) {
                String[] edgeDataString = line.split("\t");
                int parentId = (Integer.parseInt(edgeDataString[1]));
                int childId = Integer.parseInt(edgeDataString[3]);
                parentList.add(parentId);
                childList.add(childId);
            }
        }
        write(partPath + "parentArray.txt",parentList);
        write(partPath + "childArray.txt",childList);
        in.close();
        br.close();
        db.commit();
    }

    private void write(String filePath, LinkedList<Integer> x) throws IOException {
        BufferedWriter edgeWriter = null;
        edgeWriter =  new BufferedWriter(new FileWriter(filePath));

        for (int i: x) {
            edgeWriter.write(i +",");
        }
        edgeWriter.flush();
        edgeWriter.close();
        prefs.putInt(filePath + "size", x.size());
    }

    private int[] read(String partPath, boolean isParent) throws IOException {
        String additionToPath;
        if (isParent) {
            additionToPath = "parentArray.txt";
        } else {
            additionToPath = "childArray.txt";
        }
        InputStream in = new FileInputStream(System.getProperty("user.dir")+ System.getProperty("file.separator") + partPath + additionToPath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String []StrNums = br.readLine().split(",");
        int size = prefs.getInt(partPath+"childArray.txtsize", -1);
        if (size == -1) {
            throw new java.lang.RuntimeException("Size not in preferences file");
        }
        int [] nodeArray = new int[size];
        for (int i=0; i < StrNums.length; i++) {
            nodeArray[i] = Integer.parseInt(StrNums[i]);

        }
        return nodeArray;
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
     * Converts an List<Integer> to int[].
     * @param integers list with integers.
     * @return int[].
     */
    private static int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i);
        }
        return ret;
    }

    public int[] getParentArray(String partPath) throws IOException {
        return read(partPath, true);
    }

    public int[] getChildArray( String partPath) throws IOException {
        return read(partPath, false);
    }

    public int getCounter() {
        return counter;
    }

    public String getPartPath() {
        return partPath;
    }


}
