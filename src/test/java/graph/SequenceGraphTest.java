package graph;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.*;
import parser.GfaParser;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class SequenceGraphTest {
    private int[] parentArray = new int[27];
    private int[] childArray = new int[27];

    private SequenceGraph graph;
    private BTreeMap<Long, String> map;
    private BTreeMap<Integer, int[]> offSets;
    private BTreeMap<Integer, int[]> genomes;
    private DB db;
    private DB db2;
    private DB db3;


    @Before
    public void setUp() throws Exception {
        parentArray = new int[]{1,2,2,3,4,4,5,5,6,7,7,7,8,9,10,11,11,11,12,13,14,15,15,16,16,17,18};
        childArray = new int[]{2,3,4,4,5,7,6,7,7,8,9,10,11,11,11,12,13,14,13,15,15,16,19,17,18,19,19};
        db = DBMaker.tempFileDB().closeOnJvmShutdown().make();
        map = db.treeMap("test map").keySerializer(Serializer.LONG).
                valueSerializer(Serializer.STRING).createOrOpen();
        db2 = DBMaker.tempFileDB().closeOnJvmShutdown().make();
        offSets = db2.treeMap("test map").keySerializer(Serializer.INTEGER).
                valueSerializer(Serializer.INT_ARRAY).createOrOpen();
        db3 = DBMaker.tempFileDB().closeOnJvmShutdown().make();
        genomes = db3.treeMap("test map").keySerializer(Serializer.INTEGER).
                valueSerializer(Serializer.INT_ARRAY).createOrOpen();
        //populate sequencemap
        for(int i = 1; i <= 19; i++) {
            map.put((long) i, "A");
            int[] temp = new int[1];
            temp[0] = i;
            offSets.put(i, temp);
            genomes.put(i, temp);
        }

        graph = new SequenceGraph(parentArray, childArray, map, offSets, genomes);
        graph.createSubGraph(1, 27);
    }

    @Test
    public void createSubGraph() throws Exception {
        assertEquals(graph.getNodes().size(), 27);
        // check nodes for correct ID
        for(int i = 1; i <= 19; i++) {
            assertTrue(graph.getNode(i).getId() == i);
        }
    }

    @Test
    public void size() throws Exception {
        assertEquals(graph.size(), 27);
    }



    @Test
    public void getColumns() throws Exception {
        // column size == longes path length
        assertTrue(graph.getColumns().size() == 15);
    }

    @Test
    public void addNode() throws Exception {
        assertTrue(graph.size() == 27);
        SequenceNode node = new SequenceNode(42);
        graph.getNode(19).addChild(42);
        graph.addNode(node);
        assertTrue(graph.size() == 28);

    }

    @Test
    public void getNode() throws Exception {
        assertEquals(graph.getNodes().size(), 27);
        // check nodes for correct ID
        for(int i = 1; i <= 19; i++) {
            assertTrue(graph.getNode(i).getId() == i);
        }
    }

    @Test
    public void getNodes() throws Exception {

    }

    @Test
    public void setNodes() throws Exception {

    }

    @Test (expected = IllegalArgumentException.class)
    public void invalidArgument() throws Exception {
        graph = new SequenceGraph(parentArray, childArray, null, null, null);
        graph.createSubGraph(-1, 1);
    }
}