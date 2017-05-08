import graph.SequenceGraph;
import org.junit.Test;
import parser.GfaParser;

import java.io.FileNotFoundException;
import static org.junit.Assert.*;


public class GfaParserTest {

    @Test
    public void getHeaders() throws Exception {
        GfaParser parser = new GfaParser();
        SequenceGraph graph = parser.parse("src/main/resources/test (1).gfa");
        assertTrue(parser.getHeaders().get(0).equals("\tVN:Z:1.0"));
        assertTrue(parser.getHeaders().get(1).equals("\tORI:Z:TKK-01-0015.fasta;TKK-01-0026.fasta;TKK-01-0029.fasta;TKK-01-0058.fasta;TKK-01-0066.fasta;TKK_02_0018.fasta;TKK_02_0068.fasta;TKK_04_0002.fasta;TKK_04_0031.fasta;TKK_REF.fasta;"));
    }

    @Test
    public void parse() throws Exception {
        GfaParser parser = new GfaParser();
        SequenceGraph graph = parser.parse("src/main/resources/test (1).gfa");
        assertEquals(27, graph.getEdges().size());
        assertEquals(19, graph.getNodes().size());
    }

    @Test(expected = FileNotFoundException.class)
    public void parseNullPointer() throws Exception {
        GfaParser parser = new GfaParser();
        SequenceGraph graph = parser.parse("/non-existent-file.gfa");
    }

}