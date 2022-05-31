package com.ouyangliuy.tree;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 二叉树的按层遍历
 */
public class Code03_LevelTraversalBTree {

    /**
     * 按层遍历的过程：
     * 1.队列出一个cur，打印
     * 2.cur有左入左，有右入右
     */
    public  static void level(TreeNode head){
        if(head == null) return;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(head);
        while (!queue.isEmpty()){
            TreeNode cur = queue.poll();
            System.out.println(cur.value);
            if(cur.left != null)
                queue.add(cur.left);
            if(cur.right != null)
                queue.add(cur.right);
        }
    }

    public static void main(String[] args) {
        TreeNode head = new TreeNode(1);
        head.left = new TreeNode(2);
        head.right = new TreeNode(3);
        head.left.left = new TreeNode(4);
        head.left.right = new TreeNode(5);
        head.right.left = new TreeNode(6);
        head.right.right = new TreeNode(7);
        level(head);
    }
}
