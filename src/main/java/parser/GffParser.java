package parser;

import gui.CustomProperties;
import gui.DrawableCanvas;
import structures.Annotation;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by lex_b on 12/06/2017.
 */
public class GffParser {
    private String filePath;
    private static final int BUCKET_SIZE = 20000;

    /**
     * Constructor.
     *
     * @param absolutePath The path location of the file.
     */
    public GffParser(String absolutePath) {
        filePath = absolutePath;
    }

    /**
     * @return an arrayList with the Annotations.
     * @throws IOException If it goes wrong.
     */
    public HashMap<Integer, HashSet<Annotation>> parseGff() throws IOException {
        InputStream in = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line;
        //Intialize the genome that the annotation wants to be on at 0
        int suggestionGenomeOfAnnotation = 0;
        CustomProperties properties = new CustomProperties();
        properties.updateProperties();
        int maxCor = Integer.parseInt(properties.getProperty(
                DrawableCanvas.getInstance().getParser().getPartPath() + "Max-Cor", "-1"));
        HashMap<Integer, HashSet<Annotation>> buckets = initializeBucketArray(maxCor);
        while ((line = br.readLine()) != null) {
            int annotationIdentifier = 0;
            String[] data = line.split("\t");

            String[] nameGenomes = data[0].split("\\.");

            String nameGenome = "";
            for (int i = 0; i < nameGenomes.length - 1; i++) {
                nameGenome = nameGenome.concat(nameGenomes[i]);
            }
            //Here we check if that genome exists/is real.
            Integer nameGenomeID = DrawableCanvas.getInstance().getAllGenomes().get(nameGenome);
            //If it is an actual genome we want to suggest it
            if (nameGenomeID != null) {
                suggestionGenomeOfAnnotation = nameGenomeID;
            }

            String info = data[8].replace(";", "\t");
            int start = Integer.parseInt(data[3]);
            int end = Integer.parseInt(data[4]);
            Annotation anno = new Annotation(annotationIdentifier, start, end, info);
            annotationIdentifier++;
            int startBucket = (start / BUCKET_SIZE);
            int endBucket = (end / BUCKET_SIZE);


            for (int i=startBucket; i <= endBucket; i++) {
                HashSet<Annotation> set = buckets.get(i);
                if (set == null) {
                    set = new HashSet<>();
                }
                set.add(anno);
            buckets.put(startBucket, set);
            }
        }
        DrawableCanvas.getInstance().setAnnotationGenome(suggestionGenomeOfAnnotation);
        return buckets;
    }

    public HashMap<Integer, HashSet<Annotation>> initializeBucketArray(int maxCor) {
        int size = (maxCor / BUCKET_SIZE);

        return new HashMap<>(size);
    }
}
