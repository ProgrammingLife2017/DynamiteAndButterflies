package structures;

import java.util.Vector;

/**
 * Created by Jip on 19-6-2017.
 */
public class BinaryTree {


    boolean doOverlap(Annotation a1, int startCor, int length) {
        if (a1.getStart() <= startCor + length || startCor <= a1.getEnd()) {
            return true;
        }
        return false;
    }

    TreeNode overlapSearch(TreeNode root, int startCor, int length) {
        if (root == null) {
            return null;
        }

        if (doOverlap(root.getAnnotation(), startCor, length)) {
            return root;
        }

        if (root.getLeft() != null && root.getLeft().getMax() >= startCor) {
            return overlapSearch(root.getLeft(), startCor, length);
        }

        return overlapSearch(root.getRight(), startCor, length);
    }

    /* Recursive function to construct binary tree */
    public TreeNode buildTreeUtil(Vector<TreeNode> nodes, int start, int end) {
        // base case
        if (start > end) {
            return null;
        }

        /* Get the middle element and make it root */
        int mid = (start + end) / 2;
        TreeNode node = nodes.get(mid);

        /* Using index in Inorder traversal, construct
           left and right subtress */
        node.setLeft(buildTreeUtil(nodes, start, mid - 1));
        node.setRight(buildTreeUtil(nodes, mid + 1, end));

        return node;
    }
}

