package parser;

import gui.DrawableCanvas;
import structures.Annotation;
import structures.BinaryTree;
import structures.TreeNode;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

/**
 * Created by lex_b on 12/06/2017.
 */
public class GffParser {
    private String filePath;

    /**
     * Constructor.
     *
     * @param absolutePath The path location of the file.
     */
    public GffParser(String absolutePath) {
        filePath = absolutePath;
    }

    /**
     * @return an arrayList with the Annotations.
     * @throws IOException If it goes wrong.
     */
    public ArrayList<Annotation> parseGff() throws IOException {
        ArrayList<Annotation> annotationList = new ArrayList<>();
        Vector<TreeNode> annotations = new Vector<>();
        BinaryTree tree = new BinaryTree();
        InputStream in = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split("\t");
            String nameGenome = data[0].split("\\.")[0];
            String info = data[8].replace(";", "\t");
            int start = Integer.parseInt(data[3]);
            int end = Integer.parseInt(data[4]);
            Annotation anno = new Annotation(DrawableCanvas.getInstance().getAllGenomes().get(nameGenome), start, end, info);
            annotations.add(TreeNode.newNode(anno));
            annotationList.add(anno);
        }
        annotations.sort(new Comparator<TreeNode>() {
            @Override
            public int compare(TreeNode a1, TreeNode a2) {
                if (a1.getAnnotation().getStart() < a2.getAnnotation().getStart()) {
                    return -1;
                } else if (a1.getAnnotation().getStart() > a2.getAnnotation().getStart()) {
                    return 1;
                }
                return 0;
            }
        });
        TreeNode root = tree.buildTreeUtil(annotations, 0, annotations.size() - 1);
        return annotationList;
    }
}
