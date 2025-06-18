package com.ouyang.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class T_NotifyHoldingLock { //wait notify
	//添加volatile，使t2能够得到通知
	volatile List lists = new ArrayList();
	public void add(Object o) {
		lists.add(o);
	}
	public int size() {
		return lists.size();
	}
	public static void main(String[] args) {
		T_NotifyHoldingLock c = new T_NotifyHoldingLock();
		final Object lock = new Object();
        //需要注意先启动t2再启动t1
		new Thread(() -> {
			synchronized(lock) {
				System.out.println("t2 启动");
				if(c.size() != 5) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("t2 结束");
			}
            //通知t1继续执行
            lock.notifyAll();
		}, "t2").start();

		new Thread(() -> {
			System.out.println("t1 启动");
			synchronized(lock) {
				for(int i=0; i<10; i++) {
					c.add(new Object());
					System.out.println("add " + i);
					if(c.size() == 5) {
						lock.notify();
                        //释放锁，让t2得以执行
                        try{
                           lock.wait();
                        }catch(InterruptedException e){
					  	 e.printStackTrace();
                        }
					}
					try {
						TimeUnit.MILLISECONDS.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, "t1").start();
	}
}