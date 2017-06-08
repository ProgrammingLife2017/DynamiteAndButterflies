package gui;

import graph.SequenceGraph;
import gui.sub_controllers.PanningController;
import parser.GfaParser;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by eric on 8-6-17.
 */
public class DrawableCanvas implements Observer {


    private static DrawableCanvas canvas = new DrawableCanvas( );

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
        if (o instanceof PanningController) {
            {
                if (arg instanceof Integer) {
                    try {
                        int[] childArray = parser.getChildArray();

                        int [] parentArray = parser.getParentArray();
                        SequenceGraph graph = new SequenceGraph();

                        graph.createSubGraph(nodeId, renderRange )
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
//
//        }
//        Flag new datastructure
//        draw data structure


//        Suppose the panning exceeds on the possitive



    }
}
