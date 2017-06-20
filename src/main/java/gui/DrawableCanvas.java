package gui;

import graph.SequenceGraph;
import gui.sub_controllers.FileController;
import gui.sub_controllers.PanningController;
import gui.sub_controllers.ScrollbarController;
import gui.sub_controllers.SpecificGenomeProperties;
import parser.GfaParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by eric on 8-6-17.
 */
public class DrawableCanvas extends Observable implements Observer {

    public static final int START_NODE_ID = 1000;

    private static DrawableCanvas canvas = new DrawableCanvas();
    private int annotationGenome;

    private GfaParser parser;

    private MenuController mc;

    private  SpecificGenomeProperties specificGenomeProperties;


    private DrawableCanvas() {

    }

    public static DrawableCanvas getInstance() {
        return canvas;
    }


    public void setSpecificGenomeProperties(SpecificGenomeProperties sgp) {
        this.specificGenomeProperties = sgp;
    }
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof FileController) {
            {
                if (arg instanceof Integer) {
                    if ( ( (Integer) arg) == 0) {
                        try {
                            int[] childArray = parser.getChildArray();
                            HashMap<Integer, String> allGenomes = parser.getAllGenomesMapReversed();

                            int [] parentArray = parser.getParentArray();
                            SequenceGraph graph = new SequenceGraph(parentArray, childArray, getParser().getSequenceHashMap());
                            graph.createSubGraph(START_NODE_ID, PanningController.RENDER_RANGE);
                            GraphDrawer.getInstance().setGraph(graph);
                            GraphDrawer.getInstance().moveShapes(0.0);
                            Minimap.getInstance().initialize(graph.getFullGraphRightBoundID());
                            ScrollbarController.getInstance().initialize(graph.getMaxColumnSize());
                            setChanged();
                            notifyObservers(parser.getFilePath());
                            setChanged();
                        } catch (IOException e) {
                            e.printStackTrace();
                    }

                    }
                }
            }
        }
    }

    public void setMenuController(MenuController mc) {
        this.mc = mc;
        this.addObserver(mc);
    }
    public void setParser(GfaParser parser) {
        this.parser = parser;

    }

    public GfaParser getParser() {
        return this.parser;
    }

    public SpecificGenomeProperties getSpecificGenomeProperties() {
        return this.specificGenomeProperties;
    }

    public HashMap<String, Integer> getAllGenomes() {
        return parser.getAllGenomesMap();
    }

    public HashMap<Integer, String> getAllGenomesReversed() {
        return parser.getAllGenomesMapReversed();
    }

    public void setAnnotationGenome(int annotationGenomeArg) {
        annotationGenome = annotationGenomeArg;
    }

    public int getAnnotationGenome() {
        return annotationGenome;
    }
}
