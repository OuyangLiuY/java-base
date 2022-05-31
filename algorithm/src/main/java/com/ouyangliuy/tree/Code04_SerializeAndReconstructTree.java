package com.ouyangliuy.tree;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * 树形结构的序列化和反序列化
 */
public class Code04_SerializeAndReconstructTree {

    // 1.1 先序序列化
    public static Queue<Integer> perSerial(TreeNode node){
        Queue<Integer> queue = new LinkedList<>();
        pres(queue,node);
        return queue;
    }

    private static void pres(Queue<Integer> queue, TreeNode head) {
        if(head == null){
            queue.add(null);
        }else {
            queue.add(head.value);
            pres(queue,head.left);
            pres(queue,head.right);
        }
    }
    // 1.2 先序反序列化
    public static TreeNode buildPre(Queue<Integer> pre){
        if(pre== null || pre.size() ==0)
            return null;
        return preb(pre);
    }

    private static TreeNode preb(Queue<Integer> queue) {
        Integer value = queue.poll();
        if(value == null)
            return null;
        TreeNode node = new TreeNode(value);
        node.left = preb(queue);
        node.right = preb(queue);
        return node;
    }

    // 2.1 中序序列化
    public static Queue<Integer> inSerial(TreeNode node){
        Queue<Integer> queue = new LinkedList<>();
        ins(queue,node);
        return queue;
    }

    private static void ins(Queue<Integer> queue, TreeNode head) {
        if(head == null){
            queue.add(null);
        }else {
            pres(queue,head.left);
            queue.add(head.value);
            pres(queue,head.right);
        }
    }

//    // 2.2 先序反序列化
//    public static TreeNode buildIns(Queue<Integer> ins){
//        if(ins== null || ins.size() ==0)
//            return null;
//        return insb(ins);
//    }
//
//    private static TreeNode insb(Queue<Integer> queue) {
//        Integer value = queue.poll();
//        if(value == null)
//            return null;
//        TreeNode node = new TreeNode(value);
//        node.left = preb(queue);
//        node.right = preb(queue);
//        return node;
//    }

    // 3.1 后序序列化
    public static Queue<Integer> posSerial(TreeNode node){
        Queue<Integer> queue = new LinkedList<>();
        pos(queue,node);
        return queue;
    }

    private static void pos(Queue<Integer> queue, TreeNode head) {
        if(head == null){
            queue.add(null);
        }else {
            pres(queue,head.left);
            pres(queue,head.right);
            queue.add(head.value);
        }
    }

    // 3.2 后序序列化
    public static TreeNode buildPos(Queue<Integer> pos){
        if(pos== null || pos.size() ==0)
            return null;
        Stack<Integer> stack = new Stack<>();
        while (!pos.isEmpty()){
            stack.push(pos.poll());
        }
        return posb(stack);
    }

    private static TreeNode posb(Stack<Integer> stack) {
        Integer value = stack.pop();
        if(value == null)
            return null;
        TreeNode node = new TreeNode(value);
        node.right = posb(stack);
        node.left = posb(stack);
        return node;
    }

    public static Queue<Integer> levelSerial(TreeNode head){
        Queue<Integer> queue = new LinkedList<>();
        if(head  != null){
            Queue<TreeNode> traversal = new LinkedList<>();
            traversal.add(head);
            queue.add(head.value);
            while (!traversal.isEmpty()){
                TreeNode cur = traversal.poll();
                if(cur.left != null){
                    queue.add(cur.left.value);
                    traversal.add(cur.left);
                }else {
                    queue.add(null);
                }
                if(cur.right != null){
                    queue.add(cur.right.value);
                    traversal.add(cur.right);
                }else {
                    queue.add(null);
                }
            }
        }
        return queue;
    }

    public static  TreeNode buildByLevelQueue(Queue<Integer> levelList){
        if(levelList == null || levelList.size() ==0) return null;
        Integer val = levelList.poll();
        if(val == null) return null;
        TreeNode node = new TreeNode(val);

        return  node;
    }
}
