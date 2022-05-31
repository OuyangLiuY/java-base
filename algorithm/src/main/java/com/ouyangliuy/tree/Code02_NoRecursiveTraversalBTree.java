package com.ouyangliuy.tree;


import java.util.Stack;

/**
 * 树的非递归遍历
 */
public class Code02_NoRecursiveTraversalBTree {


    /**
     * 非递归先序遍历过程：
     * 1.栈顶出来记为cur
     * 2.有右压入右，有左压入左
     * 3.一定先右在左
     * 思想：
     * 对于一个head树来说，他的右树一定先到栈，左数再出来，
     * 那么弹出之后就一定是 先左再右。
     * @param head
     */
    public static void pre(TreeNode head) {
        if (head == null) return;
        Stack<TreeNode> stack = new Stack<>();
        stack.push(head);
        System.out.print("pre - tree : ");
        while (!stack.isEmpty()) {
            head = stack.pop();
            System.out.print(head.value + " ");
            // 栈，先进后出，所以需要先放右边，再放入左边的
            if (head.right != null) {
                stack.push(head.right);
            }
            if (head.left != null) {
                stack.push(head.left);
            }
        }
        System.out.println();
    }

    /**
     * 非递归后序遍历过程：
     * 1.准备两个栈，对于s1栈进行类似先序遍历的过程遍历一遍，区别是先放入左，再放入右
     * 2.每次弹出s1栈中的节点时，将这个数push到s2栈中。
     * 3.将s2的栈依次弹出，即可得到当前栈的后序遍历。
     * 思想：
     * 参考先序遍历的思想过程，放入栈中过程是 ：头   左 右 弹出之后就是：头 右 左
     * 那么此时准备另外一个栈，将：头 右 左 的过程放入到另外一个栈中，那么此时这个栈
     * 弹出数据的过程就是：左  右 头 ，此时就是后序遍历的结果。
     * @param head
     */
    public static void post(TreeNode head) {
        if (head == null) return;
        Stack<TreeNode> s1 = new Stack<>();
        Stack<TreeNode> s2 = new Stack<>();
        s1.push(head);
        System.out.print("post - tree : ");
        while (!s1.isEmpty()) {
            head = s1.pop();        // 对于s1：头 右 左
            s2.push(head);          // 对于s2来说：push进去之后就是，左 右 头
            if (head.left != null) {
                s1.push(head.left);
            }
            if (head.right != null) {
                s1.push(head.right);
            }
        }
        while (!s2.isEmpty())
            System.out.print(s2.pop().value + " ");
        System.out.println();
    }

    /**
     * 非递归中序遍历过程：
     * 1.当前节点cur，cur头树，整条左边界进栈，直到遇到空
     * 2.栈中弹出打印，节点的右孩子节点为cur，转步骤1
     * 3.只要栈空或者cur为空，就停
     * 思想：
     * 二叉树中，每课树，都是最左边，从头开始进入到栈，那么弹出一个之后，
     * 再看它的cur的右树，并且，只有当cur的右树都被弹出打印之后，才会到cur父节点，依此类推。
     * @param head
     */
    public static void in(TreeNode head) {
        if (head == null) return;
        Stack<TreeNode> stack = new Stack<>();
        TreeNode cur = head;
        System.out.print("in - tree : ");
        while (!stack.isEmpty() || cur != null) {
            if (cur != null) {
                stack.push(cur);
                cur = cur.left;
            } else {
                cur = stack.pop();
                System.out.print(cur.value + " ");
                cur = cur.right;
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        TreeNode head = new TreeNode(1);
        head.left = new TreeNode(2);
        head.right = new TreeNode(3);
        head.left.left = new TreeNode(4);
        head.left.right = new TreeNode(5);
        head.right.left = new TreeNode(6);
        head.right.right = new TreeNode(7);
        pre(head);
        in(head);
        post(head);
    }
}
