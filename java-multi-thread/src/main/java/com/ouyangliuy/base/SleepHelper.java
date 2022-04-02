package com.ouyangliuy.base;

import java.util.concurrent.TimeUnit;

public class SleepHelper {
    public static void sleepSeconds(int second) {
        try {
            Thread.sleep(second * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
