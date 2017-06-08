package gui.sub_controllers;

import javafx.scene.paint.Color;

/**
 * Created by Jip on 8-6-2017.
 */
public class ColourController {

    private int[] selectedGenomes;
    private boolean largeSelection;
    private boolean initialized;

//    private int[] LOWER;
//    private int[] LOWMIDDLE;
//    private int[] HIGHMIDDLE;
//    private int[] HIGHER;

    private Integer SINGLE1 = null;
    private Integer SINGLE2 = null;
    private Integer SINGLE3 = null;
    private int[] SINGLE4;
    private int[] SINGLE5;
    private int[] SINGLE6;
    private int[] SINGLE7;
    private int lowMiddleCounter;

    public ColourController(int[] allSelectedGenomes) {
        selectedGenomes = allSelectedGenomes;
        largeSelection = false;
        initialized = false;
        lowMiddleCounter = 0;
        initialize();
    }

    private void initialize() {
        if (selectedGenomes.length == 0) {
            return;
        }
        if (selectedGenomes.length > 3) {
            largeSelection = true;
        } else {
            initialized = true;
            if (selectedGenomes.length == 1) {
                SINGLE1 = selectedGenomes[0];
            } else if (selectedGenomes.length == 2) {
                SINGLE1 = selectedGenomes[0];
                SINGLE2 = selectedGenomes[1];

                SINGLE4 = new int[2];
                SINGLE4[0] = selectedGenomes[0];
                SINGLE4[1] = selectedGenomes[1];
            } else if (selectedGenomes.length == 3) {
                SINGLE1 = selectedGenomes[0];
                SINGLE2 = selectedGenomes[1];
                SINGLE3 = selectedGenomes[2];

                SINGLE4 = new int[2];
                SINGLE4[0] = selectedGenomes[0];
                SINGLE4[1] = selectedGenomes[1];

                SINGLE5 = new int[2];
                SINGLE5[0] = selectedGenomes[0];
                SINGLE5[1] = selectedGenomes[2];

                SINGLE6 = new int[2];
                SINGLE6[0] = selectedGenomes[1];
                SINGLE6[1] = selectedGenomes[2];

                SINGLE7 = new int[3];
                SINGLE7[0] = selectedGenomes[0];
                SINGLE7[1] = selectedGenomes[1];
                SINGLE7[2] = selectedGenomes[2];
            }
        }
    }

    public Color getColor(int[] genomesThroughMe) {
        if (largeSelection) {
            return getLowMiddle();
        }
        if (selectedGenomes.length < 0) {
            return getBase();
        }

        if (initialized) {
            if (allInclusive(genomesThroughMe, SINGLE7)) {
                return getSingle(7);
            } else if (allInclusive(genomesThroughMe, SINGLE4)) {
                return getSingle(4);
            } else if (allInclusive(genomesThroughMe, SINGLE5)) {
                return getSingle(5);
            } else if (allInclusive(genomesThroughMe, SINGLE6)) {
                return getSingle(6);
            } else if (contains(genomesThroughMe, SINGLE3)) {
                return getSingle(3);
            } else if (contains(genomesThroughMe, SINGLE2)) {
                return getSingle(2);
            } else if (contains(genomesThroughMe, SINGLE1)) {
                return getSingle(1);
            }
        }
        return getBase();
    }

    private boolean allInclusive(int[] genomesThroughMe, int[] checkSet) {
        if (checkSet == null) {
            return false;
        }
        for (int genome : checkSet) {
            if (!contains(genomesThroughMe, genome)) {
                return false;
            }
        }
        return true;
    }

    private boolean contains(int[] checkSet, Integer genome) {
        if (genome == null) {
            return false;
        }

        for (int check : checkSet) {
            if (check == genome) {
                return true;
            }
        }
        return false;
    }

    public Color getSingle(int combo) {
        switch (combo) {
            case 1:
                return Color.LIGHTBLUE;
            case 2:
                return Color.LIGHTGREEN;
            case 3:
                return Color.LIGHTPINK;
            case 4:
                return Color.BLUEVIOLET;
            case 5:
                return Color.ORANGE;
            case 6:
                return Color.RED;
            case 7:
                return Color.DARKGREEN;
            default:
                return Color.DARKBLUE;
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
                return Color.WHITE;
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
        return Color.DARKBLUE;
    }
}
