# 1、Java线程与常用线程池体系

## 1.1、Java线程实现方式

### 1.1.1、继承Thread

1. **继承Thread**
2. 定义Thread类的子类，并重写该类的run方法，该run方法的方法体就代表了线程要完成的任务。因此把run()方法称为执行体。
3. 创建Thread子类的实例，即创建了线程对象。
4. 调用线程对象的start()方法来启动该线程。

### 1.1.2、实现runnable接口

（1）定义runnable接口的实现类，并重写该接口的run()方法，该run()方法的方法体同样是该线程的线程执行体。

（2）创建 Runnable实现类的实例，并以此实例作为Thread的target来创建Thread对象，该Thread对象才是真正的线程对象。

（3）调用线程对象的start()方法来启动该线程。

### 1.1.3、实现callable接口

1）创建Callable接口的实现类，并实现call()方法，该call()方法将作为线程执行体，并且有返回值。
public interface Callable{
　　      V call() throws Exception; }
2）创建Callable实现类的实例，使用FutureTask类来包装Callable对象，该FutureTask对象封装了该Callable对象的call()方法的返回值。（FutureTask是一个包装器，它通过接受Callable来创建，它同时实现了Future和Runnable接口）
3）使用FutureTask对象作为Thread对象的target创建并启动新线程。
4）调用FutureTask对象的get()方法来获得子线程执行结束后的返回值。

```java
public class Demo implements Callable<Integer>  {  
  
    public static void main(String[] args)  
    {  
        Demo demo = new Demo ();  
        FutureTask<Integer> ft = new FutureTask<>(demo); 
        new Thread(ft ,"有返回值的线程").start();
        ft.get();
    } 
    @Override  
    public Integer call() throws Exception  
    {  
          return 1;  
    }  
}
```

## 1.2、常用线程池体系

1. Executor：线程池顶级接口
2. ExecutorService：线程池次级接口，对Executor做了一些扩展，增加了一些功能
3. ScheduleExecutorService：对ExecutorService做了一些扩展，增加了一些订什任务相关的功能
4. AbstractExecutorService：抽象类，运用模板方法设计模式实现了一部分方法
5. ThreadPoolExecutor：普通线程池类，包含最基本的一些线程池操作相关的方法实现
6. ScheduleThreadPoolExecutor：定时任务线程池类，用于实现定时任务相关功能
7. ForkJoinPool：新型线程池类，Java7中新增的线程池类，基于工作窃取理论实现，用于大任务拆小任务，任务无限多的场景
8. Executors：线程池工具类，定义了一些快速实现线程池的方法

### 1.2.1、Executor

```java
public interface Executor {

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the {@code Executor} implementation.
     * 执行无返回值任务,根据Executor的实现判断，可能是在新线程、线程池、线程调用中执行 
     */
    void execute(Runnable command);
}
```

### 1.2.2、ExecutorService

```java
public interface ExecutorService extends Executor {

    // 关闭线程池，不再接受新任务，但已经提交的任务会执行完成
    void shutdown();

    /**
     * 立即关闭线程池，尝试停止正在运行的任务，未执行的任务不在执行，被迫停止及未执行的任务将以列表形式返回
     */
    List<Runnable> shutdownNow();
	// 检查线程状态是否已关闭
    boolean isShutdown();

    /**
     * Returns {@code true} if all tasks have completed following shut down.
     * Note that {@code isTerminated} is never {@code true} unless
     * either {@code shutdown} or {@code shutdownNow} was called first.
     * 检查线程是否已终止，只有在shutdown()或shutdownNow()之后调用才为true
     */
    boolean isTerminated();

    /**
     * Blocks until all tasks have completed execution after a shutdown
     * request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
     * @return {@code true} if this executor terminated and
     * 		   {@code false} if the timeout elapsed before termination
     * 在指定时间内线程池达到终止状态了才会返回true
     */
    boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException;

    /**
     * Submits a value-returning task for execution and returns a
     * Future representing the pending results of the task. The
     * Future's {@code get} method will return the task's result upon
     * successful completion.
     *
     * 执行有返回值的任务，任务的返回值为task.call()的结果
     */
    <T> Future<T> submit(Callable<T> task);

    /**
     * Submits a Runnable task for execution and returns a Future
     * representing that task. The Future's {@code get} method will
     * return the given result upon successful completion.
     * 执行有返回值的任务，任务的返回值为这里传入的result 
     * 当然只有当任务执行完成了调用get()时才会返回
     */
    <T> Future<T> submit(Runnable task, T result);

    /**
     * Submits a Runnable task for execution and returns a Future
     * representing that task. The Future's {@code get} method will
     * return {@code null} upon <em>successful</em> completion.
     * 执行有返回值的任务，任务的返回值为null，当然只有当任务执行完成了调用get()时才会返回
     */
    Future<?> submit(Runnable task);

    /**
     * Executes the given tasks, returning a list of Futures holding
     * their status and results when all complete.
     * {@link Future#isDone} is {@code true} for each
     * element of the returned list.
     * Note that a <em>completed</em> task could have
     * terminated either normally or by throwing an exception.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     * 批量执行任务，只有当这些任务都完成了这个方法才会返回
     */
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException;

    /**
     * Executes the given tasks, returning a list of Futures holding
     * their status and results
     * when all complete or the timeout expires, whichever happens first.
     * {@link Future#isDone} is {@code true} for each
     * element of the returned list.
     * Upon return, tasks that have not completed are cancelled.
     * Note that a <em>completed</em> task could have
     * terminated either normally or by throwing an exception.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     * 在指定时间内批量执行任务，未执行完成的任务将被取消
     * 这里的timeout是所有任务的总时间，不是单个任务的时间 
     */
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                  long timeout, TimeUnit unit)
        throws InterruptedException;

    /**
     * Executes the given tasks, returning the result
     * of one that has completed successfully (i.e., without throwing
     * an exception), if any do. Upon normal or exceptional return,
     * tasks that have not completed are cancelled.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     * 返回任意一个已完成任务的执行结果，未执行完成的任务将被取消
     */
    <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException;

    /**
     * Executes the given tasks, returning the result
     * of one that has completed successfully (i.e., without throwing
     * an exception), if any do before the given timeout elapses.
     * Upon normal or exceptional return, tasks that have not
     * completed are cancelled.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     * 在指定时间内如果有任务已完成，则返回任意一个已完成任务的执行结果，未执行完成的任务将被取消
     */
    <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                    long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
```

### 1.2.3、AbstractExecutorService

```java

public abstract class AbstractExecutorService implements ExecutorService {

    /**
     * Returns a {@code RunnableFuture} for the given runnable and default
     * value.
     * 将runnable包装为RunnableFuture对象，返回值为T
     */
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new FutureTask<T>(runnable, value);
    }

    /**
     * Returns a {@code RunnableFuture} for the given callable task.
     * 将callable包装为RunnableFuture
     */
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new FutureTask<T>(callable);
    }

    public Future<?> submit(Runnable task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<Void> ftask = newTaskFor(task, null);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Runnable task, T result) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task, result);
        execute(ftask);
        return ftask;
    }


    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
    }

    /**
     * the main mechanics of invokeAny.
     */
    private <T> T doInvokeAny(Collection<? extends Callable<T>> tasks,
                              boolean timed, long nanos)
        throws InterruptedException, ExecutionException, TimeoutException {
        if (tasks == null)
            throw new NullPointerException();
        int ntasks = tasks.size();
        if (ntasks == 0)
            throw new IllegalArgumentException();
        ArrayList<Future<T>> futures = new ArrayList<>(ntasks);
        // 将this对象放入ecs中使用ecs，完成任务执行
        ExecutorCompletionService<T> ecs =
            new ExecutorCompletionService<T>(this);

        // For efficiency, especially in executors with limited
        // parallelism, check to see if previously submitted tasks are
        // done before submitting more of them. This interleaving
        // plus the exception mechanics account for messiness of main
        // loop.

        try {
            // Record exceptions so that if we fail to obtain any
            // result, we can throw the last exception we got.
            ExecutionException ee = null;
            // 判断一下是否是timed超时执行
            final long deadline = timed ? System.nanoTime() + nanos : 0L;
            Iterator<? extends Callable<T>> it = tasks.iterator();

            // Start one task for sure; the rest incrementally
            // 开始执行一个任务，剩余的任务减一个
            futures.add(ecs.submit(it.next()));
            --ntasks; //减少操作
            int active = 1;	//当前激活线程为1个

            for (;;) {
                Future<T> f = ecs.poll(); // 从阻塞队列中拿出上面添加的任务
                if (f == null) {	// 为null，说明没有执行完毕
                    if (ntasks > 0) {	// 需要执行线程还存在还有
                        --ntasks;
                        futures.add(ecs.submit(it.next()));	// 通过ecs执行
                        ++active;
                    }
                    else if (active == 0)
                        break;
                    else if (timed) {
                        f = ecs.poll(nanos, NANOSECONDS);
                        if (f == null)
                            throw new TimeoutException();
                        nanos = deadline - System.nanoTime();
                    }
                    else
                        f = ecs.take();
                }
                if (f != null) { // 不为空，执行完毕
                    --active;	// 激活线程数--
                    try {
                        return f.get();	// 拿到返回值
                    } catch (ExecutionException eex) {
                        ee = eex;
                    } catch (RuntimeException rex) {
                        ee = new ExecutionException(rex);
                    }
                }
            }

            if (ee == null)
                ee = new ExecutionException();
            throw ee;

        } finally {
            cancelAll(futures);
        }
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException {
        try {
            return doInvokeAny(tasks, false, 0);
        } catch (TimeoutException cannotHappen) {
            assert false;
            return null;
        }
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                           long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        return doInvokeAny(tasks, true, unit.toNanos(timeout));
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException {
        if (tasks == null)
            throw new NullPointerException();
        ArrayList<Future<T>> futures = new ArrayList<>(tasks.size());
        try {
            // for 循环将callable任务包装成RunnableFuture，并执行
            for (Callable<T> t : tasks) {
                RunnableFuture<T> f = newTaskFor(t);
                futures.add(f);
                execute(f);
            }
            // for循环等待每个任务依次运行结束
            for (int i = 0, size = futures.size(); i < size; i++) {
                Future<T> f = futures.get(i);
                if (!f.isDone()) {
                    try { f.get(); }
                    catch (CancellationException | ExecutionException ignore) {}
                }
            }
            // 返回运行结果futures
            return futures;
        } catch (Throwable t) {
            cancelAll(futures);
            throw t;
        }
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                         long timeout, TimeUnit unit)
        throws InterruptedException {
        if (tasks == null)
            throw new NullPointerException();
        final long nanos = unit.toNanos(timeout);
        final long deadline = System.nanoTime() + nanos;
        ArrayList<Future<T>> futures = new ArrayList<>(tasks.size());
        int j = 0;
        timedOut: try {
            for (Callable<T> t : tasks)	//添加任务
                futures.add(newTaskFor(t));

            final int size = futures.size();

            // Interleave time checks and calls to execute in case
            // executor doesn't have any/much parallelism.
            for (int i = 0; i < size; i++) {
                if (((i == 0) ? nanos : deadline - System.nanoTime()) <= 0L)
                    break timedOut;
                execute((Runnable)futures.get(i));	//执行所有任务，在超时时间之内
            }

            for (; j < size; j++) {
                Future<T> f = futures.get(j);
                if (!f.isDone()) {
                    try { f.get(deadline - System.nanoTime(), NANOSECONDS); }
                    catch (CancellationException | ExecutionException ignore) {}
                    catch (TimeoutException timedOut) {
                        break timedOut;
                    }
                }
            }
            return futures;
        } catch (Throwable t) {
            cancelAll(futures);
            throw t;
        }
        // Timed out before all the tasks could be completed; cancel remaining
        cancelAll(futures, j);
        return futures;
    }

    private static <T> void cancelAll(ArrayList<Future<T>> futures) {
        cancelAll(futures, 0);
    }

    /** Cancels all futures with index at least j. */
    private static <T> void cancelAll(ArrayList<Future<T>> futures, int j) {
        for (int size = futures.size(); j < size; j++)
            futures.get(j).cancel(true);
    }
}
```

#### 1.2.3.1、inovkeAny

总结：inovkeAny会出现执行多个任务，但只取第一个任务结果的情况

#### 1.2.3.2、invokeAll

总结：执行所有的方法，并返回所有的执行结果

### 1.2.4、ExecutorCompletionService

```java
public class ExecutorCompletionService<V> implements CompletionService<V> {
    private final Executor executor;
    private final AbstractExecutorService aes;
    private final BlockingQueue<Future<V>> completionQueue;

    /**
     * FutureTask extension to enqueue upon completion.
     */
    private static class QueueingFuture<V> extends FutureTask<Void> {
        QueueingFuture(RunnableFuture<V> task,
                       BlockingQueue<Future<V>> completionQueue) {
            super(task, null);
            this.task = task;
            this.completionQueue = completionQueue;
        }
        private final Future<V> task;
        private final BlockingQueue<Future<V>> completionQueue;
        protected void done() { completionQueue.add(task); }
    }

    private RunnableFuture<V> newTaskFor(Callable<V> task) {
        if (aes == null)
            return new FutureTask<V>(task);
        else
            return aes.newTaskFor(task);
    }

    private RunnableFuture<V> newTaskFor(Runnable task, V result) {
        if (aes == null)
            return new FutureTask<V>(task, result);
        else
            return aes.newTaskFor(task, result);
    }

    /**
     * Creates an ExecutorCompletionService using the supplied
     * executor for base task execution and a
     * {@link LinkedBlockingQueue} as a completion queue.
     *
     * @param executor the executor to use
     * @throws NullPointerException if executor is {@code null}
     */
    public ExecutorCompletionService(Executor executor) {
        if (executor == null)
            throw new NullPointerException();
        this.executor = executor;
        this.aes = (executor instanceof AbstractExecutorService) ?
            (AbstractExecutorService) executor : null;
        this.completionQueue = new LinkedBlockingQueue<Future<V>>();
    }

    /**
     * Creates an ExecutorCompletionService using the supplied
     * executor for base task execution and the supplied queue as its
     * completion queue.
     *
     * @param executor the executor to use
     * @param completionQueue the queue to use as the completion queue
     *        normally one dedicated for use by this service. This
     *        queue is treated as unbounded -- failed attempted
     *        {@code Queue.add} operations for completed tasks cause
     *        them not to be retrievable.
     * @throws NullPointerException if executor or completionQueue are {@code null}
     */
    public ExecutorCompletionService(Executor executor,
                                     BlockingQueue<Future<V>> completionQueue) {
        if (executor == null || completionQueue == null)
            throw new NullPointerException();
        this.executor = executor;
        this.aes = (executor instanceof AbstractExecutorService) ?
            (AbstractExecutorService) executor : null;
        this.completionQueue = completionQueue;
    }

    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public Future<V> submit(Callable<V> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<V> f = newTaskFor(task);
        executor.execute(new QueueingFuture<V>(f, completionQueue));
        return f;
    }

    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public Future<V> submit(Runnable task, V result) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<V> f = newTaskFor(task, result);
        executor.execute(new QueueingFuture<V>(f, completionQueue));
        return f;
    }

    public Future<V> take() throws InterruptedException {
        return completionQueue.take();
    }

    public Future<V> poll() {
        return completionQueue.poll();
    }

    public Future<V> poll(long timeout, TimeUnit unit)
            throws InterruptedException {
        return completionQueue.poll(timeout, unit);
    }

}
```

## 2、ThreadPoolExecutor

### 2.1、ThreadPoolExecutor原理

![](../doc-all/images/muti-thread/TPE.png)

### 2.2、线程池的状态设计

1. RUNNING

   > Accept new tasks and process queued tasks
   >
   > 接收新任务，执行队列中任务
   >
2. SHUTDOWN

   > Don't accept new tasks, but process queued tasks
   >
   > 不接受任务，执行队列任务
   >
3. STOP

   > Don't accept new tasks, don't process queued tasks, and interrupt in-progress task
   >
   > 不接受任务，不执行队列中任务，中断正在执行的任务
   >
4. TIDYING

   > All tasks have terminated, workerCount is zero,the thread transitioning to state TIDYING will run the terminated() hook method.
   >
   > 所有的任务执行完毕，转化状态为TIDYING，执行terminated钩子函数
   >
5. TERMINATED

   > terminated() has completed.
   >
   > 钩子函数执行完毕，状态为TERMINATED
   >

```java
/*   RUNNING:  Accept new tasks and process queued tasks
*   SHUTDOWN: Don't accept new tasks, but process queued tasks
*   STOP:     Don't accept new tasks, don't process queued tasks,
*             and interrupt in-progress tasks
*   TIDYING:  All tasks have terminated, workerCount is zero,
*             the thread transitioning to state TIDYING
*             will run the terminated() hook method
			所有的任务执行完毕，转化状态为TIDYING，执行terminated钩子函数
*   TERMINATED: terminated() has completed
			// 钩子函数执行完毕，状态为TERMINATED
*/
```

**钩子函数：**

> 执行完主方法之后，需要执行的钩子函数，将执行结果通过钩子函数通知调用者

**使用int的高三位来表示运行的状态，其他位代笔线程运行数据**

```java
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
private static final int COUNT_BITS = Integer.SIZE - 3;
private static final int COUNT_MASK = (1 << COUNT_BITS) - 1;
// runState is stored in the high-order bits
private static final int RUNNING    = -1 << COUNT_BITS;		// 111 00..
private static final int SHUTDOWN   =  0 << COUNT_BITS;		// 000 00..
private static final int STOP       =  1 << COUNT_BITS;		// 001 00..
private static final int TIDYING    =  2 << COUNT_BITS;		// 010 00..
private static final int TERMINATED =  3 << COUNT_BITS;		// 011 00..
```

思考：为什么这样设计

> 此时只有ctl小于0，说明线程是在运行中的。条件判断非常方便。
>
> ```c
> if(ctl<0)
> 	doSomething();
> ```

### 2.2、ThreadPoolExecutor核心方法

#### 2.2.1、拒绝策略

ThreadPoolExecutor中有默认四种拒绝策略实现，具体如下：

拒绝接口

```java
public interface RejectedExecutionHandler {
    void rejectedExecution(Runnable r, ThreadPoolExecutor executor);
}
```

##### 2.2.1.1、拒绝策略之抛出异常

```java
public static class AbortPolicy implements RejectedExecutionHandler {
    /**
     * Creates an {@code AbortPolicy}.
     */
    public AbortPolicy() { }

    /**
     * Always throws RejectedExecutionException.
     *
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     * @throws RejectedExecutionException always
     */
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        throw new RejectedExecutionException("Task " + r.toString() +
                                             " rejected from " +
                                             e.toString());
    }
}
```

##### 2.2.1.2、拒绝策略之丢弃啥也不做

```java
/**
 * A handler for rejected tasks that silently discards the
 * rejected task.
 */
public static class DiscardPolicy implements RejectedExecutionHandler {
    /**
     * Creates a {@code DiscardPolicy}.
     */
    public DiscardPolicy() { }

    /**
     * Does nothing, which has the effect of discarding task r.
     *
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     */
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    }
}
```

##### 2.2.1.3、拒绝策略之丢弃最老的执行最新的

```java
/**
 * A handler for rejected tasks that discards the oldest unhandled
 * request and then retries {@code execute}, unless the executor
 * is shut down, in which case the task is discarded.
 */
public static class DiscardOldestPolicy implements RejectedExecutionHandler {
    /**
     * Creates a {@code DiscardOldestPolicy} for the given executor.
     */
    public DiscardOldestPolicy() { }

    /**
     * Obtains and ignores the next task that the executor
     * would otherwise execute, if one is immediately available,
     * and then retries execution of task r, unless the executor
     * is shut down, in which case task r is instead discarded.
     *
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     */
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (!e.isShutdown()) {
            e.getQueue().poll();
            e.execute(r);
        }
    }
}
```

##### 2.2.1.4、拒绝策略之主线程自己执行任务

```java
/**
 * A handler for rejected tasks that runs the rejected task
 * directly in the calling thread of the {@code execute} method,
 * unless the executor has been shut down, in which case the task
 * is discarded.
 */
public static class CallerRunsPolicy implements RejectedExecutionHandler {
    /**
     * Creates a {@code CallerRunsPolicy}.
     */
    public CallerRunsPolicy() { }

    /**
     * Executes task r in the caller's thread, unless the executor
     * has been shut down, in which case the task is discarded.
     *
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     */
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (!e.isShutdown()) {
            r.run();
        }
    }
}
```

#### 2.2.2、ThreadFactory

```java
public interface ThreadFactory {

    /**
     * Constructs a new {@code Thread}.  Implementations may also initialize
     * priority, name, daemon status, {@code ThreadGroup}, etc.
     *
     * @param r a runnable to be executed by new thread instance
     * @return constructed thread, or {@code null} if the request to
     *         create a thread is rejected
     */
    Thread newThread(Runnable r);
}


private static class DefaultThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    DefaultThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                              Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" +
                      poolNumber.getAndIncrement() +
                     "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                              namePrefix + threadNumber.getAndIncrement(),
                              0);
        // 由此可见，线程工程创建出的线程不是后台线程
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
```

线程核心原理

阻塞队列

```java
/* A {@link Queue} that additionally supports operations that wait for
 * the queue to become non-empty when retrieving an element, and wait
 * for space to become available in the queue when storing an element.
 * 检索(获取)元素时，等待队列中直到有数据，添加元素直到有可用空间
 */
public interface BlockingQueue<E> extends Queue<E> {
   
 }   
```

#### 2.2.3、execute

总结：核心方法主要有以下步骤

1. 如果运行的线程数小于核心线程数，那么就新增一个核心线程去执行任务
2. 如果从队列中拿到一个任务，并重新检测状态，使用核心线程数去执行任务
3. 如果任务不能加入到队列中，那么尝试新启动一个线程去执行，这个线程是非核心线程数
4. 如果队列满了，运行的线程数已经是最大线程数了，那么就执行拒绝策略

```java
public void execute(Runnable command) {
    if (command == null)
        throw new NullPointerException();
    /*
     * Proceed in 3 steps:
     *
     * 1. If fewer than corePoolSize threads are running, try to
     * start a new thread with the given command as its first
     * task.  The call to addWorker atomically checks runState and
     * workerCount, and so prevents false alarms that would add
     * threads when it shouldn't, by returning false.
     *
     * 2. If a task can be successfully queued, then we still need
     * to double-check whether we should have added a thread
     * (because existing ones died since last checking) or that
     * the pool shut down since entry into this method. So we
     * recheck state and if necessary roll back the enqueuing if
     * stopped, or start a new thread if there are none.
     *
     * 3. If we cannot queue task, then we try to add a new
     * thread.  If it fails, we know we are shut down or saturated
     * and so reject the task.
     */
    int c = ctl.get();
    // 当前工作线程数，小于核心线程数，那么就添加到worker中，执行
    if (workerCountOf(c) < corePoolSize) {
        if (addWorker(command, true))
            return;
        c = ctl.get();
    }
    // 此时运行线程数已经大于了核心线程数，那么尝试添加任务到工作队列中，
    // 并重新检查是否已经有任务执行完成，或
    if (isRunning(c) && workQueue.offer(command)) {
        int recheck = ctl.get();
        if (! isRunning(recheck) && remove(command))
            reject(command);
        else if (workerCountOf(recheck) == 0)
            addWorker(null, false);
    }
    else if (!addWorker(command, false))	// 否则，就尝试添加为非核心线程数得线程去执行任务
        reject(command);
}
```

#### 2.2.4、addWorker

新增线程实际执行体Worker方法，添加成功，就执行。

```java
 private boolean addWorker(Runnable firstTask, boolean core) {
        retry:
     // 当前方法，判断状态，线程数变量+1
        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            // Check if queue empty only if necessary.
            if (rs >= SHUTDOWN &&	// 在线程池状态为shutdown之后，保证
                ! (rs == SHUTDOWN &&	// 线程已经终止
                   firstTask == null &&	// 新任务为空，
                   ! workQueue.isEmpty()))	// 工作队列不为空，此时返回false，执行下面得for执行任务
                return false;

            for (;;) {
                int wc = workerCountOf(c);	// 工作线程得数量
                if (wc >= CAPACITY || // wc大于最大容量，直接返回false
                    wc >= (core ? corePoolSize : maximumPoolSize)) // 根据core
                    return false;
                if (compareAndIncrementWorkerCount(c)) // c数量+1
                    break retry;
                // cas更新失败，重新读一遍，c，因为有可能被其他改过
                c = ctl.get();  // Re-read ctl 
                if (runStateOf(c) != rs)  // 判断状态，重新执行
                    continue retry;
                // else CAS failed due to workerCount change; retry inner loop
            }
        }

        boolean workerStarted = false;
        boolean workerAdded = false;
        Worker w = null;
        try {
            // 创建任务执行类，线程得包装
            w = new Worker(firstTask);
            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                // 多线程，保证workers原子性
                mainLock.lock();
                try {
                    // Recheck while holding lock.
                    // Back out on ThreadFactory failure or if
                    // shut down before lock acquired.
                    int rs = runStateOf(ctl.get());

                    if (rs < SHUTDOWN ||
                        (rs == SHUTDOWN && firstTask == null)) {
                        if (t.isAlive()) // precheck that t is startable
                            throw new IllegalThreadStateException();
                        workers.add(w);
                        int s = workers.size();
                        if (s > largestPoolSize)
                            largestPoolSize = s;
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    t.start();		// 添加成功，运行work包装得run方法
                    workerStarted = true;
                }
            }
        } finally {
            if (! workerStarted)
                addWorkerFailed(w);	// 启动失败，从workers移除，并将c减1
        }
        return workerStarted;
    }
```

#### 2.2.5、runWork

从队列中拿到任务，并运行

```java
final void runWorker(Worker w) {
    Thread wt = Thread.currentThread();
    Runnable task = w.firstTask;
    w.firstTask = null;
    w.unlock(); // allow interrupts,改变标志位，允许中断
    boolean completedAbruptly = true;
    try {
        while (task != null || (task = getTask()) != null) { // 不为空，或去拿
            w.lock();
            // If pool is stopping, ensure thread is interrupted;
            // if not, ensure thread is not interrupted.  This
            // requires a recheck in second case to deal with
            // shutdownNow race while clearing interrupt
            // 检查状态，状态判断为true，那么就中断线程
            if ((runStateAtLeast(ctl.get(), STOP) || // 任务已经被stop了，关闭了
                 (Thread.interrupted() &&	// 清除标记位，任务是否被中断过
                  runStateAtLeast(ctl.get(), STOP))) &&	// 
                !wt.isInterrupted())		// 检查是否已经调用过，没有那么就调用下面中断
                wt.interrupt();	// 直接中断线程
            try {
                beforeExecute(wt, task);
                Throwable thrown = null;
                try {
                    task.run();
                } catch (RuntimeException x) {
                    thrown = x; throw x;
                } catch (Error x) {
                    thrown = x; throw x;
                } catch (Throwable x) {
                    thrown = x; throw new Error(x);
                } finally {
                    afterExecute(task, thrown); // 最后回调，
                }
            } finally {
                task = null;
                w.completedTasks++;
                w.unlock();
            }
        }
        completedAbruptly = false;
    } finally {
        processWorkerExit(w, completedAbruptly);
    }
}

// Lock methods
//
// The value 0 represents the unlocked state.
// The value 1 represents the locked state.
 protected boolean isHeldExclusively() {
     return getState() != 0;
 }
```

#### 2.2.6、processWorkerExit

在runWork中的实际任务执行结束之后运行，修改线程的状态并执行回调函数。

```java
// 只有在运行afterExecute或beforeExecute出现异常之后，completedAbruptly才为true
private void processWorkerExit(Worker w, boolean completedAbruptly才为true) {
    if (completedAbruptly) // If abrupt, then workerCount wasn't adjusted
        decrementWorkerCount();	// 工作线程-1

    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        completedTaskCount += w.completedTasks;
        workers.remove(w); // 移除
    } finally {
        mainLock.unlock();
    }

    tryTerminate();

    int c = ctl.get();
    if (runStateLessThan(c, STOP)) {	// 当前状态是running或shutdown才为true
        if (!completedAbruptly) {		// 是否是用户异常，
            // 这里面代码，是为了保证至少有一个工作线程
            int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
            if (min == 0 && ! workQueue.isEmpty())
                min = 1; 	// 为了保证至少有一个工作线程
            if (workerCountOf(c) >= min) // 如果没有一个线程，那么执行addWorker(null, false)
                return; // replacement not needed
        }
        addWorker(null, false);			// 另起一个线程
    }
}
```

#### 2.2.7、tryTerminate()

任务执行结束之后的钩子函数，修改状态，执行terminated回调方法

```java
final void tryTerminate() {
    for (;;) {
        int c = ctl.get();
        if (isRunning(c) ||	// 当前线程是否正在运行，
            runStateAtLeast(c, TIDYING) ||	// 当前状态已经是TIDYING了
            (runStateOf(c) == SHUTDOWN && ! workQueue.isEmpty()))	// 线程中断了，并且工作队列不为空，那么就不执行tryTerminate方法，直接返回
            return;
        // 如何workerCountOf为0，那么当前执行它的就是最后一个线程，那么就继续执行terminated
        if (workerCountOf(c) != 0) { // Eligible to terminate
            interruptIdleWorkers(ONLY_ONE); //确保只有一个线程执行terminated方法
            return;
        }

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) { //cas修改状态为TIDYING
                try {
                    terminated();
                } finally {
                    ctl.set(ctlOf(TERMINATED, 0));	// 执行完毕之后修改状态为TERMINATED
                    termination.signalAll();	// 唤醒所有等待termination的方法
                }
                return;
            }
        } finally {
            mainLock.unlock();
        }
        // else retry on failed CAS
    }
}
```

#### 2.2.8、shutdown

设置线程SHUTDOWN状态，结束空闲线程，并不再接收新的任务，等待线程池的任务执行完毕

```java
public void shutdown() {
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        checkShutdownAccess();			// 检查权限，jdk自带，不需要研究
        advanceRunState(SHUTDOWN);	// 设置SHUTDOWN状态
        interruptIdleWorkers();			// 干掉空闲线程
        onShutdown(); // hook for ScheduledThreadPoolExecutor，钩子函数用于扩展，在onShutdown之后做一些事情
    } finally {
        mainLock.unlock();
    }
    tryTerminate();
}
```

#### 2.2.9、shutdownNow

设置线程STOP状态，中断正在执行的任务，返回未执行的任务。

```java
public List<Runnable> shutdownNow() {
    List<Runnable> tasks;
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        checkShutdownAccess();
        advanceRunState(STOP);	// 设置STOP状态
        interruptWorkers();		// 中断任务
        tasks = drainQueue();	// 取出未执行任务
    } finally {
        mainLock.unlock();
    }
    tryTerminate();
    return tasks;
}
```

```java
/**
 * Transitions runState to given target, or leaves it alone if
 * already at least the given target.
 *
 * @param targetState the desired state, either SHUTDOWN or STOP
 *        (but not TIDYING or TERMINATED -- use tryTerminate for that)
 */
private void advanceRunState(int targetState) {
    // assert targetState == SHUTDOWN || targetState == STOP;
    for (;;) {
        int c = ctl.get();		// 拿到当前状态c
        if (runStateAtLeast(c, targetState) ||	// c状态要大于targetState
            ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c))))	// 使用cas,将c的工作线程数和state状态进行合并 | 运算
            break;
    }
}
```

中断空闲线程

```java
private void interruptIdleWorkers(boolean onlyOne) {
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        for (Worker w : workers) {
            Thread t = w.thread;
            // 是否是空闲线程，是根据tryLock中状态判断，为0是，为1不是空闲
            if (!t.isInterrupted() && w.tryLock()) {	// cas 更新成功说明线程未被持有
                try {
                    t.interrupt();
                } catch (SecurityException ignore) {
                } finally {
                    w.unlock();
                }
            }
            if (onlyOne)
                break;
        }
    } finally {
        mainLock.unlock();
    }
}
```

## 3、ScheduledThreadPoolExecutor

### 3.1、线程调度存在的问题

因为调度线程中使用的是无界限队列，所以当添加的任务大于指定的核心线程数，那么会造成周期执行的任务或延迟任务会在线程数超级多的情况下，出现卡顿，甚至卡死的情况，**XXL-JOB使用该方法**进行分布式任务调度。

**解决办法：**周期执行调度任务线程池中嵌套线程池，因为线程池有拒绝策略，来避免线程过多情况。

```java
 ExecutorService service = Executors.newFixedThreadPool(3);
// 在只有一个核心线程数的情况下，同时运行了5个任务 
ScheduledThreadPoolExecutor schedule = new ScheduledThreadPoolExecutor(1);
 schedule.schedule(()->service.execute(()->{
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
 }),1, TimeUnit.SECONDS);
```

### 3.2、DelayedWorkQueue

```java
static class DelayedWorkQueue extends AbstractQueue<Runnable>
        implements BlockingQueue<Runnable> {
	private static final int INITIAL_CAPACITY = 16;
	private RunnableScheduledFuture<?>[] queue =
    new RunnableScheduledFuture<?>[INITIAL_CAPACITY];
	private final ReentrantLock lock = new ReentrantLock();
	private int size = 0;
  
}
// 基于数组实现的小根堆任务队列
```

```java
/**
 * Sifts element added at bottom up to its heap-ordered spot.
 * Call only when holding lock.
 * 调整堆结构的插入方法
 */
private void siftUp(int k, RunnableScheduledFuture<?> key) {
    while (k > 0) {
        int parent = (k - 1) >>> 1;
        RunnableScheduledFuture<?> e = queue[parent];
        if (key.compareTo(e) >= 0)
            break;
        queue[k] = e;
        setIndex(e, k);
        k = parent;
    }
    queue[k] = key;
    setIndex(key, k);
}

/**
 * Sifts element added at top down to its heap-ordered spot.
 * Call only when holding lock.
 * 调整堆结构的下沉方法
 */
private void siftDown(int k, RunnableScheduledFuture<?> key) {
    int half = size >>> 1;
    while (k < half) {
        int child = (k << 1) + 1;
        RunnableScheduledFuture<?> c = queue[child];
        int right = child + 1;
        if (right < size && c.compareTo(queue[right]) > 0)
            c = queue[child = right];
        if (key.compareTo(c) <= 0)
            break;
        queue[k] = c;
        setIndex(c, k);
        k = child;
    }
    queue[k] = key;
    setIndex(key, k);
}
```

```java
// 添加任务
public boolean offer(Runnable x) {
    if (x == null)
        throw new NullPointerException();
    RunnableScheduledFuture<?> e = (RunnableScheduledFuture<?>)x;
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        int i = size;
        if (i >= queue.length)
            grow();
        size = i + 1;
        if (i == 0) {
            queue[0] = e;
            setIndex(e, 0);
        } else {
            siftUp(i, e);
        }
        if (queue[0] == e) { // 队列中0位置的元素是当前任务
            leader = null;	// 重置leader
            available.signal();	// 唤醒需要执行的任务线程
        }
    } finally {
        lock.unlock();
    }
    return true;
}
```

```java
// 取出任务
// 这个方法是ThreadPoolExecutor中调用的take方法，等待直到能够取出，然后执行
public RunnableScheduledFuture<?> take() throws InterruptedException {
            final ReentrantLock lock = this.lock;
            lock.lockInterruptibly();
            try {
                for (;;) {
                    RunnableScheduledFuture<?> first = queue[0];
                    if (first == null)
                        available.await();
                    else {
                        long delay = first.getDelay(NANOSECONDS);
                        if (delay <= 0)	// 超时了
                            return finishPoll(first);
                        first = null; // don't retain ref while waiting
                        if (leader != null)
                            available.await();	// 等到执行时间到达
                        else {
                            Thread thisThread = Thread.currentThread();
                            leader = thisThread;
                            try {
                                available.awaitNanos(delay);	//等待延迟delay时间后唤醒
                            } finally {
                                if (leader == thisThread)	
                                    leader = null;			// 判断是否当前线程，最后将复原
                            }
                        }
                    }
                }
            } finally {
                if (leader == null && queue[0] != null)	// 队列中还有任务，leader已经执行了
                    available.signal();					// 唤醒下一个线程执行队列中的任务
                lock.unlock();
            }
   }
```

### 3.3、schedule

该方法表示，任务延迟delay时间后执行。并且只执行一次

```java
public ScheduledFuture<?> schedule(Runnable command,
                                   long delay,
                                   TimeUnit unit) {
    if (command == null || unit == null)
        throw new NullPointerException();
    RunnableScheduledFuture<?> t = decorateTask(command,
        new ScheduledFutureTask<Void>(command, null,
                                      triggerTime(delay, unit)));
    delayedExecute(t);
    return t;
}
```

### 3.4、scheduleAtFixedRate

该方法表示，任务是以固定延迟频率执行，不论该任务是否已经执行完毕。

```java
public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                              long initialDelay,
                                              long period,
                                              TimeUnit unit) {
    if (command == null || unit == null)
        throw new NullPointerException();
    if (period <= 0)
        throw new IllegalArgumentException();
    ScheduledFutureTask<Void> sft =
        new ScheduledFutureTask<Void>(command,
                                      null,
                                      triggerTime(initialDelay, unit),
                                      unit.toNanos(period));
    RunnableScheduledFuture<Void> t = decorateTask(command, sft);
    sft.outerTask = t;	// 拿到任务，赋给outerTask，以便于下次执行直接拿
    delayedExecute(t);
    return t;
}
```

### 3.5、scheduleWithFixedDelay

该方法表示，任务在当前任务执行完毕之后，以delay时间执行

```java
public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                 long initialDelay,
                                                 long delay,
                                                 TimeUnit unit) {
    if (command == null || unit == null)
        throw new NullPointerException();
    if (delay <= 0)
        throw new IllegalArgumentException();
    ScheduledFutureTask<Void> sft =
        new ScheduledFutureTask<Void>(command,
                                      null,
                                      triggerTime(initialDelay, unit),
                                      unit.toNanos(-delay));
    RunnableScheduledFuture<Void> t = decorateTask(command, sft);
    sft.outerTask = t;	// 拿到任务，赋给outerTask，以便于下次执行直接拿
    delayedExecute(t);
    return t;
}
```

### 3.6、ScheduledFutureTask

该方式是周期执行任务的包装体

```java
private class ScheduledFutureTask<V>
        extends FutureTask<V> implements RunnableScheduledFuture<V> {

    /** Sequence number to break ties FIFO */
    private final long sequenceNumber;

    /** The time the task is enabled to execute in nanoTime units */
    private long time;

    /**
     * Period in nanoseconds for repeating tasks.  A positive
     * value indicates fixed-rate execution.  A negative value
     * indicates fixed-delay execution.  A value of 0 indicates a
     * non-repeating task.
     */
    private final long period;

    /** The actual task to be re-enqueued by reExecutePeriodic */
    RunnableScheduledFuture<V> outerTask = this;

    /**
     * Index into delay queue, to support faster cancellation.
     */
    int heapIndex;

    /**
     * Creates a one-shot action with given nanoTime-based trigger time.
     */
    ScheduledFutureTask(Runnable r, V result, long ns) {
        super(r, result);
        this.time = ns;
        this.period = 0;
        this.sequenceNumber = sequencer.getAndIncrement();
    }

    /**
     * Creates a periodic action with given nano time and period.
     */
    ScheduledFutureTask(Runnable r, V result, long ns, long period) {
        super(r, result);
        this.time = ns;
        this.period = period;
        this.sequenceNumber = sequencer.getAndIncrement();
    }

    /**
     * Creates a one-shot action with given nanoTime-based trigger time.
     */
    ScheduledFutureTask(Callable<V> callable, long ns) {
        super(callable);
        this.time = ns;
        this.period = 0;
        this.sequenceNumber = sequencer.getAndIncrement();
    }
	// 拿到延迟任务时间
    public long getDelay(TimeUnit unit) {
        return unit.convert(time - now(), NANOSECONDS);
    }

    // 用于放入小根堆结构的比较器
    public int compareTo(Delayed other) {
        if (other == this) // compare zero if same object
            return 0;
        if (other instanceof ScheduledFutureTask) {
            ScheduledFutureTask<?> x = (ScheduledFutureTask<?>)other;
            long diff = time - x.time;
            if (diff < 0)
                return -1;
            else if (diff > 0)
                return 1;
            else if (sequenceNumber < x.sequenceNumber)
                return -1;
            else
                return 1;
        }
        long diff = getDelay(NANOSECONDS) - other.getDelay(NANOSECONDS);
        return (diff < 0) ? -1 : (diff > 0) ? 1 : 0;
    }

    /**
     * Returns {@code true} if this is a periodic (not a one-shot) action.
     *
     * @return {@code true} if periodic
     */
    public boolean isPeriodic() {
        return period != 0;
    }

    /**
     * Sets the next time to run for a periodic task.
     */
    private void setNextRunTime() {	// 设置下次运行时间
        long p = period;
        if (p > 0)	// 是周期时间
            time += p;
        else
            time = triggerTime(-p);
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancelled = super.cancel(mayInterruptIfRunning);
        if (cancelled && removeOnCancel && heapIndex >= 0)
            remove(this);
        return cancelled;
    }

    /**
     * Overrides FutureTask version so as to reset/requeue if periodic.
     */
    public void run() {
        boolean periodic = isPeriodic();		// 是否是周期
        if (!canRunInCurrentRunState(periodic))	// 检查线程池状态是否被shutdown了
            cancel(false);						// 取消任务
        else if (!periodic)						// 不是周期
            ScheduledFutureTask.super.run();	// 那么直接执行一次
        else if (ScheduledFutureTask.super.runAndReset()) {	// futureTask任务运行成功
            setNextRunTime();								// 设置下次周期执行时间
            reExecutePeriodic(outerTask);		// 重新运行outerTask，也就是我们传进来的任务
        }
    }
}
```

### 3.7、delayedExecute

该方法是实际调用RunnableScheduledFuture的run方法的入口

```java
private void delayedExecute(RunnableScheduledFuture<?> task) {
    if (isShutdown())	// 检查状态，拒绝任务
        reject(task);
    else {
        super.getQueue().add(task);		// 添加任务到当前的延迟队列
        if (isShutdown() &&
            !canRunInCurrentRunState(task.isPeriodic()) &&
            remove(task))	// 状态不对，移除任务从队列
            task.cancel(false);	// 取消任务
        else
            ensurePrestart();	// 从队列中拿任务，运行
    }
}
```

### 3.8、ensurePrestart

```java
void ensurePrestart() {
    int wc = workerCountOf(ctl.get());	// 计算任务运行的数量
    if (wc < corePoolSize)				// 小于核心线程数，那么从队列获取运行
        addWorker(null, true);
    else if (wc == 0)				// 核心线程数为0，且没有运行的任务
        addWorker(null, false);		// 使用非核心线程数
}
```

### 3.9、delayedExecute

```java
// 重新之心周期任务，跟delayedExecute方法类似
void reExecutePeriodic(RunnableScheduledFuture<?> task) {
    if (canRunInCurrentRunState(true)) {
        super.getQueue().add(task);
        if (!canRunInCurrentRunState(true) && remove(task))
            task.cancel(false);
        else
            ensurePrestart();
    }
}
```

## 4、ForkJoinPool

### 4.1、submit

```java
// 将任务封装成FJT，因为FJT只执行自己的任务
public ForkJoinTask<?> submit(Runnable task) {
    if (task == null)
        throw new NullPointerException();
    ForkJoinTask<?> job;
    if (task instanceof ForkJoinTask<?>) // avoid re-wrap
        job = (ForkJoinTask<?>) task;
    else
        job = new ForkJoinTask.AdaptedRunnableAction(task);
    externalPush(job);	// 核心方法
    return job;
}
```

### 4.2、externalPush

```java
final void externalPush(ForkJoinTask<?> task) {
    WorkQueue[] ws; WorkQueue q; int m;
    int r = ThreadLocalRandom.getProbe();	// 取TLR的随机数种子，线程安全的取
    int rs = runState;						// 当前线程FJP的运行状态
    // 第一次进来，workQueue为空
    if ((ws = workQueues) != null && (m = (ws.length - 1)) >= 0 &&
        (q = ws[m & r & SQMASK]) != null && r != 0 && rs > 0 &&
        U.compareAndSwapInt(q, QLOCK, 0, 1)) {
        ForkJoinTask<?>[] a; int am, n, s;
        if ((a = q.array) != null &&
            (am = a.length - 1) > (n = (s = q.top) - q.base)) {
            int j = ((am & s) << ASHIFT) + ABASE;
            U.putOrderedObject(a, j, task);
            U.putOrderedInt(q, QTOP, s + 1);
            U.putIntVolatile(q, QLOCK, 0);
            if (n <= 1)
                signalWork(ws, q);
            return;
        }
        U.compareAndSwapInt(q, QLOCK, 1, 0);
    }
    // 真正执行提交任务的方法
    externalSubmit(task); 
}
```

### 4.3、externalSubmit

总结：

1. 初始化workQueues队列
2. 负载均衡，获取队列中的任务，执行
3. 初始化完成，切负载均衡到的队列位置没有任务，且线程池rs持有锁，说明有任务在执行，那么此时设置move为true，修改r随机值，让其负载均衡到有任务的位置

```java
private void externalSubmit(ForkJoinTask<?> task) {
    int r;                                    // initialize caller's probe
    if ((r = ThreadLocalRandom.getProbe()) == 0) {	// 取随机数
        ThreadLocalRandom.localInit();
        r = ThreadLocalRandom.getProbe();
    }
    for (;;) {
        WorkQueue[] ws; WorkQueue q; int rs, m, k;
        boolean move = false;
        if ((rs = runState) < 0) {	// 判断线程池是否已经关闭
            tryTerminate(false, false);     // help terminate
            throw new RejectedExecutionException();
        }
        // STARTED状态没有赋值，需要初始化
        else if ((rs & STARTED) == 0 ||     // initialize
                 ((ws = workQueues) == null || (m = ws.length - 1) < 0)) {	// workQueue未初始化
            // 注意：如果该分支不执行，但是由于||运算符的存在，这里ws和m变量已初始化
            int ns = 0;
            rs = lockRunState();
            try {
                if ((rs & STARTED) == 0) { // 再次判断了一下STARTED的状态位，为何，因为多线程操作的，可能线程初始化了，所以没有必要再进行CAS
               
                    U.compareAndSwapObject(this, STEALCOUNTER, null,
                                           new AtomicLong());
                    // create workQueues array with size a power of two
                    // p是并行度，工作线程的数量
                    int p = config & SMASK; // ensure at least 4 slots
                    int n = (p > 1) ? p - 1 : 1; // n最小是1，那么n运行下面代码之后是4
                    // 就是传入的数，取它最近的2的倍数
                    // 将当前数的右边的二进制位，全部赋予1
                    n |= n >>> 1; n |= n >>> 2;  n |= n >>> 4;
                    n |= n >>> 8; n |= n >>> 16; n = (n + 1) << 1;
                    // 最后再加个1，那么就是它最近的2的倍数
                    workQueues = new WorkQueue[n];
                    ns = STARTED;	// 设置状态，初始化成功，此时状态为STARTED
                }
            } finally {
                unlockRunState(rs, (rs & ~RSLOCK) | ns); // 释放锁
            }
        }
        // 上一个分支执行完毕，第二次for循环会进入到这里
        // SQMASK = 111 1110 = 126，所以由SQMASK前面的1来限定长度，末尾的0来表明，外部提交队列一定在偶数位
        else if ((q = ws[k = r & m & SQMASK]) != null) {// 线程初始化，之后ws不为空
            // 两个队列：外部队列，内部队列（任务窃取队列），那么这时，需要找到我这个任务需要放到那个外部提价队列里面。通过上面获取的随机种子r
            // 由于当前提交的队列是外部队列，那么一定会有多线程操作，那么为了保证并发安全，那么这里需要上锁，也即对当前提交队列进行锁定
            if (q.qlock == 0 && U.compareAndSwapInt(q, QLOCK, 0, 1)) {
                ForkJoinTask<?>[] a = q.array;	// 取提交队列的保存任务的数组arry
                int s = q.top;	// 从 top往下放，类似于压栈过程，而栈顶指针就叫sp（stack pointer）
                boolean submitted = false; // initial submission or resizing
                try {                      // locked version of push
                    if ((a != null 	// 任务数组已经初始化
                         && a.length > s + 1 - q.base) ||	// 继续判断数组是否已经满了，+1是因为此时要放入一个任务，看看容量是不是够的
                        (a = q.growArray()) != null) {	// 那么就初始化数组或扩容
                        // (a.length - 1) & s) << ASHIFT) 算出偏移量+ABASE = 绝对地址 
                        int j = (((a.length - 1) & s) << ASHIFT) + ABASE; // j就是s，也就是栈顶的绝对地址
                        U.putOrderedObject(a, j, task);	// 先放任务
                        U.putOrderedInt(q, QTOP, s + 1);	// 再对栈顶指针+1，s位置永远指向的是下一个空的可以放入任务的位置
                        // QTOP
                        submitted = true;
                    }
                } finally {
                    U.compareAndSwapInt(q, QLOCK, 1, 0); // QLOCK是volatile修饰，由于volatile的特性，这个操作CAS出去，那么QLOCK线程可见，必然上面的task和qtop可见，且有序
                }
             
                if (submitted) {
                    // 此时任务添加成功，但是没有工作线程，那么这时通过signalWork，创建工作线程并执行
                    signalWork(ws, q);
                    return;
                }
            }
            // 由于当前线程无法获取初始计算的提交队列的锁，那么这时发生了锁竞争，那么设置move标记位，让线程在一些词循环的时候，重新计算随机数，让它寻找另外的队列。
            move = true;                   // move on failure
        }
        else if (((rs = runState) & RSLOCK) == 0) { // create new queue
            // 如果找到的wq没有被创建，那么创建他，但是，这里的RSLOCK的判断，在于当没有别的线程持有RSLOCK的时候，才会进入，由于RSLOCK是管的runState，可能有别的的线程把状态改了，根本不需要再继续work了
            q = new WorkQueue(this, null);	// 创建外部提交队列，由于ForkJoinWorkerThread为null，所以为外部提交队列
            q.hint = r;	// 保存r，通过r找到外部提交队列，查找WQS的索引小标
            q.config = k | SHARED_QUEUE; // SHARED_QUEUE代表当前队列是共享队列，也即外部队列，而k为当前wa处于wqs中索引下标
            q.scanState = INACTIVE;	// 由于当前wa并没有进行扫描任务，所以扫描状态设置为无效位INACTIVE
            rs = lockRunState(); // 对wqs上锁操作：就是将上面的队列放入到wqs的偶数位中
            if (rs > 0 &&  (ws = workQueues) != null &&
                k < ws.length && ws[k] == null) // 由于可能两个线程同时进来，那么只有一个线程放创建的队列，但是这里需要注意的是：可能会有多个线程创建了WorkQueue，单是只有一个能放入成功
                ws[k] = q;                 // 将wq放入到全局的wqs中
            unlockRunState(rs, rs & ~RSLOCK);
        }
        else	// 初始化成功，当前自己的队列任务为空，且线程已经持有了锁，那么就move=ture，修改r，让其重新负载均衡到其他ws队列去尝试执行
            move = true;   // 发生竞争时，允许当前线程选取其他的wq来重试
        if (move)
            r = ThreadLocalRandom.advanceProbe(r);
    }
}
```

问题：为什么要使用putOrderedObject和putOrderedInt更新队列和s位置？

> ```java
> // 由于CPU中数据写入到cache中，开发人员觉得太慢，所以将数据先暂时存放到CPU内部的一个buffer中，这个buffer叫做store buffer，然后批量写入cache，增加性能。
> U.putOrderedObject(a, j, task);
> U.putOrderedInt(q, QTOP, s + 1);
> // 保证s被其他线程看到的时候，taks也一定能够被看到，也即task和QTOP不会发生写写storestore重排序
> ```

#### 4.3.1、store buffer原理

![](../doc-all/images/muti-thread/cpu_storebuffer.png)

#### 4.3.2、volatile 关键字

> volatile 的可见性并不是它的主要作用，为什么，因为store buffer非常小，最终由于CPU不断执行指令，会将最终的值刷入缓存，这时MESI生效，别的CPU能看到。
>
> Java中的 volatile最大作用就是**禁止JIT优化和指令屏障**。

#### 4.3.3、取余数优化

在一个数组中，用一个无线增长的数组arr，使用s来代表他当前的容量，那么使用`s%arr.length`可以在不管s当前的数是多大的情况下知道此时s所映射到数组位置下的idx；

但是计算机对于取余数运算的开销是比较大的，相比于位运算来说，

此时我们可以当数组的长度是固定2的幂次方的情况下，使用 `s & (arr.length - 1)` 即可求出当前s所映射到数组上的索引小标idx；

> ```java
> int idx = s % arr.length;
> int idx = s & (arr.length - 1);
> // 前提，arr长度必须是2的幂次方
> // 原理： arr长度为8, 0111 & s = 0 ~ 7 之间
> ```

注意：很多源码中多数数组的长度基本上就是**2的幂次方**，为什么这样做？因为**计算机使用的二进制，方便计算**

### 4.4、signalWork

```java
final void signalWork(WorkQueue[] ws, WorkQueue q) {
    long c; int sp, i; WorkQueue v; Thread p;
    // ctl 64位：第一个高16，16，16，16
    // ctl是在构造器的时候初始化的，它的第一个高16位代表了ACTIVE counts，第二个高16代表了总共的counts数
    while ((c = ctl) < 0L) {   // 符号位没有溢出，有效的最高数是被并行度控制，当活跃线程数高于改并行度，符号位溢出，ctl>0,所以只有当ctl小于0才是有效
        if ((sp = (int)c) == 0) {                  // ctl的低32位，代表了INACTIVE数，若此时sp=0，代表了没有空闲线程
                if ((c & ADD_WORKER) != 0L)// 检查ctl的第48位，也就是总共线程数状态，若不是0，那么FJP中的工作线程代表了没有达到的最大线程数
                tryAddWorker(c);
            break;
        }
        // 此时进来说明，存在空闲线程
        if (ws == null) // 此时FJP的状态没有启动，那么直接返回,TERMINATED
            break;
        //  int SMASK = 0xffff;  
        if (ws.length <= (i = sp & SMASK))  // 取sp的低16位，只要ws长度小于等于，代表了TERMINATED状态
            break;
        if ((v = ws[i]) == null) // ctl低32位的低16位存放了INACTIVE的索引小标 -> terminating
            break;
        // int SS_SEQ = 1 << 16 说明sp的高16位存放了版本信息
        // int INACTIVE     = 1 << 31; 
        // ~INACTIVE=0111 1111 1111 1111 1111111111111111：vs保留了非符号位的所有信息
        int vs = (sp + SS_SEQ) & ~INACTIVE;        // next scanState
        int d = sp - v.scanState;                  // screen CAS
        // 把获取到的INACTIVE的线程，也即空闲线程唤醒，那么唤醒后，需要给AC+1即(c + AC_UNIT)
        // (UC_MASK & (c + AC_UNIT)) 保留了的高32数
        //  (SP_MASK & v.stackPred) 保留了低32的数  v.stackPred代表了下一个出栈操作，让低32位的低16位更新位唤醒线程的一下线程
        long nc = (UC_MASK & (c + AC_UNIT)) | (SP_MASK & v.stackPred);
      	// 此时的nc就是计算好的下一个ctl，next ctl
        // d == 0代表了没有空闲线程，所以没有必要cas替换
        if (d == 0 && U.compareAndSwapLong(this, CTL, c, nc)) { // cas替换ctl
            v.scanState = vs;                      // 记录版本信息scanState，此时为正数
            if ((p = v.parker) != null)	// 唤醒工作线程
                U.unpark(p);	// unsafe唤醒
            break;
        }
        if (q != null && q.base == q.top)          // 队列没有数据，直接退出
            break;
    }
}
```

### 4.5、tryAddWorker

```java
private void tryAddWorker(long c) {
    boolean add = false;
    do {
        long nc = ((AC_MASK & (c + AC_UNIT)) |	//活跃线程数加1，此时只保留了高16位的信息
                   (TC_MASK & (c + TC_UNIT)));// 总线程数+1，此时只保留了高32位的低16位信息
        // nc此时不需要管低32位，因此调用此方法的时候，没有空闲线程
        if (ctl == c) {	// ctl没有被其他线程改变
            int rs, stop;                 // check if terminating
            // 检查FJP的状态是否为STOP
            if ((stop = (rs = lockRunState()) & STOP) == 0)
                add = U.compareAndSwapLong(this, CTL, c, nc);	// 更新ctl的值，CAS替换
            unlockRunState(rs, rs & ~RSLOCK);	// 解锁
            if (stop != 0)
                break;
            if (add) {	// 添加成功
                createWorker();	// 创建work
                break;
            }
        }
    } while (((c = ctl) & ADD_WORKER) != 0L 	// 没有达到最大线程数
             && (int)c == 0);	// 低32位为0，如果有空闲线程，不需要添加，应该给到空闲线程去执行
}
```

### 4.6、createWorker

```java

private boolean createWorker() {
    ForkJoinWorkerThreadFactory fac = factory;
    Throwable ex = null;
    ForkJoinWorkerThread wt = null;
    try {
        if (fac != null && (wt = fac.newThread(this)) != null) {	// 从线程工程中，创建线程
            wt.start(); // 并启动线程
            return true;
        }
    } catch (Throwable rex) {
        ex = rex;
    }
    deregisterWorker(wt, ex);	// 如果出现异常，将ctl前面加的1回滚
    return false;
}

// 默认工厂，用来创建线程
 static final class DefaultForkJoinWorkerThreadFactory
        implements ForkJoinWorkerThreadFactory {
        public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return new ForkJoinWorkerThread(pool);
        }
    }

   protected ForkJoinWorkerThread(ForkJoinPool pool) {
        // Use a placeholder until a useful name can be set in registerWorker
        super("aForkJoinWorkerThread");	// 默认的名称
        this.pool = pool;	// 保存对外的pool引用
        this.workQueue = pool.registerWorker(this);	// 创建队列并注册到FJP
    }
```

### 4.7、registerWorker

创建工作队列WorkQueue，将自己的注册到FJP全局workQueues中，也就是保存到FJP的奇数位中

```java
final WorkQueue registerWorker(ForkJoinWorkerThread wt) {
    UncaughtExceptionHandler handler;
    wt.setDaemon(true);                           // 默认位守护线程
    if ((handler = ueh) != null)	// 设置线程的异常处理器
        wt.setUncaughtExceptionHandler(handler);
    WorkQueue w = new WorkQueue(this, wt);	// 创建工作线程，注意：创建外部提交队列时： WorkQueue w = new WorkQueue(this, null);
    int i = 0;                  // 创建的工作队列所保存在wqs的索引下标 // assign a pool index
    int mode = config & MODE_MASK;	// 取工作模式：FIFO、LIFO
    int rs = lockRunState();
    try {
        WorkQueue[] ws; int n;                    // skip if no array
        if ((ws = workQueues) != null && (n = ws.length) > 0) {	// 日常判空
            // int SEED_INCREMENT = 0x9e3779b9; 取大的质数，减少hash碰撞
            int s = indexSeed += SEED_INCREMENT;  // indexSeed默认位0
            int m = n - 1;	// wps长度-1，用于取模运算
            i = ((s << 1) | 1) & m;   // 找存放w的小标
            if (ws[i] != null) {                  // 此时发生了碰撞
                int probes = 0;                   // step by approx half n
                // int EVENMASK     = 0xfffe; 
                int step = (n <= 4) ? 2 : ((n >>> 1) & EVENMASK) + 2; //发生碰撞二次寻址
                // step保证偶数，+1等于奇数
                while (ws[i = (i + step) & m] != null) {
                    if (++probes >= n) {	// 寻址达到了极限，那么扩容
                        workQueues = ws = Arrays.copyOf(ws, n <<= 1); // 扩容容量为2倍
                        m = n - 1;
                        probes = 0;
                    }
                }
            }
            w.hint = s;                  // s作为随机数保存在wq的hint中
            w.config = i | mode;		// 保存索引下标和模式
             // publication fence， scanState为volatile，
           w.scanState = i; // 此时对它进行写操作，storestore写成功，上面的变量一定可见，且不会和下面的ws[i]赋值发生重排序，注意这里的scanState变成了odd，也即奇数，所以要开始扫描获取任务并执行       
            ws[i] = w;
        }
    } finally {
        unlockRunState(rs, rs & ~RSLOCK);	// 解锁
    }
    wt.setName(workerNamePrefix.concat(Integer.toString(i >>> 1)));
    return w;
}
```

### 4.8、FJP核心run方法

createWorker中调用线程的start方法，此时调用的是ForkJoinWorkerThread的run方法

```java
public void run() {
    if (workQueue.array == null) { // only run once，array用来存放FJWT任务，确保只运行一次
        Throwable exception = null;
        try {
            onStart();		// run之前的钩子函数
            pool.runWorker(workQueue);
        } catch (Throwable ex) {
            exception = ex;
        } finally {
            try {
                onTermination(exception);	// run之后的回调函数
            } catch (Throwable ex) {
                if (exception == null)	// 保存最先发生的异常
                    exception = ex;
            } finally {
                pool.deregisterWorker(this, exception);	// 线程退出后，进行状态还远
            }
        }
    }
}
```

### 4.9、runWorker

```java
/**
 * Top-level runloop for workers, called by ForkJoinWorkerThread.run.
 */
final void runWorker(WorkQueue w) {
    w.growArray();                   // allocate queue
    // 随机数r
    int seed = w.hint;               // initially holds randomization hint
    int r = (seed == 0) ? 1 : seed;  // avoid 0 for xorShift
    for (ForkJoinTask<?> t;;) {
        if ((t = scan(w, r)) != null)	// 扫描可执行任务
            w.runTask(t);				// 拿到任务之后，开始执行
        else if (!awaitWork(w, r))			// 如果没有任务，那么awaitWork等待任务执行
            break;
        r ^= r << 13; r ^= r >>> 17; r ^= r << 5; // 异或，基于上一个随机数r，计算下一个伪随机数
    }
}
```

### 4.10、growArray

```java
final ForkJoinTask<?>[] growArray() {
    ForkJoinTask<?>[] oldA = array;
    // oldA存在，那么进行扩容，否则size为初始化大小INITIAL_QUEUE_CAPACITY = 1<<13
    int size = oldA != null ? oldA.length << 1 : INITIAL_QUEUE_CAPACITY = 1;
    // 如果扩容之后，超过最大容量MAXIMUM_QUEUE_CAPACITY=1<<26,也即64M，抛出异常
    if (size > MAXIMUM_QUEUE_CAPACITY)
        throw new RejectedExecutionException("Queue capacity exceeded");
    int oldMask, t, b;
    // 创建新的array数组
    ForkJoinTask<?>[] a = array = new ForkJoinTask<?>[size];
    // 需要将array中的任务放入到新的数组中
    // 为什么不用更快速的System.arrayCopy?
    // 如何保证数组中的元素可见性？使用U.getObjectVolatile(数组首地址，偏移量)即可
    if (oldA != null && (oldMask = oldA.length - 1) >= 0 &&
        (t = top) - (b = base) > 0) {
        int mask = size - 1;
        do { // emulate poll from old array, push to new array
            ForkJoinTask<?> x;
            int oldj = ((b & oldMask) << ASHIFT) + ABASE;	// 旧的oldj偏移
            int j    = ((b &    mask) << ASHIFT) + ABASE;	// 新的oldj偏移
            x = (ForkJoinTask<?>)U.getObjectVolatile(oldA, oldj);	// 关键，注意语义1
            if (x != null &&
                U.compareAndSwapObject(oldA, oldj, x, null))	// 使用CAS，防止其他线程来的时候也进行数据的替换。因此 由于队列是工作窃取队列，可能有别的线程持有old数组引用，正通过base引用窃取尾部任务
                U.putObjectVolatile(a, j, x);	// volatile语义2
        } while (++b != t); // 循环，直到转移成功
    }
    return a;
}
```

### 4.11、核心方法之scan

scan方法中for循环执行流程如下：

有两个分支：

1、(q = ws[k]) != null，任务队列wq 不为null

1. 计算下wq的队列中存在（ q.base - q.top）任务，并且q.array不为空，说明有还未被分配的任务，
2. 计算a的索引下标
3. 通过getObjectVolatile获取到ForkJoinTask任务队列t，并且此时q.base == b，说明base任务没有被其他线程拿去
   1. 此时t不空，scanState是在创建FJWT构造register的时候赋值的，ss=scanState = i ，是数组奇数位的索引，所以第一次进来ss必定大于0，
      1. 此时使用CAS将i位置的任务t置空，操作成功
      2. 修改q.base的指针位置+1，
         1. 如果 n<-1，说明当前队列任务比较多，那么通知其他线程来取任务signalWork
      3. 此时返回当前任务t
   2. 如果ss小于0，并且oldSum == 0 && w.scanState < 0，说明当前状态是INACTIVE，此时需要释放当前线程，通知其他线程来进行扫描tryRelease
4. 此时任务t存在且q.base已经被拿到了，或者任务t不存在，且ss<0，
   1. 修改ss的状态，从全局拿取ss = w.scanState;
5. 此时任务t存在且q.base已经被拿到了，或者任务t不存在，只可能是因为出现了多线程竞争，所以为了减少竞争，那么重新计算随机数，转移获取任务的wq，复位origin，oldSum，checkSum，方便重新计算
6. 如果执行到这里，那么此时修改checkSum += b;计算检查的次数

2、(k = (k + 1) & m) == origin，说明此时k已经循环了一周了，那么需要将其变成稳定状态：扫描状态不变，且 没有线程操作队列

1. 如果ss小于0，判断ss状态是否改变，并且判断旧的oldSum和checkSum标记，同时更新oldSum。如果为true
   1. 判断ss是否小于0，如果小于0，已经是非激活状态，直接break，或者 w.qlock < 0 此时线程已经被terminated了，直接break
   2. 修改状态为INACTIVE： int ns = ss | INACTIVE;
   3. 将ctl的线程活跃数-1，并且将状态都放置到64位中，long nc = ((SP_MASK & ns) |(UC_MASK & ((c = ctl) - AC_UNIT)));
   4. 拿到ctl中的栈地址stackPred赋值给w.stackPred
   5. 使用U.putInt(w, QSCANSTATE, ns)设置当前工作队列w的状态ns，使用unsafe操作，优化：由于这里是volatile，进行直接赋值将会导致storestore和StoreLoad屏障，所以用unsafe类来普通变量赋值，减少性能损耗，而后面的CAS操作好，自然能能够保证这里的scanState语义。
   6. 使用CAS更新CTL的状态nc ，
      1. 如果此时CAS操作成功，ctl替换成功，必然scanState写成功，那么局部变量直接更新位最新值，而不用在去读scanState变量
      2. 否则，那么退回到原来的状态，因为ctl没有改变，也即active counts没有减1成功，而scanState是用volatile修饰的，保证了其他线程可以看到当前修改的ss
2. 此时修改checkSum的状态为0，等待下次循环到来，使其到达稳定状态。

总结：

```java
// 在FJWT
private ForkJoinTask<?> scan(WorkQueue w, int r) {
    WorkQueue[] ws; int m;
    if ((ws = workQueues) != null && (m = ws.length - 1) > 0 && w != null) {
        int ss = w.scanState;                     // 保存之前设置的扫描状态，是创建FJWT构造register的时候赋值i
        // 扫描获取任务，origin初始为伪随机数取模的下标，k初始为origin
        // 由于在扫描过程中，可能有别的线程添加获取等操作，那么怎么样让当前FJWT返回呢，不可能一直在扫描，实在没有任务了，那么必须退出，避免造成性能损耗，那么问题就变为：如何发现当前没有可执行的任务呢？
        // 采用oldSum和checkSum来判断整个wqs是否处于稳定状态，也即没有别的线程在往里面添加任务了，而且经历一定周期之后
        for (int origin = r & m, k = origin, oldSum = 0, checkSum = 0;;) {
            WorkQueue q; ForkJoinTask<?>[] a; ForkJoinTask<?> t;
            int b, n; long c;
            // 在wqs中，找打一个不为空的队列
            if ((q = ws[k]) != null) {
                if ((n = (b = q.base) - q.top) < 0 &&	// array队列不为空，有可执行任务。初始时base=top
                    (a = q.array) != null) {      // non-empty
                    long i = (((a.length - 1) & b) << ASHIFT) + ABASE;	// 取array索引下标base的偏移地址
                    if ((t = ((ForkJoinTask<?>)
                              U.getObjectVolatile(a, i))) != null &&	//任务存在
                        q.base == b) {		// base引用没有改变，也即任务没有被取走
                        if (ss >= 0) {		// 如果扫描状态正常
                            // CAS将任务t置空
                            if (U.compareAndSwapObject(a, i, t, null)) {
                                q.base = b + 1;			// 增加base值
                                if (n < -1)       // 队列大于一个任务 ，那么需要通知其他任务也来取任务xx
                                    signalWork(ws, q);
                                return t;	// 返获取的任务
                            }
                        }
                        else if (oldSum == 0 &&   // oldSum未改变之前，才能判断w的扫描状态，
                                 w.scanState < 0)	// 如果扫描状态小于0，代表INACTIVE，此时需要尝试唤醒空闲线程进行扫描工作
                            // ws[m & (int)c]：取队列栈顶wq，也即当前wq
                            tryRelease(c = ctl, ws[m & (int)c], AC_UNIT);
                    }
                    // 任务不存在，那么判断此时扫描状态处理INACTIVE的话，那么需要从新获取扫描状态，可能别的线程已经将其置为扫描状态
                    if (ss < 0)                   // refresh
                        ss = w.scanState;
                    // 执行到这里，那么只可能是因为线程竞争导致的，所以为了减少竞争，那么重新计算随机数，转移获取任务的wq，复位origin，oldSum，checkSum，方便重新计算
                    r ^= r << 1; r ^= r >>> 3; r ^= r << 10;
                    origin = k = r & m;           // move and rescan
                    oldSum = checkSum = 0;
                    continue;
                }
                checkSum += b;	// 通过base指针数据来校验判断
            }
            // (k = (k + 1) & m) == origin，表示k已经扫描了一遍队列一个周期
            // 直到稳定态。如何保证
            // 使用这种代码 oldSum == (oldSum = checkSum)。直到下次循环进来一样之后就稳定了
            if ((k = (k + 1) & m) == origin) {    // continue until stable
                if ((ss >= 0 || (ss == (ss = w.scanState))) && // ss小于0，判断ss状态是否改变
                    oldSum == (oldSum = checkSum)) {  // 旧的oldSum和checkSum标记，同时更新oldSum
                    // stable 稳定态：扫描状态不变，且 没有线程操作队列 
                    if (ss < 0 || w.qlock < 0)    // already inactive
                        // 此时状态已经稳定，直接退出循环
                        break;
                    // 将扫描状态设置为INACTIVE
                    int ns = ss | INACTIVE;       // try to inactivate
                    long nc = ((SP_MASK & ns) |	 // 将低32数据设置ns状态
                               (UC_MASK & ((c = ctl) - AC_UNIT))); // 对活跃状态数减少1
                    // 之前栈空闲线程的索引下标+版本号
                    // ctl的低32位就是空闲线程的栈顶。workQueue的stackPred就是栈中的空闲线程
                    w.stackPred = (int)c;         // hold prev stack top
                    // 由于这里是volatile，进行直接赋值将会导致storestore和StoreLoad屏障，所以用unsafe类来普通变量赋值，减少性能损耗，而后面的CAS操作好，自然能能够保证这里的scanState语义。
                    U.putInt(w, QSCANSTATE, ns);
                    if (U.compareAndSwapLong(this, CTL, c, nc))
                        ss = ns;	// 因为ctl替换成功，必然scanState写成功，那么局部变量直接更新位最新值，而不用在去读scanState变量
                    else
                        w.scanState = ss; // 如果CAS失败了，那么退回到原来的状态，因为ctl没有改变，也即active counts没有减1成功，而scanState是用volatile修饰的，保证了其他线程可以看到当前修改的ss
                }
                checkSum = 0;
            }
        }
    }
    return null;
}
```

### 4.12、tryRelease

```java
private boolean tryRelease(long c, WorkQueue v, long inc) {
    int sp = (int)c, vs = (sp + SS_SEQ) & ~INACTIVE; Thread p;
    if (v != null && v.scanState == sp) {          // v is at top of stack
        long nc = (UC_MASK & (c + inc)) | (SP_MASK & v.stackPred);
        if (U.compareAndSwapLong(this, CTL, c, nc)) {
            v.scanState = vs;
            if ((p = v.parker) != null)
                U.unpark(p);
            return true;
        }
    }
    return false;
}
```

### 4.13、runTask

```java
final void runTask(ForkJoinTask<?> task) {
    if (task != null) {
        scanState &= ~SCANNING; // mark as busy，标记当前wq的工作线程处于执行获取到的任务状态，即标记为负数
        (currentSteal = task).doExec();	// task是当前线程从队列里获取的，也即task设置为currentSteal，这里就运行了
        // 执行完毕之后释放currentSteal应用。为什么这么写？storeBuffer -》 StoreLoad -> volatile语义，写变量时，storeStore，StoreLoad。那么这里为了保证写入的顺序->putOrderedObject避免了StoreLoad屏障对性能的损耗
        U.putOrderedObject(this, QCURRENTSTEAL, null); // release for GC
        // 执行本地任务。
        // 问题：谁能往当前线程的工作队列里放任务？当前线程在执行FJT时往自己队列里放了任务，也只有当前线程才能往array任务数组里放任务。
        execLocalTasks();
        ForkJoinWorkerThread thread = owner;
        if (++nsteals < 0)      // nsteals代表了当前线程的数量，如果小于0代表符号位溢出，
            transferStealCount(pool);
        scanState |= SCANNING;	// 任务执行完毕，回复当前线程为扫描状态
        if (thread != null)
            thread.afterTopLevelExec();	// 任务执行完毕之后的钩子函数
    }
}

// 当前32位计数值达到饱和，那么将其假如到FJP的64位全局变量计数器中，并将nsteals清0。
 final void transferStealCount(ForkJoinPool p) {
            AtomicLong sc;
            if (p != null && (sc = p.stealCounter) != null) {
                int s = nsteals;
                nsteals = 0;            // if negative, correct for overflow
                sc.getAndAdd((long)(s < 0 ? Integer.MAX_VALUE : s));
            }
        }
```

### 4.14、awaitWork

```java
private boolean awaitWork(WorkQueue w, int r) {
    if (w == null || w.qlock < 0)                 // 线程池正在 terminating
        return false;
    // SPINS 默认自旋次数0
   // 取当前线程工作压入空闲栈中的前一个工作版本+下标（低32位）
    for (int pred = w.stackPred, spins = SPINS, ss;;) { // 找打前面一个pred的位置
        if ((ss = w.scanState) >= 0)
            break;
        else if (spins > 0) {	// 没有达到自旋次数阈值
            r ^= r << 6; r ^= r >>> 21; r ^= r << 7;
            if (r >= 0 && --spins == 0) {         // randomize spins
                WorkQueue v; WorkQueue[] ws; int s, j; AtomicLong sc;
                if (pred != 0 && (ws = workQueues) != null &&
                    (j = pred & SMASK) < ws.length &&
                    (v = ws[j]) != null &&        // see if pred parking
                    (v.parker == null || v.scanState >= 0))
                    spins = SPINS;                // continue spinning
            }
        }
        else if (w.qlock < 0)                     // recheck after spins
            return false;
        else if (!Thread.interrupted()) {	// 如果当前FJWT工作线程没有发生中断，那么尝试睡眠，否则清除标记位后继续scan扫描任务，
            long c, prevctl, parkTime, deadline;
            // 构造器中：this.config = (parallelism & SMASK) | mode;
            int ac = (int)((c = ctl) >> AC_SHIFT) 	// 获取ctl的高16位值：活跃线程数
                + (config & SMASK);					// 取低16位config的值：即在构造器设置并行度
            // 如果ac < 0说明 活跃线程数 + 并行度 相加越界了，正常情况是 ac > 0
            if ((ac <= 0 && tryTerminate(false, false)) ||
                (runState & STOP) != 0)           // 线程池状态处理stop，所以停止执行
                return false;
            if (ac <= 0 && ss == (int)c) {        // is last waiter
                prevctl = (UC_MASK & (c + AC_UNIT)) | (SP_MASK & pred);
                int t = (short)(c >>> TC_SHIFT);  // shrink excess spares
                if (t > 2 && U.compareAndSwapLong(this, CTL, c, prevctl))
                    return false;                 // else use timed wait
                parkTime = IDLE_TIMEOUT * ((t >= 0) ? 1 : 1 - t);
                deadline = System.nanoTime() + parkTime - TIMEOUT_SLOP;
            }
            else
                prevctl = parkTime = deadline = 0L;
            Thread wt = Thread.currentThread();
            U.putObject(wt, PARKBLOCKER, this);   // emulate LockSupport
            w.parker = wt;
            if (w.scanState < 0 && ctl == c)      // recheck before park
                U.park(false, parkTime);
            U.putOrderedObject(w, QPARKER, null);
            U.putObject(wt, PARKBLOCKER, null);
            if (w.scanState >= 0)
                break;
            if (parkTime != 0L && ctl == c &&
                deadline - System.nanoTime() <= 0L &&
                U.compareAndSwapLong(this, CTL, c, prevctl))
                return false;                     // shrink pool
        }
    }
    return true;
}
```

### push

```java
final void push(ForkJoinTask<?> task) {
    ForkJoinTask<?>[] a; ForkJoinPool p;
    int b = base, s = top, n;
    if ((a = array) != null) {    // ignore if queue removed
        int m = a.length - 1;     // fenced write for task visibility
        U.putOrderedObject(a, ((m & s) << ASHIFT) + ABASE, task);
        U.putOrderedInt(this, QTOP, s + 1);
        if ((n = s - b) <= 1) {
            if ((p = pool) != null)
                p.signalWork(p.workQueues, this);
        }
        else if (n >= m)
            growArray();
    }
}
```

### 4.5、ForkJoinPool构造器

ForkJoinPool中的工作队列中：

> 偶数位：外部提交队列
>
> 奇数位：工作窃取队列

```java
public ForkJoinPool() {
    this(Math.min(MAX_CAP, Runtime.getRuntime().availableProcessors()),
         defaultForkJoinWorkerThreadFactory, null, false);
}
// parallelism 默认核心线程数
 public ForkJoinPool(int parallelism,
                        ForkJoinWorkerThreadFactory factory,	// 线程工厂
                        UncaughtExceptionHandler handler,		// 线程异常处理器
                        boolean asyncMode) {	// 执行模式，异步或同步，默认false
        this(checkParallelism(parallelism),
             checkFactory(factory),
             handler,
             asyncMode ? FIFO_QUEUE : LIFO_QUEUE,	// 选择从base指针取，还是top取。默认top取
             "ForkJoinPool-" + nextPoolId() + "-worker-");
        checkPermission();
    }

  private ForkJoinPool(int parallelism,
                         ForkJoinWorkerThreadFactory factory,
                         UncaughtExceptionHandler handler,
                         int mode,
                         String workerNamePrefix) {
      	// 假如：parallelism = 64
        this.workerNamePrefix = workerNamePrefix;
        this.factory = factory;
        this.ueh = handler;
      // config高16位保存mode，低16位保存，线程池具有的并行度
        this.config = (parallelism & SMASK) | mode;
      	// np = 11111111111
        long np = (long)(-parallelism); // offset ctl counts
      	// AC:1111 1111 1000 0000 高32的高16位
      	// TC:1111 1111 1000 0000 高32的低16位
      	// 低32保存空闲线程，也就是INACTIVE counts
        this.ctl = ((np << AC_SHIFT) & AC_MASK) | ((np << TC_SHIFT) & TC_MASK);
    }

```

```java
 static final int SMASK        = 0xffff;  

// Mode bits for ForkJoinPool.config and WorkQueue.config
// 高16位保存mode，低16位保存，线程池具有的并行度
static final int MODE_MASK    = 0xffff << 16;  // top half of int
static final int LIFO_QUEUE   = 0;
static final int FIFO_QUEUE   = 1 << 16;
static final int SHARED_QUEUE = 1 << 31;       // must be negative
```

### 4.4、lockRunState

```java
private int lockRunState() {
    int rs;
    // 该方法不是一进来就cas，如果有锁，那么就等待
    // 如果没有锁，那么再去cas，因为cas有开销
    // 一上来判断锁状态，就是一个优化
    return ((((rs = runState) & RSLOCK) != 0 	// 因为不为0，已经有线程持有了锁
             ||!U.compareAndSwapInt(this, RUNSTATE, rs, rs |= RSLOCK)) ?	// 无锁的话，CAS获取
            awaitRunStateLock() : rs);
}
```

### 4.5、awaitRunStateLock

```java
private int awaitRunStateLock() {
    Object lock;
    boolean wasInterrupted = false;
    // SPINS 自旋，
    //  private static final int SPINS  = 0; 默认为0
    for (int spins = SPINS, r = 0, rs, ns;;) {
        // 锁已经释放，其他线程cas去争取锁
        if (((rs = runState) & RSLOCK) == 0) {	// 没有锁，使用cas获取
            if (U.compareAndSwapInt(this, RUNSTATE, rs, ns = rs | RSLOCK)) {
                if (wasInterrupted) {
                    try {
                        Thread.currentThread().interrupt();
                    } catch (SecurityException ignore) {
                    }
                }
                return ns;	// 此时上锁成功，返回上锁成功的状态ns
            }
        }
        else if (r == 0)	// 初始化随机数
            r = ThreadLocalRandom.nextSecondarySeed();
        else if (spins > 0) { // 随机，减少自旋次数
            r ^= r << 6; r ^= r >>> 21; r ^= r << 7; // xorshift，异或随机数
            if (r >= 0)
                --spins;
        }
        else if ((rs & STARTED) == 0 || (lock = stealCounter) == null)
            // 由于当前rs的STARTED状态为0，代表了，当前FJP没有任务运行，那么没有必要再去睡眠了，因为这个状态维持时间非常短
            Thread.yield();   // initialization race
        else if (U.compareAndSwapInt(this, RUNSTATE, rs, rs | RSIGNAL)) {
            // 这里不能只睡眠，需要有人唤醒，所以这里必须置位 RSIGNAL 唤醒位，提示另外的线程需要唤醒它
            synchronized (lock) {
                if ((runState & RSIGNAL) != 0) {	// 检查一下RSIGNAL状态不为0，那么就等待，因为其他线程已经修改了runState这个状态
                    try {
                        lock.wait();
                    } catch (InterruptedException ie) {
                        if (!(Thread.currentThread() instanceof
                              ForkJoinWorkerThread))
                            wasInterrupted = true;
                    }
                }
                else
                    lock.notifyAll();	// 否则就唤醒其他线程继续操作
            }
        }
    }
}
```

### 4.6、unlockRunState

注意：此时oldRunState就是externalSubmit方法中的rs，有如下情况：

1. 在lockRunState获取锁方法中，已经有锁，那么就去awaitRunStateLock方法等待，方法可以获取新锁标记，此时lockRunState方法返回的是具有锁标记的rs。

   rs=RSLOCK，=》unlockRunState中cas**修改全局状态成功**，此时没有等待线程，所以无需唤醒
2. 在awaitRunStateLock方法中，还有可能，rs状态带上了RSIGNAL状态位，那么此时lockRunState方法返回的是具有锁标记和RSIGNAL状态位的rs

   rs=RSLOCK+RSIGNAL，有锁和RSIGNAL状态，=》**此时CAS失败**，肯定有等待的线程，需要唤醒

```java
// 解锁
private void unlockRunState(int oldRunState, int newRunState) {
    // rs状态更新失败，说明当前的状态
    // 注意此方法CAS成功和失败的几种情况
    if (!U.compareAndSwapInt(this, RUNSTATE, oldRunState, newRunState)) {
        Object lock = stealCounter;
        // 清除了RSIGNAL标志位，解锁成功，唤醒其他线程去争锁
        runState = newRunState;              // clears RSIGNAL bit
        if (lock != null)
            synchronized (lock) { lock.notifyAll(); }
    }
}
```

8、ForkJoinPool设计理念与普通线程池区别与联系

9、ForkJoinPool核心数据结构

10、ForkJoinTask、RecurisveAction、RecursiveTask、CountedCompleter任务区别

11、ForkJoinPool提交任务执行过程源码与原理

12、ForkJoinPool Fork/Join 过程源码与原理

13、ForkJoinPool shutdown 过程源码与原理

14、ForkJoinPool awaitTermination 过程源码与原理

15、CompletionStage与Future原理

16、 Completion及其子类与ForkJoinTask原理

## 5、基础知识

### 5.1、反码，补码、原码

**从一个面试问题开始：为什么一个byte可以表示-128~127？**

**推理：**我们要表示负数和正数对吧，所以需要一位来表示正或负，所以选取最高位用于表示整数和负数。定：最高位0为正数，最高位1为负数

先直接运算：十进制数： 1+(-1) = 0 等价于1-1 = 0

然后用上面的二进制规则来表示：A: 0000 0001 B: 1000 0001

**原码表示**：

> A+B = 0000 0001 + 1000 0001 = 1000 0010 = -2(十进制)，结果不对。故不能采用直接相加方法

**反码表示**：(正数反码不变，负数符号位不变，按位取反)

反码：A: 0000 0001 B:1111 1110

> A+B = 0000 0001 + 1111 1110 = 1111 1111(f反码) - 》 变为原码 - 》 1000 0000 结果不对
>
> 1000 0000的十进制数：-0

问题：为什么导致运算结果出现了不符合逻辑的数？因为忽略了符号位，也即符号位没有参与到运算中。

**此时定义补码**：（正数补码不变，负数取反码之后，加1）

补码：A:0000 0001 B：1111 11111

> A+B = 0000 0001 + 1111 1111 = 1 (溢出) 0000 0000（补码）-》变为原码 - 》0000 0000 结果正确，因此这就时补码出现的意义

**面试答案：**

> 正数表示最大范围：0 111 1111 = +127
>
> 负数表示最大范围：1 1111 111  = -127 ，从上面的推理可知，负数最小也就-127
>
> 但是由于，能借符号位，即最高位即当值也当符合位，所以：
>
> 1 000 0000 才是负数的范围，也即 -128

## 5、CompletableFuture

17、CompletableFuture调用链原理

18、CompletableFuture常用方法执行过程

## 6、多线程实战

1. 使用WebFlux增加系统整体吞吐量
2. 使用并行流排序减少等待时间
3. 使用AsyncEventBus事件机制解耦与异步执行增加系统吞吐量
4. 使用Netty 构造Reactor机制
5. 使用CompletableFuture调用链异步执行并回调，让出调度线程异步执行
6. 使用ForkJoinPool大任务拆分多个子任务并行处理与合并
7. 使用ScheduledThreadPoolExecutor异步定时或延时调度业务服务
8. 使用RingBuffer并行处理日志，增加日志写入速度
9. 使用ThreadPoolExecutor与Tomcat线程池搭配使用增加Tomcat吞吐量
