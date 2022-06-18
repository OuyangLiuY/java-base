package com.ouyangliuy.monotonous_stack.problem;
import com.ouyangliuy.utils.AlgorithmUtils;

import java.util.Stack;

/**
 * https://leetcode.com/problems/sum-of-total-strength-of-wizards/
 * 求巫师力量总和问题
 * 作为国王的统治者，你有一支巫师军队听你指挥。
 * 给你一个下标从 0 开始的整数数组 strength ，
 * 其中 strength[i] 表示第 i 位巫师的力量值。
 * 对于连续的一组巫师（也就是这些巫师的力量值是 strength 的 子数组），
 * 总力量 定义为以下两个值的 乘积 ：
 * //todo 重点:巫师中 最弱 的能力值 * 组中所有巫师的个人力量值 之和 。
 * 请你返回 所有 巫师组的 总 力量之和。由于答案可能很大，请将答案对 109 + 7 取余 后返回。
 * 子数组 是一个数组里 非空 连续子序列。
 */

/**
 * 解题：
 * 1.对于任意一个位置的值，必须以这个值做子数组的最小值。故可以使用单调栈解题。
 * 大流程: (arr = [1,2,3,4])必须分别以1,2,3,4做子数组最小值，求其解法
 * 2.前缀和+前前缀和
 * todo：难点：单调栈 + 前缀和/前前缀和(拼凑答案) + mod
 */
public class LC2281_Sum_of_Total_Strength_of_Wizards {
    public static final long mod = 1000000007;

    public static int totalStrength(int[] arr) {
        if (arr == null || arr.length <= 0) {
            return 0;
        }
        int N = arr.length;
        // 前缀和
        long preSum = arr[0];
        // 前缀和的和
        long[] sumSum = new long[N];
        sumSum[0] = arr[0];
        for (int i = 1; i < N; i++) {
            preSum += arr[i];
            sumSum[i] = (sumSum[i - 1] + preSum) % mod;
        }
        // 使用数组代替单调栈结构
        int[] stack = new int[N];
        int size = 0;
        long ans = 0;
        for (int i = 0; i < N; i++) {
            while (size > 0 && arr[stack[size - 1]] >= arr[i]) {
                int m = stack[--size];
                int l = size > 0 ? stack[size - 1] : -1;
                ans += magicSum(arr, sumSum, l, m, i);
                ans %= mod;
            }
            stack[size++] = i;
        }
        while (size > 0) {
            int m = stack[--size];
            int l = size > 0 ? stack[size - 1] : -1;
            ans += magicSum(arr, sumSum, l, m, N);
            ans %= mod;
        }
        return (int) ans;
    }

    // 如果求得值，需要其演算结果，可以看图
    public static long magicSum(int[] arr, long[] sumSum, int L, int m, int R) {
        long left = (m - L) * (sumSum[R - 1] - (m - 1 >= 0 ? sumSum[m - 1] : 0) + mod) % mod;
        long right = (R - m) * ((m - 1 >= 0 ? sumSum[m - 1] : 0) - (L - 1 >= 0 ? sumSum[L - 1] : 0) + mod) % mod;
        return arr[m] * ((left - right + mod) % mod);
    }


    public static int totalStrengths(int[] arr) {
        if (arr == null || arr.length <= 0) {
            return 0;
        }
        int N = arr.length;
        // 前缀和
        long preSum = arr[0];
        // 前前缀和
        long[] pPreSum = new long[N];
        pPreSum[0] = preSum;
        for (int i = 1; i < N; i++) {
            preSum += arr[i];
            pPreSum[i] = (pPreSum[i - 1] + preSum) % mod;
        }
        long res = 0;
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < N; i++) {
            while (!stack.isEmpty() && arr[stack.peek()] >= arr[i]) {
                Integer moreIdx = stack.pop();
                int L = !stack.isEmpty() ? stack.peek() : -1;
                res += calSum(arr, pPreSum, L, moreIdx, i);
                res %= mod;
            }
            stack.push(i);
        }
        while (!stack.isEmpty()) {
            Integer moreIdx = stack.pop();
            int L = !stack.isEmpty() ? stack.peek() : -1;
            res += calSum(arr, pPreSum, L, moreIdx, N);
            res %= mod;
        }
        return (int) res;
    }

    public static long calSum(int[] arr, long[] pPreSum, int L, int x, int R) {
        int m1 = x - L;
        int m2 = R - x;
        long p1 = (pPreSum[R - 1] - ((x - 1) >= 0 ? pPreSum[x - 1] : 0) + mod);
        long p2 = (((x - 1) >= 0 ? pPreSum[x - 1] : 0) - ((L - 1) >= 0 ? pPreSum[L - 1] : 0) + mod);
        // 不考虑边界情况：pPreSum[R - 1] - pPreSum[x - 1]
        // 不考虑边界情况：pPreSum[x - 1] -pPreSum[L - 1]
        return arr[x] * (((m1 * p1 - m2 * p2) % mod + mod) % mod);
    }

    public static void main(String[] args) {
        int maxSize = 1000;
        int maxValue = 1000;
        int testTime = 100000;
        System.out.println("start!");
        for (int i = 0; i < testTime; i++) {
            int[] arr1 = AlgorithmUtils.generateRandomArray(maxSize, maxValue);
            int[] arr2 = AlgorithmUtils.copyArray(arr1);
            int re1 = totalStrengths(arr1);
            int re2 = totalStrength(arr2);
            if (re1 != re2) {
                System.out.println("Oops !!!!");
                System.out.println(re1);
                System.out.println(re2);
            }
        }
        System.out.println("finish!");
    }
}
