package gui;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by TUDelft SID on 18-5-2017.
 */
public class Console extends OutputStream {
    private final TextArea console;

    /**
     * Constructor of the console.
     * @param console the textArea that is the console
     */
    public Console(TextArea console) {
        this.console = console;
    }

    /**
     * Appends text to the Console.
     * @param valueOf The string to be appended.
     */
    public void appendText(final String valueOf) {
        Platform.runLater(new Runnable() {
            public void run() {
                console.appendText(valueOf);
            }
        });
    }

    /**
     * Writes an int to the console.
     * @param b the int to be written
     * @throws IOException throws exception when console not found
     */
    public void write(int text) throws IOException {
        appendText(String.valueOf((char) text));
    }
}
