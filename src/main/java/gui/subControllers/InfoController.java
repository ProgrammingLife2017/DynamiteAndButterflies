package gui.subControllers;

import graph.SequenceGraph;
import javafx.scene.control.Label;

/**
 * Created by Jip on 17-5-2017.
 */
public class InfoController {

    Label nodeLabel;
    Label edgeLabel;
    Label seqLabel;

    public InfoController(Label nodeLabelArg, Label edgeLabelArg, Label seqLabelArg) {
        nodeLabel = nodeLabelArg;
        edgeLabel = edgeLabelArg;
        seqLabel = seqLabelArg;
    }

    public void displayInfo(SequenceGraph graph) {
        nodeLabel.setText(graph.getNodes().size() + "");
        edgeLabel.setText(graph.getEdges().size() + "");
    }

    public void updateSeqLabel(String newString) {
        seqLabel.setText(newString);
    }
}
