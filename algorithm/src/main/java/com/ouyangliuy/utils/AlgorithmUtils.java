package com.ouyangliuy.utils;

import com.ouyangliuy.tree.TreeNode;

import java.util.Arrays;

public class AlgorithmUtils {

    public static int[] generateRandomArray(int maxSize, int maxValue) {
        // Math.random()   [0,1)  是0 ~ 1 的左闭又开区间
        // Math.random() * N   [0,N)  是0 ~ 1 的左闭又开区间
        // (int)(Math.random() * N)  [0, N-1] 的左闭又开区间
        int[] arr = new int[(int) (Math.random() * (maxSize + 1))];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (int) (Math.random() * (maxValue + 1)) - (int) (Math.random() * (maxValue + 1));
        }
        return arr;
    }

    public static void printArray(int[] arr) {
        System.out.println(Arrays.toString(arr));
    }

    public static boolean isEqual(int[] arr1, int[] arr2) {
        if ((arr1 == null && arr2 != null) || (arr1 != null && arr2 == null)) {
            return false;
        }
        if (arr1 == null && arr2 == null) {
            return true;
        }
        if (arr1.length != arr2.length) {
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i]) return false;
        }
        return true;
    }

    public static void comparator(int[] arr) {
        Arrays.sort(arr);
    }

    public static int[] copyArray(int[] arr) {
        if (arr == null) return null;

        int[] res = new int[arr.length];
        System.arraycopy(arr, 0, res, 0, arr.length);
        return res;
    }

    //  6:110 7:111
    //  111
    // i和j是一个位置的话，会出错
    public static void swapNoEqualIndex(int[] arr, int i, int j) {
        arr[i] = arr[i] ^ arr[j];// 110 ^ 111 = 001
        arr[j] = arr[i] ^ arr[j];// 001 ^ 111 = 110
        arr[i] = arr[i] ^ arr[j];// 001 ^ 110 = 111
    }

    public static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // for test
    public static String generateRandomString(int strLen) {
        char[] ans = new char[(int) (Math.random() * strLen) + 1];
        for (int i = 0; i < ans.length; i++) {
            int value = (int) (Math.random() * 6);
            ans[i] = (char) (97 + value);
        }
        return String.valueOf(ans);
    }

    // for test
    public static String[] generateRandomStringArray(int arrLen, int strLen) {
        String[] ans = new String[(int) (Math.random() * arrLen) + 1];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = generateRandomString(strLen);
        }
        return ans;
    }

    // 生成一个满二叉树
    public static TreeNode generateRandomFullTreeNode(int maxHeight, int maxValue) {
        return generate(1, maxHeight, maxValue);
    }

    private static TreeNode generate(int level, int maxLevel, int maxValue) {
        if (level > maxLevel) {
            return null;
        }
        int val = (int) (Math.random() * maxValue) + 1;
        TreeNode node = new TreeNode(val);
        node.left = generate(level + 1, maxLevel, maxValue);
        node.right = generate(level + 1, maxLevel, maxValue);
        return node;
    }

    public  static void printMidTreeNode(TreeNode node){
        if(node == null){
            return;
        }
        printMidTreeNode(node.left);
        System.out.print(node.value + ",");
        printMidTreeNode(node.right);
    }

    // for test
    public static void printTree(TreeNode head) {
        System.out.println("Binary Tree:");
        printInOrder(head, 0, "H", 17);
        System.out.println();
    }
    public static void printInOrder(TreeNode head, int height, String to, int len) {
        if (head == null) {
            return;
        }
        printInOrder(head.right, height + 1, "v", len);
        String val = to + head.value + to;
        int lenM = val.length();
        int lenL = (len - lenM) / 2;
        int lenR = len - lenM - lenL;
        val = getSpace(lenL) + val + getSpace(lenR);
        System.out.println(getSpace(height * len) + val);
        printInOrder(head.left, height + 1, "^", len);
    }
    public static String getSpace(int num) {
        String space = " ";
        StringBuilder buf = new StringBuilder("");
        for (int i = 0; i < num; i++) {
            buf.append(space);
        }
        return buf.toString();
    }
}
