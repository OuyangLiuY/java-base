

## JUC之如何实现一把锁？

1. 如何表示锁状态，无锁，有锁？

   > 无重入：true有锁 ，false无锁
   >
   > 有重入：int times，使用变量。
   >
   > 什么时候发生锁重入：被锁的方法可能被多个方法调用
   >
   > 优化：使用int state；来表示锁状态和重入次数

2. 如何保证多线程枪锁安全

   > CAS

3. 如何处理获取不到锁的线程

   > 自旋（不停重复某个操作，直到条件满足或次数达到限制），
   >
   > 阻塞：达不到条件，告诉操作系统，让我阻塞（放入到阻塞队列）
   >
   > 自旋锁消耗的时间，大约上下文切换所需要的时间，那么就选择阻塞，否则，自旋
   >
   > 自旋+阻塞：自旋到一定次数，升级为阻塞

4. 如何释放锁

   > 自旋：自己抢锁
   >
   > 阻塞：唤醒

### 自旋优缺点：

**缺点：**CPU占用不干事，导致性能障碍，占着茅坑不拉屎

**优点：**适用于执行步骤比较少且块的操作，自旋一会马上就能获取到锁，这也不会消耗太多的CPU资源

**注意：**当CPU个数增加且线程数增加的情况下，优点会退化成自旋锁的缺点

**适用场景：**争用比较少且代码小的临界区（线程少，并发低）



### 从如何实现锁到什么是AQS？

Abstract：因为不知道怎么上锁，模板方法设计模式即可，暴露上锁逻辑

Queue：线程阻塞队列

Synchronize：同步

CAS+state ：完成多线程枪锁逻辑，Queue完成抢不到的锁的线程排队。



## AQS核心代码

AbstractQueuedSynchronizer

acquire-获取锁

```java
/**
 * Acquires in exclusive mode, ignoring interrupts.  Implemented
 * by invoking at least once {@link #tryAcquire},
 * returning on success.  Otherwise the thread is queued, possibly
 * repeatedly blocking and unblocking, invoking {@link
 * #tryAcquire} until success.  This method can be used
 * to implement method {@link Lock#lock}.
 *
 * @param arg the acquire argument.  This value is conveyed to
 *        {@link #tryAcquire} but is otherwise uninterpreted and
 *        can represent anything you like.
 */
public final void acquire(int arg) {
    if (!tryAcquire(arg) &&		// 子类获取锁失败，返回false，
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg))	// 获取失败后添加到阻塞队列
        selfInterrupt();
}

// 子类实现获取锁的逻辑，AQS并不知道怎么这个state来上锁
 protected boolean tryAcquire(int arg) {
      throw new UnsupportedOperationException();
  }
```

release-释放锁

```java
 /**
     * Releases in exclusive mode.  Implemented by unblocking one or
     * more threads if {@link #tryRelease} returns true.
     * This method can be used to implement method {@link Lock#unlock}.
     *
     * @param arg the release argument.  This value is conveyed to
     *        {@link #tryRelease} but is otherwise uninterpreted and
     *        can represent anything you like.
     * @return the value returned from {@link #tryRelease}
     */
public final boolean release(int arg) {
        if (tryRelease(arg)) {	// 子类实现释放锁逻辑
            // 检查阻塞队列，唤醒即可
            Node h = head;
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
       }
        return false;
}
// 子类实现释放锁的逻辑
protected boolean tryRelease(int arg) {
    throw new UnsupportedOperationException();
}
```

总结：

> 子类只需要实现自己获取锁逻辑和释放锁逻辑即可，至于排队阻塞等待，唤醒机制均由AQS来完成

## ReentrantLock原理

概念

### 核心变量和构造器

```java
public class ReentrantLock implements Lock, java.io.Serializable {
    private static final long serialVersionUID = 7373984872572414699L;
    /** Synchronizer providing all implementation mechanics */
    private final Sync sync;

    /**
     * Creates an instance of {@code ReentrantLock}.
     * This is equivalent to using {@code ReentrantLock(false)}.
     */
    public ReentrantLock() {
        sync = new NonfairSync();
    }

    /**
     * Creates an instance of {@code ReentrantLock} with the
     * given fairness policy.
     *
     * @param fair {@code true} if this lock should use a fair ordering policy
     */
    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }
  
}
```

### 公平和非公平

公平和非公平是针对排队的线程

公平：直接放入到阻塞队列，不抢

非公平：不管是否有线程排队，先枪锁。抢不到再进入队列

为什么非公平性能要高于公平：因为先放入到队列，阻塞等待，需要状态切换也即上下文切换

> 总结：线程上下文切换和延迟调度

```c
   /**
     * Sync object for non-fair locks
     */
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        /**
         * Performs lock.  Try immediate barge, backing up to normal
         * acquire on failure.
         */
        final void lock() {
            // 非公平，直接抢锁，不管有没有线程排队
            if (compareAndSetState(0, 1))
                // 上锁成功，那么标识当前线程为获取锁的线程
                setExclusiveOwnerThread(Thread.currentThread());
            else
                // 抢锁失败，进入AQS标准获取锁流程
                acquire(1);
        }

        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
    }

    /**
     * Sync object for fair locks
     */
    static final class FairSync extends Sync {
        private static final long serialVersionUID = -3000897897090466540L;
		
        // ReentrantLock 调用
        final void lock() {
            // 直接进入AQS标准获取锁的流程
            acquire(1);
        }

        /**
         * Fair version of tryAcquire.  Don't grant access unless
         * recursive call or no waiters or is first.
         */
        // AQS调用，子类实现锁的流程
        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            // 此时正好有线程释放了锁
            if (c == 0) {
                // 注意：这里和非公平锁的区别在于：hasQueuePredecessors看队列中是否有线程正在排队，如果没有，那么就尝试使用CAS抢锁
                if (!hasQueuedPredecessors() &&
                    compareAndSetState(0, acquires)) {
                    // 抢锁成功
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            // 当前线程就是获取锁的线程，那么这里就是锁重入，和非公平锁操作一样
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            // 返回false说明获取锁失败，需要AQS将线程放入到阻塞队列，等待唤醒
            return false;
        }
    }
```



核心方法-lock

```java
public void lock() {
    sync.lock();
}
```

### Sync

```java
abstract static class Sync extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = -5179523762034025860L;

    /**
     * Performs {@link Lock#lock}. The main reason for subclassing
     * is to allow fast path for nonfair version.
     */
    abstract void lock();

    /**
     * Performs non-fair tryLock.  tryAcquire is implemented in
     * subclasses, but both need nonfair try for trylock method.
     */
    // 非公平锁标准获取锁方法
    final boolean nonfairTryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        // 执行到这里，正好获取锁的线程释放了锁，那么可以尝试抢锁
        if (c == 0) {
            // 继续抢锁，不管有没有线程排队
            if (compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        // 当前线程就是持有锁的线程，表示锁重入
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            // int超过了范围，表示符号溢出，所以抛出异常
            if (nextc < 0) // overflow
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }
	// 公平锁和非公平锁的公用方法，因为再释放锁的时候，不需要区分是否公平
    protected final boolean tryRelease(int releases) {
        int c = getState() - releases;
        // 当前线程不是上锁的线程，抛出异常
        if (Thread.currentThread() != getExclusiveOwnerThread())
            throw new IllegalMonitorStateException();
        boolean free = false;
        // 不是重入锁，那么当前线程一定是释放了锁，然后我们把当前AQS用于保存当前锁对象的变量exclusiveOwnerThread设置为null，表明当前线程没有锁
        if (c == 0) {
            free = true;
            setExclusiveOwnerThread(null);
        }
        // 注意：此时state全局变量没有改变，也就是意味着setState之前，没有别的线程能够获取锁，这保证了以上操作的原子性
        setState(c);
        // 返回状态，表示我锁释放成功
        return free;
    }

    protected final boolean isHeldExclusively() {
        // While we must in general read state before owner,
        // we don't need to do so to check if current thread is owner
        return getExclusiveOwnerThread() == Thread.currentThread();
    }

    final ConditionObject newCondition() {
        return new ConditionObject();
    }


    /**
     * Reconstitutes the instance from a stream (that is, deserializes it).
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        setState(0); // reset to unlocked state
    }
}
```

读写锁：

互斥锁：

1. 读锁

   就是用于读互斥区中保护的变量

2. 写锁

   就是用于写互斥区中保护的变量

3. 锁升级

   线程A获取了读锁，此时想要释放读锁，获取写锁

4. 锁降级

   线程A获取了写锁，此时想要释放写锁，获取读锁

```java
public interface ReadWriteLock {
    /**
     * Returns the lock used for reading.
     *
     * @return the lock used for reading
     */
    Lock readLock();

    /**
     * Returns the lock used for writing.
     *
     * @return the lock used for writing
     */
    Lock writeLock();
}
```



## ReentrantReadWriteLock

```
ReentrantReadWriteLock
```

释放读锁流程:

1.  先减掉当前线程持有的读锁标记位的大小
2.  然后减掉全局的共享锁的大小
3.  如果状态减完之后为0了，那么如果有等待的写线程，那么就唤醒，没有直接退出

读写锁实现原理：

> 如果想要知道当前线程获取到了多少次共享锁，也即重入了多少次共享锁怎么搞？
>
> 因为state得高16位是所有读线程共享得位，通过ThreadLocal来记录每个线程获取了多少次共享锁即可，所以我们能用state得高16位用于存储所有读线程获取共享锁得次数，TL用于表示当前线程自己得重入次数。
>
> 总共得次数 = （All Thread's TL 求和) 
>
> 注意：写锁，不需要记录进TL，因为互斥，用state得低16位即可
>
> 场景：几乎所有时间，都是同一线程获取读锁，那么有没有必要使用TL，TL占用内存，
>
> 优化：再读写锁总维护一个：first 获取读锁得变量和线程对象即可。
>


状态和线程数

```java
        // 对于int
        // 高16位存储所有读线程获取共享锁得次数
        // 低16位存储当前所有线程个数，包括读/写
        static final int SHARED_SHIFT   = 16;
        // 
        static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
        static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
        static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

        /** Returns the number of shared holds represented in count  */
        static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
        /** Returns the number of exclusive holds represented in count  */
        static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }
```


tryAcquireShared

获取共享锁

```java
protected final int tryAcquireShared(int unused) {
    /*
     * Walkthrough:
     * 1. If write lock held by another thread, fail.
     * 2. Otherwise, this thread is eligible for
     *    lock wrt state, so ask if it should block
     *    because of queue policy. If not, try
     *    to grant by CASing state and updating count.
     *    Note that step does not check for reentrant
     *    acquires, which is postponed to full version
     *    to avoid having to check hold count in
     *    the more typical non-reentrant case.
     * 3. If step 2 fails either because thread
     *    apparently not eligible or CAS fails or count
     *    saturated, chain to version with full retry loop.
     */
    Thread current = Thread.currentThread();
    int c = getState();
    if (exclusiveCount(c) != 0 && // 解析下低
        getExclusiveOwnerThread() != current)
        return -1;
    int r = sharedCount(c);
    if (!readerShouldBlock() &&
        r < MAX_COUNT &&
        compareAndSetState(c, c + SHARED_UNIT)) {
        if (r == 0) {
            firstReader = current;
            firstReaderHoldCount = 1;
        } else if (firstReader == current) {
            firstReaderHoldCount++;
        } else {
            HoldCounter rh = cachedHoldCounter;
            if (rh == null || rh.tid != getThreadId(current))
                cachedHoldCounter = rh = readHolds.get();
            else if (rh.count == 0)
                readHolds.set(rh);
            rh.count++;
        }
        return 1;
    }
    return fullTryAcquireShared(current);
}
```

fullTryAcquireShared

获取共享锁

```java
/**
 * Full version of acquire for reads, that handles CAS misses
 * and reentrant reads not dealt with in tryAcquireShared.
 */
final int fullTryAcquireShared(Thread current) {
    /*
     * This code is in part redundant with that in
     * tryAcquireShared but is simpler overall by not
     * complicating tryAcquireShared with interactions between
     * retries and lazily reading hold counts.
     */
    HoldCounter rh = null;
    for (;;) {
        int c = getState();
        if (exclusiveCount(c) != 0) {
            if (getExclusiveOwnerThread() != current)
                return -1;
            // else we hold the exclusive lock; blocking here
            // would cause deadlock.
        } else if (readerShouldBlock()) {
            // Make sure we're not acquiring read lock reentrantly
            if (firstReader == current) {
                // assert firstReaderHoldCount > 0;
            } else {
                if (rh == null) {
                    rh = cachedHoldCounter;
                    if (rh == null || rh.tid != getThreadId(current)) {
                        rh = readHolds.get();
                        if (rh.count == 0)
                            readHolds.remove();
                    }
                }
                if (rh.count == 0)
                    return -1;
            }
        }
        if (sharedCount(c) == MAX_COUNT)
            throw new Error("Maximum lock count exceeded");
        if (compareAndSetState(c, c + SHARED_UNIT)) {
            if (sharedCount(c) == 0) {
                firstReader = current;
                firstReaderHoldCount = 1;
            } else if (firstReader == current) {
                firstReaderHoldCount++;
            } else {
                if (rh == null)
                    rh = cachedHoldCounter;
                if (rh == null || rh.tid != getThreadId(current))
                    rh = readHolds.get();
                else if (rh.count == 0)
                    readHolds.set(rh);
                rh.count++;
                cachedHoldCounter = rh; // cache for release
            }
            return 1;
        }
    }
}
```

### tryReleaseShared

释放共享锁过程：

1. 检查当前线程是否是firstReaderHoldCount，如果是，count大于1，count--，否则firstReader置空
2. 否则检查一下rh=cachedHoldCounter，此时这个缓存时last缓存，如果rh不存在，拿到当前线程的tid，获取一下rh对象，并获取rh的count，如果count小于等于1，移除readHolds读锁持有的本地缓存，最后count--
3. 最后获取全局状态，并更新全局得状态为读锁减去1的状态。注意是int的高16位。

```java
protected final boolean tryReleaseShared(int unused) {
    Thread current = Thread.currentThread();
    if (firstReader == current) {
        // assert firstReaderHoldCount > 0;
        if (firstReaderHoldCount == 1)
            firstReader = null;
        else
            firstReaderHoldCount--;
    } else {
        HoldCounter rh = cachedHoldCounter;
        if (rh == null || rh.tid != getThreadId(current))
            rh = readHolds.get();
        int count = rh.count;
        if (count <= 1) {
            readHolds.remove();
            if (count <= 0)
                throw unmatchedUnlockException();
        }
        --rh.count;
    }
    for (;;) {
        int c = getState();
        int nextc = c - SHARED_UNIT;
        if (compareAndSetState(c, nextc))
            // Releasing the read lock has no effect on readers,
            // but it may allow waiting writers to proceed if
            // both read and write locks are now free.
            return nextc == 0;
    }
}
```