package parser;

import gui.CustomProperties;
import org.mapdb.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;
import java.util.regex.Pattern;

/**
 * This class contains a parser to parse a .gfa file into our data structure.
 */
public class GfaParser extends Observable implements Runnable {
    private BTreeMap<Long, String> sequenceMap;

    private BTreeMap<Integer, int[]> genomes;
    private BTreeMap<Integer, int[]> offSets;

    private String filePath;

    private String partPath;
    private CustomProperties properties = new CustomProperties();

    private DB db;

    private HashMap<String, Integer> genomesMap;
    private HashMap<Integer, String> reversedGenomesMap;

    /**
     * Constructor.
     *
     * @param absolutePath The path location of the file.
     */
    public GfaParser(String absolutePath) {
        filePath = absolutePath;
    }

    /**
     * Getter for the path the file is stored.
     *
     * @return the Path.
     */
    public String getPartPath() {
        return this.partPath;
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
     * Getter for the filePath.
     *
     * @return the filePath.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * getter for db of the sequencemap.
     *
     * @return db.
     */
    public DB getDb() {
        return db;
    }


    /**
     * This method parses the file specified in filepath into a sequence graph.
     *
     * @param filePath A string specifying where the file is stored.
     * @throws IOException For instance when the file is not found
     */
    @SuppressWarnings("Since15")
    private synchronized void parseGraph(String filePath) throws IOException {
        properties.updateProperties();
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] partPaths = filePath.split(pattern);
        partPath = partPaths[partPaths.length - 1];
        db = DBMaker.fileDB(partPath + ".database.db").fileMmapEnable().
                fileMmapPreclearDisable().
                cleanerHackEnable().
                allocateIncrement( 64 * 1024 * 1024 ).
                closeOnJvmShutdown().checksumHeaderBypass().make();
        if (db.get(partPath + ".sequence.db") != null) {
            sequenceMap = db.treeMap(partPath + ".sequence.db").
                    keySerializer(Serializer.LONG).
                    valueSerializer(Serializer.STRING).createOrOpen();
            genomes = db.treeMap(partPath + ".genomes.db").
                    keySerializer(Serializer.INTEGER).
                    valueSerializer(Serializer.INT_ARRAY).createOrOpen();
            offSets = db.treeMap(partPath + ".offSets.db").
                    keySerializer(Serializer.INTEGER).
                    valueSerializer(Serializer.INT_ARRAY).createOrOpen();
            parseHeaders();
        } else {
            properties.setProperty(partPath, "false");
            properties.saveProperties();
            sequenceMap = db.treeMap(partPath + ".sequence.db").
                    keySerializer(Serializer.LONG).
                    valueSerializer(Serializer.STRING).createOrOpen();
            genomes = db.treeMap(partPath + ".genomes.db").
                    keySerializer(Serializer.INTEGER).
                    valueSerializer(Serializer.INT_ARRAY).createOrOpen();
            offSets = db.treeMap(partPath + ".offSets.db").
                    keySerializer(Serializer.INTEGER).
                    valueSerializer(Serializer.INT_ARRAY).createOrOpen();
            parseHeaders();
            parseSpecific(filePath);
        }
        this.setChanged();
        this.notifyObservers(1);
        this.setChanged();
        this.notifyObservers(partPath);
    }

    private void parseHeaders() throws IOException {
        InputStream in = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("H")) {
                String header = line.split("\t")[1];
                if (header.startsWith("ORI:Z:")) {
                    String allGenomes = header.split(":")[2];
                    setAllGenomesMap(allGenomes);
                }
            }
            if (line.startsWith("S")) {
                break;
            }
        }
        in.close();
        br.close();
    }

    /**
     * Getter for the sequenceHashMap.
     *
     * @return The HashMap.
     */
    public synchronized BTreeMap<Long, String> getSequenceHashMap() {
        return sequenceMap;
    }

    public synchronized BTreeMap<Integer, int[]> getOffSets() {
        return offSets;
    }

    public synchronized BTreeMap<Integer, int[]> getGenomes() {
        return genomes;
    }

    /**
     * Parses the file with a boolean whether to create a db file or not. Creates the Graph
     *
     * @param filePath The file to parse/
     * @throws IOException Reader.
     */
    private synchronized void parseSpecific(String filePath) throws IOException {
        BufferedWriter parentWriter =
                new BufferedWriter(new FileWriter(partPath + "parentArray.txt"));
        BufferedWriter childWriter =
                new BufferedWriter(new FileWriter(partPath + "childArray.txt"));
        //BufferedWriter genomeWriter = new BufferedWriter(new FileWriter(partPath + "genomes.txt"));
        InputStream in = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line;
        int sizeOfFile = 0;
        int maxCor = Integer.MIN_VALUE;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("S")) {
                String[] data = line.split(("\t"));
                int id = Integer.parseInt(data[1]);
                for (String aData : data) {
                    if (aData.startsWith("ORI:Z:")) {
                        String[] genomes = aData.split(":")[2].split(";");
                        writeGenomes(id, genomes);
                    } else if (aDataStartsWithCorrect(aData)) {
                        String offSets = aData.split(":")[2];
                        String[] offSetStrings = offSets.split(";");
                        int[] offSetInts = new int[offSetStrings.length];
                        maxCor = getMaxCor(maxCor, offSetStrings);
                        for (int i = 0; i < offSetStrings.length; i++) {
                            offSetInts[i] = Integer.parseInt(offSetStrings[i]);
                        }
                        this.offSets.put(id, offSetInts);
                    }
                }
                sequenceMap.put((long) (id), data[2]);

            } else if (line.startsWith("L")) {
                writeEdge(parentWriter, childWriter, line);
                sizeOfFile++;
            }
        }
        closeStreams(parentWriter, childWriter, in, br);
        db.commit();
        updateProperties(sizeOfFile, maxCor);
    }

    /**
     * Update the properties.
     *
     * @param sizeOfFile - the size of the file.
     * @param maxCor     - max coord value
     */
    private void updateProperties(int sizeOfFile, int maxCor) {
        properties.updateProperties();
        properties.setProperty(partPath + "childArray.txtsize", Integer.toString(sizeOfFile));
        properties.setProperty(partPath, "true");
        properties.setProperty(partPath + "Max-Cor", Integer.toString(maxCor));
        properties.saveProperties();
    }

    /**
     * Close all the streams used to parse the data.
     *
     * @param parentWriter - parentWriter to close.
     * @param childWriter  - childWriter to close.
     * @param in           - input stream to close.
     * @param br           - buffered reader to close.
     * @throws IOException - throws IO exception.
     */
    private void closeStreams(BufferedWriter parentWriter,
                              BufferedWriter childWriter,
                              InputStream in,
                              BufferedReader br) throws IOException {
        in.close();
        br.close();
        parentWriter.flush();
        parentWriter.close();
        childWriter.flush();
        childWriter.close();
    }

    /**
     * checks if aData starts with the correct strings.
     *
     * @param aData - the data string
     * @return - boolean true or false
     */
    private boolean aDataStartsWithCorrect(String aData) {
        return aData.startsWith("START:Z:")
                || aData.startsWith("OFFSETS:i:")
                || aData.startsWith("OFFSETS:Z:");
    }

    /**
     * Writes the edges to an edges file.
     *
     * @param parentWriter - the parent writer
     * @param childWriter- the child writer
     * @param line         - line to write
     * @throws IOException if something goes wrong with the file.
     */
    private void writeEdge(BufferedWriter parentWriter, BufferedWriter childWriter,
                           String line) throws IOException {
        String[] edgeDataString = line.split("\t");
        int parentId = Integer.parseInt(edgeDataString[1]);
        int childId = Integer.parseInt(edgeDataString[3]);
        parentWriter.write(parentId + ",");
        childWriter.write(childId + ",");
    }

    /**
     * Get the highest coordinate from the offsets.
     *
     * @param maxCor        - the current highest coord.
     * @param offSetStrings - the strings which are compared.
     * @return - the max coordinate.
     */
    private int getMaxCor(int maxCor, String[] offSetStrings) {
        int tempOffSetInt;
        for (String singleOffSet : offSetStrings) {
            tempOffSetInt = Integer.parseInt(singleOffSet);
            if (tempOffSetInt > maxCor) {
                maxCor = tempOffSetInt;
            }
        }
        return maxCor;
    }

    /**
     * Writes the genomes to a file.
     *
     * @param id - the id
     * @throws IOException - can throw IO exception
     */
    private void writeGenomes(int id, String[] genomes)
            throws IOException {
        int[] genomeInts = new int[genomes.length];
        for (int i = 0; i < genomes.length; i++) {
            String[] name = genomes[i].split("\\.");
            String nameGenome = "";
            for (int j = 0; j < name.length - 1; j++) {
                nameGenome = nameGenome.concat(name[j]);
            }
            if (genomesMap.get(nameGenome) != null) {
                genomeInts[i] = genomesMap.get(nameGenome);
            } else {
                genomeInts[i] = Integer.parseInt(genomes[i]);
            }
        }
        this.genomes.put(id,genomeInts);
    }

    /**
     * converts the array txt file to an int[].
     *
     * @param isParent - is parent boolean
     * @return - the int[]
     * @throws IOException - can throw IO exception due to using an inputstream.
     */
    private int[] read(boolean isParent) throws IOException {
        String additionToPath;
        if (isParent) {
            additionToPath = "parentArray.txt";
        } else {
            additionToPath = "childArray.txt";
        }
        InputStream in = new FileInputStream(System.getProperty("user.dir")
                + System.getProperty("file.separator") + partPath + additionToPath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = br.readLine();
        if (line != null) {
            String[] strNums = line.split(",");
            int size = strNums.length;
            int[] nodeArray = new int[size];
            for (int i = 0; i < size; i++) {
                nodeArray[i] = Integer.parseInt(strNums[i]);

            }
            return nodeArray;
        } else {
            return null;
        }
    }

    /**
     * Wrapper for read function to get the childArray.
     *
     * @return the childArray.
     * @throws IOException - reads a file, so it might cause IO Exceptions.
     */
    public int[] getParentArray() throws IOException {
        return read(true);
    }

    /**
     * wrapper for read function to get the parentArray.
     *
     * @return the parentArray
     * @throws IOException - reads a file, so it might cause IO Exceptions
     */
    public int[] getChildArray() throws IOException {
        return read(false);
    }

    /**
     * puts the genomes in maps with indices.
     *
     * @param allGenomes - string containing genomes.
     */
    private void setAllGenomesMap(String allGenomes) {
        String[] strNums = allGenomes.split(";");
        this.genomesMap = new HashMap<String, Integer>();
        this.reversedGenomesMap = new HashMap<Integer, String>();
        for (int i = 0; i < strNums.length; i++) {
            String[] nameGenomes = strNums[i].split("\\.");
            String nameGenome = "";
            for (int j = 0; j < nameGenomes.length - 1; j++) {
                nameGenome = nameGenome.concat(nameGenomes[j]);
            }
            if (nameGenomes.length == 1) {
                nameGenome = nameGenomes[0];
            }
            this.genomesMap.put(nameGenome, i);
            this.reversedGenomesMap.put(i, nameGenome);
        }
    }

    public HashMap<String, Integer> getAllGenomesMap() {
        return genomesMap;
    }

    /**
     * @return The map with the genomes.
     */
    public HashMap<Integer, String> getAllGenomesMapReversed() {
        return reversedGenomesMap;
    }

}
