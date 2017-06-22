package gui.sub_controllers;

import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by Jip on 8-6-2017.
 * <p>
 * The class handles the colour of the nodes.
 * <p>
 * It takes a int[] representing the selected genomes.
 * If it's empty it will always return the base.
 * If it is small it will find the correct colours for the relevant genomes.
 * If it is large it will choose a colour red wich is intense or not based on overlap.
 * <p>
 * I suppress these warnings because of the large amount of magic number determining colours.
 */
@SuppressWarnings("MagicNumber")
public class ColourController {

    private int[] selectedGenomes;
    private boolean rainbowView;

    private int lowerPart;
    private int middlePart;
    private int higherPart;

    //The amount of splits we make when the selection is large
    private static final int SPLIT_WHEN_LARGE = 4;

    /**
     * Constructor of the colourController.
     *
     * @param allSelectedGenomes A int[] with all the selected genomes.
     */
    public ColourController(int[] allSelectedGenomes, boolean rainbowViewArg) {
        selectedGenomes = allSelectedGenomes;
        this.rainbowView = rainbowViewArg;
        initialize();
    }

    /**
     * Initializes the colourController.
     */
    private void initialize() {
        int size = selectedGenomes.length;

        if (size == 0) {
            return;
        }

        lowerPart = size / SPLIT_WHEN_LARGE;
        middlePart = lowerPart * 2;
        higherPart = lowerPart + middlePart;
    }

    /**
     * Returns the position of the genome in the checkSet.
     * <p>
     * The position from checkSet is really handy for the colours because
     * the getSingle colours is based on the index of selected genomes.
     * But because that is only the single ones you can only handily use this
     * for small selections.
     *
     * @param checkSet the int[] we will be looking through
     * @param genome   the int we are looking for in the checkset
     * @return returns the position of genome in the checkset.
     */
    public int containsPos(int[] checkSet, int genome) {
        for (int i = 0; i < checkSet.length; i++) {
            int check = checkSet[i];
            if (check == genome) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets a colour assigned to the selected genome.
     *
     * @param positionInSelection an integer representing it's position in the selectedGenomes array
     * @return A color.
     */
    public Color getSingle(int positionInSelection) {
        double hue = (360 / selectedGenomes.length) * positionInSelection;
        double brightness = 0.8 + (0.2 / selectedGenomes.length) * positionInSelection;
        return Color.hsb(hue, 1, brightness);
    }

    /**
     * With a large collection we only have difference in intensity of colours.
     * That is then based on the length of the genomes selected.
     *
     * @param length the length of the selected genomes.
     * @return a color.
     */
    private Color notRainbowView(int length) {
        if (length == 0) {
            return getBaseNodeColour();
        } else if (length < lowerPart) {
            return Color.color(0.9608, 0.8235, 0.8235);
        } else if (length < middlePart) {
            return Color.color(0.9216, 0.6157, 0.6157);
        } else if (length < higherPart) {
            return Color.color(0.8824, 0.4039, 0.4039);
        } else {
            return Color.color(0.8431, 0.2196, 0.2196);
        }
    }

    /**
     * Returns the base colour.
     *
     * @return the base colour.
     */
    public Color getBaseNodeColour() {
        return Color.gray(0.5098);
    }

    /**
     * This method gets an array of the different colours the node should be.
     *
     * @param genomes The genomes in the node.
     * @return The list of colours the node should be.
     */
    public ArrayList<Color> getNodeColours(int[] genomes) {
        ArrayList<Color> res = new ArrayList<Color>();
        //If there is no selection, it should only be the base colour.
        if (selectedGenomes.length == 0) {
            res.add(getBaseNodeColour());
            return res;
        }

        // In rainbowView we assign a colour to each genome
        if (rainbowView) {
            res = rainbowViewColours(genomes);
        } else {
            // Else we choose a colour Red.
            int length = 0;
            for (int genome : genomes) {
                if (contains(selectedGenomes, genome)) {
                    length++;
                }
            }
            res.add(notRainbowView(length));
            return res;
        }

        if (res.isEmpty()) {
            res.add(getBaseNodeColour());
        }

        return res;
    }

    /**
     * A simple contains method.
     *
     * @param checkSet The set to check if it contains the genome.
     * @param genome   The genome to see if it is in the check set.
     * @return a boolean true if it is in the set or false if it is not.
     */
    public boolean contains(int[] checkSet, int genome) {
        for (int check : checkSet) {
            if (check == genome) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method gets an array of the different colours the node should be.
     *
     * @param genomes The genomes in the node.
     * @return The list of colours the node should be.
     */
    public ArrayList<Color> getEdgeColours(int[] genomes) {
        ArrayList<Color> res = new ArrayList<Color>();
        //If there is no selection, it should only be the base colour.
        if (selectedGenomes.length == 0 || !rainbowView) {
            res.add(getBaseEdgeColour());
            return res;
        }

        // In rainbowView we assign a colour to each genome
        if (rainbowView) {
            res = rainbowViewColours(genomes);
        }
        if (res.isEmpty()) {
            res.add(getBaseEdgeColour());
        }

        return res;
    }

    private ArrayList<Color> rainbowViewColours(int[] genomes) {
        ArrayList<Color> res = new ArrayList<>();
        for (int genome : genomes) {
            int check = containsPos(selectedGenomes, genome);
            if (check != -1) {
                res.add(getSingle(check));
            }
        }
        return res;
    }

    public Color getBaseEdgeColour() {
        return Color.BLACK;
    }
}
