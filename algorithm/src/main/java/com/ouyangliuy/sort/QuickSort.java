package com.ouyangliuy.sort;

import com.ouyangliuy.utils.SortUtils;

import java.util.Arrays;

public class QuickSort {


    //
    // 1、给定数组，和一个数num，请把小于等于num的数，放到num的左边，大于num的数，放到num的右边
    // 2、要求额外空间复杂度O(1)，时间复杂度O(N)
    // num= arr[R] ,默认num是数组最右变得数
    public static int partition(int[] arr, int L, int R) {
        if (L > R) {
            return -1;
        }
        if (L == R) {
            return arr[L];
        }
        int lessEqual = L - 1;
        int idx = L;
        while (idx < R) {
            if (arr[idx] <= arr[R])
                SortUtils.swap(arr, idx, ++lessEqual);
            idx++;
        }
        SortUtils.swap(arr, R, ++lessEqual);
        return lessEqual;
    }

    // 荷兰国旗
    // 给定一个数组arr，小于num数得放到它得左边，大于num得数放到右边，等于num得数放到中间
    // 默认:num = arr[R]
    public static int[] netherLandsFlag(int[] arr, int L, int R) {
        if (L > R) {
            return new int[]{-1, -1};
        }
        if (L == R) {
            return new int[]{L, R};
        }
        int less = L - 1;
        int idx = L;
        int more = R;
        while (idx < more) {
            if (arr[idx] < arr[R])
                SortUtils.swap(arr, idx++, ++less);
            else if (arr[idx] > arr[R])
                SortUtils.swap(arr, idx, --more);
            else
                idx++;
        }
        // 最后将R位置得数，放到数组中间
        SortUtils.swap(arr, more, R);
        return new int[]{less + 1, more};
    }

    // 快速排序1.0，使用小数放到左边，大数放到右边
    public static void quickSort1(int[] arr) {
        processor1(arr, 0, arr.length - 1);
    }

    // 因为每次能搞定一个位置得数不动，所以排序能完成
    private static void processor1(int[] arr, int L, int R) {
        if (L >= R) {
            return;
        }
        // 一次排序的结果：
        // L..R partition [ xx <= arr[R] arr[R] > xx ]
        int M = partition(arr, L, R);
        processor1(arr, L, M - 1);
        processor1(arr, M + 1, R);
    }

    // 快速排序2.0,使用荷兰国旗
    public static void quickSort2(int[] arr) {
        processor1(arr, 0, arr.length - 1);
    }

    // 因为每次都能搞定一批数组得位置不动，所以排序能完成
    private static void processor2(int[] arr, int L, int R) {
        if (L >= R) {
            return;
        }
        // 一次排序的结果：
        // L..R partition  [ xx <= arr[R] arr[R] > xx ]
        int[] M = netherLandsFlag(arr, L, R);
        processor2(arr, L, M[0] - 1);
        processor2(arr, M[1] + 1, R);
    }


    // 快速排序3.0，使用随机快排
    public static void quickSort3(int[] arr) {
        processor3(arr, 0, arr.length - 1);
    }

    // 因为每次都能搞定一批数组得位置不动，所以排序能完成
    private static void processor3(int[] arr, int L, int R) {
        if (L >= R) {
            return;
        }
        // 随机快排
        SortUtils.swap(arr, L + (int) (Math.random() * (R - L + 1)), R);
        // 一次排序的结果：
        // L..R partition  [ xx <= arr[R] arr[R] > xx ]
        int[] M = netherLandsFlag(arr, L, R);
        processor3(arr, L, M[0] - 1);
        processor3(arr, M[1] + 1, R);
    }


    public static void main(String[] args) {
        int[] arr = {1, 10, 20, 8, 9, 7, 2, 11, 4, 3, 0, -1, 2, 5};
        partition(arr, 0, arr.length - 1);
        int testTime = 1;
        int maxSize = 10000000;
        int maxValue = 10000;
        for (int i = 0; i < testTime; i++) {
            int[] a1 = SortUtils.generateRandomArray(maxSize, maxValue);
            int[] a2 = SortUtils.copyArray(a1);
            int[] a3 = SortUtils.copyArray(a1);
            int[] a4 = SortUtils.copyArray(a1);
            long s = System.currentTimeMillis();
            Arrays.sort(a1);
            long ee1 = System.currentTimeMillis();
            System.out.println("1="+(ee1 - s));
            long ee2 = System.currentTimeMillis();
            quickSort1(a2);
            long ee3 = System.currentTimeMillis();
            System.out.println("2="+(ee3 - ee2));
            quickSort2(a3);
            long ee8 = System.currentTimeMillis();
            System.out.println("3="+(ee8 - ee2));
            long ee4 = System.currentTimeMillis();
            quickSort3(a4);
            long ee5 = System.currentTimeMillis();
            System.out.println("4="+(ee5-ee4));
            boolean e1 = SortUtils.isEqual(a1, a2);
            boolean e2 = SortUtils.isEqual(a1, a3);
            boolean e3 = SortUtils.isEqual(a1, a4);
            if (!e1 || !e2 || !e3) {
                System.out.println("Oop,Fuck!");
                System.out.println(Arrays.toString(a1));
                System.out.println(Arrays.toString(a2));
                System.out.println(Arrays.toString(a3));
                System.out.println(Arrays.toString(a4));
                break;
            }
        }
        System.out.println("Oh,Luck,Finish!");
    }
}
