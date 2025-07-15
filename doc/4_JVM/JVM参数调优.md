# 调优

JVM调优最主要的调整下面两个指标：

停顿时间：垃圾收集器在垃圾回收中断应用执行的时间。

```
-XX:MaxGCPauseMillis
```

吞吐量：垃圾收集的时间和总时间的占比：1/(1+n)，吞吐量为1-1/(1+n)。

```
-XX:GCTimeRatio=n
```

## GC调优步骤

1. 打印GC日志

   ```
   -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -Xloggc:./gc.log
   ```

   Tomcat可以直接加载JAVA_OPTS变量里

2. 分析日志得到关键性指标

3. 分析GC原因，调优JVM参数

### Parallel Scavenge 收集器测试(默认)

分析parallel-gc.log

第一次调优，设置Metaspace大小：增大元空间大小

```
-XX:MetaspaceSize=64M  -XX:MaxMetaspaceSize=64M
```

第二次调优，曾带年轻代动态扩容增量（默认是20%），可以减少YGC:

```
-XX:YoungGenerationSizeIncrement=30
```

比较效果：

|            | 吞吐量      | 最大停顿    | 平均停顿    | YGC  | FGC  |      |
| ---------- | ----------- | ----------- | ----------- | ---- | ---- | ---- |
| 默认       | **98.161%** | **130 ms**  | **19.2 ms** | 10   | 3    |      |
| 第一次调优 | **99.041%** | **10.0 ms** | **3.75 ms** | 8    | 0    |      |
| 第二次调优 | **97.498%** | **40.0 ms** | **10.0 ms** | 8    | 0    |      |

由结果可知，具体如何调优还需要根据实际情况来决定。

### CMS收集器测试

```
-XX:+UseConcMarkSweepGC
```

分析gc-cms.log

### G1收集器测试

```
-XX:+UseG1GC
```

分析gc-g1.log

young GC:[GC pause (G1 Evacuation Pause)(young)

initial-mark:[GC pause  (Metadata GC Threshold)(young)(initial-mark) (参数：InitiatingHeapOccupancyPercent)

mixed GC:[GC pause (G1 Evacuation Pause)(Mixed) (参数：G1HeapWastePercent)

full GC:[Full GC (Allocation Failure)(无可用region)

（G1内部，前面提到的混合GC是非常重要的释放内存机制，它避免了G1出现Region没有可用的情况，否则就会触发 FullGC事件。CMS、Parallel、Serial GC都需要通过Full GC去压缩老年代并在这个过程中扫描整个老年代。G1的Full GC算法和Serial GC收集器完全一致。当一个Full GC发生时，整个Java堆执行一个完整的压缩，这样确保了最大的空余内存可用。G1的Full GC是一个单线程，它可能引起一个长时间的停顿时间，G1的设计目标是减少Full GC，满足应用性能目标。）

查看发生MixedGC的阈值：jinfo -flag InitiatingHeapOccupancyPercent 进程ID

调优：

第一次调优，设置Metaspace大小：增大元空间大小-XX:MetaspaceSize=64M  -XX:MaxMetaspaceSize=64M

第二次调优，添加吞吐量和停顿时间参数：-XX:GCTimeRatio=99 -XX:MaxGCPauseMillis=10

## GC常用参数

### 堆栈设置

-Xss:每个线程的栈大小

-Xms:初始堆大小，默认物理内存的1/64

-Xmx:最大堆大小，默认物理内存的1/4

-Xmn:新生代大小

-XX:NewSize:设置新生代初始大小

-XX:NewRatio:默认2表示新生代占年老代的1/2，占整个堆内存的1/3。

-XX:SurvivorRatio:默认8表示一个survivor区占用1/8的Eden内存，即1/10的新生代内存。

-XX:MetaspaceSize:设置元空间大小

-XX:MaxMetaspaceSize:设置元空间最大允许大小，默认不受限制，JVM Metaspace会进行动态扩展。

### 垃圾回收统计信息

-XX:+PrintGC

-XX:+PrintGCDetails

-XX:+PrintGCTimeStamps

-Xloggc:filename

### 收集器设置

-XX:+UseSerialGC:设置串行收集器

-XX:+UseParallelGC:设置并行收集器

-XX:+UseParallelOldGC:老年代使用并行回收收集器

-XX:+UseParNewGC:在新生代使用并行收集器

-XX:+UseParalledlOldGC:设置并行老年代收集器

-XX:+UseConcMarkSweepGC:设置CMS并发收集器

-XX:+UseG1GC:设置G1收集器

-XX:ParallelGCThreads:设置用于垃圾回收的线程数

### 并行收集器设置

-XX:ParallelGCThreads:设置并行收集器收集时使用的CPU数。并行收集线程数。

-XX:MaxGCPauseMillis:设置并行收集最大暂停时间

-XX:GCTimeRatio:设置垃圾回收时间占程序运行时间的百分比。公式为1/(1+n)

### CMS收集器设置

-XX:+UseConcMarkSweepGC:设置CMS并发收集器

-XX:+CMSIncrementalMode:设置为增量模式。适用于单CPU情况。

-XX:ParallelGCThreads:设置并发收集器新生代收集方式为并行收集时，使用的CPU数。并行收集线程数。

-XX:CMSFullGCsBeforeCompaction:设定进行多少次CMS垃圾回收后，进行一次内存压缩

-XX:+CMSClassUnloadingEnabled:允许对类元数据进行回收

-XX:UseCMSInitiatingOccupancyOnly:表示只在到达阀值的时候，才进行CMS回收

-XX:+CMSIncrementalMode:设置为增量模式。适用于单CPU情况

-XX:ParallelCMSThreads:设定CMS的线程数量

-XX:CMSInitiatingOccupancyFraction:设置CMS收集器在老年代空间被使用多少后触发

-XX:+UseCMSCompactAtFullCollection:设置CMS收集器在完成垃圾收集后是否要进行一次内存碎片的整理

### G1收集器设置

-XX:+UseG1GC:使用G1收集器

-XX:ParallelGCThreads:指定GC工作的线程数量

-XX:G1HeapRegionSize:指定分区大小(1MB~32MB，且必须是2的幂)，默认将整堆划分为2048个分区

-XX:GCTimeRatio:吞吐量大小，0-100的整数(默认9)，值为n则系统将花费不超过1/(1+n)的时间用于垃圾收集

-XX:MaxGCPauseMillis:目标暂停时间(默认200ms)

-XX:G1NewSizePercent:新生代内存初始空间(默认整堆5%)

-XX:G1MaxNewSizePercent:新生代内存最大空间

-XX:TargetSurvivorRatio:Survivor填充容量(默认50%)

-XX:MaxTenuringThreshold:最大任期阈值(默认15)

-XX:InitiatingHeapOccupancyPercen:老年代占用空间超过整堆比IHOP阈值(默认45%),超过则执行混合收集

-XX:G1HeapWastePercent:堆废物百分比(默认5%)

-XX:G1MixedGCCountTarget:参数混合周期的最大总次数(默认8)