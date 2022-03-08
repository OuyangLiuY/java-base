package com.ouyangliuy.juc;

import com.ouyangliuy.utils.MyUnsafe;
import sun.misc.Unsafe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ForthTopicTest {

    static  long count_offset ;
    static  long a_offset ;
    static Unsafe UNSAFE ;

    static {
        Class<ForthTopicTest> forthTopicTestClass = ForthTopicTest.class;
        try {
            UNSAFE = MyUnsafe.getTheUnsafe();
            a_offset = UNSAFE.staticFieldOffset(forthTopicTestClass.getDeclaredField("a"));
            count_offset = UNSAFE.staticFieldOffset(forthTopicTestClass.getDeclaredField("counter"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    static  int a = 0;
    static int counter = 1;

    static  void inc(){
        lock();
        a++;
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        unlock();
    }
    static void lock(){

        for(;;){
            int t = a;
            if(UNSAFE.compareAndSwapInt(ForthTopicTest.class,count_offset,1,0)){
                break;
            };
//            Thread.yield();
        }
    }
    static void  unlock(){

      counter = 1;
    }

    static  void inca(){

        for(;;){
            int t = a;
            if(UNSAFE.compareAndSwapInt(ForthTopicTest.class,a_offset,t,++t)){
                break;
            };
        }
    }

    public static void main(String[] args) throws Exception{
        ExecutorService executorService = Executors.newCachedThreadPool();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            executorService.execute(()->{
                inc();
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);
        long end = System.currentTimeMillis();
        System.out.println(end -start);
        System.out.println(a);
    }

}
