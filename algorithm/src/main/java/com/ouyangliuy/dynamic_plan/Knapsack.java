package com.ouyangliuy.dynamic_plan;

// 经典背包问题：
public class Knapsack {
    public static int maxValue1(int[] w, int[] v, int bag) {
        if (w == null || v == null || w.length != v.length || bag < 0) {
            return -1;
        }
        return process1(w, v, 0, bag);
    }

    private static int process1(int[] w, int[] v, int idx, int rest) {
        if (rest < 0)
            return -1;
        if (idx == w.length)
            return 0;
        int p1 = 0;
        int curV = v[idx];
        int next = process1(w, v, idx + 1, rest - w[idx]); //要当前位置
        if (next != -1)
            p1 = curV + next;
        int p2 = process1(w, v, idx + 1, rest);// 不要当前位置
        return Math.max(p1, p2);
    }

    public static int dp(int[] w, int[] v, int bag) {
        if (w == null || v == null || w.length != v.length || bag < 0) {
            return -1;
        }
        int N = v.length;
        int dp[][] = new int[N + 1][bag + 1];

    }
}
