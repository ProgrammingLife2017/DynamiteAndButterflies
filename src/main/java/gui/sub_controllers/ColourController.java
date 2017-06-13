package gui.sub_controllers;

import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by Jip on 8-6-2017.
 */
public class ColourController {

    private int[] selectedGenomes;
    private boolean largeSelection;

    private int lowerPart;
    private int middlePart;
    private int higherPart;

    public ColourController(int[] allSelectedGenomes) {
        selectedGenomes = allSelectedGenomes;
        largeSelection = false;
        initialize();
    }

    private void initialize() {
        int size = selectedGenomes.length;

        if (size == 0) {
            return;
        }

        if (size >= 7) {
            largeSelection = true;
            lowerPart = size / 4;
            middlePart = lowerPart * 2;
            higherPart = lowerPart + middlePart;
        }
    }

    private int containsPos(int[] checkSet, int genome) {
        for (int i = 0; i < checkSet.length; i++) {
            int check = checkSet[i];
            if (check == genome) {
                return i;
            }
        }
        return -1;
    }

    public Color getSingle(int combo) {
        switch (combo) {
            case 0:
                return Color.color(0.8471, 0, 0);
            case 1:
                return Color.color(0, 0, 0.8471);
            case 2:
                return Color.color(0.8471, 0.8471, 0);
            case 3:
                return Color.color(0.8471, 0, 0.8471);
            case 4:
                return Color.color(1, 0.6039, 0);
            case 5:
                return Color.color(0, 0.8471, 0);
            case 6:
                return Color.color(0, 0.8471, 0.8471);
            default:
                return getBase();
        }
    }

    private Color getLargeSelColour(int length) {
        if (length < lowerPart) {
            return Color.color(0.9608, 0.8235, 0.8235);
        } else if (length < middlePart) {
            return Color.color(0.9216, 0.6157, 0.6157);
        } else if (length < higherPart) {
            return Color.color(0.8824, 0.4039, 0.4039);
        } else {
            return Color.color(0.8431, 0.2196, 0.2196);
        }
    }

    public Color getBase() {
        return Color.gray(0.5098);
    }

    public ArrayList<Color> getColors(int[] genomes) {
        ArrayList<Color> res = new ArrayList<Color>();

        if (selectedGenomes.length == 0) {
            res.add(getBase());
            return res;
        }

        if (largeSelection) {
            res.add(getLargeSelColour(genomes.length));
            return res;
        }

        for (int i = 0; i < genomes.length; i++) {
            int check = containsPos(selectedGenomes, genomes[i]);
            if (check != -1) {
                res.add(getSingle(check));
            }
        }
        if (res.isEmpty()) {
            res.add(getBase());
        }
        return res;
    }
}
