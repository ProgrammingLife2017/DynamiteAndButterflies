package gui;

import graph.SequenceGraph;
import gui.sub_controllers.FileController;
import gui.sub_controllers.PanningController;
import gui.sub_controllers.ScrollbarController;
import gui.sub_controllers.RecentGenomeController;
import javafx.application.Platform;
import parser.GfaParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by eric on 8-6-17.
 *
 * A singleton class which represents our canvas and some data it needs.
 */
public final class DrawableCanvas extends Observable implements Observer {

    private static final int START_NODE_ID = 1000;

    private static DrawableCanvas canvas = new DrawableCanvas();
    private int annotationGenome;
    private int prefGenomeToTraverse;

    private GfaParser parser;

    private RecentGenomeController recentGenomeController;


    private DrawableCanvas() {

    }

    /**
     * Gets the DrawableCanvas instance.
     * @return the DrawableCanvas
     */
    public static DrawableCanvas getInstance() {
        return canvas;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof FileController) {
            if (arg instanceof Integer) {
                if (((Integer) arg) == 0) {
                    try {
                        int[] childArray = parser.getChildArray();
                        int[] parentArray = parser.getParentArray();
                        Platform.runLater(new Runnable() {
                            public void run() {
                                SequenceGraph graph = new SequenceGraph(
                                        parentArray, childArray, getParser().getSequenceHashMap(), getParser().getOffSets(), getParser().getGenomes());
                                graph.createSubGraph(START_NODE_ID, PanningController.RENDER_RANGE);
                                Minimap.getInstance().initialize(graph.getFullGraphRightBoundID());
                                GraphDrawer.getInstance().setGraph(graph);
                                GraphDrawer.getInstance().redraw();
                                ScrollbarController.getInstance().initialize(graph.getMaxColumnSize());
                                setChanged();
                                notifyObservers(parser.getFilePath());
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    public void setMenuController(MenuController mc) {
        this.addObserver(mc);
    }

    public void setParser(GfaParser parser) {
        this.parser = parser;

    }

    public GfaParser getParser() {
        return this.parser;
    }

    public RecentGenomeController getRecentGenomeController() {
        return this.recentGenomeController;
    }

    public HashMap<String, Integer> getAllGenomes() {
        return parser.getAllGenomesMap();
    }

    public HashMap<Integer, String> getAllGenomesReversed() {
        return parser.getAllGenomesMapReversed();
    }

    public void setAnnotationGenome(int annotationGenomeArg) {
        this.annotationGenome = annotationGenomeArg;
    }

    public int getAnnotationGenome() {
        return annotationGenome;
    }

    public void setRecentGenomeController(RecentGenomeController sgp) {
        this.recentGenomeController = sgp;
    }

    public void setPrefGenomeToTraverse(int genomeToTraverse) {
        this.prefGenomeToTraverse = genomeToTraverse;
    }

    public int getPrefGenomeToTraverse() {
        return prefGenomeToTraverse;
    }
}
