public class main {

    public static void main(String[] args) throws Exception{
        GfaParser parser = new GfaParser();
        SequenceGraph graph = parser.parse("src/main/resources/test (1).gfa");
        graph.initialize();
        graph.layerizeGraph();
        System.out.println("Done");
    }

}