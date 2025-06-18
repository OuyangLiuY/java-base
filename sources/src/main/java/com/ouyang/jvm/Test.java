package com.ouyang.jvm;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Test {
    public static void main(String[] args) throws Exception {
//        process(1);
        Map<Object, Object> hs = new HashMap<>();
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 10; i++) {
                hs.put(i, i);
            }
        });
        future.join();
        hs.forEach((k, v) -> {
            System.out.println(k + "-" + v);
        });


        while (true){
            Object o = new Object();
            //System.out.println(o);
        }
    }


    private static void process(long n) {
        if (n == Long.MAX_VALUE) {
            return;
        }
        System.out.println(n);
        process(n + 1);
    }
}
