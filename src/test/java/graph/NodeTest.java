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
public class NodeTest {

    private Node node;
    private SequenceNode child;

    @Before
    public void setUp() {
        node = new Node();
        child = new SequenceNode(-1);
    }

    @Test
    public void constructorTest() {
        assertEquals(node.getChildren(), new ArrayList<SequenceNode>());
        assertEquals(node.getColumn(), 0);
    }

    @Test
    public void returnChildrenTest() {
        assertEquals(node.returnChildren(), new ArrayList<SequenceNode>());
        assertEquals(node.returnChildren(), node.getChildren());
    }

    @Test
    public void hasChildrenFalseTest() {
        assertFalse(node.hasChildren());
    }

    @Test
    public void addChildTest() {
        node.addChild(child);
        assertEquals(node.getChildren().get(0), child);
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
}
