package com.ouyangliuy.juc;

import com.ouyangliuy.utils.MyUnsafe;
import sun.misc.Unsafe;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class MultiThread {

    public static void main(String[] args) {
        Unsafe unsafe = MyUnsafe.getTheUnsafe();
        unsafe.loadFence();
        FutureTask<Integer> f = new FutureTask<>(new TT());
        new Thread(f,"xx").start();
    }

    static class TT implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            return 1;
        }
    }
}
