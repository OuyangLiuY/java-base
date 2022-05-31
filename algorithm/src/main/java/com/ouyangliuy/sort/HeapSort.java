package com.ouyangliuy.sort;

import com.ouyangliuy.utils.AlgorithmUtils;

/**
 * ### 什么是完全二叉树？
 * <p>
 * 树结构中，叶子节点，从左往右依次变满得过程就是完成二叉树。
 * <p>
 * ### 使用数组实现堆结构：
 * <p>
 * 1. 数组从左往右，依次走得过程，可以想象成是一个完全二叉树
 * 2. 数组任意位置idx，它的
 * 3. 左孩子节点：2*idx+1
 * 4. 右孩子节点：2*idx+2
 * 5. 父亲节点：idx-1)/2
 * <p>
 * ### 大根堆：
 * <p>
 * 一个树中，从任意一个头节点开始，这个数据的头节点就是这个树的最大值
 * <p>
 * ### 小根堆：
 * <p>
 * 一个树中，从任意一个头节点开始，这个数据的头节点就是这个树的最小值
 */
public class HeapSort {

    // 大根堆
    static class MaxHeap {

        public int heapSize;
        public int[] arr;
        public int limit;

        public MaxHeap(int limit) {
            this.arr = new int[limit];
            heapSize = 0;
            this.limit = limit;
        }

        public boolean isEmpty() {
            return heapSize == 0;
        }

        public boolean isFull() {
            return heapSize == limit;
        }


        public void push(int value) {
            if (isFull()) {
                throw new RuntimeException("heap is full !");
            }
            arr[heapSize] = value;
            heapInsert(arr, heapSize++);
        }

        public int pop() {
            if (isEmpty()) {
                throw new RuntimeException("heap is empty !");
            }
            int ans = arr[0];
            swap(arr, 0, --heapSize);
            heapify(arr, 0, heapSize);
            return ans;
        }
    }

    private static void heapify(int[] arr, int idx, int heapSize) {
        int left = (idx * 2) + 1;
        while (left < heapSize) {
            int large = left + 1 < heapSize && arr[left] < arr[left + 1] ? left + 1 : left;
            large = arr[idx] < arr[large] ? large : idx;
            if (large == idx) { //  说明index就是最大的一个值
                break;
            }
            swap(arr, idx, large);
            idx = large;
            left = (idx * 2) + 1;
        }
    }

    private static void heapInsert(int[] arr, int idx) {
        // 10
        while (arr[idx] > arr[(idx - 1) / 2]) {
            swap(arr, idx, (idx - 1) / 2);
            idx = (idx - 1) / 2; // 继续计算父亲的父亲位置
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    // 大根堆对数器
    static class RightMaxHeap {

        public int heapSize;
        public int[] arr;
        public int limit;

        public RightMaxHeap(int limit) {
            this.arr = new int[limit];
            heapSize = 0;
            this.limit = limit;
        }

        public boolean isEmpty() {
            return heapSize == 0;
        }

        public boolean isFull() {
            return heapSize == limit;
        }


        public void push(int value) {
            if (isFull()) {
                throw new RuntimeException("heap is full !");
            }
            arr[heapSize++] = value;
        }

        public int pop() {
            if (isEmpty()) {
                throw new RuntimeException("heap is empty !");
            }
            int maxIdx = 0;
            for (int i = 0; i < heapSize; i++) {
                if (arr[i] > arr[maxIdx]) {
                    maxIdx = i;
                }
            }
            int ans = arr[maxIdx];
            arr[maxIdx] = arr[--heapSize]; //将最后一个位置数放到最大数的位置上
            return ans;
        }
    }

    public static void main(String[] args) {
        // 小根堆
        int value = 1000;
        int limit = 100;
        int testTimes = 1;
        for (int i = 0; i < testTimes; i++) {
            int curLimit = (int) (Math.random() * limit) + 1;
            MaxHeap my = new MaxHeap(curLimit);
            RightMaxHeap test = new RightMaxHeap(curLimit);
            int curOpTimes = (int) (Math.random() * limit);
            for (int j = 0; j < curOpTimes; j++) {
                if (my.isEmpty() != test.isEmpty()) {
                    System.out.println("Oops1!");
                }
                if (my.isFull() != test.isFull()) {
                    System.out.println("Oops2!");
                }
                if (my.isEmpty()) {
                    int curValue = (int) (Math.random() * value);
                    my.push(curValue);
                    test.push(curValue);
                } else if (my.isFull()) {
                    if (my.pop() != test.pop()) {
                        System.out.println("Oops3!");
                    }
                } else {
                    if (Math.random() < 0.6) {
                        int curValue = (int) (Math.random() * value);
                        my.push(curValue);
                        test.push(curValue);
                    } else {
                        int p1 = my.pop();
                        int p2 = test.pop();
                        if (p1 != p2) {
                            System.out.println(p1);
                            System.out.println(p2);
                            System.out.println("Oops4!");
                        }
                    }
                }
            }
        }
        System.out.println("finish!");
    }

    // 使用大根堆，排序
    static class MaxSortHeap {

        public static void heapSort(int[] arr) {
            if (arr == null || arr.length < 1) {
                return;
            }
            // 1.先变成堆结构
/*
            // 1.1。可以使用heapify时间复杂度O（N）
            for (int i = arr.length-1; i >=0 ; i--) {
                heapify(arr,i,arr.length);
            }
*/

            // 1.2：可以使用heapInsert时间复杂度O（N*LogN）
            for (int i = 0; i < arr.length; i++) {
                heapInsert(arr, i);
            }
            //2.在开始排序
            int heapSize = arr.length;
            // 先交换，最后一个位置就是数组最大值，依次将heapSize--遍历，最后的arr就是排好序的
            swap(arr, 0, --heapSize);
            while (heapSize > 0) {
                heapify(arr, 0, heapSize);
                swap(arr, 0, --heapSize);
            }
        }

        public static void main(String[] args) {
            int testTime = 500000;
            int maxSize = 100;
            int maxValue = 100;
            boolean succeed = true;
            for (int i = 0; i < testTime; i++) {
                int[] arr1 = AlgorithmUtils.generateRandomArray(maxSize, maxValue);
                int[] arr2 = AlgorithmUtils.copyArray(arr1);
                heapSort(arr1);
                AlgorithmUtils.comparator(arr2);
                if (!AlgorithmUtils.isEqual(arr1, arr2)) {
                    AlgorithmUtils.printArray(arr1);
                    succeed = false;
                    AlgorithmUtils.printArray(arr2);
                    break;
                }
            }
            System.out.println(succeed ? "Nice!" : "Fucking fucked!");
        }
    }
}
