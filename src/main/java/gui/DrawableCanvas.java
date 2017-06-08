package gui;

import parser.GfaParser;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by eric on 8-6-17.
 */
public class DrawableCanvas implements Observer {


    private static DrawableCanvas canvas = new DrawableCanvas( );

    private double lowerBound;
    private double upperBound;
    private GfaParser parser;


    private DrawableCanvas(GfaParser parser) {
        this.parser = parser;
    }


    public static DrawableCanvas getInstance() {
        return canvas;
    }
//            if (o instanceof GfaParser) {
//        if (arg instanceof Integer) {
//            try {
//                childArray = parser.getChildArray();
//                parentArray = parser.getParentArray();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.out.println(nodeId);
//            System.out.println(renderRange);
//            graph = new SequenceGraph();
//            graph.createSubGraph(nodeId, renderRange, parentArray, childArray);
//            sequenceHashMap = parser.getSequenceHashMap();
//            assignSequenceLenghts();
//            drawer = new GraphDrawer(graph, gc);
//            drawer.moveShapes(0.0);
//            progressBarController.done();
//        }
//    }

    @Override
    public void update(Observable o, Object arg) {
//        if (o instanceof DrawableCanvas) {
//
//        }
//        Flag new datastructure
//        draw data structure


    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }
}
