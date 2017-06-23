package graph;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jip on 16-5-2017.
 */
public class SequenceNodeTest {


    SequenceNode node;
    int child;

    @Before
    public void setUp() {
        node = new SequenceNode(-1);
        child = 1;
    }

    @Test
    public void highlight() throws Exception {
        assertTrue(!node.isHighlighted());
        node.highlight();
        assertTrue(node.isHighlighted());
    }

    @Test
    public void lowlight() throws Exception {
        assertTrue(!node.isHighlighted());
        node.highlight();
        assertTrue(node.isHighlighted());
        node.lowlight();
        assertTrue(!node.isHighlighted());
    }


    @Test
    public void addChild() throws Exception {
        node.addChild(42);
        assertTrue(node.hasChildren());
        assertTrue(node.getChild(0) == 42);
        node.addChild(43);
        assertTrue(node.getChildren().size() == 2);
    }

    @Test
    public void removeChild() throws Exception {
        node.addChild(42);
        assertTrue(node.hasChildren());
        assertTrue(node.getChild(0) == 42);
        node.addChild(43);
        assertTrue(node.getChildren().size() == 2);
        node.removeChild(42);
        assertTrue(node.getChildren().size() == 1);
        node.removeChild(43);
        assertTrue(!node.hasChildren());
    }

    @Test
    public void addParent() throws Exception {
        node.addParent(42);
        assertTrue(node.getParents().get(0) == 42);
    }

    @Test
    public void isDummy() throws Exception {
        assertTrue(!node.isDummy());
        node.setDummy(true);
        assertTrue(node.isDummy());
    }

    @Test
    public void incrementColumn() throws Exception {
        node.incrementColumn(1);
        assertTrue(node.getColumn() == 2);
        node.incrementColumn(2);
        assertTrue(node.getColumn() == 3);
        node.incrementColumn(3);
        assertTrue(node.getColumn() == 4);
    }

    @Test
    public void setGenomes() throws Exception {
        int[] genomes = {1,2,3,4};
        node.setGenomes(genomes);
        assertTrue(node.getGenomes()[0] == 1);
        assertTrue(node.getGenomes()[1] == 2);
        assertTrue(node.getGenomes()[2] == 3);
        assertTrue(node.getGenomes()[3] == 4);
    }

    @Test
    public void setOffSets() throws Exception {
        int[] offsets = {10,20,30,40};
        node.setOffSets(offsets);
        assertTrue(node.getOffsets()[0] == 10);
        assertTrue(node.getOffsets()[1] == 20);
        assertTrue(node.getOffsets()[2] == 30);
        assertTrue(node.getOffsets()[3] == 40);
    }

    @Test
    public void incrementBaryCenterValue() throws Exception {
        node.addParent(1);
        node.incrementInDegree();
        node.incrementBaryCenterValue(5);
        assertTrue(node.getBaryCenterValue() == 5);
        node.incrementInDegree();
        assertTrue(node.getBaryCenterValue() == 2.5f);
    }

    @Test
    public void toStringTest() throws Exception {
        int[] testarray = {1,2,3,4};
        node.addParent(1);
        node.addChild(1);
        node.setOffSets(testarray);
        node.setSequenceLength(100);
        String info = node.toString("testsequence");
        System.out.println(info);
        assertTrue(info.contains("Node ID:"));
        assertTrue(info.contains("Column index:"));
        assertTrue(info.contains("Children:"));
        assertTrue(info.contains("Parents:"));
        assertTrue(info.contains("SequenceLength:"));
        assertTrue(info.contains("Sequence:"));
        assertTrue(info.contains("Genome coords:"));

    }

    @Test
    public void addIdTest() {
        node.addChild(-2);
        node.addChild(-3);
    }

    @Test
    public void isDummyTestTrue() {
        node.setDummy(true);
        assertTrue(node.isDummy());
    }

    @Test
    public void isDummyTestFalse() {
        node.setDummy(false);
        assertFalse(node.isDummy());
    }


    @Test
    public void returnChildrenTest() {
        assertEquals(node.getChildren(), new ArrayList<Integer>());
        assertEquals(node.getChildren(), node.getChildren());
    }

    @Test
    public void hasChildrenFalseTest() {
        assertFalse(node.hasChildren());
    }

    @Test
    public void addChildTest() {
        node.addChild(child);
        int test = node.getChildren().get(0);
        assertEquals(test, child);
    }

    @Test
    public void hasChildrenTrueTest() {
        node.addChild(child);
        assertTrue(node.hasChildren());
    }

    @Test
    public void incrementCorrectColumnTest() {
        node.addChild(child);
        node.incrementColumn(3);
        assertEquals(node.getColumn(), 4);
    }

    @Test
    public void incrementIncorrectColumnTest() {
        node.addChild(child);
        node.setColumn(5);
        node.incrementColumn(2);
        assertEquals(node.getColumn(), 5);
    }

    @Test
    public void setNodeTest() {
        node.setIndex(20);
        assertEquals(node.getIndex(), 20);
    }

    @Test
    public void getsetboolTest() {
        node.setSequenceLength(1);
        assertTrue(node.getSequenceLength() == 1);
        node.setSNP(false);
        assertTrue(!node.isSNP());
        node.setSNP(true);
        assertTrue(node.isSNP());

        node.incrementInDegree();
        node.addChild(1);
        assertTrue(node.getOutDegree() == 1);
    }

}
