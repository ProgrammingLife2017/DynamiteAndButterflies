package parser;

import graph.Annotation;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by lex_b on 12/06/2017.
 */
public class GffParser {
    private String filePath;

    /**
     * Constructor.
     * @param absolutePath The path location of the file.
     */
    public GffParser(String absolutePath) {
        filePath = absolutePath;
    }

    /**
     *
     * @return an arrayList with the Annotations.
     * @throws IOException If it goes wrong.
     */
    public ArrayList<Annotation> parseGff() throws IOException {
        ArrayList<Annotation> annotationList = new ArrayList<>();
        InputStream in = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split("\t");
            ArrayList<String> info = new ArrayList<>();
            String[] infoArray = data[8].split(";");
            for(int i = 0; i < infoArray.length; i++) {
                info.add(infoArray[i]);
            }
            int start = Integer.parseInt(data[3]);
            int end = Integer.parseInt(data[4]);
            Annotation anno = new Annotation(data[0], start, end, info);
            annotationList.add(anno);
        }
        return annotationList;
    }
}