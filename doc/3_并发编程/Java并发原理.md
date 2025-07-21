# AQS 架构核心

原理是：**使用双向同步队列 + 共享状态state，来实现线程的阻塞，唤醒和同步**。

## 核心原理：

### 1、共享状态state

AQS使用一个 volatile int state 变量表示同步状态。通过CAS操作修改状态：

- 独占模式：ReentrantLock

  state表示锁持有的次数（可重入）

- 共享模式：Semaphore

  state：表示可用许可数

### 2、双向同步队列

- 未获取到资源的线程会被封装成Node节点，加入一个FIFO双向队列中等待。

- 对立头部节点head是当前持有资源的线程，后续节点为阻塞中的线程

### 3、线程阻塞和唤醒

- 通过LockSupport。park（）和unpack，实现线程的阻塞和唤醒。
- 当资源释放时，AQS会唤醒队列中符合条件的后续节点。

## 关键方法：

1. 独占模式：
   - acquire，尝试获取锁，失败则加入阻塞。
   - release：释放锁资源，唤醒后续节点。
2. 共享模式：
   - acquireShared，获取共享资源。
   - releaseShared，释放共享资源

## 工作流程：ReentrantLock

1. 加锁lock
   - 线程调用acquire方法，尝试通过CAS修改状态将state从0改成1.
   - 成功则独占资源，失败则创建Node加入到队列尾部，并阻塞。
2. 解锁：unlock
   - 调用release将state减1，若state=0，则唤醒队列中下一个线程。

## AQS特性：

- 可重入：同一个线程可多次获取锁，state累加
- 公平/非公平：通过队列现实公平锁，非公平锁允许插队（减少上下文的切换）
- 可中断：支持acquireInterruptibly()响应中断



