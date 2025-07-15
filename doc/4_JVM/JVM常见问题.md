

# G1参数如何设置？

JVM 启动参数：

当前系统  : 8G Memory 、4C 

```sh
-XX:+UseG1GC
-XX:MaxGCPauseMillis=100
-XX:G1NewSizePercent=60
-XX:G1MaxNewSizePercent=80
# -XX:NewRatio=1
-XX:SurvivorRatio=8
-XX:ParallelGCThreads=2
-XX:ConcGCThreads=1
-XX:+AlwaysPreTouch
-XX:+UseStringDeduplication
-Xms4g -Xmx5g
```

* `-XX:+UseG1GC`
  启用 G1 垃圾回收器，适合大堆、低延迟场景。
* `-XX:MaxGCPauseMillis=100`
  期望每次 GC 停顿不超过 100 毫秒，G1 会尽量满足。
* `-XX:G1NewSizePercent=60`
  新生代最小占整个堆的 60%。
* `-XX:G1MaxNewSizePercent=80`
  新生代最大占整个堆的 80%。
* `-XX:NewRatio=1`
  老年代与新生代的比例为 1:1（对 G1 影响较小，主要用于其他 GC）。
* `-XX:SurvivorRatio=8`
  Eden 区与每个 Survivor 区的比例为 8:1:1。
* `-XX:ParallelGCThreads=2`
  年轻代 GC 时使用的并行线程数为 8。
* `-XX:ConcGCThreads=1`
  G1 并发阶段（如并发标记）使用的线程数为 1。
* `-XX:+AlwaysPreTouch`
  JVM 启动时预先分配并触碰所有堆内存，避免运行时分配带来的延迟。
* `-XX:+UseStringDeduplication`
  启用字符串去重，减少内存中重复字符串的占用（G1 GC 专属）。
* `-Xms4g`
  堆初始大小为 5GB。
* `-Xmx6g`
  堆最大大小为 6GB。
* `-XX:NewRatio`
  设置老年代与新生代的比例（不推荐与G1一起用）。
* `-XX:MaxNewSize` 和 `-XX:NewSize`
  这两个参数对G1影响有限，G1主要根据堆的使用情况动态调整新生代大小。
* `-XX:G1NewSizePercent`
  新生代最小占整个堆的百分比（默认5%）。
* `-XX:G1MaxNewSizePercent`
  新生代最大占整个堆的百分比（默认60%）。

JVM启动参数：

`java -jar -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:G1NewSizePercent=60 -XX:G1MaxNewSizePercent=80 -XX:NewRatio=1 -XX:SurvivorRatio=8 -XX:ParallelGCThreads=2 -XX:ConcGCThreads=1 -XX:+AlwaysPreTouch -XX:+UseStringDeduplication -Xms6g -Xmx7g gateway-0.1.jar`