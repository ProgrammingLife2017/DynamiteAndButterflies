package gui.sub_controllers;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

/**
 * Controller for the progressBar.
 */
public class ProgressBarController {

    private ProgressBar progressBar;

    /**
     * Constructor.
     * @param bar The progressbar.
     */
    public ProgressBarController(ProgressBar bar) {
        progressBar = bar;
    }

    /**
     * The run method on open File.
     */
    void run() {
        Platform.runLater(new Runnable() {
            public void run() {
                progressBar.setProgress(-1.0);
            }
        });
        progressBar.setVisible(true);
    }

    /**
     * The method to disable the progressbar after load.
     */
    void done() {
        Platform.runLater(new Runnable() {
            public void run() {
                progressBar.setProgress(1.0);
            }
        });
        progressBar.setVisible(false);
    }
}
