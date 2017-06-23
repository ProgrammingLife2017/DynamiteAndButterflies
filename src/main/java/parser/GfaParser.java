package parser;

import gui.CustomProperties;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.*;
import java.util.HashMap;
import java.util.Observable;
import java.util.regex.Pattern;

/**
 * This class contains a parser to parse a .gfa file into our data structure.
 */
public class GfaParser extends Observable implements Runnable {
    private HTreeMap<Long, String> sequenceMap;

    private String filePath;

    private String partPath;
    private CustomProperties properties = new CustomProperties();

    private DB db;

    private HashMap<String, Integer> genomesMap;
    private HashMap<Integer, String> reversedGenomesMap;

    /**
     * Constructor.
     * @param absolutePath The path location of the file.
     */
    public GfaParser(String absolutePath) {
        filePath = absolutePath;
    }

    /**
     * Getter for the path the file is stored.
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
     * @return the filePath.
     */
    public String getFilePath() {
        return filePath;
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
        properties.updateProperties();
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] partPaths = filePath.split(pattern);
        partPath = partPaths[partPaths.length - 1];
        db = DBMaker.fileDB(partPath + ".sequence.db").fileMmapEnable().
                                                fileMmapPreclearDisable().cleanerHackEnable().
                                                closeOnJvmShutdown().checksumHeaderBypass().make();
        if (db.get(partPath + ".sequence.db") != null) {
            sequenceMap = db.hashMap(partPath + ".sequence.db").
                            keySerializer(Serializer.LONG).
                            valueSerializer(Serializer.STRING).createOrOpen();
            parseHeaders();
        } else {
            properties.setProperty(partPath, "false");
            properties.saveProperties();
            sequenceMap = db.hashMap(partPath + ".sequence.db").
                                    keySerializer(Serializer.LONG).
                                    valueSerializer(Serializer.STRING).createOrOpen();
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
    private synchronized void parseSpecific(String filePath) throws IOException {
        BufferedWriter parentWriter =
                new BufferedWriter(new FileWriter(partPath + "parentArray.txt"));
        BufferedWriter childWriter =
                new BufferedWriter(new FileWriter(partPath + "childArray.txt"));
        BufferedWriter genomeWriter =
                new BufferedWriter(new FileWriter(partPath + "genomes.txt"));
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
                        for (int j = 0; j < genomes.length; j++) {
                            writeGenomes(genomeWriter, genomes, j);
                        }
                    } else if (aDataStartsWithCorrect(aData)) {
                        String offSets = aData.split(":")[2];
                        String[] offSetStrings = offSets.split(";");
                        maxCor = getMaxCor(maxCor, offSetStrings);
                        genomeWriter.write(offSets);
                        genomeWriter.newLine();
                    }
                }
                sequenceMap.put((long) (id), data[2]);
            } else if (line.startsWith("L")) {
                writeEdge(parentWriter, childWriter, line);
               sizeOfFile++;
            }
        }
        in.close();
        br.close();
        parentWriter.flush();
        parentWriter.close();
        genomeWriter.flush();
        genomeWriter.close();
        childWriter.flush();
        childWriter.close();
        db.commit();
        properties.updateProperties();
        properties.setProperty(partPath + "childArray.txtsize", Integer.toString(sizeOfFile));
        properties.setProperty(partPath, "true");
        properties.setProperty(partPath + "Max-Cor", Integer.toString(maxCor));
        properties.saveProperties();
    }

    /**
     * checks if aData starts with the correct strings.
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
     * @param parentWriter - the parent writer
     * @param childWriter- the child writer
     * @param line - line to write
     * @throws IOException if something goes wrong with the file.
     */
    private void writeEdge(BufferedWriter parentWriter, BufferedWriter childWriter, String line) throws IOException {
        String[] edgeDataString = line.split("\t");
        int parentId = Integer.parseInt(edgeDataString[1]);
        int childId = Integer.parseInt(edgeDataString[3]);
        parentWriter.write(parentId + ",");
        childWriter.write(childId + ",");
    }

    /**
     * Get the highest coordinate from the offsets.
     * @param maxCor - the current highest coord.
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
     * @param genomeWriter - the writer
     * @param genomes - the genomes
     * @param j - loop counter
     * @throws IOException - can throw IO exception
     */
    private void writeGenomes(BufferedWriter genomeWriter, String[] genomes, int j) throws IOException {
        if (genomesMap.get(genomes[j].split("\\.")[0]) != null) {
            if (j == genomes.length - 1) {
                genomeWriter.write(genomesMap.get(genomes[j].split("\\.")[0]) + "-");
            } else {
                genomeWriter.write(genomesMap.get(genomes[j].split("\\.")[0]) + ";");
            }
        } else {
            if (j == genomes.length - 1) {
                genomeWriter.write(genomes[j] + "-");
            } else {
                genomeWriter.write(genomes[j] + ";");
            }
        }
    }

    /**
     * converts the array txt file to an int[].
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
        String []strNums = br.readLine().split(",");
        int size = strNums.length;
        int [] nodeArray = new int[size];
        for (int i = 0; i < size; i++) {
            nodeArray[i] = Integer.parseInt(strNums[i]);

        }
        return nodeArray;
    }

    public int[] getParentArray() throws IOException {
        return read(true);
    }

    public int[] getChildArray() throws IOException {
        return read(false);
    }

    /**
     * puts the genomes in maps with indices.
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
            this.genomesMap.put(nameGenome, i);
            this.reversedGenomesMap.put(i, nameGenome);
        }
    }

    public HashMap<String, Integer> getAllGenomesMap() {
        return genomesMap;
    }

    /**
     *
     * @return The map with the genomes.
     */
    public HashMap<Integer, String> getAllGenomesMapReversed() {
        return reversedGenomesMap;
    }

}
