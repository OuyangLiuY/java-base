package com.ouyangliuy.base.questions;

import java.util.LinkedList;

/**
 * 一个固定容量同步容器，拥有put和get方法，以及getCount方法，
 * 能够支撑2个生产者线程以及10个消费者线程的阻塞调用。
 *
 * @param <T>
 */
public class Q_Container_Sync<T> {

    LinkedList<T> item;
    int max = 10;
    int count = 0;

    public Q_Container_Sync() {
        this.item = new LinkedList<>();
    }

    public synchronized T get() {
        // 注意：这里要用while，因为如果队列为空的情况下，要阻塞等待
        while (item.size() == 0) {
            try {
                this.wait();     // 调用wait释放当前持有的锁
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        T t = item.removeFirst();
        --count;
        this.notifyAll();
        return t;
    }

    public synchronized void put(T val) {
        // 注意：这里要用while，因为如果队列满的情况下，要阻塞等待
        while (item.size() == max) {
            try {
                this.wait();    // 调用wait释放当前持有的锁
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        item.add(val);
        ++count;
        this.notifyAll(); // 通知消费者拿数据
    }

    public int getCount() {
        return this.count;
    }


}
