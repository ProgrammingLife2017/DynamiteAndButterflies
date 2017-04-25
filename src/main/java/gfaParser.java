import java.io.*;
import java.util.ArrayList;


/**
 * Created by marc on 25-4-17.
 */
public class gfaParser {


    private void parse(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            String header1 = line.split("H")[1];
            line = br.readLine();
            String header2 = line.split("H")[1];
            while ((line = br.readLine()) != null) {

            }
        }
        catch(IOException e) {
                e.printStackTrace();
            }
    }
}
