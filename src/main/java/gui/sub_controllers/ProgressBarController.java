package gui.sub_controllers;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

/**
 * Created by Jip on 22-5-2017.
 */
public class ProgressBarController {

    private ProgressBar pb;

    /**
     * Constructor.
     * @param bar The progressbar.
     */
    public ProgressBarController(ProgressBar bar) {
        pb = bar;
    }

    /**
     * The run method on open File.
     */
    void run() {
        Platform.runLater(new Runnable() {
            public void run() {
                pb.setProgress(-1.0);
            }
        });
        pb.setVisible(true);
    }

    /**
     * The method to disable the progressbar after load.
     */
    void done() {
        Platform.runLater(new Runnable() {
            public void run() {
                pb.setProgress(1.0);
            }
        });
        pb.setVisible(false);
    }
}
