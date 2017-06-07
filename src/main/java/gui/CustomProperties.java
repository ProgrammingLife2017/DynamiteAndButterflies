package gui;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Jip on 7-6-2017.
 *
 * Custom properties class that extends a java api properties
 * Create my own two methods to ensure they use our file.
 */
public class CustomProperties extends java.util.Properties {

    private static final String PROPERY_FILENAME = "properties.txt";

    /**
     * Constructor of the customProperties to put and get from.
     */
    public CustomProperties() {
        super();
    }

    /**
     * Will update the current CustomProperties object.
     * Does so by loading the appropriate file.
     */
    public void updateProperties() {
        try {
            FileReader fileReader = new FileReader(PROPERY_FILENAME);
            this.load(fileReader);
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will save the current CustomProperties.
     * Does so by storing it to the appropriate file
     */
    public void saveProperties() {
        try {
            FileWriter fileWriter = new FileWriter(PROPERY_FILENAME);
            this.store(fileWriter, "Property files for Dynamite and Butterflies");
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
