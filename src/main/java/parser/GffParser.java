package parser;

import graph.Annotation;
import gui.DrawableCanvas;

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
            String nameGenome = data[0].split("\\.")[0];
            String info = data[8].replace(";", "\t");
            int start = Integer.parseInt(data[3]);
            int end = Integer.parseInt(data[4]);
            Annotation anno = new Annotation(DrawableCanvas.getInstance().getAllGenomes().get(nameGenome), start, end, info);
            annotationList.add(anno);
        }
        in.close();
        br.close();
        return annotationList;
    }
}
