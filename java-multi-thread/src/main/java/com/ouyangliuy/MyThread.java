package com.ouyangliuy;

import java.util.concurrent.Semaphore;

public class MyThread {

    public static void main(String[] args) throws Exception {
        out:
        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < 10; j++) {
//                System.out.println(j+i);
////               if(j == 5){
////                   break out;
////               }
//
//            }
            for (int j = 0; j < 4; j++) {

                if(j == 2){
                    continue out;
                }
                System.out.println(i + " - " + j);
            }

        }
        System.out.println("111");

    }

    void tt() throws Exception {
        Semaphore semaphore = new Semaphore(0);

        new Thread(() -> {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("exit 1");
        }).start();


        new Thread(() -> {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("exit 2");
        }).start();

        Thread.sleep(1000);
        semaphore.release();
    }
}
