package gui;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Jip on 7-6-2017.
 * <p>
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
            try {
                this.load(fileReader);
            } catch (IOException e) {
                System.err.println("Something went wrong while reading properties.");
            }
            try {
                fileReader.close();
            } catch (IOException e) {
                System.err.println("Something went wrong with closing the fileReader");
            }
        } catch (FileNotFoundException e) {
            System.err.println("Properties files does not exist.");
        }
    }

    /**
     * Will save the current CustomProperties.
     * Does so by storing it to the appropriate file
     */
    public void saveProperties() {
        try {
            FileWriter fileWriter = new FileWriter(PROPERY_FILENAME);
            try {
                this.store(fileWriter, "Property files for Dynamite and Butterflies");
            } catch (IOException e) {
                System.err.println("Something went wrong with writing properties");
            }
            try {
                fileWriter.close();
            } catch (IOException e) {
                System.err.println("Something went wrong with closing the fileWriter");
            }
        } catch (IOException e) {
            System.err.println("Something went wrong while making fileWriter.");
        }
    }

}
