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
    public void checkClickTestTrue() {
        node.setCoordinates(0,0,10,10);
        assertTrue(node.checkClick(5,5));
    }

    @Test
    public void checkClickTestFalseRight() {
        node.setCoordinates(0,0,10,10);
        assertFalse(node.checkClick(11,5));
    }

    @Test
    public void checkClickTestFalseLeft() {
        node.setCoordinates(2,2,10,10);
        assertFalse(node.checkClick(1,5));
    }

    @Test
    public void checkClickTestFalseUp() {
        node.setCoordinates(2,2,10,10);
        assertFalse(node.checkClick(5,1));
    }

    @Test
    public void checkClickTestFalseDown() {
        node.setCoordinates(0,0,10,10);
        assertFalse(node.checkClick(5,11));
    }
}
