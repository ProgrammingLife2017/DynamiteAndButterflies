package gui.sub_controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * Created by Jip on 29-5-2017.
 *
 * This will handle the GUI of the tutorial.
 */
public class TutorialController {
    public Text tutorialExplanation;
    public ImageView imagePlace;
    public Label tutorialHeader;
    public Label tutorialTitle;


    public Button nextBut;
    public Button prevBut;
    public Button homeBut;
    public Button travBut;
    public Button bookBut;
    public Button zoomBut;
    public Button infoBut;
    public Button debugBut;

    private int counter = 0;

    public void nextClicked() {
        counter++;
        getNewView(counter);
    }

    public void prevClicked() {
        counter--;
        getNewView(counter);
    }

    private void getNewView(int stage) {
        switch (stage) {
            case 1: homePage(); homeBut.setVisible(true); break;
            case 2: traversingGraph(); travBut.setVisible(true); break;
            case 3: bookmarks(); bookBut.setVisible(true); break;
            case 4: zooming(); zoomBut.setVisible(true); break;
            case 5: information(); infoBut.setVisible(true); break;
            case 6: debugging(); debugBut.setVisible(true); break;
            case 7: finished(); break;
            default: counter = 1; getNewView(counter);
        }
    }

    private void finished() {
        tutorialTitle.setText("Thank you for walking through the tutorial");
        tutorialHeader.setText("");
        //imagePlace.setImage();
        tutorialExplanation.setText("Use the buttons below to navigate through the different topics.");
    }

    private void debugging() {
        tutorialHeader.setText("Debugging");
        //imagePlace.setImage();
        tutorialExplanation.setText("You can switch from information into a debugging console using this button. " +
                                    "Here you can see in depth what exactly is going on in the application.");
    }

    private void information() {
        tutorialHeader.setText("Graph information");
        //imagePlace.setImage();
        tutorialExplanation.setText("When loading the graph you can see the total number of edges and nodes in the bottom left. " +
                                    "The sequence you see is either that of the centre node specified " +
                                    "or the node that you have clicked on.");
    }

    private void zooming() {
        tutorialHeader.setText("Zooming in and out of the graph");
        //imagePlace.setImage();
        tutorialExplanation.setText("You can zoom into the graph using the scroll wheel on your mouse. " +
                                    "Beware though, you can't zoom in or out too far or we will stop you.");
    }

    private void bookmarks() {
        tutorialHeader.setText("Saving and using bookmarks");
        //imagePlace.setImage();
        tutorialExplanation.setText("When in a view you wish to view later, you can save that position with a bookmark. " +
                                    "Press on the bookmark at any time to return to that position, even in a new session. " +
                                    "All bookmarks are file specific, so don't be scared of overwriting old ones.");
    }

    private void traversingGraph() {
        tutorialHeader.setText("Traversing the loaded graph");
        //imagePlace.setImage();
        tutorialExplanation.setText("You can traverse the graph using the buttons in the top left. " +
                                    "The top text field determines the centre node. " +
                                    "The other the radius of nodes you wish to see." +
                                    "Press the Go To This Node button to go to your determined view.");
    }

    public void homePage() {
        tutorialHeader.setText("The home screen");
        //imagePlace.setImage();
        tutorialExplanation.setText("The home screen is the first view you are met with." +
                                    "To get started you can either choose a file to load with the file chooser" +
                                    " or choose a file from your recent files.");


    }

    public void homeClick() {
        counter = 1;
        homePage();
    }

    public void travClick() {
        counter = 2;
        traversingGraph();
    }

    public void bookClick() {
        counter = 3;
        bookmarks();
    }

    public void zoomClick() {
        counter = 4;
        zooming();
    }

    public void infoClick() {
        counter = 5;
        information();
    }

    public void debugClick() {
        counter = 6;
        debugging();
    }
}
