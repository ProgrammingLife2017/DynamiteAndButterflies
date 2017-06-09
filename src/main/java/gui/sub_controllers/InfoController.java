package gui.sub_controllers;

import graph.SequenceGraph;
import javafx.scene.control.Label;

/**
 * Created by Jip on 17-5-2017.
 */
public class InfoController {

    private final Label nodeLabel;
    private final Label edgeLabel;
    private final Label seqLabel;

    /**
     * Constructor of the Information controller  that controls the infoPane.
     * @param nodeLabelArg Label labeled node.
     * @param edgeLabelArg Label labeled edge.
     * @param seqLabelArg Label labled sequence.
     */
    public InfoController(Label nodeLabelArg, Label edgeLabelArg, Label seqLabelArg) {
        nodeLabel = nodeLabelArg;
        edgeLabel = edgeLabelArg;
        seqLabel = seqLabelArg;
    }

    /**
     * Displays the size and centre node of the graph.
     * @param graph The SequenceGraph being shown.
     */
    public void displayInfo(SequenceGraph graph) {
//        nodeLabel.setText(graph.getNodes().size() + "");
//        Fix this by implementing counter when adding children to nodes.
//        edgeLabel.setText(graph.getEdges().size() + "");
    }

    /**
     * updates the sequence Label with the sequence.
     * @param newString the new String that is the sequence
     */
    public void updateSeqLabel(String newString) {
        seqLabel.setText(newString);
    }
}
