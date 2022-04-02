# 1、线程的基本概念

之前的硬件，只有一个CPU

之前的OS，只运行一个进程

随着多核CPU的出现，人们开始追求对CPU效率的极致压榨

多线程的程序随之诞生，但随之诞生的，也是非常难以应对的各种并发bug

## 1.1、线程/进程

1. 什么是进程：资源分配的基本单位（静态概念）
2. 什么是线程：资源调度的基本单位（动态概念）
3. 通俗来说：线程是CPU执行的最小单位，进程是CPU，内存，磁盘等资源分配的最小单位。

## 1.2、为什么要写多线程程序

> 为了压栈CPU，提高资源利用率

## 1.3、启动线程的5中方法

1. new MyThread().start();
2. new Thread(r).start();
3. new Thread(()->{}).start();
4. ThreadPool
5. Future Callable and FutureTask

## 1.4、常见线程方法

### sleep()

> Sleep,意思就是睡眠，当前线程暂停一段时间让给别的线程去运行。Sleep是怎么复活的？由你的睡眠时间而定，等睡眠到规定的时间自动复活。

```c
 static void testSleep() {
        new Thread(()->{
            for(int i=0; i<100; i++) {
                System.out.println("A" + i);
                try {
                    Thread.sleep(500);
                    //TimeUnit.Milliseconds.sleep(500)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
```



### yield()

> Yield,就是当前线程正在执行的时候停止下来进入等待队列（就绪状态，CPU依然有可能把这个线程拿出来运行），回到等待队列里在系统的调度算法里头呢还是依然有可能把你刚回去的这个线程拿回来继续执行，当然，更大的可能性是把原来等待的那些拿出一个来执行，所以yield的意思是我让出一下CPU，后面你们能不能抢到那我不管。

```java
static void testYield() {
        new Thread(()->{
            for(int i=0; i<100; i++) {
                System.out.println("A" + i);
                if(i%10 == 0) Thread.yield();


            }
        }).start();

        new Thread(()->{
            for(int i=0; i<100; i++) {
                System.out.println("------------B" + i);
                if(i%10 == 0) Thread.yield();
            }
        }).start();
    }
```



### join()

> join， 意思就是在自己当前线程加入你调用Join的线程（），本线程等待。等调用的线程运行完了，自己再去执行。t1和t2两个线程，在t1的某个点上调用了t2.join,它会跑到t2去运行，t1等待t2运行完毕继续t1运行（自己join自己没有意义） 

```java
static void testJoin() {
        Thread t1 = new Thread(()->{
            for(int i=0; i<100; i++) {
                System.out.println("A" + i);
                try {
                    Thread.sleep(500);
                    //TimeUnit.Milliseconds.sleep(500)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(()->{
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(int i=0; i<100; i++) {
                System.out.println("A" + i);
                try {
                    Thread.sleep(500);
                    //TimeUnit.Milliseconds.sleep(500)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
    }
```

### wait

### notify

### notifyAll

## 1.5、线程的状态

### 1.5.1、Java的6种线程状态

1. NEW：												线程刚创建，还没启动
2. RUNNABLE：                                     可运行状态，由线程调度器安排执行（包括READY和RUNNING状态）
3. WAITING：                                         等待被唤醒
4. TIMED WAITING：                             隔一段时间后自动唤醒
5. BLOCKED：                                        被阻塞，正在等待锁
6. TERMINATED：                                  线程结束

![](../doc-all/images/muti-thread/thread_states.png)

线程调用Start()方法，会被线程调度器来执行，交给操作系统执行，操作系统来执行的时候，整个状态叫Runnable，Runnable内部有两个状态，**READY就绪状态**和**Running运行状态**。

> **READY状态和RUNNING状态变化**：就绪状态是将线程放到CPU等待队列里面去排队，等真正在CPU上运行的时候叫Running状态。调用**yiled**方法的时候会将线程从Running状态变到Ready状态，线程调度器选中执行的时候，又从ready状态变到Running状态。

线程顺利结束完，就会进入到**TERMINATED结束状态**。

**RUNNABLE：**

这个状态里还有一些状态变迁，**TimedWaiting等待**、**Waiting等待**、**Blocked阻塞**，在同步代码块的情况就下没得到锁就会**阻塞状态**，获得锁的时候就绪运行状态，在运行的时候如果调用了 o.wait()，o.join()，LockSupport.park()，进入Waiting状态。调用o.notify()，o.notifyAll()，LockSupport.unpark()，就会又进入到Running状态。

**TimedWaiting**按时间等待，等时间结束自己就回去了比如如下方法都是：

> Thread.sleep(time)，o.wait(time)、t.jion(time)、LockSupport.parkNanos()、LockSupport.parkUntil()

### 15.2、问题1：线程状态哪些是JVM管理的？哪些是操作系统管理的？

> 这些状态全是由JVM管理的，因为JVM管理的时候也要通过操作系统，所以呢，那个是操作系统和那个是JVM他俩分不开，JVM是跑在操作系统上的一个普通程序。

### 15.3、问题2：线程什么状态时候会被挂起？挂起是否也是一个状态？

> Running的时候，在一个cpu上会跑很多个线程，cpu会隔一段时间执行这个线程一下，在隔一段时间执行那个线程一下，这个是cpu内部的一个调度，把这个状态线程扔出去，从running扔回去就叫线程被挂起，cpu控制它。

## 1.5.2、线程中断（Interrupt）

### Interrupt相关方法：

1. interrupt()：	实例方法，设置线程中断标记
2. isInterrupted()：实例方法，有没有人中断过我？意思就是有人调用了interrupt方法吗
3. interrupted()：静态方法，有没有人中断过我？如果有，恢复标记位。

```java
//Thread.java  
public void interrupt()            // t.interrupt() 打断t线程（设置t线程某给标志位f=true，并不是打断线程的运行）
public boolean isInterrupted()     //t.isInterrupted() 查询打断标志位是否被设置（是不是曾经被打断过）
public static boolean interrupted()//Thread.interrupted() 查看“当前”线程是否被打断，如果被打断，恢复标志位
```

### Interrupt是否能中断正在竞争锁的线程？

> 答：interrupt不能中断正在竞争synchronized锁的线程

```java
public class T_Interrupt_Synchronized {

    private static final Object o = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(()-> {
            synchronized (o) {
                SleepHelper.sleepSeconds(10);
            }
        });
        t1.start();
        SleepHelper.sleepSeconds(1);
        Thread t2 = new Thread(()-> {
            synchronized (o) {
            }
            System.out.println("t2 end!");
        });
        t2.start();
        t2.interrupt();
    }
}
```

### 如何中断正在竞争锁的线程？

> 答：使用ReentrantLock的lockInterruptibly()方法

```java
public class T_Interrupt_LockInterruptibility {
    private static final ReentrantLock lock = new ReentrantLock();
    public static void main(String[] args) {
        Thread t1 = new Thread(()-> {
            lock.lock();
            System.out.println("t1 start!");
            try {
                SleepHelper.sleepSeconds(10);
            } finally {
                lock.unlock();
            }
            System.out.println("t1 end!");
        });
        t1.start();
        SleepHelper.sleepSeconds(1);
        Thread t2 = new Thread(()-> {
            System.out.println("t2 start!");
            try {
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            System.out.println("t2 end!");
        });
        t2.start();
        SleepHelper.sleepSeconds(1);
        t2.interrupt();
    }
}
```

### 如何优雅的结束线程

1. 自然运行完毕结束
2. stop(); suspend(); resume();
3. volatile标志
   1. 不适合某些场景（比如还没有同步的时候，线程做了阻塞操作，没有办法循环回去）
   2. 中断事件也不是特别精确 ，比如一个阻塞容器，容量为5的时候结束生产者，但是由于volatile同步线程标志位的时间控制不是很精确，有可能生产者还继续生产一段时间。
4. interrupt() and isInterrupted (比较优雅)

### 线程组

> ThreadGroups - Thread groups are best viewed as an unsuccessful experiment , and you may simply ignore their existence! - Joshua Bloch  one of JDK designers

## 1.5.3、并发程序的特性

**程序是什么？** 

> 比如：QQ.exe ，WeChat.exe。

**进程是什么？**

> 程序启动执行，进入内存，CPU，资源分配的基本单位

**线程是什么？**

> 程序执行的基本单位

**程序如何开始运行？**

> CPU读指令 - PC（存储指令地址），读数据Register，计算，回写。下一条。

**线程如何进行调度？**

> linux线程调度器（OS）操作系统

**线程切换的概念时什么？**

> Context Switch CPU保持线程，执行新线程，恢复现场，继续执行原线程这样的一个过程。

### 线程底层知识之可见性、有序性、原子性

**面试题：**

1. 是不是线程数越多，效率就越高？

   > 并不是，要看场景。

2. 单个CPU设定多线程是否有意义？

   > 有意义：可以让多个线程轮流执行，避免因某一个线程不执行或一直执行导致卡死问题。

### 15.3.1、可见性

线程间的可见性

MESI

多线程提高效率，本地缓存数据，造成数据修改不可见。

要想保证可见，要么触发同步指令，要么加上volatile，被修饰的内存，只要有修改，马上同步涉及到的每个线程

#### volatile保证可见性

> volatile 关键字，使一个变量在多个线程间可见，
>
>  A B线程都用到一个变量，java默认是A线程中保留一份copy，这样如果B线程修改了该变量，则A线程未必知道，使用volatile关键字，会让所有线程都会读到变量的修改值。
>
> 注意：volatile并不能保证多个线程共同修改running变量时所带来的不一致问题，也就是说volatile不能替代synchronized

#### 缓存行对齐

1、缓存行对齐

> **缓存行64个字节**是CPU同步的基本单位，缓存行隔离会比伪共享效率要高

2、缓存行对齐的编程技巧

```java
/**
 * 缓存行对齐编程技巧
 */
public class T_CacheLinePadding {
    public static long COUNT = 10_0000_0000L; //10亿

    private static class T {
//        private long p1, p2, p3, p4, p5, p6, p7;
        public long x = 0L;
//        private long p9, p10, p11, p12, p13, p14, p15;
    }
    public static T[] arr = new T[2];
    static {
        arr[0] = new T();
        arr[1] = new T();
    }

    public static void main(String[] args) throws Exception{
        CountDownLatch latch = new CountDownLatch(2);
        Thread t1 = new Thread(()->{
            for (long i = 0; i < COUNT; i++) {
                arr[0].x = i;
            }
            latch.countDown();
        });
        Thread t2 = new Thread(()->{
            for (long i = 0; i < COUNT; i++) {
                arr[1].x = i;
            }
            latch.countDown();
        });
        final long start = System.nanoTime();
        t1.start();
        t2.start();
        latch.await();
        System.out.println((System.nanoTime() - start)/100_0000);
    }
}
// 测试结果：
// 使用缓存行：255ms
// 未使用缓存行：640ms
// 结果：性能相差2倍多
```

3、JDK1.8引入`@sun.misc.Contended`注解，来保证缓存行隔离效果

注意：使用次注解，必须要去掉限制参数：`-XX:-RestrictContended`

```java
//注意：运行程序的时候，需要加参数：-XX:-RestrictContended
public class T_Contended {
    public static long COUNT = 10_0000_0000L;
    private static class T {
        @Contended  // 只有1.8起作用 , 保证x位于单独一行中
        public long x = 0L;
    }
    public static T[] arr = new T[2];
    static {
        arr[0] = new T();
        arr[1] = new T();
    }
    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(2);

        Thread t1 = new Thread(()->{
            for (long i = 0; i < COUNT; i++) {
                arr[0].x = i;
            }
            latch.countDown();
        });
        Thread t2 = new Thread(()->{
            for (long i = 0; i < COUNT; i++) {
                arr[1].x = i;
            }
            latch.countDown();
        });
        final long start = System.nanoTime();
        t1.start();
        t2.start();
        latch.await();
        System.out.println((System.nanoTime() - start)/100_0000);
    }
}
// 测试的某一次结果为：
// 使用注解：252
// 不使用注解：636
// 总结：跟手动设置缓存行对齐，作用几乎无差别
```

4、伪共享

> 当多线程修改相互独立的变量时，如果这些变量在同一个缓存行，就会无意中影响彼此的性能，这个就是伪共享
>
> 什么情况下会产生伪共享：
>
> CPU在读取数据时，是以一个缓存行为单位读取的，假设这个缓存行中有两个long类型的变量a、b，当一个线程A读取a，并修改a，线程A在未写回缓存之前，另一个线程B读取了b，读取的这个b所在的缓存是无效的（前面说的缓存失效），本来是为了提高性能是使用的缓存，现在为了提高命中率，反而被拖慢了，这就是传说中的伪共享
>
> 如何消除伪共享：
>
> 使用缓存行对齐，或者使用注解。

### 15.3.2、有序性

##### CPU乱序执行的证明

```java
public class T_Disorder_CPU_RUN {

    private static int x = 0, y = 0;
    private static int a = 0, b =0;

    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        for(;;) {
            i++;
            x = 0; y = 0;
            a = 0; b = 0;
            Thread one = new Thread(new Runnable() {
                public void run() {
                    //由于线程one先启动，下面这句话让它等一等线程two.
                    // 可根据自己电脑的实际性能适当调整等待时间.
                    shortWait(1000);
                    a = 1;
                    x = b;
                }
            });

            Thread other = new Thread(new Runnable() {
                public void run() {
                    b = 1;
                    y = a;
                }
            });
            one.start();other.start();
            one.join();other.join();
            String result = "第" + i + "次 (" + x + "," + y + "）";
            if(x == 0 && y == 0) {
                System.err.println(result);
                break;
            } else {
                System.out.println(result);
            }
        }
    }
    
    public static void shortWait(long interval){
        long start = System.nanoTime();
        long end;
        do{
            end = System.nanoTime();
        }while(start + interval >= end);
    }
}
// 输出结果：
// 某一次：第50606次 (0,0）
```

##### 线程的as-if-serial

单个线程，两条语句，未必时按顺序执行

单线程的重排序，必须要保证最终一致性

as-if-serial：看上去像时序列化（单线程）

##### 后果？

> 多线程会产生不希望看到的结果。

##### 哪些指令可与互换顺序？

> hanppens-before原则（JVM规定重排序必须遵守的规则）
>
> JLS17.4.5 （不需要记住）

- 程序次序规则：同一个线程内，按照代码出现的顺序，前面的代码先行于后面的代码，准确的说是控制流顺序，因为要考虑到分支和循环结构。
- 管程锁定规则：一个unlock操作先行发生于后面（时间上）对同一个锁的lock操作。
- **volatile变量规则：对一个volatile变量的写操作先行发生于后面（时间上）对这个变量的读操作。** 
- 线程启动规则：Thread的start( )方法先行发生于这个线程的每一个操作。 
- 线程终止规则：线程的所有操作都先行于此线程的终止检测。可以通过Thread.join( )方法结束、Thread.isAlive( )的返回值等手段检测线程的终止。 
- 线程中断规则：对线程interrupt( )方法的调用先行发生于被中断线程的代码检测到中断事件的发生，可以通过Thread.interrupt( )方法检测线程是否中断 
- 对象终结规则：一个对象的初始化完成先行于发生它的finalize()方法的开始。
- 传递性：如果操作A先行于操作B，操作B先行于操作C，那么操作A先行于操作C

##### 使用内存屏障阻止乱序执行

内存屏障：特殊指令，看到这种指令，前面的必须执行完，后面的才能执行

JVM：java定义了LoadLoad，LoadStore，StoreLoad，StoreStore指令，其实都是依据intel的实现。

intel：intel定义了四种指令：lfence，sfence，mfence

#### volatile的底层实现

> volatile修饰的内存，不可以重排序，对volatile修饰变量的读写访问，都不可以换顺序

1: volatile i

2: ACC_VOLATILE

3: JVM的内存屏障

​	屏障两边的指令不可以重排！保障有序！

​    happends-before ,as - if - serial

4：hotspot实现

bytecodeinterpreter.cpp

```c
if (cache -> is_volatile()) {
            if (support_IRIW_for_not_multiple_copy_atomic_cpu) {
                OrderAccess::fence ();
            }
        }
```

orderaccess_linux_x86.inline.hpp

```c++
inline void OrderAccess::fence() {
  if (os::is_MP()) {
    // always use locked addl since mfence is sometimes expensive
#ifdef AMD64
    __asm__ volatile ("lock; addl $0,0(%%rsp)" : : : "cc", "memory");
#else
    __asm__ volatile ("lock; addl $0,0(%%esp)" : : : "cc", "memory");
#endif
  }
}
```

> lock指令作用：
>
> 在多处理器中执行指令时对共享内存的独占作用。
>
> 它的作用时能够将当前处理器对应缓存的内容刷新到内存，并使其他处理器对应的缓存失效，另外他还提供了有序的指令无法越过这个内存屏障的作用。

#### 面试题：DCL单例要不要加volatile

```java
public class T_DCL_Singleton {

    private static volatile T_DCL_Singleton object;

    public static T_DCL_Singleton getObject(){
        if (object == null){
            synchronized (T_DCL_Singleton.class){
                if(object == null){
                    object = new T_DCL_Singleton();
                }
            }
        }
        return object;
    }
    public static void main(String[] args){
        getObject();
    }
}
```

#### DCL单例优化

> 使用UNSAFE的storeFence方法，来保证cur对象的new过程，不能和object=cur；赋值操作指令进行重排。

在常见的TSO模型下，只有StoreLoad重排序，因此不会导致StoreStore重排序，此时 UNSAFE.storeFence()其实是一个空操作，但是再其他模型比如：PSO，RMO下，那么将会导致StoreStore乱序。这时我们就需要指令屏障来保证指令结果写入顺序，而Java的volatile语义恰好满足了这特性。

**JMM模型与CPU MMO模型关系图：**

![](..\doc-all\images\muti-thread\tso_pso_m.png)

```java
import com.ouyangliuy.utils.MyUnsafe;
import sun.misc.Unsafe;

public class T_DCL_Singleton_Unsafe {
    private static T_DCL_Singleton_Unsafe object;
    public static final Unsafe UNSAFE = MyUnsafe.getTheUnsafe();

    public static T_DCL_Singleton_Unsafe getObject() {
        if (object == null) {
            synchronized (T_DCL_Singleton_Unsafe.class) {
                T_DCL_Singleton_Unsafe cur = new T_DCL_Singleton_Unsafe();
                UNSAFE.storeFence();
                object = cur;
            }
        }
        return object;
    }

    public static void main(String[] args) {
        getObject();
    }
}
```

**总结：**

> 只需要保证写入顺序即可，这时我们将volatile修饰符去掉，随后加上storeFence屏障，保证局部变量和全局变量的写顺序，这时就避免了会导致storestore内存顺序的CPU上写写的顺序性。
>
> 我们不需要使用volatile关键字，通过unsafe的屏障就能完成同样的工作。这种现象在Linux内核中非常常见，不允许使用volatile，因为它禁止了编译器在使用这些变量时的优化，而对于内核来说，它必须要满足高性能，这时就要求：不能使用volatile关键字，当需要指令顺序时，采用编译器屏障（:::“memory”）或者指令屏障（lfence，sfence，mfence，lock前缀）。
>
> 以我们这里使用storefence避免了storestore的重排序现象，不同的CPU下的MOB模型，也即内存顺序缓冲区访问模型的不同，将会导致不同程度下的loadload、loadstore、storeload、storestore现象，当然我们现在最常见的就是TSO模型，比如x86等等。那么我们这里使用storeFence保证了局部变量的写入和全局变量的写入顺序性，即可完善单例模型下的高性能操作，因为我们在读单例变量时实在不需要读屏障，同时在TSO模型下由于不存在storestore的乱序，所以storeFence就等同于空操作，更进一步的提升性能。 

https://www.bilibili.com/read/cv11941815?spm_id_from=333.999.0.0

### 15.3.3、原子性

#### 概念：

- race condition  		竞争条件，指的是多个线程访问共享数据的时候产生竞争
- 数据不一致：并发访问下产生的不期望出现的结果
- 如何保证数据一致性呢？  线程同步（线程执行的顺序安排好）
- monitor（管程）： 锁
- critical section ：临界区
- 锁粗/细：  如何临界区执行时间长，语句多，叫做锁的粗粒比较粗，反之，就是锁粒度比较细。



原子性（Atomicity)

1. 悲观的认为这个操作会被别的线程打断（悲观锁）synchronized（上一个小程序）
2. 乐观的认为这个做不会被别的线程打断（乐观锁 自旋锁 无锁）cas操作
   CAS = Compare And Set/Swap/Exchange

#### 上锁的本质：

> 上锁的本质就是，把并发编程序列化。
>
> 同时保证可见性。

注意：序列化并非其他程序一直没机会执行，而是有可能会被调度，但是抢不到锁，又回到Blocked或者Waiting状态（sync锁升级）

#### 什么样的语句（指令）具备原子性？

CPU级别：查汇编手册

Java中8大原子操作：

1. lock：主内存，标识变量为线程独占
2. unlock：主内存， 解锁线程独占变量
3. read：主内存，读取内存到线程缓存（工作内存）
4. load：工作内存，read后的值放入线程本地变量副本
5. use：工作内存，传值给执行引擎
6. assign：工作内存，执行引擎结果赋值给线程本地变量
7. store：工作内存，存值到主内存给write备用
8. write：主内存，写变量值

#### JVM中的两种锁

重量级锁：经过操作系统的调度，synchronized早期都是这种锁，目前的实现中升级到最好

轻量级锁：CAS的实现，不经过OS调度，=》无锁 + 自旋+乐观锁

#### CAS的深度剖析

CAS的ABA问题解决方案 - Version

CAS操作本身的原子性保障

AtomicInteger

```java
public final int incrementAndGet() {
        for (;;) {
            int current = get();
            int next = current + 1;
            if (compareAndSet(current, next))
                return next;
        }
    }

public final boolean compareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
}
```

jdk8u: unsafe.cpp:

cmpxchg = compare and exchange set swap

```c++
UNSAFE_ENTRY(jboolean, Unsafe_CompareAndSwapInt(JNIEnv *env, jobject unsafe, jobject obj, jlong offset, jint e, jint x))
  UnsafeWrapper("Unsafe_CompareAndSwapInt");
  oop p = JNIHandles::resolve(obj);
  jint* addr = (jint *) index_oop_from_field_offset_long(p, offset);
  return (jint)(Atomic::cmpxchg(x, addr, e)) == e;
UNSAFE_END
```

jdk8u: atomic_linux_x86.inline.hpp **93行**

is_MP = Multi Processors  

```c++
inline jint     Atomic::cmpxchg    (jint     exchange_value, volatile jint*     dest, jint     compare_value) {
  int mp = os::is_MP();
  __asm__ volatile (LOCK_IF_MP(%4) "cmpxchgl %1,(%3)"
                    : "=a" (exchange_value)
                    : "r" (exchange_value), "a" (compare_value), "r" (dest), "r" (mp)
                    : "cc", "memory");
  return exchange_value;
}
```

jdk8u: os.hpp is_MP()

```c++
  static inline bool is_MP() {
    // During bootstrap if _processor_count is not yet initialized
    // we claim to be MP as that is safest. If any platform has a
    // stub generator that might be triggered in this phase and for
    // which being declared MP when in fact not, is a problem - then
    // the bootstrap routine for the stub generator needs to check
    // the processor count directly and leave the bootstrap routine
    // in place until called after initialization has ocurred.
    return (_processor_count != 1) || AssumeMP;
  }
```

jdk8u: atomic_linux_x86.inline.hpp

```c++
#define LOCK_IF_MP(mp) "cmp $0, " #mp "; je 1f; lock; 1: "
```

最终实现：

cmpxchg = cas修改变量值

```assembly
lock cmpxchg 指令
```

硬件：

lock指令在执行的时候视情况采用缓存锁或者总线锁

#### 两种锁的效率

不同的场景：

临界区执行时间比较长 ， 等的人很多 -> 重量级

时间短，等的人少 -> 自旋锁

## 16、synchronized

### synchronized用法

#### 1、简单的方式就是**synchronized(this)**锁定当前对象就行

```java

public class T {
	
	private int count = 10;
	
	public void m() {
		synchronized(this) { // 任何线程想要执行那个下面的代码，必须先要拿到this的锁
      count--;
			System.out.println(Thread.currentThread().getName() + " count = " + count);
		}
	}
	
}
```

#### 2、synchronized void m()，使用方法上

```java
public class T {

	private int count = 10;
	
	public synchronized void m() { // 等同于在方法的代码执行时要synchronized(this)
		count--;
		System.out.println(Thread.currentThread().getName() + " count = " + count);
	}
}
```

#### 3、使用T.class的类对象。这里的synchronized(T.class)锁的就是T类的对象

```java
public class T {

	private static int count = 10;
	
	public synchronized static void m() { //这里等同于synchronized(T.class)
		count--;
		System.out.println(Thread.currentThread().getName() + " count = " + count);
	}
	
	public static void mm() {
		synchronized(T.class) { 	// 考虑一下这里写synchronized(this)是否可以？
			count --;
		}
	}
}
```

#### 4、问题：T.class是单例的吗？

> 一个class load到内存它是不是单例的，想想看。一般情况下是，如果是在同一个ClassLoader空间那它一定是。不是同一个类加载器就不是了，不同的类加载器互相之间也不能访问。所以说你能访问它，那他一定就是单例。
>
> 一言以蔽之：只要是同一个ClassLoad加载到内存中的，那它一定是单例的。

#### 5、异常下的synchronized

>  程序在执行过程中，如果出现异常，默认情况锁会被释放，所以，在并发处理的过程中，有异常要多加小心，不然可能会发生不一致的情况。
>
> 比如，在一个web app处理过程中，多个servlet线程共同访问同一个资源，这时如果异常处理不合适，在第一个线程中抛出异常，其他线程就会进入同步代码区，有可能会访问到异常产生时的数据。因此要非常小心的处理同步业务逻辑中的异常。

### synchronized底层实现

> 早期：jdk早期的时候，这个synchronized的底层实现是重量级的，重量级到这个synchronized都是要去找操作系统去申请锁的地步，这就会造成synchronized效率非常低，java后来越来越开始处理高并发的程序的时候，很多程序员都不满意，说这个synchrionized用的太重了，我没办法，就要开发新的框架，不用你原生的了
>
> 后来：改进才有了锁升级的概念

### synchronized锁升级过程

无锁-》偏向锁-》CAS操作-》轻量级锁-》自旋锁（CAS+SpinLimit=5000，自旋最大次数5000（jdk1.8））-》重量级锁

**偏向锁：**当我们使用synchronized的时候HotSpot的实现是这样的：上来之后第一个去访问某把锁的线程 比如sync (Object) ，来了之后先在这个Object的头上面markword记录这个线程。（如果只有第一个线程访问的时候实际上是没有给这个Object加锁的，在内部实现的时候，只是记录这个线程的ID（**偏向锁**））。

**自旋锁：**偏向锁如果有线程争用的话，就升级为自旋锁。

> 注意：JDK 1.7 之前是**普通自旋**，设定一个最大的自旋次数，**默认是 10 次**，超过这个阈值就停止自旋。JDK 1.7 之后，引入了**适应性自旋**。简单来说就是：这次自旋获取到锁了，自旋的次数就会**增加**；这次自旋没拿到锁，自旋的次数就会**减少**。
>
> **synchronized底层都是使用CAS进行锁升级替换。**
>
> <font color='red'>MARK：自旋锁次数是10次吗？</font>

**借用道友synchronized锁升级图：**

![](..\doc-all\images\muti-thread\锁升级过程.png)





### synchronized如何保障可见性

![](..\doc-all\images\muti-thread\synchronized保障可见性.png)

### JVM中的线程和OS线程对应关系

JVM 1:1 -> LOOM -> M:N (golang)

## 17、超线程（Hyper-Threading）

一个ALU单元 + 2组Registers + PC

> 超线程技术是一项硬件创新，允许在每个内核上运行多个线程。更多的线程意味着可以并行完成更多的工作。
>
> 超线程如何工作？当英特尔® 超线程技术处于激活状态时，CPU 会在每个物理内核上公开两个执行上下文。这意味着，一个物理内核现在就像两个“逻辑内核”一样，可以处理不同的软件线程。例如，当启用超线程时，10 核[英特尔® 酷睿™ i9-10900K](https://www.intel.cn/content/www/cn/zh/gaming/i9-desktop-processors-for-gaming.html) 处理器会有 20 个线程。

## 18、线程注意点

1. **Daemon线程中finally不会执行，因为Daemon线程会在所有线程结束后被JVM直接terminate**
2. 线程池中的SingleThreadPool有自己特有的用途--用作很多任务的顺序执行，省了synchronized的管理
3. ThreadGroups - Thread groups are best viewed as an unsuccessful experiment , and you may simply ignore their existence! - Joshua Bloch  one of JDK designers

## 19、volatile

volatile 关键字，使一个变量在多个线程间可见，A B线程都用到一个变量，java默认是A线程中保留一份copy,这样如果B线程修改了该变量，则A线程未必知道，使用volatile关键字，会让所有线程都会读到变量的修改值。

但是volatile并不能保证多个线程共同修改running变量时所带来的不一致问题，也就是说volatile不能替代synchronized

### 1、保证线程可见性

> java里面是有堆内存的，堆内存是所有线程共享里面的内存，除了共享的内存之外呢，每个线程都有自己的专属的区域，都有自己的工作内存，如果说在共享内存里有一个值的话，当我们线程，某一个线程都要去访问这个值的时候，会将这个值copy一份，copy到自己的这个工作空间里头，然后对这个值的任何改变，首先是在自己的空间里进行改变，什么时候写回去，就是改完之后会马上写回去。什么时候去检查有没有新的值，也不好控制。
>
> 在这个线程里面发生的改变，并没有及时的反应到另外一个线程里面，这就是线程之间的不可见 ，对这个变量值加了volatile之后就能够保证 一个线程的改变，另外一个线程马上就能看到。

### 2、禁止指令重排序

> 指令重排序也是和cpu有关系，每次写都会被线程读到，加了volatile之后。cpu原来执行一条指令的时候它是一步一步的顺序的执行，但是现在的cpu为了提高效率，它会把指令并发的来执行，第一个指令执行到一半的时候第二个指令可能就已经开始执行了，这叫做流水线式的执行。在这种新的架构的设计基础之上呢想充分的利用这一点，那么就要求你的编译器把你的源码编译完的指令之后呢可能进行一个指令的重新排序。
>
> 这个是通过实际工程验证了，不仅提高了，而且提高了很多。

## 20、CAS

> CAS：是无锁优化，或者叫自旋。

CAS操作，如：incrementAndGet() 调用了getAndAddInt

```java
public final int incrementAndGet() { return U.getAndAddInt(this,VALUE,1)+1;}
```

AtomicInteger它的内部是调用了 Unsafe这个类里面的方法CompareAndSetInt（CAS）,这个方法有三个参数，cas（V，Expected，NewValue）。 

> **V：**第一个参数是要改的那个值，或者是当前的要改变的对象
>
> **Expected：**第二个参数是期望当前的这个值会是几
>
> **NewValue：**要设定的新值。
>
> 假如当前线程想改这个值的时候我期望你这值是0，你不能是1，如果是1那么说明不对，此时操作失败，那么假如我期望的值是0，此时你是0 ，那么我就使用cas将0替换成1，替换成功，说明我获取了锁。

**ABA：**

> 这个ABA问题是这样的，假如说你有一个值，我拿到这个值是1，想把它变成2，我拿到1用cas操作，期望值是1，准备变成2，这个对象Object，在这个过程中，没有一个线程改过我肯定是可以更改的，但是如果有一个线程先把这个1变成了2后来又变回1，中间值更改过，它不会影响我这个cas下面操作，这就是ABA问题。
>
> 这种问题怎么解决。如果是int类型的，最终值是你期望的，也没有关系，这种没关系可以不去管这个问题。如果你确实想管这个问题可以加版本号，做任何一个值的修改，修改完之后加一，后面检查的时候连带版本号一起检查。

如果是基础类型：无所谓。不影响结果值；

如果是引用类型：就像是你的女朋友和你分手之后又复合，中间经历了别的男人。

**Unsafe：**



## 21、Atomic类和线程同步新机制

#### 为什么Atomic要比Sync快？

因为不加锁，刚刚我们说了synchronized是要加锁的，有可能它要去操作系统申请重量级锁，所以synchronized效率偏低，在这种情形下效率偏低。

#### LongAdder为什么要比Atomicx效率要高呢？

是因为LongAdder的内部做了一个分段锁，类似于分段锁的概念。在它内部的时候，会把一个值放到一个数组里，比如说数组长度是4，最开始是0，1000个线程，250个线程锁在第一个数租元素里，以此类推，每一个都往上递增算出来结果在加到一起。



## 17、JUC

## ReentrantLock

部分场合替代synchronized

* 手工释放锁

  * 标准写法：

    ```java
    lock.lock();
    try {
        xxxxx
    } finally {
        lock.unlock();
    }
    ```

    

* 可以是公平锁

  * 公平锁：线程抢锁先排队
  * 非公平锁：线程到了就插队抢

* 可被打断的上锁过程

  * tryLock()
  * lockInterruptibly()

* 锁上面的队列可以指定任意数量

  * 区分了不同条件下的等待队列（Condition）
  * ABC ABC 问题

### Reentrantlock vs synchronized

> ReentrantLock可以替代synchronized这是没问题的，他也可以重入，可以锁定的。本身的底层是cas 
>
> trylock：自己来控制，我锁不住该怎么办
>
> lockInterruptibly：这个类，中间你还可以被打断
>
> 它还可以公平和非公平的切换
>
> 现在除了synchronized之外，多数内部都是用的cas。当我们聊这个AQS的时候实际上它内部用的是park和unpark，也不是全都用的cas,他还是做了一个锁升级的概念，只不过这个锁升级做的比较隐秘，在你等待这个队列的时候如果你拿不到还是进入一个阻塞的状态，前面至少有一个cas的状态，他不像原先就直接进入阻塞状态了。

## CountDownLatch

> CountDown：倒数，Latch是门栓的意思（倒数的一个门栓，5、4、3、2、1数到了，我这个门栓就开了）

用join实际上不太好控制，必须要你线程结束了才能控制，但是如果是一个门栓的话我在线程里不停的CountDown，在一个线程里就可以控制这个门栓什么时候往前走，用join我只能是当前线程结束了你才能自动往前走，当然用join可以，但是CountDown比它要灵活。

```java
import java.util.concurrent.CountDownLatch;
/**
usingCountDownLatch，new了100个线程，接下来，又来了100个数量的CountDownLatch，什么意思，就是，这是一个门栓，门栓上记了个数threads.length是100，每一个线程结束的时候我让 latch.countDown()，然后所有线程start()，再latch.await()，最后结束。那CountDown是干嘛使得呢，看latch.await()，它的意思是说给我看住门，给我插住不要动。每个线程执行到latch.await()的时候这个门栓就在这里等着，并且记了个数是100，每一个线程结束的时候都会往下CountDown，CountDown是在原来的基础上减1，一直到这个数字变成0的时候门栓就会被打开，这就是它的概念，它是用来等着线程结束的。
*/
public class T_CountDownLatch {
    public static void main(String[] args) {
        usingJoin();
        usingCountDownLatch();
    }

    private static void usingCountDownLatch() {
        Thread[] threads = new Thread[100];
        CountDownLatch latch = new CountDownLatch(threads.length);
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                int result = 0;
                for (int j = 0; j < 10000; j++) result += j;
                latch.countDown();
            });
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end latch");
    }

    private static void usingJoin() {
        Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                int result = 0;
                for (int j = 0; j < 10000; j++) result += j;
            });
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("end join");
    }
}
```

## Latch

门闩

## Semaphore 

信号量（n个线程的限流）

> Semaphore，信号灯。可以往里面传一个数，permits是允许的数量，你可以想着有几盏信号灯，一个灯里面闪着数字表示到底允许几个来参考我这个信号灯。
>
> s.acquire()这个方法叫阻塞方法，阻塞方法的意思是说我大概acquire不到的话我就停在这，acquire的意思就是得到。如果我 Semaphore s = new Semaphore(1) 写的是1，我取一下，acquire一下他就变成0，当变成0之后别人是acquire不到的，然后继续执行，线程结束之后注意要s.release()，执行完该执行的就把他release掉，release又把0变回去1，还原化。
>
> **Semaphore的含义就是限流**，比如说你在买票，Semaphore写5就是只能有5个人可以同时买票。acquire的意思叫获得这把锁，线程如果想继续往下执行，必须得从Semaphore里面获得一个许可，他一共有5个许可用到0了你就得给我等着。

> 默认Semaphore是非公平的，new Semaphore(2, true)第二个值传true才是设置公平。公平这个事儿是有一堆队列在哪儿等，大家伙过来排队。用这个车道和收费站来举例子，就是我们有四辆车都在等着进一个车道，当后面在来一辆新的时候，它不会超到前面去，要在后面排着这叫公平。所以说内部是有队列的，不仅内部是有队列的，这里面用到的东西，Semaphore还有后面要讲的Exchanger都是用同一个队列，同一个类来实现的，这个类叫**AQS。**

```java
import java.util.concurrent.Semaphore;

public class T_Semaphore {

    public static void main(String[] args) {
        //Semaphore s = new Semaphore(2);
        Semaphore s = new Semaphore(1, true);
        //允许一个线程同时执行
        //Semaphore s = new Semaphore(1);
        new Thread(()->{
            try {
                s.acquire();
                System.out.println("T1 running...");
                Thread.sleep(200);
                System.out.println("T1 finished...");

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                s.release();
            }
        }).start();

        new Thread(()->{
            try {
                s.acquire();
                System.out.println("T2 running...");
                Thread.sleep(200);
                System.out.println("T2 finished...");
                s.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
```

## Exchanger

**同步交换器**

> 什么是Exchanger：定义了一个Exchanger，Exchanger叫做交换器，俩人之间互相交换个数据用的。怎么交换呢，看这里，我第一个线程有一个成员变量叫s，然后exchanger.exchange(s)，第二个也是这样，t1线程名字叫T1，第二个线程名字叫T2。到最后，打印出来你会发现他们俩交换了一下。线程间通信的方式非常多，这只是其中一种，就是线程之间交换数据用的。

**详细解释**

> exchanger你可以把它想象成一个容器，这个容器有两个值，两个线程，有两个格的位置，第一个线程执行到exchanger.exchange的时候，阻塞，但是要注意我这个exchange方法的时候是往里面扔了一个值，你可以认为把T1扔到第一个格子了，然后第二个线程开始执行，也执行到这句话了，exchange，他把自己的这个值T2扔到第二个格子里。接下来这两个哥们儿交换一下，T1扔给T2，T2扔给T1，两个线程继续往前跑。exchange只能是两个线程之间，交换这个东西只能两两进行。

```java
import java.util.concurrent.Exchanger;

public class T_Exchanger {
    static Exchanger<String> exchanger = new Exchanger<>();

    public static void main(String[] args) {
        new Thread(()->{
            String s = "T1";
            try {
                s = exchanger.exchange(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " " + s);

        }, "t1").start();

        new Thread(()->{
            String s = "T2";
            try {
                s = exchanger.exchange(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " " + s);
        }, "t2").start();
    }
}
```

## CyclicBarrier

> 来讲这个同步工具叫CyclicBarrier，意思是循环栅栏，这有一个栅栏，什么时候人满了就把栅栏推倒，哗啦哗啦的都放出去，出去之后扎栅栏又重新起来，再来人，满了，推倒之后又继续。

**使用场景：**

> 比如：某一个操作需要访问 数据库，需要访问网络，需要访问文件，有一种方式是顺序执行，挨个的都执行完，效率非常低，这是一种方式，还有一种可能性就是并发执行，原来是1、2、3顺序执行，并发执行是不同的线程去执行不同的操作，有的线程去数据库找，有的线程去网络访问，有的线程去读文件，必须是这三个线程全部到位了我才能去进行，这个时候我们就可以用CyclicBarrier。

```java
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class T_CyclicBarrier {
    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(20, () -> System.out.println("满人"));
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    System.out.println("i=" + finalI);
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
```

## Phaser

阶段同步器

> Phaser它就更像是结合了CountDownLatch和CyclicBarrier，翻译一下叫阶段。这个稍微复杂一些。

**Phaser：**

> Phaser是按照不同的阶段来对线程进行执行，就是它本身是维护着一个阶段这样的一个成员变量，当前我是执行到那个阶段，是第0个，还是第1个阶段啊等等，每个阶段不同的时候这个线程都可以往前走，有的线程走到某个阶段就停了，有的线程一直会走到结束。你的程序中如果说用到分好几个阶段执行 ，而且有的人必须得几个人共同参与的一种情形的情况下可能会用到这个Phaser。
>
> **意思是只有当运行到某个阶段的时候，才能进行下一阶段的工作。**

**模拟场景：**

> 模拟了一个结婚的场景，结婚是有好多人要参加的，因此，我们写了一个类Person是一个Runnable可以new出来，扔给Thread去执行，模拟我们每个人要做一些操作，有这么几种方法，arrive()到达、eat()吃、leave()离开、hug()拥抱这么几个。作为一个婚礼来说它会分成好几个阶段，第一阶段大家好都得到齐了，第二个阶段大家开始吃饭， 三阶段大家离开，第四个阶段新郎新娘入洞房，那好，每个人都有这几个方法，在方法的实现里头我就简单的睡了1000个毫秒，我自己写了一个方法，把异常处理写到了方法里了。

**模拟流程：**

> 一共有五个人参加婚礼了，接下来新郎，新娘参加婚礼，一共七个人。它一start就好调用我们的run（）方法，它会挨着牌的调用每一个阶段的方法。那好，我们在每一个阶段是不是得控制人数，第一个阶段得要人到期了才能开始，二阶段所有人都吃饭，三阶段所有人都离开，但是，到了第四阶段进入洞房的时候就不能所有人都干这个事儿了。所以，要模拟一个程序就要把整个过程分好几个阶段，而且每个阶段必须要等这些线程给我干完事儿了你才能进入下一个阶段。

**如何模拟：**

> 我定义了一个phaser，我这个phaser是从Phaser这个类继承，重写onAdvance方法，前进，线程抵达这个栅栏的时候，所有的线程都满足了这个第一个栅栏的条件了onAdvance会被自动调用，目前我们有好几个阶段，这个阶段是被写死的，必须是数字0开始，onAdvance会传来两个参数phase是第几个阶段，registeredParties是目前这个阶段有几个人参加，每一个阶段都有一个打印，返回值false，一直到最后一个阶段返回true，所有线程结束，整个栅栏组，Phaser栅栏组就结束了。
>
> 我怎么才能让我的线程在一个栅栏面前给停住呢，就是调用phaser.arriveAndAwaitAdvance()这个方法，这个方法的意思是到达等待继续往前走，直到新郎新娘如洞房，其他人不在参与，调用phaser.arriveAndDeregister() 这个方法。还有可以调用方法phaser.register()往上加，不仅可以控制栅栏上的个数还可以控制栅栏上的等待数量，这个就叫做phaser。

```java
import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public class T_Phaser {
    static Random r = new Random();
    static MarriagePhaser phaser = new MarriagePhaser();

    static void milliSleep(int milli) {
        try {
            TimeUnit.MILLISECONDS.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        phaser.bulkRegister(7);
        for (int i = 0; i < 5; i++) {
            new Thread(new Person("p" + i)).start();
        }
        new Thread(new Person("新郎")).start();
        new Thread(new Person("新娘")).start();

    }

    static class MarriagePhaser extends Phaser {
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
          // phase:从0开始，代表当前某一个阶段
          // 数子越大,说明阶段性越高
            switch (phase) {
                case 0:
                    System.out.println("所有人都到齐了！" + registeredParties);
                    System.out.println();
                    return false;
                case 1:
                    System.out.println("所有人都吃完了！" + registeredParties);
                    System.out.println();
                    return false;
                case 2:
                    System.out.println("所有人都离开了！" + registeredParties);
                    System.out.println();
                    return false;
                case 3:
                    System.out.println("婚礼结束！新郎新娘抱抱！" + registeredParties);
                    return true;
                default:
                    return true;
            }
        }
    }

    static class Person implements Runnable {
        String name;

        public Person(String name) {
            this.name = name;
        }

        public void arrive() {
            milliSleep(r.nextInt(1000));
            System.out.printf("%s 到达现场！\n", name);
            phaser.arriveAndAwaitAdvance();
        }

        public void eat() {
            milliSleep(r.nextInt(1000));
            System.out.printf("%s 吃完!\n", name);
            phaser.arriveAndAwaitAdvance();
        }

        public void leave() {
            milliSleep(r.nextInt(1000));
            System.out.printf("%s 离开！\n", name);
            phaser.arriveAndAwaitAdvance();
        }

        private void hug() {
            if (name.equals("新郎") || name.equals("新娘")) {
                milliSleep(r.nextInt(1000));
                System.out.printf("%s 洞房！\n", name);
                phaser.arriveAndAwaitAdvance();
            } else {
                phaser.arriveAndDeregister();
                //phaser.register()
            }
        }

        @Override
        public void run() {
            arrive();
            eat();
            leave();
            hug();

        }
    }
}
```

## ReadWriteLock

这个ReadWriteLock 是读写锁。读写锁的概念其实就是共享锁和排他锁，读锁就是共享锁，写锁就是排他锁。那这个是什么意思，我们先要来理解这件事儿，读写有很多种情况，比如说你数据库里的某条儿数据你放在内存里读的时候特别多，而改的时候并不多。

**模拟场景：**

> 我们**公司的组织结构**，我们要想显示这组织结构下有哪些人在网页上访问，所以这个组织结构被访问到会读，但是很少更改，读的时候多写的时候就并不多，这个时候好多线程来共同访问这个结构的话，有的是读线程有的是写线程，要求他不产生这种数据不一致的情况下我们采用最简单的方式就是加锁，我读的时候只能自己读，写的时候只能自己写，但是这种情况下效率会非常的底，尤其是读线程非常多的时候，那我们就可以做成这种锁，当读线程上来的时候加一把锁是允许其他读线程可以读，写线程来了我不给它，你先别写，等我读完你在写。读线程进来的时候我们大家一块读，因为你不改原来的内容，写线程上来把整个线程全锁定，你先不要读，等我写完你在读。

**模拟用法：**

> 读写锁怎么用，我们这有两个方法，read()读一个数据，write()写一个数据。read这个数据的时候我需要你往里头传一把锁，这个传那把锁你自己定，我们可以传自己定义的全都是排他锁，也可以传读写锁里面的读锁或写锁。write的时候也需要往里面传把锁，同时需要你传一个新值，在这里值里面传一个内容。我们模拟这个操作，读的是一个int类型的值，读的时候先上锁，设置一秒钟，完了之后read over，最后解锁unlock。再下面写锁，锁定后睡1000毫秒，然后把新值给value，write over后解锁，非常简单。

**实现方法：**

> 第一种：我们现在的问题是往里传这个lock有两种传法，第一种直接new ReentrantLock()传进去，分析下这种方法，主程序定义了一个Runnable对象，第一个是调用read() 方法，第二个是调用write() 方法同时往里头扔一个随机的值，然后起了18个读线程，起了两个写线程，这个两个我要想执行完的话，我现在传的是一个ReentrantLock，这把锁上了之后没有其他任何人可以拿到这把锁，而这里面每一个线程执行都需要1秒钟，在这种情况下你必须得等20秒才能干完这事儿；
>
> 第二种：我们换了锁 new ReentrantReadWriteLock() 是ReadWriteLock的一种实现，在这种实现里头我又分出两把锁来，一把叫readLock，一把叫writeLock，通过他的方法readWriteLock.readLock()来拿到readLock对象，读锁我就拿到了。通过readWriteLock.writeLock()拿到writeLock对象。这两把锁在我读的时候扔进去，因此，读线程是可以一起读的，也就是说这18个线程可以一秒钟完成工作结束。所以使用读写锁效率会大大的提升。

```java
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class T_ReadWriteLock {
    static Lock lock = new ReentrantLock();
    private static int value;

    static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    static Lock readLock = readWriteLock.readLock();
    static Lock writeLock = readWriteLock.writeLock();

    public static void read(Lock lock) {
        try {
            lock.lock();
            Thread.sleep(1000);
            System.out.println("read over!");
            //模拟读取操作
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void write(Lock lock, int v) {
        try {
            lock.lock();
            Thread.sleep(1000);
            value = v;
            System.out.println("write over!");
            //模拟写操作
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        //Runnable readR = ()-> read(lock);
        Runnable readR = () -> read(readLock);

        //Runnable writeR = ()->write(lock, new Random().nextInt());
        Runnable writeR = () -> write(writeLock, new Random().nextInt());

        for (int i = 0; i < 18; i++) new Thread(readR).start();
        for (int i = 0; i < 2; i++) new Thread(writeR).start();
    }
}
```

## LockSupport

替代wait notify

### 为什么会引出LockSupport

#### synchronized的限制

> 1. 因为wait()方法需要释放锁，所以必须在synchronized中使用，否则会抛出异常IllegalMonitorStateException
> 2. notify()方法也必须在synchronized中使用，并且应该指定对象
> 3. synchronized()、wait()、notify()对象必须一致，一个synchronized()代码块中只能有一个线程调用wait()或notify()

以上诸多限制，体现出了很多的不足，所以LockSupport的好处就体现出来了。

> 在JDK1.6中的java.util.concurrent的子包locks中引了LockSupport这个API，LockSupport是一个比较底层的工具类，用来创建锁和其他同步工具类的基本线程阻塞原语。java锁和同步器框架的核心 AQS: AbstractQueuedSynchronizer，就是通过调用 LockSupport .park()和 LockSupport .unpark()的方法，来实现线程的阻塞和唤醒的。

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class T_LockSupport {
    public static void main(String[] args) {
       
        Thread t = new Thread(()->{
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                if(i == 5) {
                    //使用LockSupport的park()方法阻塞当前线程t
                    LockSupport.park();
                }
                try {
                    //使当前线程t休眠1秒
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        //启动当前线程t
        t.start();
    }
}
```

> LockSupport使用起来的是比较灵灵活的，没有了所谓的限制。我们来分析一下代码的执行过程，首先我们使用lombda表达式创建了线程对象 " t " ，然后通过 " t " 对象调用线程的启动方法start()，然后我们再看线程的内容，在for循环中，当 i 的值等于5的时候，我们调用了LockSupport的.park()方法使当前线程阻塞，注意看方法并没有加锁，就默认使当前线程阻塞了，由此可以看出LockSupprt.park()方法并没有加锁的限制。

```java
//启动当前线程t
t.start();
//唤醒线程t
LockSupport.unpark(t);
```

> 在主线程中线程 " t " 调用了start()方法以后，因为紧接着执行了LockSupport的unpark()方法，所以也就是说，在线程 " t "还没有执行还没有被阻塞的时候，已经调用了LockSupport的unpark()方法来唤醒线程 " t " ，之后线程 " t "才启动调用了LockSupport的park()来使线程 " t " 阻塞，但是线程 " t " 并没有被阻塞，由此可以看出，LockSupport的unpark()方法可以先于LockSupport的park()方法执行。



```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class T_LockSupport {
    public static void main(String[] args) {

        Thread t = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                if (i == 5) {
                    //使用LockSupport的park()方法阻塞当前线程t
                    LockSupport.park();
                }
                if (i == 8) {
                    LockSupport.park();
                }
                try {
                    //使当前线程t休眠1秒
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        //启动当前线程t
        t.start();
        //唤醒线程t
        LockSupport.unpark(t);
    }
}
```

> 在i等于8的时候再次调用LockSupport的park()方法来使线程 " t " 阻塞， 我们可以看到线程被阻塞了，原因是LockSupport的unpark()方法就像是获得了一个“令牌”，而在i等于5的时候已经用掉了令牌,所以在i等于8的时候会一直阻塞

### park()和unpark()方法的实现原理

> park()和unpark()方法的实现是由Unsefa类提供的，而Unsefa类是由C和C++语言完成的，其实原理也是比较好理解的，它主要通过**一个变量作为一个标识**，变量值在0，1之间来回切换，当这个变量大于0的时候线程就获得了“令牌”，从这一点我们不难知道，其实park()和unpark()方法就是在改变这个变量的值，来达到线程的阻塞和唤醒的，具体实现不做赘述了。

## ThreadLocal原理与源码

### 什么是ThreadLocal

线程本地

> ThreadLocal:线程独有,就是说这个线程里用到这个ThreadLocal的时候，只有自己去往里设置，设置的是只有自己线程里才能访问.这就是ThreadLocal的含义

### ThreadLocal源码

```java
//ThraedLocal源码
public class ThreadLocal<T> {
    public void set(T value) {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }
  	ThreadLocalMap getMap(Thread t){
        return t.threadLocals;
    }
}
```

> set方法中：getMap(Thread t)，其实获取的就是当前Thread中的threadLocals，也就是ThreadLocalMap
>
> 所以：当使用ThreadLocal的set方法的时候，其实是将当前数据设置到当前线程的ThreadLocalMap中。
>
> 也就是：
>
> ```tex
> ·set
> 	- Thread.currentThread.map(ThreadLocal,obj)
> ```
>

在Thread类中，有threadLocals的引用。

```java
public class Thread implements Runnable {
    /* ThreadLocal values pertaining to this thread. This map is maintained
     * by the ThreadLocal class. */
    ThreadLocal.ThreadLocalMap threadLocals = null;
    /*
     * InheritableThreadLocal values pertaining to this thread. This map is
     * maintained by the InheritableThreadLocal class.
     */
    ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;
 
}
```

其实就是obj被set到了当前线程的 map中去，因此这个obj的值，只能是被当前线程访问到，不能被其他线程访问。所以在使用了ThreadLocal的时候，用当前线程的set和get将其他隔离开，就是自己线程里所持有的，其他线程没有。

### 为什么要用ThreadLocal

根据Spirng的声明式事务来解析，声明式事务一般来讲我们是要通过数据库的，比方说第1个方法写了去配置文件里拿到数据库连接Connection，第2个、第3个都是一样去拿数据库连接，然后声明式事务可以把这几个方法合在一起，视为一个完整的事务，如果说在这些方法里，每一个方法拿的连接，它拿的不是同一个对象。因此可以将Connection放到这个线程的本地对象里ThreadLocal里面，以后再拿的时候，实际上我是从ThreadLocal里拿的，第1个方法拿的时候就把Connection放到ThreadLocal里面，后面的方法要拿的时候，从ThreadLocal里直接拿，不从线程池拿。

### ThreadLocal如何解决内存泄漏

```java
//ThreadLocal源码
public class ThreadLocal<T> {
    public void set(T value) {
        //获取当前线程
        Thread t = Thread.currentThread();
        //获取当前线程的Map
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }
    ThreadLocalMap getMap(Thread t){
        return t.threadLocals;
    }
    private void set(ThreadLocal<?> key, Object value) {
       Entry[] tab = table;
       int len = tab.length;
       int i = key.threadLocalHashCode & (len-1);
       for (Entry e = tab[i];
            e != null;
            e = tab[i = nextIndex(i, len)]) {
           ThreadLocal<?> k = e.ge
           if (k == key) {
               e.value = value;
               return;
        
           if (k == null) {
               replaceStaleEntry(key, value, i);
               return;
           }
       tab[i] = new Entry(key, value);
       int sz = ++size;
       if (!cleanSomeSlots(i, sz) && sz >= threshold)
           rehash();
   }
   static class Entry extends WeakReference<ThreadLocal<?>> {
       /** The value associated with this ThreadLocal. */
        Object value;
        Entry(ThreadLocal<?> k, Object v) {
            super(k);
            value = v;
        }
   }
}
```

![](../doc-all\images\muti-thread/thread_local.png)

从源码角度来看：这里的tl是一个指向Thread的ThreadLocal强引用，而Map里的key是通过一个弱引用的。

#### 为什么要用弱引用呢？

> 假如是强引用，当tl指向这个ThreadLocal对象消息的时候，tl也会跟着消失，当前tl消失了，如果这个ThreadLocal对象还被一个强引用的key指向的时候，那么这个ThreadLocal对象不能被回收，而且由于线程长期存在的，那么这个tl长期存在，这个Map也会长期存在，Map的key，vlaue也会长期存在，因此ThreadLocal对象永远不会被消失，所以这里会发生内存泄漏。
>
> 因此将ThreadLocal的key设置为虚引用，那么在tl消失的时候，ThreadLocal也会被回收掉，所以这就是为什么key要设置成虚引用的原因。

#### 为什么会出现内存泄漏？

在将key定义为虚引用的情况下：

> 当我们tl这个强引用消失了，key的指向也被回收了，可是很不幸的是这个key指向了一个null值，但是value是存在的，假如在value比较大的情况下，会发生内存严重的泄漏，所以需要正确操作当前的ThreadLocal。

#### 如何解决内存泄漏

```java
// 正确使用，如果在当前ThreadLocal不需要的时候，要正确的调用remove方法。
ThradLocalM> tl = new ThreadLocal<>();
tl.set(new M());
tl.remove();
```



# JMH

**JMH：Java Microbenchmark Harness**

官网：http://openjdk.java.net/projects/code-tools/jmh/

### 创建JMH测试

#### 1、创建Maven项目，添加依赖，我们需要添加两个依赖：

```xml
<dependencies>
    <!-- https://mvnrepository.com/artifact/org.openjdk.jmh/jmh-core -->
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-core</artifactId>
        <version>1.21</version>
    </dependency>

    <!-- https://mvnrepository.com/artifact/org.openjdk.jmh/jmh-generator-annprocess -->
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-generator-annprocess</artifactId>
        <version>1.21</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### 2、定义需要测试类PS (ParallelStream)

> 看这里，写了一个类，并行处理流的一个程序，定义了一个list集合，然后往这个集合里扔了1000个数。写了一个方法来判断这个数到底是不是一个质数。写了两个方法，第一个是用forEach来判断我们这1000个数里到底有谁是质数；第二个是使用了并行处理流，这个forEach的方法就只有单线程里面执行，挨着牌从头拿到尾，从0拿到1000，但是并行处理的时候会有多个线程采用ForkJoin的方式来把里面的数分成好几份并行的尽兴处理。一种是串行处理，一种是并行处理，都可以对他们进行测试，但需要注意这个基准测试并不是对比测试的，你只是侧试一下你这方法写出这样的情况下他的吞吐量到底是多少，这是一个非常专业的测试的工具。严格的来讲这部分是测试开发专业的。

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PS {

   static List<Integer> nums = new ArrayList<>();
   static {
      Random r = new Random();
      for (int i = 0; i < 10000; i++) nums.add(1000000 + r.nextInt(1000000));
   }

   static void foreach() {
      nums.forEach(v->isPrime(v));
   }

   static void parallel() {
      nums.parallelStream().forEach(PS::isPrime);
   }
   
   static boolean isPrime(int num) {
      for(int i=2; i<=num/2; i++) {
         if(num % i == 0) return false;
      }
      return true;
   }
}
```

#### 3、单元测试

> 我对这个方法进行测试testForEach，很简单我就调用PS这个类的foreach就行了，对它测试最关键的是我加了这个注解@Benchmark，这个是JMH的注解，是要被JMH来解析处理的，这也是我们为么要把那个Annotation Processing给设置上的原因，非常简单，你只要加上注解就可以对这个方法进行微基准测试了，点击右键直接run。

```java
import org.openjdk.jmh.annotations.*;


public class PSTest {
    @Benchmark
    @Warmup(iterations=1,time=3)//在专业测试里面首先要进行预热，预热多少次，预热多少时间
    @Fork(5)//意思是用多少个线程去执行我们的程序
    @BenchmarkMode(Mode.Throughput)//是对基准测试的一个模式，这个模式用的最多的是Throughput吞吐量
    @Measurement(iterations = 1, time = 3)//是整个测试要测试多少遍，调用这个方法要调用多少次
    public void testForEach() {
        PS.foreach();
    }
}
```

#### 4、运行结果

```txt
# JMH version: 1.21
# VM version: JDK 1.8.0_281, Java HotSpot(TM) 64-Bit Server VM, 25.281-b09
# VM invoker: C:\software\Java\jdk1.8\jdk\jre\bin\java.exe
# VM options: -javaagent:E:\dev-tools\IntelliJ IDEA 2021.1\lib\idea_rt.jar=59347:E:\dev-tools\IntelliJ IDEA 2021.1\bin -Dfile.encoding=UTF-8
# Warmup: 1 iterations, 3 s each
# Measurement: 1 iterations, 3 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: com.ouyangliuy.PSTest.testForEach

# Run progress: 0.00% complete, ETA 00:00:30
# Fork: 1 of 5
# Warmup Iteration   1: 0.492 ops/s
Iteration   1: 0.498 ops/s

# Run progress: 20.00% complete, ETA 00:00:35
# Fork: 2 of 5
# Warmup Iteration   1: 0.495 ops/s
Iteration   1: 0.496 ops/s

# Run progress: 40.00% complete, ETA 00:00:26
# Fork: 3 of 5
# Warmup Iteration   1: 0.527 ops/s
Iteration   1: 0.528 ops/s

# Run progress: 60.00% complete, ETA 00:00:17
# Fork: 4 of 5
# Warmup Iteration   1: 0.532 ops/s
Iteration   1: 0.537 ops/s

# Run progress: 80.00% complete, ETA 00:00:08
# Fork: 5 of 5
# Warmup Iteration   1: 0.525 ops/s
Iteration   1: 0.528 ops/s


Result "com.ouyangliuy.PSTest.testForEach":
  0.517 ±(99.9%) 0.073 ops/s [Average]
  (min, avg, max) = (0.496, 0.517, 0.537), stdev = 0.019
  CI (99.9%): [0.444, 0.591] (assumes normal distribution)


# Run complete. Total time: 00:00:41

REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.

Benchmark            Mode  Cnt  Score   Error  Units
PSTest.testForEach  thrpt    5  0.517 ± 0.073  ops/s
```

#### 5、JMH中的基本概念

1. Warmup：预热，由于JVM中对于特定代码会存在优化（本地化），预热对于测试结果很重要
2. Mesurement：总共执行多少次测试
3. Timeout
4. Threads：线程数，由fork指定
5. Benchmark mode：基准测试的模式
6. Benchmark：测试哪一段代码

# Disruptor

按照英文翻译的话，Disruptor应该是分裂、瓦解。这个Disruptor是一个做金融的、做股票的这样一个公司交易所来开发的，为自己来开发的这么一个底层的框架，开发出来之后受到了很多的认可，开源之后，2011年获得Duke将。如果你想把它用作MQ的话，单机最快的MQ。性能非常的高，主要是它里面用的全都是cas，另外把各种各样的性能开发到了极致，所以他单机支持很高的一个并发。

> Disruptor不是平时我们学的这个redis、不是平时我们所学的kafka，他可以跟他们一样有类似的用途，但他是单机，redis、kafka也可以用于集群。redis他有这种序列化的机制，就是你可以把它存储到硬盘上或数据库当中是可以的，kafka当然也有，Disruptor没有，Disruptor就是在内存里，Disruptor简单理解就是内存里用于存放元素的一个高效率的队列。

### **什么是disruptor：目前性能最高的MQ**

> Disruptor叫无锁、高并发、环形Buffer，直接覆盖（不用清除）旧的数据，降低GC频率，用于生产者消费者模式（如果说按照设计者角度来讲他就是观察者模式）。什么叫观察者模式，想象一下，我们在前面学各种各样的队列的时候，队列就是个容器，好多生产者往里头扔东西，好多消费者从里头往外拿东西。所谓的生产者消费者就是这个意思，为什么我们可以叫他观察者呢，因为这些消费者正在观察着里面有没有新东西，如果有的话我马上拿过来消费，所以他也是一种观察者模式。Disruptor实现的就是这个容器

### Disruptor核心与特点

Disruptor也是一个队列，和其他队列不一样的是他是一个环形队列，环形的Buffer。一般情况下我们的容器是一个队列，不管你是用链表实现还是用数组实现的，它会是一个队列，那么这个队列生产者这边使劲往里塞，消费者这边使劲往外拿，但Disruptor的核心是一个环形的buffer。

Disruptor是用数组实现，把数组的头尾相连。这样的一个队列，你可以认为Disruptor就是用数组实现的ConcurrentArrayQueue，另外这个Queue是首尾相连的。

Disruptor只有一个指针， 这个指针位置怎么计算：使用当前sequence序列的数对容量求余。

- RingBuffer是一个环形队列
- RingBuffer的序号，指向下一个可用的元素
- 采用数组实现，没有首尾指针
- 对比ConcurrentLinkedQueue，用数组实现的速度更快

> 假如长度为8，当添加到第12个元素的时候在哪个序号上呢？用12%8决定
>
> 当Buffer被填满的时候到底是覆盖还是等待，由Produce决定
>
> 长度设为2的n次幂，利于二进制计算，例如：12%8=12&（8-1）

#### 问题：我生产者线程生产的特别多，消费者没来得及消费那我在往后覆盖的话怎么办？

> 不会轻易覆盖的，其中是有策略的，我生产者生产满了，要在生产一个的话就马上覆盖这个位置上的数了。这时候是不能覆盖的，指定了一个策略叫等待策略，这里面有**8种等待策略**，分情况自己去用。最常见的是BlockingWait，满了我就在这等着，什么时候你空了消费者来唤醒一下就继续。

### Disruptor如何使用

### ProducerType生产者线程模式

- ProducerType有两种模式ProducerMULTI和Producer.SINGLE
- 默认是MULTI，表示在多线程模式下产生sequence
- 如果确认是单线程生产者，那么可以指定SINGLE，效率会提升
- 如果是多个生产者（多线程），但模式指定为SINGLE，会出什么问题？

### 等待策略

- （常用）BlockingWaitStrategy：通过线程堵塞的方式，等待生产者唤醒，被唤醒后，再循环检查依赖的sequence是否已经消费。
- BusySpinWaitStrategy：线程一直自旋等待，可能比较耗cpu
- LiteBlockingWaitStrategy：线程阻塞等待生产者唤醒，与BlockingWaitStrategy相比，区别在signalNeeded.getAndSet，如果两个线程同时访问一个访问waitfor，一个访问signalAll时，可以减少lock加锁次数
- LiteTimeoutBlockingWaitStrategy：与LiteBlockingWaitStrategy相比，设置了阻塞时间，超过时间后抛出异常
- PhasedBackoffWaitStrategy：根据时间参数和传入的等待策略来决定使用那种等待策略
- TimeoutBlockingWaitStrategy：相对于BlockingWaitStrategy来说，设置了等待时间，超过后抛出异常
- （常用）YieldingWaitStrategy：尝试100次，然后Thread.yield()让出cpu
- （常用）SleepingWaitStrategy：sleep

> 用的BlockingWaitStrategy满了就等着；SleepingWaitStrategy满了就睡一觉，睡醒了看看能不能继续执行了；YieldingWaitStrategy让出cpu，让你消费者赶紧消费，消费完了之后我又回来看看我是不是又能生产了；一般YieldingWaitStrategy效率是最高的，但也要看实际情况适用不适用。

### 多个消费者情况

多个消费者怎么指定，默认的情况下只有一个消费者，你想要有多个消费者的时候也非常简单，看下面代码我定义了两个消费者h1、h2，disruptor.handleEventsWith(h1,h2)这里面是一个可变参数，所以你要想有多个消费者的时候就往里装，多个消费者是位于多个线程里面的。

```java
package com.ouyangliuy.disruptor;

import java.util.concurrent.*;

import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;


public class Main06_MultiConsumer {
    public static void main(String[] args) throws Exception {

        //the factory for the event
        LongEventFactory factory = new LongEventFactory();

        //Specify the of the ring buffer,must be power of 2.
        int bufferSize = 1024;

        //Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<>(factory,
                bufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.MULTI, new SleepingWaitStrategy());

        //Connect the handlers
        LongEventHandler h1 = new LongEventHandler();
        LongEventHandler h2 = new LongEventHandler();
        // 提供多个消费者
        disruptor.handleEventsWith(h1, h2);

        //Start the Disruptor,start all threads running
        disruptor.start();

        //Get the ring buffer form the Disruptor to be used for publishing.
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        //========================================================================
        final int threadCount = 10;
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        ExecutorService service = Executors.newCachedThreadPool();
        for (long i = 0; i < threadCount; i++) {
            final long threadNum = i;
            service.submit(() -> {
                System.out.printf("Thread %s ready to start!\n", threadNum);
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }

                for (int j = 0; j < 10; j++) {
                    ringBuffer.publishEvent((event, sequence) -> {
                        event.set(threadNum);
                        System.out.println("生产了" + threadNum);
                    });
                }
            });
        }

        service.shutdown();
        //disruptor.shutdown();
        TimeUnit.SECONDS.sleep(3);
        System.out.println(LongEventHandler.count);
    }
}
```



### 消费者异常执行

默认：disruptor.setDefaultExceptionHandler()

覆盖：disruptor.handleExceptionFor().with()

> 这这里方法里写了一个EventHandler是我们的消费者，在消费者里打印了event之后马上抛出了异常，当我们消费者出现异常之后你不能让整个线程停下来，有一个消费者出了异常那其他的消费者就不干活了，肯定不行。handleExceptionsFor为消费者指定Exception处理器 (h1).with后面是我们的ExceptionHandler出了异常之后该怎么办进行处理，重写三个方法，第一个是当产生异常的时候在这很简单直接打印出来了；第二个是handleOnStart如果启动的时候出异常；第三个handleOnShutdown你该怎么处理。



```java
package com.ouyangliuy.disruptor;

import java.util.concurrent.*;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.ProducerType;

public class Main07_ExceptionHandler {
    public static void main(String[] args) throws Exception {

        //the factory for the event
        LongEventFactory factory = new LongEventFactory();

        //Specify the of the ring buffer,must be power of 2.
        int bufferSize = 1024;

        //Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<>(factory, bufferSize, Executors.defaultThreadFactory(), ProducerType.MULTI, new SleepingWaitStrategy());

        //Connect the handlers
        EventHandler h1 = (event, sequence, end) -> {
            System.out.println("消费者出异常");
        };
        disruptor.handleEventsWith(h1);
           
        disruptor.handleExceptionsFor(h1).with(new ExceptionHandler<LongEvent>() {
            @Override
            public void handleEventException(Throwable throwable, long l, LongEvent longEvent) {
                throwable.printStackTrace();
            }

            @Override
            public void handleOnStartException(Throwable throwable) {
                System.out.println("Exception Start to Handle!");
            }

            @Override
            public void handleOnShutdownException(Throwable throwable) {
                System.out.println("Exception End to Handle!");
            }
        });

        //Start the Disruptor,start all threads running
        disruptor.start();

        //Get the ring buffer form the Disruptor to be used for publishing.
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        //========================================================================
        final int threadCount = 1;
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        ExecutorService service = Executors.newCachedThreadPool();
        for (long i = 0; i < threadCount; i++) {
            final long threadNum = i;
            service.submit(() -> {
                System.out.printf("Thread %s ready to start!\n", threadNum);
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }

                for (int j = 0; j < 10; j++) {
                    ringBuffer.publishEvent((event, sequence) -> {
                        event.set(threadNum);
                        System.out.println("生产了" + threadNum);
                    });
                }
            });
        }

        service.shutdown();
        //disruptor.shutdown();
        TimeUnit.SECONDS.sleep(3);
        System.out.println(LongEventHandler.count);
    }
}
```

### disruptor总结：

disruptor是一个环，然后这个环有多个生产者可以往里头生产，由于它是环形的设计效率会非常的高，我们写程序的时候是这样写的，首先你自己定义好Event消息的格式，然后定义消息工厂，消息工厂是用来初始化整个环的时候相应的一些位置上各种各样不同的消息先把它new出来，new出来之后先占好空间，我们在生产的时候只需要把这个位置上这个默认的这块空间拿出来往里头填值，填好值之后消费者就可以往里头消费了，消费完了生产者就可以继续往里头生产了，如果说你生产者消费的比较快，消费着消费的比较慢，满了怎么办，就是用各种各样的等待策略，消费者出了问题之后可以用ExceptionHandler来进行处理。

# 2、多线程面试题

## 题目1: 两个线程操作容器,提供add和size方法,实时监控元素个数，当个数到5个时，线程2给出出提示并结束

实现一个容器，提供两个方法add、size，写两个线程：

线程1，添加10个元素到容器中

线程2，实时监控元素个数，当个数到5个时，线程2给出提示并结束

## 面试题2:固定容量同步容器,实现阻塞插入和获取

写一个固定容量同步容器，拥有put和get方法，以及getCount方法，能够支持2个生产者线程以及10个消费者线程的阻塞调用。

### 方法1:使用synchronized和wait, notifyAll实现

```java
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
```

### 方法2:使用ReentrantLock和Condition的await, signalAll实现

```java
package com.ouyangliuy.base.questions;

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
```