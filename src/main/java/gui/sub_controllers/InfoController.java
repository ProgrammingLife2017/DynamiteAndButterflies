package gui.sub_controllers;

import graph.SequenceGraph;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * Created by Jip on 17-5-2017.
 */
public class InfoController {

    private final Label nodeLabel;
    private final Label edgeLabel;
    private final TextArea seqLabel;
    private final TextArea seqLabelAlt;

    /**
     * Constructor of the Information controller  that controls the infoPane.
     * @param nodeLabelArg Label labeled node.
     * @param edgeLabelArg Label labeled edge.
     * @param seqLabelArg Label labled sequence.
     */
    public InfoController(Label nodeLabelArg, Label edgeLabelArg, TextArea seqLabelArg, TextArea seqLabelArgAlt) {
        nodeLabel = nodeLabelArg;
        edgeLabel = edgeLabelArg;
        seqLabel = seqLabelArg;
        seqLabelAlt = seqLabelArgAlt;
    }

    /**
     * Displays the size and centre node of the graph.
     * @param graph The SequenceGraph being shown.
     */
    public void displayInfo(SequenceGraph graph) {
        nodeLabel.setText(graph.getFullGraphRightBoundID() + "");
        edgeLabel.setText(graph.totalSize() + "");
    }

    /**
     * updates the sequence Label with the sequence.
     * @param newString the new String that is the sequence
     */
    public void updateSeqLabel(String newString) {
        seqLabel.setText(newString);
    }

    /**
     * updates the sequence alt Label with the sequence.
     * @param newString the new String that is the sequence
     */
    public void updateSeqAltLabel(String newString) {
        seqLabelAlt.setText(newString);
    }


}
