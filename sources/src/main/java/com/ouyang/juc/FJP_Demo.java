package com.ouyang.juc;


import java.sql.Time;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class FJP_Demo {



    public static void main(String[] args) throws Exception{

        ForkJoinPool fjp = new ForkJoinPool();
        fjp.submit(()->{
            System.out.println("1111111");
        });
        fjp.shutdown();
        fjp.awaitTermination(1, TimeUnit.DAYS);


        int p = 3; // ensure at least 4 slots
        int n = (p > 1) ? p - 1 : 1; // n最小是1，那么n运行下面代码之后是4
        // 就是传入的数，去它的最近的2的倍数
        n |= n >>> 1; n |= n >>> 2;  n |= n >>> 4;
        n |= n >>> 8; n |= n >>> 16;
        System.out.println(n);
        n = (n + 1) << 1;
        System.out.println(n);

        System.out.println(Integer.toBinaryString(1<<31));
        System.out.println(Integer.toBinaryString((1<<31)-1));
        System.out.println(Integer.toBinaryString(-1));
        System.out.println(1<<31);
        System.out.println((1<<31)-1);


       long ADD_WORKER = 0x0001L << (32 + 15); //

        System.out.println(ADD_WORKER);
        System.out.println(Long.toBinaryString(ADD_WORKER));

    }
}
