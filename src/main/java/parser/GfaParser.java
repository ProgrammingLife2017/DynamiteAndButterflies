package parser;

import gui.CustomProperties;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.regex.Pattern;

/**
 * This class contains a parser to parse a .gfa file into our data structure.
 */
public class GfaParser extends Observable implements Runnable {
    private String header1;
    private String header2;
    private HTreeMap<Long, String> sequenceMap;
    private String filePath;
    private String partPath;
    private CustomProperties properties = new CustomProperties();

    Boolean indexedGfaFile = false;
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
        } else {
            properties.setProperty(partPath, "false");
            properties.saveProperties();
            sequenceMap = db.hashMap(partPath + ".sequence.db").
                                    keySerializer(Serializer.LONG).
                                    valueSerializer(Serializer.STRING).createOrOpen();
            parseSpecific(filePath);
        }
        this.setChanged();
        this.notifyObservers(1);
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
        BufferedWriter parentWriter =
                new BufferedWriter(new FileWriter(partPath + "parentArray.txt"));
        BufferedWriter childWriter =
                new BufferedWriter(new FileWriter(partPath + "childArray.txt"));
        BufferedWriter genomeWriter =
                new BufferedWriter(new FileWriter(partPath + "genomes.txt"));
        InputStream in = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line;
        HashMap<String, Integer> genome = new HashMap<String, Integer>();
        int sizeOfFile = 0;
        while ((line = br.readLine()) != null) {
            if(line.startsWith("H")) {
                String header = line.split("\t")[1];
                if (header.startsWith("ORI:Z:")) {
                    String allGenomes = header.split(":")[2];
                    BufferedWriter genomesWriter =
                            new BufferedWriter(new FileWriter(partPath + "allGenomes.txt"));
                    genomesWriter.write(allGenomes + ",");
                    System.out.println(allGenomes);
                    genomesWriter.flush();
                    genomesWriter.close();
                    genome = getAllGenomesMap();
                }
                else if (header.startsWith("BUILD:Z:VCF2GRAPH")) {
                    indexedGfaFile = true;
                }
            }
            if (line.startsWith("S")) {
                String[] data = line.split(("\t"));
                int id = Integer.parseInt(data[1]);
                for (int i = 0; i < data.length; i++) {
                    if (data[i].startsWith("ORI:Z:")) {
                        String[] genomes = data[i].split(":")[2].split(";");
                        for (int j = 0; j < genomes.length; j++) {
                            genomeWriter.flush();
                            if(indexedGfaFile) {
                                if (j == genomes.length - 1) {
                                    genomeWriter.write(genomes[j].toString());
                                    genomeWriter.newLine();
                                } else {
                                    genomeWriter.write(genomes[j] + ";");
                                }
                            } else {
                                if (j == genomes.length - 1) {
                                    genomeWriter.write(genome.get(genomes[j]).toString());
                                    genomeWriter.newLine();
                                } else {
                                    genomeWriter.write(genome.get(genomes[j]) + ";");
                                }
                            }

                        }
                    }
                }


                sequenceMap.put((long) (id), data[2]);
            } else if (line.startsWith("L")) {
                String[] edgeDataString = line.split("\t");
                int parentId = Integer.parseInt(edgeDataString[1]);
                int childId = Integer.parseInt(edgeDataString[3]);
               parentWriter.write(parentId + ",");
               childWriter.write(childId + ",");
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
        properties.saveProperties();
    }

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
        int size = Integer.parseInt(properties.getProperty(partPath + "childArray.txtsize", "-1"));
        if (size == -1) {
            throw new java.lang.RuntimeException("Size not in preferences file");
        }
        int [] nodeArray = new int[size];
        for (int i = 0; i < strNums.length; i++) {
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

    public HashMap<String, Integer> getAllGenomesMap() throws IOException {
        return readAllGenomeFile();
    }

    private int[] readIds() throws IOException {
        InputStream in = new FileInputStream(System.getProperty("user.dir")
                + System.getProperty("file.separator") + partPath + "ids.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String [] strNums = br.readLine().split(",");
        int[] ids = new int[strNums.length];
        for (int i = 0; i < strNums.length; i++) {
            ids[i] = Integer.parseInt(strNums[i]);
        }
        return ids;
    }

    private HashMap<String, Integer> readAllGenomeFile() throws IOException {
        InputStream in = new FileInputStream(System.getProperty("user.dir")
                + System.getProperty("file.separator") + partPath + "allGenomes.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String[] strNums = br.readLine().split(";");
        HashMap<String, Integer> genomesMap = new HashMap<String, Integer>();
        for (int i = 0; i < strNums.length; i++) {
            genomesMap.put(strNums[i], i);
        }
        return genomesMap;
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
     *
     * @return The map with the genomes.
     * @throws IOException For reading a file.
     */
    public HashMap<Integer, String> getAllGenomesMapReversed() throws  IOException {
        InputStream in = new FileInputStream(System.getProperty("user.dir")
                + System.getProperty("file.separator") + partPath + "allGenomes.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String[] strNums = br.readLine().split(";");
        HashMap<Integer, String> genomesMap = new HashMap<Integer, String>();
        for (int i = 0; i < strNums.length; i++) {
            genomesMap.put(i, strNums[i]);
        }
        return genomesMap;
    }
}
