package gui.subControllers;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

/**
 * Created by Jip on 22-5-2017.
 */
public class ProgressBarController extends Thread {

    private ProgressBar pb;

    public ProgressBarController(ProgressBar bar) {
        pb = bar;
    }

    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            public void run() {
                pb.setProgress(-1.0);
            }
        });
    }

    public void done() {
        pb.setProgress(1.0);
    }
}
