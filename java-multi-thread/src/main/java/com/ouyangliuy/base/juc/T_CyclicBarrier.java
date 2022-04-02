package com.ouyangliuy.base.juc;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class T_CyclicBarrier {
    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(20, () -> System.out.println("满人"));
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    System.out.println("i=" + finalI);
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
