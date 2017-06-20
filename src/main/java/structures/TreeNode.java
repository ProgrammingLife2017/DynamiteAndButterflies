package structures;

import java.util.ArrayList;

public class TreeNode {
    private Annotation annotation;
    private int max;
    TreeNode left, right;

    private TreeNode() {
        left = null;
        right = null;
    }

    public static TreeNode newNode(Annotation annotation) {
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

    public void setLeft(TreeNode leftArg) {
        this.left = leftArg;
        if (leftArg != null) {
            if (leftArg.getMax() > this.max) {
                this.max = leftArg.getMax();
            }
        }
    }

    public void setRight(TreeNode rightArg) {
        this.right = rightArg;
        if (rightArg != null) {
            if (rightArg.getMax() > this.max) {
                this.max = rightArg.getMax();
            }
        }
    }

    public ArrayList<Annotation> getAllChildAnnotations() {

        ArrayList<Annotation> res = new ArrayList<>();
        res.add(this.getAnnotation());

        if (this.getLeft() != null) {
            res.add(this.getLeft().getAnnotation());
        }
        if (this.getRight() != null) {
            res.add(this.getRight().getAnnotation());
        }
        if (this.getRight() == null && this.getLeft() == null) {
            return res;
        }
        return res;
    }
}
