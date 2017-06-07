package gui;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Jip on 7-6-2017.
 */
public class JipProps extends Properties {

    public JipProps() {
        super();
    }

    public Properties updateProperties() {
        try {
            FileReader fileReader = new FileReader("properties.txt");
            this.load(fileReader);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return this;
    }

    public void saveProperties() {
        try {
            FileWriter fileWriter = new FileWriter("properties.txt");
            this.store(fileWriter, "Property files for Dynamite and Butterflies");
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
