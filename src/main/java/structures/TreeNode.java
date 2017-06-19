package structures;

public class TreeNode {
    private Annotation annotation;
    private int max;
    TreeNode left, right;

    private TreeNode() {
        left = null;
        right = null;
    }

    static TreeNode newNode(Annotation annotation) {
        TreeNode res = new TreeNode();
        res.setAnnotation(annotation);
        res.setMax(annotation.getEnd());
        return res;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getLow() {
        return annotation.getStart();
    }

    public TreeNode getLeft() {
        return left;
    }

    public TreeNode getRight() {
        return right;
    }

    public int getMax() {
        return max;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setLeft(TreeNode left) {
        this.left = left;
    }

    public void setRight(TreeNode right) {
        this.right = right;
    }
}
