package graph;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class BoundaryTest {

    private Boundary bound;

    @Before
    public void setUp() {
        int[] parentArray = new int[]{1, 2, 2, 3, 4, 4, 5, 5, 6, 7, 7, 7, 8, 9, 10, 11, 11, 11, 12, 13, 14, 15, 15, 16, 16, 17, 18};
        int[] childArray = new int[]{2, 3, 4, 4, 5, 7, 6, 7, 7, 8, 9, 10, 11, 11, 11, 12, 13, 14, 13, 15, 15, 16, 19, 17, 18, 19, 19};
        bound = new Boundary(10, 5, parentArray, childArray);

    }

    @Test
    public void getRightBoundIndex() throws Exception {
        assertTrue(bound.getRightBoundIndex() == 22);
    }

    @Test
    public void getLeftBoundIndex() throws Exception {
        assertTrue(bound.getLeftBoundIndex() == 6);
    }

    @Test
    public void getLeftBoundID() throws Exception {
        assertTrue(bound.getLeftBoundID() == 5);
    }

    @Test
    public void getRightBoundID() throws Exception {
        assertTrue(bound.getRightBoundID() == 15);
    }

    @Test
    public void getRange() throws Exception {
        assertTrue(bound.getRange() == 5);
    }

}