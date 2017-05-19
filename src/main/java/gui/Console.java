package gui;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by TUDelft SID on 18-5-2017.
 */
public class Console extends OutputStream {
    private TextArea console;

    public Console(TextArea console) {
        this.console = console;
    }

    public void appendText(final String valueOf) {
        Platform.runLater(new Runnable() {
            public void run() {
                console.appendText(valueOf);
            }
        });
    }

    public void write(int b) throws IOException {
        appendText(String.valueOf((char)b));
    }
}
