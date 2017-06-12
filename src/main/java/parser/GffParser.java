package parser;

import java.util.regex.Pattern;

/**
 * Created by lex_b on 12/06/2017.
 */
public class GffParser {
    private String filePath;
    private String partPath;

    /**
     * Constructor.
     * @param absolutePath The path location of the file.
     */
    public GffParser(String absolutePath) {
        filePath = absolutePath;
    }

    /**
     *
     */
    public void parseGff() {
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] partPaths = filePath.split(pattern);
        partPath = partPaths[partPaths.length - 1];
    }
}
