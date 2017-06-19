package structures;

/**
 * Created by Jip on 19-6-2017.
 */
public class BinaryTree {

    public TreeNode addNode(TreeNode root, Annotation annotation) {
        if (root == null) {
            return TreeNode.newNode(annotation);
        }

        int low = root.getLow();
        if (annotation.getStart() < low) {
            root.setLeft(addNode(root.getLeft(), annotation));
        } else {
            root.setRight(addNode(root.getRight(), annotation));
        }

        if (root.getMax() < annotation.getEnd()) {
            root.setMax(annotation.getEnd());
        }

        return root;
    }

    boolean doOverlap(Annotation a1, Annotation a2) {
        if (a1.getStart() <= a2.getEnd() && a2.getStart() <= a1.getEnd()) {
            return true;
        }
        return false;
    }

    Annotation overlapSearch(TreeNode root, Annotation annotation) {
        if (root == null) {
            return null;
        }

        if (doOverlap(root.getAnnotation(), annotation)) {
            return root.getAnnotation();
        }

        // If left child of root is present and max of left child is
        // greater than or equal to given interval, then i may
        // overlap with an interval is left subtree
        if (root.getLeft() != null && root.getLeft().getMax() >= annotation.getStart()) {
            return overlapSearch(root.getLeft(), annotation);
        }

        return overlapSearch(root.getRight(), annotation);
    }
}

