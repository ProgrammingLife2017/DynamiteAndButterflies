package graph;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jip on 16-5-2017.
 */
public class EdgeTest {

    Edge edge;
    ArrayList<Integer> test, listOfColumns;

    @Before
    public void setUp() {
        edge = new Edge(1, 3);
        test = new ArrayList<Integer>();
        listOfColumns = new ArrayList<Integer>();
    }

    @Test
    public void addColumnTest() {
        edge.addColumn(2);
        test.add(2);
        assertEquals(edge.getColumnSpan(), test);
    }

    @Test
    public void setEntireColumnSpanTest() {
        edge.setEntireColumnSpan(1, 5);
        test.add(2);
        test.add(3);
        test.add(4);
        assertEquals(edge.getColumnSpan(), test);
    }

    @Test
    public void setGetColumnSpanTest() {
        test.add(2);
        test.add(3);
        edge.setColumnSpan(test);

        listOfColumns.add(2);
        listOfColumns.add(3);

        assertEquals(edge.getColumnSpan(), listOfColumns);
    }

    @Test
    public void getParentTest() {
        assertTrue(edge.getParent() == 1);
    }

    @Test
    public void getChildTest() {
        assertTrue(edge.getChild() == 3);
    }
}
