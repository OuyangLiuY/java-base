

# 1、JUC之如何实现一把锁？

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

# 2、自旋优缺点：

**缺点：**CPU占用不干事，导致性能障碍，占着茅坑不拉屎

**优点：**适用于执行步骤比较少且块的操作，自旋一会马上就能获取到锁，这也不会消耗太多的CPU资源

**注意：**当CPU个数增加且线程数增加的情况下，优点会退化成自旋锁的缺点

**适用场景：**争用比较少且代码小的临界区（线程少，并发低）



# 3、从如何实现锁到什么是AQS？

Abstract：因为不知道怎么上锁，模板方法设计模式即可，暴露上锁逻辑

Queue：线程阻塞队列

Synchronize：同步

CAS+state ：完成多线程枪锁逻辑，Queue完成抢不到的锁的线程排队。



# 4、AQS核心代码

## 1.2.1、AbstractQueuedSynchronizer

当前类是AQS核心代码，该类是抽象类，提供了自己的默认实现，并且定义了规范，如：获取锁/释放锁。具体由子类来实现。

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

# 5、ReentrantLock

**Concept：**

```c
/*
* A ReentrantLock is owned by the thread last
* successfully locking, but not yet unlocking it. A thread invoking
* lock will return, successfully acquiring the lock, when
* the lock is not owned by another thread. The method will return
* immediately if the current thread already owns the lock. This can
* be checked using methods isHeldByCurrentThread() and getHoldCount().
*/
```

## 1.3.1、Field & Construct:

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

## 1.3.2、Fair & NonFair

**Concept：**

> 公平和非公平是针对排队的线程
>
> 公平：直接放入到阻塞队列，不抢
>
> 非公平：不管是否有线程排队，先枪锁。抢不到再进入队列

**为什么非公平性能要高于公平：**

因为先放入到队列，阻塞等待，需要状态切换也即上下文切换

> 总结：线程上下文切换和延迟调度

### 1.3.2.1、Nonfair

```java
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
```

### 1.3.2.2、Fair

```c
  

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

## 1.3.3、Sync

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

# 6、读写锁：

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



## 6.1、读写锁实现原理



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

## 6.2、Sync

**State Field:**

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

**Class Field：**

```java
/** The hold count of the last thread to successfully acquire readLock. */
// 最近刚获取锁的线程缓存的count
private transient HoldCounter cachedHoldCounter; 
/** The number of reentrant read locks held by current thread.*/
// 当前线程持有的可重入读锁的次数。
private transient ThreadLocalHoldCounter readHolds;

```



### 6.2.1、tryRelease

释放锁过程：

1. 检查线程是否合法
2. 拿到需要减去的状态值，
3. 检查当前线程所共享锁的次数是否为0
4. 如果为0，需要修改锁线程为null，否则只需要修改状态即可。

```java
protected final boolean tryRelease(int releases) {
    if (!isHeldExclusively()) //检查操作释放lock的线程是否是当前获取锁的线程
        throw new IllegalMonitorStateException();
    int nextc = getState() - releases; // 修改状态
    boolean free = exclusiveCount(nextc) == 0; // 查看是否共享次数为0，为0，说明需要释放，不然只需要将状态值减1
    if (free)
        setExclusiveOwnerThread(null);
    setState(nextc);
    return free;
}
```

### 6.2.2、tryAcquireShared

获取共享锁：



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
    if (exclusiveCount(c) != 0 && // 解析低16位，获取线程持有数，如果不为0，说明有线程获取过锁，那么检查当前线程是否是已经获取锁的线程。如果不是，直接返回，获取失败。
        getExclusiveOwnerThread() != current)
        return -1;
    int r = sharedCount(c); //获取共享锁的高16位所代表的数量
    if (!readerShouldBlock() && // 不需要被block
        r < MAX_COUNT && // r合法
        compareAndSetState(c, c + SHARED_UNIT)) { // CAS修改变量
        if (r == 0) {	// r为0，说明是第一次尝试获取该锁
            firstReader = current;
            firstReaderHoldCount = 1;
        } else if (firstReader == current) { // 说明第一个获取锁的线程又来获取锁
            firstReaderHoldCount++; // holdCount++
        } else {
            // 说明其他线程获取共享锁，那么检查
            HoldCounter rh = cachedHoldCounter;
            if (rh == null || rh.tid != getThreadId(current))
                cachedHoldCounter = rh = readHolds.get();
            else if (rh.count == 0) // 说明其他线程是第一次来，
                readHolds.set(rh);
            rh.count++;				// 否则需要将其count++
        }
        return 1;
    }
    // 什么情况会调用该方法呢？
    // 1.CAS失败，
    // 2.需要block
    return fullTryAcquireShared(current);
}
```

### 6.2.3、fullTryAcquireShared

获取共享锁

```java
/**
 * Full version of acquire for reads, that handles CAS misses
 * and reentrant reads not dealt with in tryAcquireShared.
 */
// 获取读取的完整版本，它处理CAS未命中和在可重入读取方法tryAcquireShared中未处理
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
        } else if (readerShouldBlock()) { //说明，读锁是不能被重入。
            // Make sure we're not acquiring read lock reentrantly
            if (firstReader == current) {
                // assert firstReaderHoldCount > 0;
            } else {
                if (rh == null) {
                    rh = cachedHoldCounter;
                    if (rh == null || rh.tid != getThreadId(current)) {
                        rh = readHolds.get();
                        if (rh.count == 0)
                            readHolds.remove(); //删除reader持有的缓存。
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

### 6.2.4、tryReleaseShared

释放共享锁过程：

1. 检查当前线程是否是firstReaderHoldCount，如果是，count大于1，count--，否则firstReader置空
2. 否则检查一下rh=cachedHoldCounter，此时这个缓存时last缓存，如果rh不存在，拿到当前线程的tid，获取一下rh对象，并获取rh的count，如果count小于等于1，移除readHolds读锁持有的本地缓存，最后count--
3. 最后获取全局状态，并更新全局得状态为读锁减去1的状态。注意是int的高16位。

```java
protected final boolean tryReleaseShared(int unused) {
    Thread current = Thread.currentThread();
    // 因为它最先会被设置，所以需要修改其状态。
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
        --rh.count; // 修改持有count
    }
    // 相当于加锁修改其全局状态，只有CAS成功才会返回成功，否则会失败。保证原子性和线程安全。
    // 为什么要for循环，因为有可能其他线程同样也在修改这个状态。所以需要不断尝试。
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