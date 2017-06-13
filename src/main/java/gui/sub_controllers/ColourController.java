package gui.sub_controllers;

import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by Jip on 8-6-2017.
 */
public class ColourController {

    private int[] selectedGenomes;
    private boolean largeSelection;

    private int LOWER;
    private int LOWMIDDLE;
    private int HIGHMIDDLE;
    private int HIGHER;

    private int lowMiddleCounter;

    public ColourController(int[] allSelectedGenomes) {
        selectedGenomes = allSelectedGenomes;
        largeSelection = false;
        lowMiddleCounter = 0;
        initialize();
    }

    private void initialize() {
        int size = selectedGenomes.length;

        if (size == 0) {
            return;
        }

        if (size >= 7) {
            largeSelection = true;
            LOWER = size / 4;
            LOWMIDDLE = LOWER * 2;
            HIGHMIDDLE = LOWER + LOWMIDDLE;
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
                return Color.color(0.6078, 0.2588, 0);
            default:
                return getBase();
        }
    }

//    public Color getLower() {
//        lowerCounter++;
//        switch (lowerCounter) {
//            case 1:
//                return Color.DODGERBLUE;
//            case 2:
//                return Color.CADETBLUE;
//            case 3:
//                return Color.CORNFLOWERBLUE;
//            case 4:
//                return Color.ALICEBLUE;
//            case 5:
//                lowerCounter = 0;
//                return getLower();
//            default:
//                return Color.WHITE;
//        }
//    }

    public Color getLowMiddle() {
        lowMiddleCounter++;
        switch (lowMiddleCounter) {
            case 1:
                lowMiddleCounter++;
                return Color.PALEVIOLETRED;
            case 2:
                lowMiddleCounter++;
                return Color.INDIANRED;
            case 3:
                lowMiddleCounter++;
                return Color.MEDIUMVIOLETRED;
            case 4:
                lowMiddleCounter++;
                return Color.ORANGERED;
            case 5:
                lowMiddleCounter = 0;
                return getLowMiddle();
            default:
                return getBase();
        }
    }

//    public Color getHighMiddle() {
//        highMiddleCounter++;
//        switch (highMiddleCounter) {
//            case 1:
//                return Color.YELLOWGREEN;
//            case 2:
//                return Color.GREENYELLOW;
//            case 3:
//                return Color.LIGHTGOLDENRODYELLOW;
//            case 4:
//                return Color.LIGHTYELLOW;
//            case 5:
//                highMiddleCounter = 0;
//                return getHighMiddle();
//            default:
//                return Color.WHITE;
//        }
//    }

//    public Color getHigher() {
//        highCounter++;
//        switch (highCounter) {
//            case 1:
//                return Color.DARKSEAGREEN;
//            case 2:
//                return Color.FORESTGREEN;
//            case 3:
//                return Color.LAWNGREEN;
//            case 4:
//                return Color.MEDIUMSEAGREEN;
//            case 5:
//                highCounter = 0;
//                return getHigher();
//            default:
//                return Color.WHITE;
//        }
//    }

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
            res.add(getLowMiddle());
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
