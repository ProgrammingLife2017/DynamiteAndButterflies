package graph;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jip on 16-5-2017.
 */
public class SequenceNodeTest {

    SequenceNode node;

    @Before
    public void setUp() {
        node = new SequenceNode(-1);
    }

    @Test
    public void addIdTest() {
        node.addId(-2);
        node.addId(-3);
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
}
