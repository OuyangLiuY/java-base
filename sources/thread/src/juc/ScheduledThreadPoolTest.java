package juc;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolTest {

    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(3);
        ScheduledThreadPoolExecutor schedule = new ScheduledThreadPoolExecutor(1);
       /* schedule.schedule(()->service.execute(()->{
                System.out.println("xxx11" + Thread.currentThread().getName());
        try {
            Thread.sleep(5000);
        }catch (Exception e){
            e.printStackTrace();
        }
        }),1, TimeUnit.SECONDS);
        schedule.schedule(()->service.execute(()->{
            System.out.println("xxx12" + Thread.currentThread().getName());
            try {
                Thread.sleep(5000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }),1, TimeUnit.SECONDS);
        schedule.schedule(()->service.execute(()->{
            System.out.println("xxx13" + Thread.currentThread().getName());
            try {
                Thread.sleep(5000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }),1, TimeUnit.SECONDS);
        schedule.schedule(()->service.execute(()->{
            System.out.println("xxx14" + Thread.currentThread().getName());
            try {
                Thread.sleep(5000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }),1, TimeUnit.SECONDS);*/

        schedule.schedule(()->{
            System.out.println("xxx14" + Thread.currentThread().getName());

        },1, TimeUnit.SECONDS);
        schedule.scheduleAtFixedRate(()->{System.out.println("xxx15" + Thread.currentThread().getName()+ "," + new Date()); },1,1,TimeUnit.SECONDS);
        schedule.scheduleWithFixedDelay(()->{
            System.out.println("xxx16" + Thread.currentThread().getName() + "," + new Date());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },1,1,TimeUnit.SECONDS);


    }
}
