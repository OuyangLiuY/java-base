package base.questions;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 一个固定容量同步容器，拥有put和get方法，以及getCount方法，
 * 能够支撑2个生产者线程以及10个消费者线程的阻塞调用。
 *
 * @param <T>
 */
public class Q_Container_Lock<T> {

    LinkedList<T> item;
    int max = 10;
    int count = 0;
    ReentrantLock lock = new ReentrantLock();
    Condition consumer = lock.newCondition();
    Condition producer = lock.newCondition();

    public Q_Container_Lock() {
        this.item = new LinkedList<>();
    }

    public T get() {
        T t = null;
        try {
            lock.lock();
            while (item.size() == 0) {
                try {
                    consumer.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            t = item.removeFirst();
            --count;
            producer.signalAll();
        } finally {
            lock.unlock();
        }
        return t;
    }

    public synchronized void put(T val) {
        try {
            lock.lock();
            while (item.size() == max) {
                try {
                    producer.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            item.add(val);
            ++count;
            consumer.signalAll(); // 通知消费者拿数据
        }finally {
            lock.unlock();
        }

    }

    public int getCount() {
        return this.count;
    }
}
