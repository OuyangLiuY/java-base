package com.ouyang.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPool extends ThreadPoolExecutor {
    public MyThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * 抛出异常会，继续执行任务，但是这个任务是会新创建一个任务。
     * @param r
     * @param t
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        System.out.println(1/0);
    }

    public static void main(String[] args) {
        MyThreadPool myThreadPool = new MyThreadPool(1,1,1,TimeUnit.DAYS,new LinkedBlockingDeque<>());
        myThreadPool.execute(()-> {System.out.println(1);
            System.out.println(Thread.currentThread().getName());});  myThreadPool.execute(()-> {System.out.println(1);
            System.out.println(Thread.currentThread().getName());});  myThreadPool.execute(()-> {System.out.println(1);
            System.out.println(Thread.currentThread().getName());});  myThreadPool.execute(()-> {System.out.println(1);
            System.out.println(Thread.currentThread().getName());});

        myThreadPool.terminated();

    }
}
