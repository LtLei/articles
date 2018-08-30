import java.util.LinkedList;
import java.util.Queue;

/**
 * 此类演示了二叉树的创建和遍历
 */
public class BinaryTreeTraverseSample{
    public static void main(String[] args) {
        TreeNode tree = new TreeNode(1);

        // 填充数据
        TreeNode node2 = new TreeNode(2);
        TreeNode node3 = new TreeNode(3);
        TreeNode node5 = new TreeNode(5);
        TreeNode node6 = new TreeNode(6);
        TreeNode node7 = new TreeNode(7);
        TreeNode node10 = new TreeNode(10);

        tree.lChild = node2;
        tree.rChild = node3;
        node2.rChild = node5;
        node3.lChild = node6;
        node3.rChild = node7;
        node5.lChild = node10;

        // 层序遍历输出
        levelTraverse(tree);

        System.out.println("---------------------------------------------------------");

        // 前序遍历输出
        preOrderTraverse(tree);

        System.out.println("---------------------------------------------------------");

        // 中序遍历输出
        inOrderTraverse(tree);

        System.out.println("---------------------------------------------------------");

        // 后序遍历输出
        postOrderTraverse(tree);
    }

    /**
     * 层序遍历输出
     * 
     * 思路是利用队列FIFO特性
     * 1. 入队根结点
     * 2. 根结点出队，入队左子结点和右子结点
     */
    private static void levelTraverse(TreeNode tree){
        if(tree == null) return;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(tree);
        TreeNode node;
        while (!queue.isEmpty()) {
            node = queue.poll();
            System.out.println("Node is " + node.data);

            if(node.lChild!=null){
                queue.offer(node.lChild);
            }
            if(node.rChild!=null){
                queue.offer(node.rChild);
            }
        }
    }

    /**
     * 前序遍历输出
     * 先根结点
     */
    private static void preOrderTraverse(TreeNode tree){
        if (tree == null) {
            return;
        }
        System.out.println("Node is " + tree.data);

        preOrderTraverse(tree.lChild);
        preOrderTraverse(tree.rChild);
    }

    /**
     * 中序遍历输出
     * 先左孩子，再根结点
     */
    private static void inOrderTraverse(TreeNode tree){
        if (tree == null) {
            return;
        }
        inOrderTraverse(tree.lChild);

        System.out.println("Node is " + tree.data);

        inOrderTraverse(tree.rChild);
    }

    /**
     * 后序遍历输出
     * 先左孩子，再右孩子，最后再根结点
     */
    private static void postOrderTraverse(TreeNode tree){
        if (tree == null) {
            return;
        }
        postOrderTraverse(tree.lChild);
        postOrderTraverse(tree.rChild);

        System.out.println("Node is " + tree.data);
    }
}

/**
 * 链式存储的结点数据结构
 */
class TreeNode{
    Integer data;
    TreeNode lChild;
    TreeNode rChild;

    public TreeNode(Integer data){
        this.data = data;
    }
}

