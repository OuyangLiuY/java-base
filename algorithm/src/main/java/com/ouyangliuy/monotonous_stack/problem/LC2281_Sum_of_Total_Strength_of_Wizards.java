package com.ouyangliuy.monotonous_stack.problem;

/**
 * https://leetcode.com/problems/sum-of-total-strength-of-wizards/
 * 求巫师力量总和问题
 * 作为国王的统治者，你有一支巫师军队听你指挥。
 * 给你一个下标从 0 开始的整数数组 strength ，
 * 其中 strength[i] 表示第 i 位巫师的力量值。
 * 对于连续的一组巫师（也就是这些巫师的力量值是 strength 的 子数组），
 * 总力量 定义为以下两个值的 乘积 ：
 * 巫师中 最弱 的能力值 * 组中所有巫师的个人力量值 之和 。
 * 请你返回 所有 巫师组的 总 力量之和。由于答案可能很大，请将答案对 109 + 7 取余 后返回。
 * 子数组 是一个数组里 非空 连续子序列。
 */
public class LC2281_Sum_of_Total_Strength_of_Wizards {
    public static final long mod = 1000000007;

    public int totalStrength(int[] arr) {
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
}
