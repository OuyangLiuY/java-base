package jvm.oom;

import java.util.concurrent.TimeUnit;

public class StackOverflowError {


    public static void main(String[] args) {
        int length = 1;
//        stackOOM(length);
        stackOOMThread();
    }

    private static void stackOOMThread() {
        for (int i = 0; i < 24; i++) {
            new Thread(()->dontStop()).start();
        }
    }

    private static void dontStop() {
        System.out.println(Thread.currentThread().getName());
        while (true){
            try {
//                System.out.println("xxx");
                TimeUnit.NANOSECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void stackOOM(int length) {
        System.out.println(length);
        stackOOM(length + 1);
    }

}
