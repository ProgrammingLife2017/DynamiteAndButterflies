package graph;

import gui.GraphDrawer;
import gui.sub_controllers.ColourController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class Node2, which represents sequences of DNA. A sequence is a part of a genome.
 * The sequence is a String consisting of A, C T and G.
 */
public class SequenceNode {

    private int id;
    private int[] genomes;
    private int[] offSets;
    private int index;
    private int column;
    private int sequenceLength;
    private double xCoordinate;
    private double yCoordinate;
    private double width;
    private double height;
    private boolean highlighted;
    private boolean isDummy;
    private float baryCenterValue;
    private int inDegree;


    private ArrayList<Integer> children;
    private ArrayList<Integer> parents;

    /**
     * Constructor for the sequenceNode.
     *
     * @param id The id of the node.
     */
    SequenceNode(int id) {
        this.id = id;
        this.index = 0;
        this.column = 0;
        this.inDegree = 0;
        this.baryCenterValue = 0;
        this.parents = new ArrayList<Integer>();
        this.children = new ArrayList<Integer>();
        this.isDummy = false;
        this.genomes = new int[0];
        this.offSets = new int[0];
    }

    /**
     * Setter for the location of a node.
     *
     * @param x      XLocation.
     * @param y      YLocation.
     * @param width  Width.
     * @param height Height.
     */
    public void setCoordinates(double x, double y, double width, double height) {
        this.xCoordinate = x;
        this.yCoordinate = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Draw the node highlighted.
     */
    public void highlight() {
        this.highlighted = true;
    }

    /**
     * Draw the node lowlighted.
     */
    public void lowlight() {
        this.highlighted = false;
    }

    /**
     * Draw the node with the color depending on it's status. Orange for highlighted nodes,
     * black for dummy nodes and blue for sequence nodes.
     *
     * @param gc               The graphicsContext of the screen.
     * @param colourController A controller that chooses colours for the node
     */
    public void draw(GraphicsContext gc, ColourController colourController, ArrayList<Annotation> annotations) {
        if (inView(gc.getCanvas().getWidth())) {
            if (isDummy) {
                GraphDrawer.getInstance().setLineWidth(genomes.length);
                gc.strokeLine(xCoordinate, yCoordinate + height / 2,
                        xCoordinate + width, yCoordinate + height / 2);
                return;
            }

            List<Color> colourMeBby;
            if (highlighted) {
                gc.setLineWidth(6);
                gc.strokeRect(xCoordinate, yCoordinate, width, height);
            }
            
           colourMeBby = colourController.getColors(genomes);
            double tempCoordinate = yCoordinate;
            double tempHeight = height / colourMeBby.size();
            for (Color beamColour : colourMeBby) {
                gc.setFill(beamColour);
                gc.fillRect(xCoordinate, tempCoordinate, width, tempHeight);
                tempCoordinate += tempHeight;
            }
            for (int i = 0; i < annotations.size(); i++) {
                Annotation annotation = annotations.get(i);
                int annoID = annotation.getId();
                double startXAnno = xCoordinate;
                double startYAnno = yCoordinate + height;
                double annoWidth = width;
                double annoHeight = height / 2;
                int indexOfGenome = colourController.containsPos(genomes, annoID);
                if (indexOfGenome != -1) {
                    int startOfAnno = annotation.getStart();
                    int endOfAnno = annotation.getEnd();
                    int startCorOfGenome = 0;

                    if (genomes.length == offSets.length) {
                        startCorOfGenome = indexOfGenome;
                    }

                    if (startOfAnno > (offSets[startCorOfGenome] + sequenceLength)
                            || endOfAnno < (offSets[startCorOfGenome])) {
                        continue;
                    }

                    double emptyAtStart = 0.0;
                    if (startOfAnno > offSets[startCorOfGenome]) {
                        emptyAtStart = startOfAnno - offSets[startCorOfGenome];
                        annoWidth = (annoWidth * (1 - (emptyAtStart / sequenceLength)));
                        startXAnno = startXAnno + (width - annoWidth);
                    }
                    if (endOfAnno < (offSets[startCorOfGenome] + sequenceLength)) {
                        int emptyAtEnd = offSets[startCorOfGenome] + sequenceLength - endOfAnno;
                        annoWidth = (annoWidth * (1 - (emptyAtEnd / (sequenceLength - emptyAtStart))));
                    }
                    gc.setFill(Color.RED);
                    gc.fillRect(startXAnno, startYAnno, annoWidth, annoHeight);
                }
            }
        }
    }

    /**
     * Check if a click event is within the borders of this node.
     *
     * @param xEvent x coordinate of the click event
     * @param yEvent y coordinate of the click event
     * @return True if the coordinates of the click event are within borders, false otherwise.
     */
    public boolean checkClick(double xEvent, double yEvent) {
        return (xEvent > xCoordinate && xEvent < xCoordinate + width && yEvent > yCoordinate && yEvent < yCoordinate + height);
    }


    /**
     * Check if a click event is within the borders of this node.
     *
     * @param xEvent x coordinate of the click event
     * @return True if the coordinates of the click event are within borders, false otherwise.
     */
    public boolean checkClickX(double xEvent) {
        return (xEvent > xCoordinate && xEvent < xCoordinate + width);
    }

    public boolean checkBounds() {
        return (xCoordinate <= 0 && (xCoordinate + width) >= 0);
    }

    public boolean inView(double viewWidth) {
        return xCoordinate + width > 0 && xCoordinate < viewWidth;
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Integer getId() {
        return id;
    }

    public Integer getChild(int id) {
        return children.get(id);
    }

    public void addChild(Integer id) {
        if (!this.children.contains(id))
            this.children.add(id);
    }

    void removeChild(Integer id) {
        this.children.remove(id);
    }

    public ArrayList<Integer> getParents() {
        return parents;
    }

    void addParent(Integer id) {
        if (!this.parents.contains(id)) {
            this.parents.add(id);
        }
    }

    boolean hasChildren() {
        return children.size() > 0;
    }

    public ArrayList<Integer> getChildren() {
        return this.children;
    }

    public boolean isDummy() {
        return isDummy;
    }

    void setDummy(boolean dummy) {
        isDummy = dummy;
    }

    public int getIndex() {
        return this.index;
    }

    void setIndex(int index) {
        this.index = index;
    }

    public int getColumn() {
        return column;
    }

    void setColumn(int col) {
        this.column = col;
    }

    void incrementColumn(int i) {
        if (this.column < i + 1) {
            column = i + 1;
        }
    }

    public void setGenomes(int[] genomesArg) {
        this.genomes = genomesArg;
    }

    public void setOffSets(int[] offSets) {
        this.offSets = offSets;
    }

    public int[] getOffsets() {
        return this.offSets;
    }

    /**
     * method to resolve the baryCenterValue.
     *
     * @return - returns the barycenterValue / inDegree
     */
    float getBaryCenterValue() {
        return baryCenterValue / inDegree;
    }


    void incrementBaryCenterValue(float baryCenterValue) {
        this.baryCenterValue += baryCenterValue;
    }


    void incrementInDegree() {
        this.inDegree++;
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    public void setSequenceLength(int sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    public int[] getGenomes() {
        return genomes;
    }


    /**
     * Forms a string of the sequence node.
     *
     * @param sequence With it's sequence which we do not constantly want in memory
     * @return A string representation of the node.
     */
    public String toString(String sequence) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Node ID:\t\t\t").append(this.id).append("\n");
        stringBuilder.append("Column index:\t\t").append(this.column).append("\n");
        appendChildren(stringBuilder);
        appendParents(stringBuilder);
        stringBuilder.append("SequenceLength:\t").append(this.sequenceLength).append("\n");
        appendSequence(sequence, stringBuilder);
        appendGenomes(stringBuilder);
        appendGenomeCoords(stringBuilder);
        return stringBuilder.toString();
    }

    /**
     * Appends genome coordinates to a string builder.
     * @param stringBuilder string builder to append to.
     */
    private void appendGenomeCoords(StringBuilder stringBuilder) {
        stringBuilder.append("Genome coords:\t");
        for (Integer i: offSets) {
            stringBuilder.append(i).append(" ");
        }
        stringBuilder.append("\n");
    }

    /**
     * Appends genomes to a string builder.
     * @param stringBuilder string builder to append to.
     */
    private void appendGenomes(StringBuilder stringBuilder) {
        stringBuilder.append("Genomes:\t\t\t");
        int[] sortedGenomes = this.getGenomes();
        Arrays.sort(sortedGenomes);
        for (Integer i: sortedGenomes) {
            stringBuilder.append(i).append(" ");
        }
        stringBuilder.append("\n");
    }

    /**
     * Append sequence to a string builder.
     * @param sequence the sequence
     * @param stringBuilder string builder to append to.
     */
    private void appendSequence(String sequence, StringBuilder stringBuilder) {
        stringBuilder.append("Sequence:\t\t");
        if (isDummy) {
            stringBuilder.append("-\n");
        } else {
            stringBuilder.append(sequence).append("\n");
        }
    }

    /**
     * Appends parents to a string builder.
     * @param stringBuilder string builder to append to.
     */
    private void appendParents(StringBuilder stringBuilder) {
        stringBuilder.append("Parents:\t\t\t");
        for (Integer i: parents) {
            stringBuilder.append(i).append(" ");
        }
        stringBuilder.append("\n");
    }
    /**
     * Appends children to a string builder.
     * @param stringBuilder string builder to append to.
     */
    private void appendChildren(StringBuilder stringBuilder) {
        stringBuilder.append("Children:\t\t\t");
        for (Integer i : children) {
            stringBuilder.append(i).append(" ");
        }
        stringBuilder.append("\n");
    }
}
