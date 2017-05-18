package gui;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jip on 16-5-2017.
 */
public class DrawableNodeTest {

    private DrawableNode node;


    @Before
    public void setUp() {
        node = new DrawableNode(0, null);
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
