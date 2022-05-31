package com.ouyangliuy.array;

import com.ouyangliuy.utils.AlgorithmUtils;

/**
 * 在一个数组中，一个数左边比它小的数的总和，叫数的小和，所有数的小河累加起来，
 * 叫数组小和。求数组小和。
 * 解题：使用归并排序，在其每次排好之前，将所有小于它的数，进行计算。
 */
public class Code01_MinSum {

    public static int minSum(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return 0;
        }
        return process(arr, 0, arr.length - 1);
    }

    private static int process(int[] arr, int L, int R) {
        // base case
        if (L == R) {
            return 0;
        }
        int mid = (L + R) / 2;
        int left = process(arr, L, mid);
        int right = process(arr, mid + 1, R);
        int merge = merge(arr, L, mid, R);
        return left + right + merge;
    }

    private static int merge(int[] arr, int L, int mid, int R) {
        int[] help = new int[R - L + 1];
        int idx = 0;
        int sum = 0;
        int p1 = L;
        int p2 = mid + 1;
        // 开始归并
        while (p1 <= mid && p2 <= R) {
            // R-p2:右边还有这么多比左边p1位数小的个数。+1是因为从0开始的。
            sum += arr[p1] < arr[p2] ? (R - p2 + 1) * arr[p1] : 0;
            help[idx++] = arr[p1] < arr[p2] ? arr[p1++] : arr[p2++];
        }
        while (p1 <= mid) {
            help[idx++] = arr[p1++];
        }
        while (p2 <= R) {
            help[idx++] = arr[p2++];
        }
        System.arraycopy(help, 0, arr, L, help.length);
        return sum;
    }

    public static int minSumComp(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return 0;
        }
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < i; j++) {
                sum += arr[j] < arr[i] ? arr[j] : 0;
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        int test = 10000;
        int maxValue = 10000;
        int length = 1000;
        for (int i = 0; i < test; i++) {
            int[] arr1 = AlgorithmUtils.generateRandomArray(length, maxValue);
            int[] arr2 = AlgorithmUtils.copyArray(arr1);
            int sum1 = minSum(arr1);
            int sum2 = minSumComp(arr2);
            if (sum1 != sum2) {
                System.out.println("Oops!");
                System.out.println(sum1);
                System.out.println(sum2);
            }
        }
        System.out.println("Nice finished!");
    }
}
