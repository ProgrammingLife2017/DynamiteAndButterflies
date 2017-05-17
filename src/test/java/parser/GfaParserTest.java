package parser;

import graph.SequenceGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class GfaParserTest{

    GfaParser parser;
    SequenceGraph graph;

    @Before
    public void initialize() throws IOException {
        parser = new GfaParser();
        graph = parser.parseGraph("src/main/resources/test (1).gfa");
    }

    @Test
    public void getHeaders() throws Exception {
        assertTrue(parser.getHeaders().get(0).equals("\tVN:Z:1.0"));
        assertTrue(parser.getHeaders().get(1).equals("\tORI:Z:TKK-01-0015.fasta;TKK-01-0026.fasta;TKK-01-0029.fasta;TKK-01-0058.fasta;TKK-01-0066.fasta;TKK_02_0018.fasta;TKK_02_0068.fasta;TKK_04_0002.fasta;TKK_04_0031.fasta;TKK_REF.fasta;"));
        parser.db.close();
    }
/*
    @Test
    public void parse() throws Exception {
        assertEquals(27, graph.getEdges().size());
        assertEquals(19, graph.getNodes().size());
        parser.db.close();
    }

    @Test(expected = FileNotFoundException.class)
    public void parseNullPointer() throws Exception {
        parser.parseGraph("/non-existent-file.gfa");
        parser.db.close();
    }
*/
}