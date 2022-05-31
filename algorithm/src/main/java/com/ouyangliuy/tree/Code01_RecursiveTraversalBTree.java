package com.ouyangliuy.tree;

import com.ouyangliuy.utils.AlgorithmUtils;

/**
 * B树的递归遍历
 */
public class Code01_RecursiveTraversalBTree {

    public static void traversal(TreeNode head){
        if(head == null){
            return;
        }
        // 1:此处打印输出，就是先序遍历
        traversal(head.left);
        // 2:此处打印输出，就是中序遍历
        traversal(head.right);
        // 3:此处打印输出，就是后序遍历
    }

    // 先序
    public static void  pre(TreeNode head){
        if(head == null)
            return ;
        // 1 此处打印输出，就是先序遍历
        System.out.println(head.value);
        pre(head.left);
        pre(head.right);
    }
    // 中序
    public static void  mid(TreeNode head){
        if(head == null)
            return ;
        mid(head.left);
        System.out.println(head.value);
        mid(head.right);
    }

    // 后序
    public static void  pos(TreeNode head){
        if(head == null)
            return ;
        pos(head.left);
        pos(head.right);
        System.out.println(head.value);
    }

    public static void main(String[] args) {
        int maxHeight = 5;
        int maxValue = 100;
        TreeNode node = AlgorithmUtils.generateRandomFullTreeNode(maxHeight, maxValue);
        AlgorithmUtils.printMidTreeNode(node);
        System.out.println();
        AlgorithmUtils.printTree(node);
    }
}
