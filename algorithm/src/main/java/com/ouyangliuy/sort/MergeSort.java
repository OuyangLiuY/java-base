package com.ouyangliuy.sort;

import java.util.Arrays;

/**
 * 归并排序
 */
public class MergeSort {

    /**
     * 并排序思想：
     * 每两个最小的小组进行排序，将两个小组的排序进行合并，最后就是整个排好序的结果
     */

    // 使用递归方式实现
    public static void mergeRecursion(int[] arr) {
        if (arr == null || arr.length < 1) {
            return;
        }
        sort(arr, 0, arr.length - 1);
    }

    private  static void sort(int[] arr, int L, int R) {
        if (L == R) { // base case
            return;
        }
        int mid = (L + R) / 2;
        sort(arr, L, mid);
        sort(arr, mid + 1, R);
        merge(arr, L, mid, R);
    }

    private static void merge(int[] arr, int L, int mid, int R) {
        int[] help = new int[R - L + 1];
        // 需要拷贝到结果的索引
        int idx = 0;
        int l = L;          // 左指针处理左边的数据
        int r = mid + 1;    // 右指针处理左边的数据
        while (l <= mid && r <= R) {
            help[idx++] = arr[l] <= arr[r] ? arr[l++] : arr[r++];
        }
        // 最后，到底谁的指针先到条件，未知。
        while (r <= R) {
            help[idx++] = arr[r++];
        }
        while (l <= mid) {
            help[idx++] = arr[l++];
        }
        // 最后将help数组拷贝到原来的数组中
        System.arraycopy(help, 0, arr, L, help.length);
    }

    // 使用非递归方式实现
    public void mergeNoRecursion(int[] arr) {

    }

    public static void main(String[] args) {
        int[] arr1 = {1,3,4,2,5,100,19,80,20,15,70,4,10001,8,1004};
        Arrays.sort(arr1);
        System.out.println(Arrays.toString(arr1));
        int[]   arr2 = {1,3,4,2,5,100,19,80,20,15,70,4,10001,8,1004};
        mergeRecursion(arr2);
        System.out.println(Arrays.toString(arr2));
    }
}
