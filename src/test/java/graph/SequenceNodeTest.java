package graph;

import org.junit.Before;
import org.junit.Test;

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
}
