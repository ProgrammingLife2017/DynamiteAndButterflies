package gui.sub_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by Jip on 29-5-2017.
 *
 * This will handle the GUI of the tutorial.
 */
public class TutorialController {
    @FXML
    private Text tutorialExplanation;
    @FXML
    private ImageView imagePlace;
    @FXML
    private Label tutorialHeader;
    @FXML
    private Label tutorialTitle;

    @FXML
    private Button nextBut;
    @FXML
    private Button prevBut;
    @FXML
    private Button homeBut;
    @FXML
    private Button travBut;
    @FXML
    private Button bookBut;
    @FXML
    private Button zoomBut;
    @FXML
    private Button infoBut;
    @FXML
    private Button debugBut;

    private int counter = 0;

    private static final int HOME = 1;
    private static final int TRAV = 2;
    private static final int BOOK = 3;
    private static final int ZOOM = 4;
    private static final int INFO = 5;
    private static final int DEBUG = 6;
    private static final int FIN = 7;

    /**
     * Handles the button press of next.
     */
    public void nextClicked() {
        if (nextBut.getText().equals("End")) {
            Stage stage = (Stage) nextBut.getScene().getWindow();
            stage.close();
        }
        counter++;
        getNewView(counter);
    }

    /**
     * Handles the button press of prev.
     */
    public void prevClicked() {
        if (nextBut.getText().equals("End")) {
            nextBut.setText("Next");
        }
        counter--;
        getNewView(counter);
    }

    /**
     * Gets the new view to be shown.
     * @param stage the integer representing which.
     */
    private void getNewView(int stage) {
        switch (stage) {
            case HOME: homePage(); homeBut.setVisible(true); break;
            case TRAV: traversingGraph(); travBut.setVisible(true); break;
            case BOOK: bookmarks(); bookBut.setVisible(true); break;
            case ZOOM: zooming(); zoomBut.setVisible(true); break;
            case INFO: information(); infoBut.setVisible(true); break;
            case DEBUG: debugging(); debugBut.setVisible(true); break;
            case FIN: finished(); break;
            default: counter = 1; getNewView(counter);
        }
    }

    /**
     * Shows the finished stage.
     */
    private void finished() {
        tutorialTitle.setText("Thank you for walking through the tutorial");
        tutorialHeader.setText("");
        imagePlace.setImage(new Image("/tutorial/fin.jpg"));
        tutorialExplanation.setText("Use the buttons below to"
                                    + " navigate through the different topics.");
        nextBut.setText("End");
    }

    /**
     * Shows the debugging stage.
     */
    private void debugging() {
        tutorialHeader.setText("Debugging");
        imagePlace.setImage(new Image("/tutorial/debug.PNG"));
        tutorialExplanation.setText("You can switch from information into a debugging "
                                    + "console using this button. Here you can see in "
                                    + "depth what exactly is going on in the application.");
    }

    /**
     * Shows the information stage.
     */
    private void information() {
        tutorialHeader.setText("Graph information");
        imagePlace.setImage(new Image("/tutorial/info.PNG"));
        tutorialExplanation.setText("When loading the graph you can see the total number of "
                                    + "edges and nodes in the bottom left. The sequence you "
                                    + "see is either that of the centre node specified "
                                    + "or the node that you have clicked on.");
    }

    /**
     * Shows the zooming stage.
     */
    private void zooming() {
        tutorialHeader.setText("Zooming in and out of the graph");
        imagePlace.setImage(new Image("/tutorial/zooming.PNG"));
        tutorialExplanation.setText("You can zoom into the graph using the scroll wheel on your "
                                    + "mouse. Beware though, you can't zoom in or out too far or "
                                    + "we will stop you.");
    }

    /**
     * Shows the bookmarks stage.
     */
    private void bookmarks() {
        tutorialHeader.setText("Saving and using bookmarks");
        imagePlace.setImage(new Image("/tutorial/bookmark.PNG"));
        tutorialExplanation.setText("When in a view you wish to view later, you can save that "
                                    + "position with a bookmark. Press on the bookmark at any "
                                    + "time to return to that position, even in a new session. "
                                    + "All bookmarks are file specific, so don't be scared of "
                                    + "overwriting old ones.");
    }

    /**
     * Shows the traversing graph stage.
     */
    private void traversingGraph() {
        tutorialHeader.setText("Traversing the loaded graph");
        imagePlace.setImage(new Image("/tutorial/traverse.PNG"));
        tutorialExplanation.setText("You can traverse the graph using the buttons in the top left. "
                                    + "The top text field determines the centre node. "
                                    + "The other the radius of nodes you wish to see."
                                    + "Press the Go To This Node button to go to your "
                                    + "determined view.");
    }

    /**
     * Shows the home stage.
     */
    private void homePage() {
        tutorialHeader.setText("The home screen");
        imagePlace.setImage(new Image("/tutorial/home.PNG"));
        tutorialExplanation.setText("The home screen is the first view you are met with."
                                    + "To get started you can either choose a file to load "
                                    + "with the file chooser or choose a file "
                                    + "from your recent files.");
    }

    /**
     * Handles pressing the shortcut to home.
     */
    public void homeClick() {
        counter = HOME;
        skipped();
        homePage();
    }

    /**
     * Handles pressing the shortcut to traversing graph.
     */
    public void travClick() {
        counter = TRAV;
        skipped();
        traversingGraph();
    }

    /**
     * Handles pressing the shortcut to bookmarks.
     */
    public void bookClick() {
        counter = BOOK;
        skipped();
        bookmarks();
    }

    /**
     * Handles pressing the shortcut to zooming.
     */
    public void zoomClick() {
        counter = ZOOM;
        skipped();
        zooming();
    }

    /**
     * Handles pressing the shortcut to information.
     */
    public void infoClick() {
        counter = INFO;
        skipped();
        information();
    }

    /**
     * Handles pressing the shortcut to debugging.
     */
    public void debugClick() {
        counter = DEBUG;
        skipped();
        debugging();
    }

    /**
     * Some functionality when skipping to a stage.
     */
    public void skipped() {
        nextBut.setText("Next");
    }
}
