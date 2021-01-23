# 总结

GC的分类
GC的主要回收区域就是年轻代(young gen)、老年代(tenured gen)、持久区（perm gen）,在jdk8之后，perm gen消失，被替换成了元空间（Metaspace）,元空间会在普通的堆区进行分配。垃圾收集为了提高效率，采用分代收集的方式，对于不同特点的回收区域使用不同的垃圾收集器。系统正常运行情况young是比较频繁的，full gc会触发整个heap的扫描和回收。在G1垃圾收集器中，最好的优化状态就是通过不断调整分区空间，避免进行full gc，可以大幅提高吞吐量。

* 串行 GC 
对年轻代使用 mark-copy(标记-复制) 算法，对老年代使用 mark-sweep-
compact(标记-清除-整理)算法。两者都是单线程的垃圾收集器，不能进行并行处理，所以都会触发全线暂停(STW)，停止所 有的应用线程。因此这种 GC 算法不能充分利用多核 CPU。不管有多少 CPU 内核，JVM 在垃圾收集时都只能使用单个核心。该选项只适合几百 MB 堆内存的 JVM，而且是单核 CPU 时比较有用。

* 并行GC
年轻代和老年代的垃圾回收都会触发 STW 事件。
在年轻代使用 标记-复制(mark-copy)算法，在老年代使用 标记-清除-整理(mark-sweep- compact)算法。
-XX:ParallelGCThreads=N 来指定 GC 线程数，其默认值为CPU核心数。
并行垃圾收集器适用于多核服务器，主要目标是增加吞吐量。因为对系统资源的有效使用，能达到 更高的吞吐量:在 GC 期间，所有 CPU 内核都在并行清理垃圾，所以总暂停时间更短;在两次GC周期的间隔期，没有GC线程在运行，不会消耗任何系统资源。

* CMS GC
其对年轻代采用并行 STW 方式的 mark-copy (标记-复制)算法，对老年代主要使用并发 mark-sweep (标记-清除)算法。
CMS GC 的设计目标是避免在老年代垃圾收集时出现长时间的卡顿，主要通过两种手段来达成 此目标:不对老年代进行整理，而是使用空闲列表(free-lists)来管理内存空间的回收。在 mark-and-sweep (标记-清除) 阶段的大部分工作和应用线程一起并发执行。在这些阶段并没有明显的应用线程暂停。但值得注意的是，它仍然和应用线程争抢 CPU 时间。默认情况下，CMS 使用的并发线程数等于 CPU 核心数的 1/4。
如果服务器是多核 CPU，并且主要调优目标是降低 GC 停顿导致的系统延迟，那么使用 CMS 是个很明智的选择。进行老年代的并发回收时，可能会伴随着多次年轻代的 minor GC。

* G1 GC
G1（Garbage-First）是在JDK 7u4版本之后发布的垃圾收集器，并在jdk9中成为默认垃圾收集器。从整体来说，G1也是利用多CPU来缩短stop the world时间，并且是高效的并发垃圾收集器。但是G1不再像上文所述的垃圾收集器，需要分代配合不同的垃圾收集器，因为G1中的垃圾收集区域是“分区”（Region）的。G1的分代收集和以上垃圾收集器不同的就是除了有年轻代的ygc，全堆扫描的fullgc外，还有包含所有年轻代以及部分老年代Region的MixedGC。G1的优势还有可以通过调整参数，指定垃圾收集的最大允许pause time。

## GCLogAnalysis.java

| GC                 | Xmx/Xms| Count   |
|:-------------------|:-------|:--------|
| UseSerialGC        | 512m   | 8290    |
| UseSerialGC        | 1g     | 9007    |
| UseSerialGC        | 2g     | 8648    |
| UseSerialGC        | 4g     | 7260    |
| UseParallelGC      | 512m   | 7565    |
| UseParallelGC      | 1g     | 8768    |
| UseParallelGC      | 2g     | 10114   |
| UseParallelGC      | 4g     | 7812    |
| UseConcMarkSweepGC | 512m   | 8433    |
| UseConcMarkSweepGC | 1g     | 9265    |
| UseConcMarkSweepGC | 2g     | 8207    |
| UseConcMarkSweepGC | 4g     | 8310    |
| UseG1GC            | 512m   | 7930    |
| UseG1GC            | 1g     | 8701    |
| UseG1GC            | 2g     | 9161    |
| UseG1GC            | 4g     | 9477    |

## gateway-server-0.0.1-SNAPSHOT

| GC                 | Xmx/Xms| Qps     |
|:-------------------|:-------|:--------|
| UseSerialGC        | 1g     | 12363   |
| UseSerialGC        | 2g     | 9702    |
| UseParallelGC      | 1g     | 12363   |
| UseParallelGC      | 2g     | 10694   |
| UseConcMarkSweepGC | 1g     | 10246   |
| UseConcMarkSweepGC | 2g     | 10792   |
| UseG1GC            | 1g     | 8913    |
| UseG1GC            | 2g     | 10415   |


# 附录：原始实验数据
机器信息：
MacBook Pro 
  Processor Name:	Dual-Core Intel Core i5
  Processor Speed:	2.5 GHz
  Memory:	16 GB

## GCLogAnalysis.java

* -XX:+UseSerialGC -Xmx512m -Xms512m -> Count:8290
> 正在执行...
2021-01-23T22:13:13.123-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.123-0800: [DefNew: 139776K->17472K(157248K), 0.0290765 secs] 139776K->45478K(506816K), 0.0291367 secs] [Times: user=0.02 sys=0.01, real=0.03 secs] 
2021-01-23T22:13:13.178-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.178-0800: [DefNew: 157248K->17471K(157248K), 0.0398339 secs] 185254K->90894K(506816K), 0.0399008 secs] [Times: user=0.03 sys=0.02, real=0.04 secs] 
2021-01-23T22:13:13.241-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.241-0800: [DefNew: 157247K->17471K(157248K), 0.0351416 secs] 230670K->137824K(506816K), 0.0351906 secs] [Times: user=0.02 sys=0.02, real=0.03 secs] 
2021-01-23T22:13:13.299-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.299-0800: [DefNew: 157138K->17469K(157248K), 0.0280283 secs] 277491K->177500K(506816K), 0.0280795 secs] [Times: user=0.02 sys=0.01, real=0.03 secs] 
2021-01-23T22:13:13.353-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.353-0800: [DefNew: 157245K->17469K(157248K), 0.0280296 secs] 317276K->217917K(506816K), 0.0280775 secs] [Times: user=0.02 sys=0.01, real=0.03 secs] 
2021-01-23T22:13:13.405-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.405-0800: [DefNew: 157245K->17471K(157248K), 0.0327236 secs] 357693K->262167K(506816K), 0.0327760 secs] [Times: user=0.02 sys=0.02, real=0.03 secs] 
2021-01-23T22:13:13.462-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.462-0800: [DefNew: 157161K->17471K(157248K), 0.0311838 secs] 401857K->309138K(506816K), 0.0312330 secs] [Times: user=0.02 sys=0.01, real=0.03 secs] 
2021-01-23T22:13:13.518-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.518-0800: [DefNew: 157247K->17469K(157248K), 0.0294964 secs] 448914K->350528K(506816K), 0.0295461 secs] [Times: user=0.01 sys=0.01, real=0.03 secs] 
2021-01-23T22:13:13.572-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.572-0800: [DefNew: 157245K->157245K(157248K), 0.0000259 secs]2021-01-23T22:13:13.572-0800: [Tenured: 333058K->273083K(349568K), 0.0435703 secs] 490304K->273083K(506816K), [Metaspace: 2679K->2679K(1056768K)], 0.0436682 secs] [Times: user=0.04 sys=0.00, real=0.04 secs] 
2021-01-23T22:13:13.641-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.641-0800: [DefNew: 139776K->17471K(157248K), 0.0070489 secs] 412859K->319564K(506816K), 0.0070966 secs] [Times: user=0.01 sys=0.00, real=0.00 secs] 
2021-01-23T22:13:13.673-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.673-0800: [DefNew: 157247K->17470K(157248K), 0.0176392 secs] 459340K->365367K(506816K), 0.0176881 secs] [Times: user=0.01 sys=0.00, real=0.02 secs] 
2021-01-23T22:13:13.712-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.712-0800: [DefNew: 157246K->157246K(157248K), 0.0000308 secs]2021-01-23T22:13:13.713-0800: [Tenured: 347897K->314134K(349568K), 0.0481689 secs] 505143K->314134K(506816K), [Metaspace: 2679K->2679K(1056768K)], 0.0482610 secs] [Times: user=0.04 sys=0.00, real=0.05 secs] 
2021-01-23T22:13:13.784-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.784-0800: [DefNew: 139776K->139776K(157248K), 0.0000235 secs]2021-01-23T22:13:13.784-0800: [Tenured: 314134K->327239K(349568K), 0.0481989 secs] 453910K->327239K(506816K), [Metaspace: 2679K->2679K(1056768K)], 0.0482902 secs] [Times: user=0.04 sys=0.00, real=0.05 secs] 
2021-01-23T22:13:13.860-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.860-0800: [DefNew: 139776K->139776K(157248K), 0.0000216 secs]2021-01-23T22:13:13.860-0800: [Tenured: 327239K->315623K(349568K), 0.0558910 secs] 467015K->315623K(506816K), [Metaspace: 2679K->2679K(1056768K)], 0.0560085 secs] [Times: user=0.05 sys=0.00, real=0.05 secs] 
2021-01-23T22:13:13.938-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.938-0800: [DefNew: 139776K->139776K(157248K), 0.0000299 secs]2021-01-23T22:13:13.938-0800: [Tenured: 315623K->342151K(349568K), 0.0332696 secs] 455399K->342151K(506816K), [Metaspace: 2679K->2679K(1056768K)], 0.0333692 secs] [Times: user=0.03 sys=0.00, real=0.04 secs] 
2021-01-23T22:13:13.993-0800: [GC (Allocation Failure) 2021-01-23T22:13:13.993-0800: [DefNew: 139776K->139776K(157248K), 0.0000303 secs]2021-01-23T22:13:13.993-0800: [Tenured: 342151K->349424K(349568K), 0.0466248 secs] 481927K->349424K(506816K), [Metaspace: 2679K->2679K(1056768K)], 0.0467352 secs] [Times: user=0.05 sys=0.01, real=0.04 secs] 
执行结束!共生成对象次数:8290
Heap
 def new generation   total 157248K, used 6111K [0x00000007a0000000, 0x00000007aaaa0000, 0x00000007aaaa0000)
  eden space 139776K,   4% used [0x00000007a0000000, 0x00000007a05f7c18, 0x00000007a8880000)
  from space 17472K,   0% used [0x00000007a8880000, 0x00000007a8880000, 0x00000007a9990000)
  to   space 17472K,   0% used [0x00000007a9990000, 0x00000007a9990000, 0x00000007aaaa0000)
 tenured generation   total 349568K, used 349424K [0x00000007aaaa0000, 0x00000007c0000000, 0x00000007c0000000)
   the space 349568K,  99% used [0x00000007aaaa0000, 0x00000007bffdc218, 0x00000007bffdc400, 0x00000007c0000000)
 Metaspace       used 2685K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K

* -XX:+UseSerialGC -Xmx1g -Xms1g -> Count:9007
> 正在执行...
2021-01-23T22:16:35.425-0800: [GC (Allocation Failure) 2021-01-23T22:16:35.425-0800: [DefNew: 279616K->34944K(314560K), 0.0496916 secs] 279616K->76673K(1013632K), 0.0497590 secs] [Times: user=0.02 sys=0.02, real=0.05 secs] 
2021-01-23T22:16:35.525-0800: [GC (Allocation Failure) 2021-01-23T22:16:35.525-0800: [DefNew: 314560K->34943K(314560K), 0.0772815 secs] 356289K->163279K(1013632K), 0.0773319 secs] [Times: user=0.04 sys=0.04, real=0.08 secs] 
2021-01-23T22:16:35.655-0800: [GC (Allocation Failure) 2021-01-23T22:16:35.655-0800: [DefNew: 314559K->34943K(314560K), 0.0562801 secs] 442895K->242023K(1013632K), 0.0563397 secs] [Times: user=0.03 sys=0.02, real=0.06 secs] 
2021-01-23T22:16:35.760-0800: [GC (Allocation Failure) 2021-01-23T22:16:35.760-0800: [DefNew: 314559K->34942K(314560K), 0.0579239 secs] 521639K->324178K(1013632K), 0.0579753 secs] [Times: user=0.03 sys=0.03, real=0.05 secs] 
2021-01-23T22:16:35.862-0800: [GC (Allocation Failure) 2021-01-23T22:16:35.862-0800: [DefNew: 314558K->34943K(314560K), 0.0538360 secs] 603794K->401932K(1013632K), 0.0538866 secs] [Times: user=0.03 sys=0.02, real=0.05 secs] 
2021-01-23T22:16:35.957-0800: [GC (Allocation Failure) 2021-01-23T22:16:35.957-0800: [DefNew: 314559K->34943K(314560K), 0.0575820 secs] 681548K->473340K(1013632K), 0.0576356 secs] [Times: user=0.03 sys=0.03, real=0.06 secs] 
2021-01-23T22:16:36.060-0800: [GC (Allocation Failure) 2021-01-23T22:16:36.060-0800: [DefNew: 314559K->34941K(314560K), 0.0575823 secs] 752956K->552064K(1013632K), 0.0576317 secs] [Times: user=0.03 sys=0.02, real=0.05 secs] 
2021-01-23T22:16:36.161-0800: [GC (Allocation Failure) 2021-01-23T22:16:36.161-0800: [DefNew: 314557K->34944K(314560K), 0.0611664 secs] 831680K->634904K(1013632K), 0.0612147 secs] [Times: user=0.03 sys=0.03, real=0.06 secs] 
执行结束!共生成对象次数:9007
Heap
 def new generation   total 314560K, used 140269K [0x0000000780000000, 0x0000000795550000, 0x0000000795550000)
  eden space 279616K,  37% used [0x0000000780000000, 0x00000007866db488, 0x0000000791110000)
  from space 34944K, 100% used [0x0000000791110000, 0x0000000793330000, 0x0000000793330000)
  to   space 34944K,   0% used [0x0000000793330000, 0x0000000793330000, 0x0000000795550000)
 tenured generation   total 699072K, used 599960K [0x0000000795550000, 0x00000007c0000000, 0x00000007c0000000)
   the space 699072K,  85% used [0x0000000795550000, 0x00000007b9f36008, 0x00000007b9f36200, 0x00000007c0000000)
 Metaspace       used 2685K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K

* -XX:+UseSerialGC -Xmx2g -Xms2g -> Count:8648
> 正在执行...
2021-01-23T22:18:55.996-0800: [GC (Allocation Failure) 2021-01-23T22:18:55.996-0800: [DefNew: 559232K->69887K(629120K), 0.1062474 secs] 559232K->158905K(2027264K), 0.1063115 secs] [Times: user=0.06 sys=0.05, real=0.11 secs] 
2021-01-23T22:18:56.211-0800: [GC (Allocation Failure) 2021-01-23T22:18:56.211-0800: [DefNew: 629119K->69887K(629120K), 0.1231955 secs] 718137K->282354K(2027264K), 0.1232592 secs] [Times: user=0.07 sys=0.06, real=0.12 secs] 
2021-01-23T22:18:56.424-0800: [GC (Allocation Failure) 2021-01-23T22:18:56.424-0800: [DefNew: 629119K->69887K(629120K), 0.0923270 secs] 841586K->408999K(2027264K), 0.0923785 secs] [Times: user=0.05 sys=0.04, real=0.09 secs] 
2021-01-23T22:18:56.598-0800: [GC (Allocation Failure) 2021-01-23T22:18:56.598-0800: [DefNew: 629119K->69887K(629120K), 0.0951484 secs] 968231K->532792K(2027264K), 0.0952001 secs] [Times: user=0.06 sys=0.04, real=0.10 secs] 
执行结束!共生成对象次数:8648
Heap
 def new generation   total 629120K, used 92551K [0x0000000740000000, 0x000000076aaa0000, 0x000000076aaa0000)
  eden space 559232K,   4% used [0x0000000740000000, 0x0000000741622020, 0x0000000762220000)
  from space 69888K,  99% used [0x0000000762220000, 0x000000076665fdd8, 0x0000000766660000)
  to   space 69888K,   0% used [0x0000000766660000, 0x0000000766660000, 0x000000076aaa0000)
 tenured generation   total 1398144K, used 462904K [0x000000076aaa0000, 0x00000007c0000000, 0x00000007c0000000)
   the space 1398144K,  33% used [0x000000076aaa0000, 0x0000000786eae3b8, 0x0000000786eae400, 0x00000007c0000000)
 Metaspace       used 2685K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K

* -XX:+UseSerialGC -Xmx4g -Xms4g -> Count:7260
> 正在执行...
2021-01-23T22:21:22.020-0800: [GC (Allocation Failure) 2021-01-23T22:21:22.020-0800: [DefNew: 1118528K->139776K(1258304K), 0.1569749 secs] 1118528K->250649K(4054528K), 0.1570644 secs] [Times: user=0.08 sys=0.07, real=0.15 secs] 
执行结束!共生成对象次数:7260
Heap
 def new generation   total 1258304K, used 960286K [0x00000006c0000000, 0x0000000715550000, 0x0000000715550000)
  eden space 1118528K,  73% used [0x00000006c0000000, 0x00000006f2147940, 0x0000000704450000)
  from space 139776K, 100% used [0x000000070ccd0000, 0x0000000715550000, 0x0000000715550000)
  to   space 139776K,   0% used [0x0000000704450000, 0x0000000704450000, 0x000000070ccd0000)
 tenured generation   total 2796224K, used 110873K [0x0000000715550000, 0x00000007c0000000, 0x00000007c0000000)
   the space 2796224K,   3% used [0x0000000715550000, 0x000000071c1964f0, 0x000000071c196600, 0x00000007c0000000)
 Metaspace       used 2685K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K

* -XX:+UseParallelGC -Xmx512m -Xms512m -> Count:7565
> 正在执行...
2021-01-23T22:23:03.881-0800: [GC (Allocation Failure) [PSYoungGen: 131584K->21493K(153088K)] 131584K->47788K(502784K), 0.0201467 secs] [Times: user=0.02 sys=0.05, real=0.02 secs] 
2021-01-23T22:23:03.925-0800: [GC (Allocation Failure) [PSYoungGen: 153077K->21497K(153088K)] 179372K->93502K(502784K), 0.0277453 secs] [Times: user=0.03 sys=0.07, real=0.03 secs] 
2021-01-23T22:23:03.976-0800: [GC (Allocation Failure) [PSYoungGen: 153081K->21489K(153088K)] 225086K->133092K(502784K), 0.0199169 secs] [Times: user=0.03 sys=0.05, real=0.02 secs] 
2021-01-23T22:23:04.018-0800: [GC (Allocation Failure) [PSYoungGen: 153073K->21489K(153088K)] 264676K->181995K(502784K), 0.0247805 secs] [Times: user=0.04 sys=0.05, real=0.03 secs] 
2021-01-23T22:23:04.065-0800: [GC (Allocation Failure) [PSYoungGen: 152466K->21498K(153088K)] 312971K->217390K(502784K), 0.0195949 secs] [Times: user=0.03 sys=0.04, real=0.02 secs] 
2021-01-23T22:23:04.107-0800: [GC (Allocation Failure) [PSYoungGen: 152911K->21497K(80384K)] 348803K->258592K(430080K), 0.0215611 secs] [Times: user=0.03 sys=0.04, real=0.02 secs] 
2021-01-23T22:23:04.139-0800: [GC (Allocation Failure) [PSYoungGen: 79920K->32693K(116736K)] 317016K->274998K(466432K), 0.0079762 secs] [Times: user=0.03 sys=0.01, real=0.01 secs] 
2021-01-23T22:23:04.157-0800: [GC (Allocation Failure) [PSYoungGen: 91573K->47994K(116736K)] 333878K->295771K(466432K), 0.0095957 secs] [Times: user=0.03 sys=0.01, real=0.01 secs] 
2021-01-23T22:23:04.180-0800: [GC (Allocation Failure) [PSYoungGen: 106522K->56567K(116736K)] 354299K->312419K(466432K), 0.0120215 secs] [Times: user=0.03 sys=0.01, real=0.01 secs] 
2021-01-23T22:23:04.202-0800: [GC (Allocation Failure) [PSYoungGen: 115447K->37323K(116736K)] 371299K->328952K(466432K), 0.0210787 secs] [Times: user=0.03 sys=0.04, real=0.02 secs] 
2021-01-23T22:23:04.223-0800: [Full GC (Ergonomics) [PSYoungGen: 37323K->0K(116736K)] [ParOldGen: 291629K->230972K(349696K)] 328952K->230972K(466432K), [Metaspace: 2679K->2679K(1056768K)], 0.0439459 secs] [Times: user=0.15 sys=0.01, real=0.04 secs] 
2021-01-23T22:23:04.278-0800: [GC (Allocation Failure) [PSYoungGen: 58623K->20878K(116736K)] 289596K->251850K(466432K), 0.0040589 secs] [Times: user=0.01 sys=0.00, real=0.01 secs] 
2021-01-23T22:23:04.291-0800: [GC (Allocation Failure) [PSYoungGen: 79758K->21946K(116736K)] 310730K->272024K(466432K), 0.0070940 secs] [Times: user=0.03 sys=0.00, real=0.00 secs] 
2021-01-23T22:23:04.309-0800: [GC (Allocation Failure) [PSYoungGen: 80798K->26525K(116736K)] 330877K->297404K(466432K), 0.0078924 secs] [Times: user=0.03 sys=0.00, real=0.01 secs] 
2021-01-23T22:23:04.328-0800: [GC (Allocation Failure) [PSYoungGen: 85319K->20332K(116736K)] 356198K->316783K(466432K), 0.0093140 secs] [Times: user=0.03 sys=0.00, real=0.01 secs] 
2021-01-23T22:23:04.349-0800: [GC (Allocation Failure) [PSYoungGen: 79195K->17713K(116736K)] 375646K->333448K(466432K), 0.0114464 secs] [Times: user=0.02 sys=0.02, real=0.02 secs] 
2021-01-23T22:23:04.360-0800: [Full GC (Ergonomics) [PSYoungGen: 17713K->0K(116736K)] [ParOldGen: 315735K->269130K(349696K)] 333448K->269130K(466432K), [Metaspace: 2679K->2679K(1056768K)], 0.0443023 secs] [Times: user=0.15 sys=0.01, real=0.04 secs] 
2021-01-23T22:23:04.415-0800: [GC (Allocation Failure) [PSYoungGen: 58880K->20333K(116736K)] 328010K->289463K(466432K), 0.0039086 secs] [Times: user=0.02 sys=0.00, real=0.00 secs] 
2021-01-23T22:23:04.430-0800: [GC (Allocation Failure) [PSYoungGen: 79155K->21155K(116736K)] 348286K->307861K(466432K), 0.0066015 secs] [Times: user=0.02 sys=0.00, real=0.00 secs] 
2021-01-23T22:23:04.448-0800: [GC (Allocation Failure) [PSYoungGen: 79990K->19548K(116736K)] 366696K->325869K(466432K), 0.0078609 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
2021-01-23T22:23:04.456-0800: [Full GC (Ergonomics) [PSYoungGen: 19548K->0K(116736K)] [ParOldGen: 306321K->282716K(349696K)] 325869K->282716K(466432K), [Metaspace: 2679K->2679K(1056768K)], 0.0423727 secs] [Times: user=0.15 sys=0.00, real=0.04 secs] 
2021-01-23T22:23:04.510-0800: [GC (Allocation Failure) [PSYoungGen: 58880K->16585K(116736K)] 341596K->299301K(466432K), 0.0033238 secs] [Times: user=0.02 sys=0.00, real=0.00 secs] 
2021-01-23T22:23:04.527-0800: [GC (Allocation Failure) [PSYoungGen: 75173K->20500K(116736K)] 357889K->318964K(466432K), 0.0061525 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
2021-01-23T22:23:04.545-0800: [GC (Allocation Failure) [PSYoungGen: 79311K->18028K(116736K)] 377776K->335414K(466432K), 0.0066036 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
2021-01-23T22:23:04.552-0800: [Full GC (Ergonomics) [PSYoungGen: 18028K->0K(116736K)] [ParOldGen: 317385K->297534K(349696K)] 335414K->297534K(466432K), [Metaspace: 2679K->2679K(1056768K)], 0.0469160 secs] [Times: user=0.17 sys=0.00, real=0.04 secs] 
2021-01-23T22:23:04.609-0800: [GC (Allocation Failure) [PSYoungGen: 58816K->21917K(116736K)] 356351K->319451K(466432K), 0.0039921 secs] [Times: user=0.01 sys=0.00, real=0.01 secs] 
2021-01-23T22:23:04.623-0800: [GC (Allocation Failure) [PSYoungGen: 80125K->19114K(116736K)] 377659K->336631K(466432K), 0.0066991 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
2021-01-23T22:23:04.630-0800: [Full GC (Ergonomics) [PSYoungGen: 19114K->0K(116736K)] [ParOldGen: 317516K->305143K(349696K)] 336631K->305143K(466432K), [Metaspace: 2679K->2679K(1056768K)], 0.0470914 secs] [Times: user=0.17 sys=0.01, real=0.04 secs] 
2021-01-23T22:23:04.688-0800: [GC (Allocation Failure) [PSYoungGen: 58880K->21435K(116736K)] 364023K->326579K(466432K), 0.0040936 secs] [Times: user=0.01 sys=0.00, real=0.01 secs] 
2021-01-23T22:23:04.702-0800: [GC (Allocation Failure) [PSYoungGen: 80221K->20637K(116736K)] 385365K->346535K(466432K), 0.0087480 secs] [Times: user=0.03 sys=0.01, real=0.01 secs] 
2021-01-23T22:23:04.711-0800: [Full GC (Ergonomics) [PSYoungGen: 20637K->0K(116736K)] [ParOldGen: 325898K->311432K(349696K)] 346535K->311432K(466432K), [Metaspace: 2679K->2679K(1056768K)], 0.0478229 secs] [Times: user=0.16 sys=0.00, real=0.04 secs] 
2021-01-23T22:23:04.770-0800: [GC (Allocation Failure) [PSYoungGen: 58599K->19862K(116736K)] 370031K->331294K(466432K), 0.0038770 secs] [Times: user=0.01 sys=0.00, real=0.00 secs] 
执行结束!共生成对象次数:7565
Heap
 PSYoungGen      total 116736K, used 49935K [0x00000007b5580000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 58880K, 51% used [0x00000007b5580000,0x00000007b72de570,0x00000007b8f00000)
  from space 57856K, 34% used [0x00000007bc780000,0x00000007bdae59e0,0x00000007c0000000)
  to   space 57856K, 0% used [0x00000007b8f00000,0x00000007b8f00000,0x00000007bc780000)
 ParOldGen       total 349696K, used 311432K [0x00000007a0000000, 0x00000007b5580000, 0x00000007b5580000)
  object space 349696K, 89% used [0x00000007a0000000,0x00000007b3022120,0x00000007b5580000)
 Metaspace       used 2685K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K

* -XX:+UseParallelGC -Xmx1g -Xms1g GCLogAnalysis -> Count:8768
> 正在执行...
2021-01-23T22:25:52.280-0800: [GC (Allocation Failure) [PSYoungGen: 262144K->43508K(305664K)] 262144K->92197K(1005056K), 0.0374449 secs] [Times: user=0.04 sys=0.10, real=0.03 secs] 
2021-01-23T22:25:52.367-0800: [GC (Allocation Failure) [PSYoungGen: 305652K->43506K(305664K)] 354341K->166596K(1005056K), 0.0498336 secs] [Times: user=0.05 sys=0.13, real=0.05 secs] 
2021-01-23T22:25:52.458-0800: [GC (Allocation Failure) [PSYoungGen: 305650K->43507K(305664K)] 428740K->241719K(1005056K), 0.0402632 secs] [Times: user=0.06 sys=0.08, real=0.04 secs] 
2021-01-23T22:25:52.541-0800: [GC (Allocation Failure) [PSYoungGen: 305651K->43503K(305664K)] 503863K->309513K(1005056K), 0.0374653 secs] [Times: user=0.05 sys=0.08, real=0.03 secs] 
2021-01-23T22:25:52.621-0800: [GC (Allocation Failure) [PSYoungGen: 305647K->43516K(305664K)] 571657K->388103K(1005056K), 0.0411938 secs] [Times: user=0.07 sys=0.09, real=0.04 secs] 
2021-01-23T22:25:52.706-0800: [GC (Allocation Failure) [PSYoungGen: 305660K->43511K(160256K)] 650247K->453828K(859648K), 0.0367750 secs] [Times: user=0.06 sys=0.08, real=0.04 secs] 
2021-01-23T22:25:52.762-0800: [GC (Allocation Failure) [PSYoungGen: 160107K->64727K(232960K)] 570423K->484908K(932352K), 0.0169695 secs] [Times: user=0.04 sys=0.01, real=0.01 secs] 
2021-01-23T22:25:52.799-0800: [GC (Allocation Failure) [PSYoungGen: 181463K->87256K(232960K)] 601644K->518560K(932352K), 0.0207240 secs] [Times: user=0.06 sys=0.01, real=0.03 secs] 
2021-01-23T22:25:52.840-0800: [GC (Allocation Failure) [PSYoungGen: 203992K->106430K(232960K)] 635296K->551199K(932352K), 0.0268303 secs] [Times: user=0.07 sys=0.01, real=0.02 secs] 
2021-01-23T22:25:52.891-0800: [GC (Allocation Failure) [PSYoungGen: 223166K->76381K(232960K)] 667935K->582925K(932352K), 0.0367989 secs] [Times: user=0.06 sys=0.06, real=0.03 secs] 
2021-01-23T22:25:52.948-0800: [GC (Allocation Failure) [PSYoungGen: 193117K->37271K(232960K)] 699661K->613065K(932352K), 0.0360056 secs] [Times: user=0.05 sys=0.07, real=0.04 secs] 
2021-01-23T22:25:53.004-0800: [GC (Allocation Failure) [PSYoungGen: 154007K->37881K(232960K)] 729801K->648022K(932352K), 0.0214403 secs] [Times: user=0.03 sys=0.03, real=0.02 secs] 
2021-01-23T22:25:53.026-0800: [Full GC (Ergonomics) [PSYoungGen: 37881K->0K(232960K)] [ParOldGen: 610140K->333757K(699392K)] 648022K->333757K(932352K), [Metaspace: 2679K->2679K(1056768K)], 0.0696585 secs] [Times: user=0.20 sys=0.02, real=0.07 secs] 
执行结束!共生成对象次数:8768
Heap
 PSYoungGen      total 232960K, used 33064K [0x00000007aab00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 116736K, 28% used [0x00000007aab00000,0x00000007acb4a098,0x00000007b1d00000)
  from space 116224K, 0% used [0x00000007b8e80000,0x00000007b8e80000,0x00000007c0000000)
  to   space 116224K, 0% used [0x00000007b1d00000,0x00000007b1d00000,0x00000007b8e80000)
 ParOldGen       total 699392K, used 333757K [0x0000000780000000, 0x00000007aab00000, 0x00000007aab00000)
  object space 699392K, 47% used [0x0000000780000000,0x00000007945ef5c8,0x00000007aab00000)
 Metaspace       used 2685K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K

* -XX:+UseParallelGC -Xmx2g -Xms2g GCLogAnalysis -> Count: 10114
> 正在执行...
2021-01-23T22:27:09.304-0800: [GC (Allocation Failure) [PSYoungGen: 524800K->87035K(611840K)] 524800K->144109K(2010112K), 0.0618229 secs] [Times: user=0.06 sys=0.15, real=0.06 secs] 
2021-01-23T22:27:09.468-0800: [GC (Allocation Failure) [PSYoungGen: 611835K->87023K(611840K)] 668909K->262449K(2010112K), 0.0872930 secs] [Times: user=0.09 sys=0.22, real=0.09 secs] 
2021-01-23T22:27:09.643-0800: [GC (Allocation Failure) [PSYoungGen: 611823K->87038K(611840K)] 787249K->380783K(2010112K), 0.0681716 secs] [Times: user=0.11 sys=0.12, real=0.07 secs] 
2021-01-23T22:27:09.794-0800: [GC (Allocation Failure) [PSYoungGen: 611838K->87022K(611840K)] 905583K->498192K(2010112K), 0.0646391 secs] [Times: user=0.09 sys=0.13, real=0.06 secs] 
2021-01-23T22:27:09.945-0800: [GC (Allocation Failure) [PSYoungGen: 611822K->87038K(611840K)] 1022992K->620807K(2010112K), 0.0686622 secs] [Times: user=0.11 sys=0.13, real=0.07 secs] 
执行结束!共生成对象次数:10114
Heap
 PSYoungGen      total 611840K, used 108259K [0x0000000795580000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 524800K, 4% used [0x0000000795580000,0x0000000796a39610,0x00000007b5600000)
  from space 87040K, 99% used [0x00000007b5600000,0x00000007baaff940,0x00000007bab00000)
  to   space 87040K, 0% used [0x00000007bab00000,0x00000007bab00000,0x00000007c0000000)
 ParOldGen       total 1398272K, used 533769K [0x0000000740000000, 0x0000000795580000, 0x0000000795580000)
  object space 1398272K, 38% used [0x0000000740000000,0x0000000760942648,0x0000000795580000)
 Metaspace       used 2685K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K

* -XX:+UseParallelGC -Xmx4g -Xms4g GCLogAnalysis -> Count: 7812
> 正在执行...
2021-01-23T22:28:07.713-0800: [GC (Allocation Failure) [PSYoungGen: 1048576K->174589K(1223168K)] 1048576K->230569K(4019712K), 0.0978026 secs] [Times: user=0.10 sys=0.23, real=0.10 secs] 
2021-01-23T22:28:07.999-0800: [GC (Allocation Failure) [PSYoungGen: 1223165K->174588K(1223168K)] 1279145K->358909K(4019712K), 0.1303791 secs] [Times: user=0.13 sys=0.34, real=0.13 secs] 
执行结束!共生成对象次数:7812
Heap
 PSYoungGen      total 1223168K, used 216562K [0x000000076ab00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 1048576K, 4% used [0x000000076ab00000,0x000000076d3fd790,0x00000007aab00000)
  from space 174592K, 99% used [0x00000007b5580000,0x00000007bffff070,0x00000007c0000000)
  to   space 174592K, 0% used [0x00000007aab00000,0x00000007aab00000,0x00000007b5580000)
 ParOldGen       total 2796544K, used 184321K [0x00000006c0000000, 0x000000076ab00000, 0x000000076ab00000)
  object space 2796544K, 6% used [0x00000006c0000000,0x00000006cb400448,0x000000076ab00000)
 Metaspace       used 2685K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K

* -XX:+UseConcMarkSweepGC -Xms512m GCLogAnalysis -> Count: 8433
> 正在执行...
2021-01-23T22:34:50.654-0800: [GC (Allocation Failure) 2021-01-23T22:34:50.654-0800: [ParNew: 139776K->17471K(157248K), 0.0228892 secs] 139776K->52129K(506816K), 0.0229652 secs] [Times: user=0.04 sys=0.06, real=0.02 secs] 
2021-01-23T22:34:50.706-0800: [GC (Allocation Failure) 2021-01-23T22:34:50.706-0800: [ParNew: 157247K->17471K(157248K), 0.0270868 secs] 191905K->98939K(506816K), 0.0271537 secs] [Times: user=0.04 sys=0.06, real=0.03 secs] 
2021-01-23T22:34:50.759-0800: [GC (Allocation Failure) 2021-01-23T22:34:50.759-0800: [ParNew: 157247K->17470K(157248K), 0.0347063 secs] 238715K->140551K(506816K), 0.0347717 secs] [Times: user=0.11 sys=0.02, real=0.04 secs] 
2021-01-23T22:34:50.820-0800: [GC (Allocation Failure) 2021-01-23T22:34:50.820-0800: [ParNew: 157246K->17472K(157248K), 0.0322481 secs] 280327K->180277K(506816K), 0.0323123 secs] [Times: user=0.09 sys=0.02, real=0.03 secs] 
2021-01-23T22:34:50.876-0800: [GC (Allocation Failure) 2021-01-23T22:34:50.876-0800: [ParNew: 157248K->17468K(157248K), 0.0356499 secs] 320053K->225937K(506816K), 0.0357137 secs] [Times: user=0.10 sys=0.02, real=0.04 secs] 
2021-01-23T22:34:50.912-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 208468K(349568K)] 226079K(506816K), 0.0003298 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:50.912-0800: [CMS-concurrent-mark-start]
2021-01-23T22:34:50.920-0800: [CMS-concurrent-mark: 0.008/0.008 secs] [Times: user=0.01 sys=0.01, real=0.01 secs] 
2021-01-23T22:34:50.920-0800: [CMS-concurrent-preclean-start]
2021-01-23T22:34:50.920-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:50.921-0800: [CMS-concurrent-abortable-preclean-start]
2021-01-23T22:34:50.938-0800: [GC (Allocation Failure) 2021-01-23T22:34:50.938-0800: [ParNew: 157244K->17470K(157248K), 0.0381679 secs] 365713K->272270K(506816K), 0.0382318 secs] [Times: user=0.11 sys=0.02, real=0.04 secs] 
2021-01-23T22:34:50.997-0800: [GC (Allocation Failure) 2021-01-23T22:34:50.997-0800: [ParNew: 157204K->17471K(157248K), 0.0360310 secs] 412004K->315996K(506816K), 0.0361040 secs] [Times: user=0.10 sys=0.02, real=0.04 secs] 
2021-01-23T22:34:51.058-0800: [GC (Allocation Failure) 2021-01-23T22:34:51.058-0800: [ParNew: 157247K->17470K(157248K), 0.0382312 secs] 455772K->364341K(506816K), 0.0382919 secs] [Times: user=0.11 sys=0.03, real=0.04 secs] 
2021-01-23T22:34:51.096-0800: [CMS-concurrent-abortable-preclean: 0.004/0.176 secs] [Times: user=0.39 sys=0.07, real=0.17 secs] 
2021-01-23T22:34:51.096-0800: [GC (CMS Final Remark) [YG occupancy: 18060 K (157248 K)]2021-01-23T22:34:51.097-0800: [Rescan (parallel) , 0.0005620 secs]2021-01-23T22:34:51.097-0800: [weak refs processing, 0.0000262 secs]2021-01-23T22:34:51.097-0800: [class unloading, 0.0002547 secs]2021-01-23T22:34:51.097-0800: [scrub symbol table, 0.0004082 secs]2021-01-23T22:34:51.098-0800: [scrub string table, 0.0001542 secs][1 CMS-remark: 346870K(349568K)] 364930K(506816K), 0.0014974 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.098-0800: [CMS-concurrent-sweep-start]
2021-01-23T22:34:51.099-0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.099-0800: [CMS-concurrent-reset-start]
2021-01-23T22:34:51.100-0800: [CMS-concurrent-reset: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-01-23T22:34:51.120-0800: [GC (Allocation Failure) 2021-01-23T22:34:51.120-0800: [ParNew: 157022K->157022K(157248K), 0.0000267 secs]2021-01-23T22:34:51.120-0800: [CMS: 302330K->263331K(349568K), 0.0507917 secs] 459353K->263331K(506816K), [Metaspace: 2679K->2679K(1056768K)], 0.0509080 secs] [Times: user=0.05 sys=0.00, real=0.05 secs] 
2021-01-23T22:34:51.171-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 263331K(349568K)] 263587K(506816K), 0.0001795 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.172-0800: [CMS-concurrent-mark-start]
2021-01-23T22:34:51.174-0800: [CMS-concurrent-mark: 0.003/0.003 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.174-0800: [CMS-concurrent-preclean-start]
2021-01-23T22:34:51.175-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.175-0800: [CMS-concurrent-abortable-preclean-start]
2021-01-23T22:34:51.193-0800: [GC (Allocation Failure) 2021-01-23T22:34:51.193-0800: [ParNew: 139776K->17470K(157248K), 0.0072222 secs] 403107K->303657K(506816K), 0.0072856 secs] [Times: user=0.03 sys=0.00, real=0.01 secs] 
2021-01-23T22:34:51.225-0800: [GC (Allocation Failure) 2021-01-23T22:34:51.225-0800: [ParNew: 157246K->17470K(157248K), 0.0126732 secs] 443433K->348305K(506816K), 0.0127356 secs] [Times: user=0.04 sys=0.00, real=0.01 secs] 
2021-01-23T22:34:51.239-0800: [CMS-concurrent-abortable-preclean: 0.003/0.064 secs] [Times: user=0.12 sys=0.00, real=0.06 secs] 
2021-01-23T22:34:51.239-0800: [GC (CMS Final Remark) [YG occupancy: 27002 K (157248 K)]2021-01-23T22:34:51.239-0800: [Rescan (parallel) , 0.0005440 secs]2021-01-23T22:34:51.240-0800: [weak refs processing, 0.0000164 secs]2021-01-23T22:34:51.240-0800: [class unloading, 0.0002531 secs]2021-01-23T22:34:51.240-0800: [scrub symbol table, 0.0004384 secs]2021-01-23T22:34:51.240-0800: [scrub string table, 0.0001650 secs][1 CMS-remark: 330835K(349568K)] 357838K(506816K), 0.0015256 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-01-23T22:34:51.241-0800: [CMS-concurrent-sweep-start]
2021-01-23T22:34:51.242-0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.242-0800: [CMS-concurrent-reset-start]
2021-01-23T22:34:51.242-0800: [CMS-concurrent-reset: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.263-0800: [GC (Allocation Failure) 2021-01-23T22:34:51.263-0800: [ParNew: 157246K->157246K(157248K), 0.0000344 secs]2021-01-23T22:34:51.263-0800: [CMS: 330835K->297565K(349568K), 0.0520311 secs] 488081K->297565K(506816K), [Metaspace: 2679K->2679K(1056768K)], 0.0521478 secs] [Times: user=0.05 sys=0.00, real=0.05 secs] 
2021-01-23T22:34:51.315-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 297565K(349568K)] 297637K(506816K), 0.0002434 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.315-0800: [CMS-concurrent-mark-start]
2021-01-23T22:34:51.320-0800: [CMS-concurrent-mark: 0.004/0.004 secs] [Times: user=0.01 sys=0.00, real=0.01 secs] 
2021-01-23T22:34:51.320-0800: [CMS-concurrent-preclean-start]
2021-01-23T22:34:51.321-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.321-0800: [CMS-concurrent-abortable-preclean-start]
2021-01-23T22:34:51.341-0800: [GC (Allocation Failure) 2021-01-23T22:34:51.341-0800: [ParNew: 139776K->17470K(157248K), 0.0083804 secs] 437341K->344835K(506816K), 0.0084518 secs] [Times: user=0.04 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.351-0800: [CMS-concurrent-abortable-preclean: 0.002/0.030 secs] [Times: user=0.06 sys=0.00, real=0.03 secs] 
2021-01-23T22:34:51.351-0800: [GC (CMS Final Remark) [YG occupancy: 24660 K (157248 K)]2021-01-23T22:34:51.351-0800: [Rescan (parallel) , 0.0011219 secs]2021-01-23T22:34:51.352-0800: [weak refs processing, 0.0000226 secs]2021-01-23T22:34:51.352-0800: [class unloading, 0.0003304 secs]2021-01-23T22:34:51.353-0800: [scrub symbol table, 0.0007975 secs]2021-01-23T22:34:51.353-0800: [scrub string table, 0.0001770 secs][1 CMS-remark: 327364K(349568K)] 352025K(506816K), 0.0025403 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.354-0800: [CMS-concurrent-sweep-start]
2021-01-23T22:34:51.354-0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.354-0800: [CMS-concurrent-reset-start]
2021-01-23T22:34:51.355-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.379-0800: [GC (Allocation Failure) 2021-01-23T22:34:51.379-0800: [ParNew: 157222K->157222K(157248K), 0.0000262 secs]2021-01-23T22:34:51.379-0800: [CMS: 326769K->315905K(349568K), 0.0554014 secs] 483992K->315905K(506816K), [Metaspace: 2679K->2679K(1056768K)], 0.0555156 secs] [Times: user=0.05 sys=0.00, real=0.06 secs] 
2021-01-23T22:34:51.435-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 315905K(349568K)] 316291K(506816K), 0.0001752 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.435-0800: [CMS-concurrent-mark-start]
2021-01-23T22:34:51.438-0800: [CMS-concurrent-mark: 0.003/0.003 secs] [Times: user=0.01 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.438-0800: [CMS-concurrent-preclean-start]
2021-01-23T22:34:51.439-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.439-0800: [CMS-concurrent-abortable-preclean-start]
2021-01-23T22:34:51.456-0800: [GC (Allocation Failure) 2021-01-23T22:34:51.456-0800: [ParNew: 139776K->17471K(157248K), 0.0096207 secs] 455681K->366032K(506816K), 0.0096790 secs] [Times: user=0.04 sys=0.01, real=0.01 secs] 
2021-01-23T22:34:51.466-0800: [CMS-concurrent-abortable-preclean: 0.001/0.027 secs] [Times: user=0.06 sys=0.01, real=0.03 secs] 
2021-01-23T22:34:51.466-0800: [GC (CMS Final Remark) [YG occupancy: 20533 K (157248 K)]2021-01-23T22:34:51.466-0800: [Rescan (parallel) , 0.0005542 secs]2021-01-23T22:34:51.467-0800: [weak refs processing, 0.0000305 secs]2021-01-23T22:34:51.467-0800: [class unloading, 0.0002521 secs]2021-01-23T22:34:51.467-0800: [scrub symbol table, 0.0004625 secs]2021-01-23T22:34:51.468-0800: [scrub string table, 0.0001748 secs][1 CMS-remark: 348561K(349568K)] 369095K(506816K), 0.0015678 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.468-0800: [CMS-concurrent-sweep-start]
2021-01-23T22:34:51.469-0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.469-0800: [CMS-concurrent-reset-start]
2021-01-23T22:34:51.469-0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.499-0800: [GC (Allocation Failure) 2021-01-23T22:34:51.499-0800: [ParNew: 157247K->157247K(157248K), 0.0000257 secs]2021-01-23T22:34:51.499-0800: [CMS: 348556K->329665K(349568K), 0.0564189 secs] 505803K->329665K(506816K), [Metaspace: 2679K->2679K(1056768K)], 0.0565260 secs] [Times: user=0.06 sys=0.00, real=0.06 secs] 
2021-01-23T22:34:51.556-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 329665K(349568K)] 329869K(506816K), 0.0002231 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-23T22:34:51.556-0800: [CMS-concurrent-mark-start]
执行结束!共生成对象次数:8433
Heap
 par new generation   total 157248K, used 5766K [0x00000007a0000000, 0x00000007aaaa0000, 0x00000007aaaa0000)
  eden space 139776K,   4% used [0x00000007a0000000, 0x00000007a05a1980, 0x00000007a8880000)
  from space 17472K,   0% used [0x00000007a8880000, 0x00000007a8880000, 0x00000007a9990000)
  to   space 17472K,   0% used [0x00000007a9990000, 0x00000007a9990000, 0x00000007aaaa0000)
 concurrent mark-sweep generation total 349568K, used 329665K [0x00000007aaaa0000, 0x00000007c0000000, 0x00000007c0000000)
 Metaspace       used 2686K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 294K, capacity 386K, committed 512K, reserved 1048576K


* -XX:+UseConcMarkSweepGC -Xmx1g -Xms1g GCLogAnalysis -> Count: 9265
* -XX:+UseConcMarkSweepGC -Xmx2g -Xms2g GCLogAnalysis -> Count: 8207
* -XX:+UseConcMarkSweepGC -Xmx4g -Xms4g GCLogAnalysis -> Count: 8310
* -XX:+UseG1GC -Xmx512m -Xms512m GCLogAnalysis -> Count: 7930
* -XX:+UseG1GC -Xmx1g -Xms1g GCLogAnalysis -> Count: 8701
* -XX:+UseG1GC -Xmx2g -Xms2g GCLogAnalysis -> Count: 9161
* -XX:+UseG1GC -Xmx4g -Xms4g GCLogAnalysis -> Count: 9477

## gateway-server-0.0.1-SNAPSHOT

| GC                 | Xmx/Xms| Qps     |
|:-------------------|:-------|:--------|
| UseSerialGC        | 1g     | 12363   |
| UseSerialGC        | 2g     | 9702    |
| UseParallelGC      | 1g     | 12363   |
| UseParallelGC      | 2g     | 10694   |
| UseConcMarkSweepGC | 1g     | 10246   |
| UseConcMarkSweepGC | 2g     | 10792   |
| UseG1GC            | 1g     | 8913    |
| UseG1GC            | 2g     | 9161    |

wrk -c1000 -t500 -d10s http://localhost:8088/api/hello
* -XX:+UseSerialGC -Xmx1g -Xms1g -> 12363Qps
> Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    87.47ms   62.65ms 603.32ms   66.56%
    Req/Sec    24.11     17.52   550.00     91.18%
  124881 requests in 10.10s, 14.90MB read
  Socket errors: connect 0, read 81, write 1, timeout 0
Requests/sec:  12363.06
Transfer/sec:      1.48MB

* -XX:+UseSerialGC -Xmx2g -Xms2g -> 9702Qps
> Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   118.04ms  106.43ms 968.68ms   53.50%
    Req/Sec    20.07     22.66   485.00     91.97%
  97998 requests in 10.10s, 11.69MB read
  Socket errors: connect 0, read 224, write 10, timeout 0
Requests/sec:   9702.06
Transfer/sec:      1.16MB

* -XX:+UseParrallelGC -Xmx1g -Xms1g -> 12363Qps>
> Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    53.64ms   43.49ms 481.25ms   68.84%
    Req/Sec    32.99     25.87   454.00     86.63%
  113920 requests in 10.12s, 13.59MB read
  Socket errors: connect 0, read 1011, write 45, timeout 0
Requests/sec:  11260.05
Transfer/sec:      1.34MB

* -XX:+UseParrallelGC -Xmx2g -Xms2g -> 10694Qps
>  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   102.66ms   82.91ms 831.78ms   58.45%
    Req/Sec    21.46     20.18   535.00     91.76%
  108049 requests in 10.10s, 12.89MB read
  Socket errors: connect 0, read 176, write 8, timeout 0
Requests/sec:  10694.13
Transfer/sec:      1.28MB

* -XX:+UseConcMarkSweepGC -Xmx1g -Xms1g -> 10246Qps
>  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   110.84ms   86.75ms   1.01s    59.40%
    Req/Sec    19.62     18.80   484.00     90.35%
  103593 requests in 10.11s, 12.36MB read
Requests/sec:  10246.91
Transfer/sec:      1.22MB

* -XX:+UseConcMarkSweepGC -Xmx2g -Xms2g -> 10792Qps
>  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   100.23ms   77.74ms 693.21ms   58.57%
    Req/Sec    21.21     18.45   405.00     89.69%
  109200 requests in 10.12s, 13.03MB read
  Socket errors: connect 0, read 325, write 5, timeout 0
Requests/sec:  10792.45
Transfer/sec:      1.29MB

* -XX:+UseG1GC -Xmx1g -Xms1g -> 8913Qps
> Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   130.69ms  115.13ms 937.87ms   54.71%
    Req/Sec    18.23     20.25   380.00     91.00%
  90078 requests in 10.11s, 10.74MB read
  Socket errors: connect 0, read 32, write 1, timeout 0
Requests/sec:   8913.64
Transfer/sec:      1.06MB

* -XX:+UseG1GC -Xmx1g -Xms1g -> 8186Qps
>  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   111.97ms   95.70ms 996.71ms   58.63%
    Req/Sec    20.38     19.68   366.00     92.38%
  105207 requests in 10.10s, 12.55MB read
  Socket errors: connect 0, read 16, write 0, timeout 0
Requests/sec:  10415.26
Transfer/sec:      1.24MB