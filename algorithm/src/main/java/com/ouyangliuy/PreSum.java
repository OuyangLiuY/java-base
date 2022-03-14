package com.ouyangliuy;


// 求数组的前缀和
public class PreSum {

    /**
     * 注意：很多算法解题过程中用到了前缀和知识
     */
    public int preSum(int[] arr){
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum;
    }


}
