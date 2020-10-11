# CalculateExercise
## 软工结对编程项目-小学生四则计算练习工具项目配套安卓APP

| **软件工程** | [这就是链接](https://edu.cnblogs.com/campus/gdgy/informationsecurity1812/) |
| :----------: | :----------------------------------------------------------: |
|   作业要求   | [这就是链接](https://edu.cnblogs.com/campus/gdgy/informationsecurity1812/homework/11157) |
|   作业目标   |              熟悉在未结对情况下如何结对开发项目              |

## Github与合作者

合作者（学号）：

- 区德明：318005422
- 虚左以待

Github链接：
https://github.com/DMingOu/CalculateExercise

由于本来的合作者临时有事不能和我一起结对编程，所以我也只好咬咬牙单刀赴会了。

因为本次的题目要求有图形化界面，而我也学过移动开发的，所以一不做二不休，直接提刀杀进AndroidStudio，开始~~秃头~~设计一款小学生也能用的四则计算题练习工具App，一来可以简化方便操作和展示，二来也算是在众多.exe中里面可以独树一帜。


## 一、PSP

| PSP2.1                                  | Personal Software Process Stages        | 预估耗时 （分钟） | 实际耗时 （分钟） |
| :-------------------------------------- | :-------------------------------------- | ----------------- | ----------------- |
| Planning                                | **计划**                                | 25                | 38                |
| · Estimate                              | · 估计这个任务需要多少时间              | 25                | 38                |
| Development                             | **开发**                                | 945               | 830               |
| · Analysis                              | · 需求分析 (包括学习新技术)             | 75                | 130               |
| · Design Spec                           | · 生成设计文档                          | 100               | 80                |
| · Design Review                         | · 设计复审                              | 60                | 35                |
| · Coding Standard                       | · 代码规范 (为目前的开发制定合适的规范) | 30                | 60                |
| · Design                                | · 具体设计                              | 120               | 110               |
| · Coding                                | · 具体编码                              | 260               | 260               |
| · Code Review                           | · 代码复审                              | 60                | 45                |
| · Test                                  | · 测试 (自我测试，修改代码，提交修改)   | 95                | 110               |
| Reporting                               | **报告**                                | 120               | 105               |
| · Test Repor                            | · 测试报告                              | 60                | 50                |
| · Size Measurement                      | · 计算工作量                            | 30                | 30                |
| · Postmortem & Process Improvement Plan | · 事后总结, 并提出过程改进计划          | 30                | 25                |
|                                         | · 合计                                  | 1090              | 973               |

## 二、四则运算练习工具APP需求分析

| 需求描述                                                     | 是否实现 |
| ------------------------------------------------------------ | -------- |
| 控制生成题目的个数                                           | 是       |
| 控制题目中数值范围                                           | 是       |
| 计算过程不能产生负数，除法的结果必须是真分数，题目不能重复，运算符不能超过3个 | 是       |
| 显示题目                                                     | 是       |
| 显示答案                                                     | 是       |
| 能支持一万道题目的生成                                       | 是       |
| 判定答案中的对错并进行数量统计                               | 是       |
| 显示得分结果                                                 | 是       |
| 具有图形化的操作界面                                         | 是       |



## 三、APP开发计划

| 功能               | 描述                                             | 开发者 | 进度 |
| ------------------ | ------------------------------------------------ | ------ | ---- |
| 生成题目           | 随机生成操作数和运算符，组成有效的四则运算表达式 | 区德明 | 完成 |
| 计算结果           | 根据生成的表达式，计算生成正确的结果             | 区德明 | 完成 |
| 练习报告           | 输出成绩结果                                     | 区德明 | 完成 |
| UI界面设计         | 设计软件的界面设计XML                            | 区德明 | 完成 |
| UI界面实现         | 使用Kotlin语言实现 Andoid APP                    | 区德明 | 完成 |
| 功能测试与故障修复 | 测试程序的功能，修复出现的故障                   | 区德明 | 完成 |
| 性能分析与优化     | 分析程序执行的性能，优化性能表现                 | 区德明 | 完成 |

#### 结构图

![image-20201010222426647](http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201010222426647.png)



![image-20201010222450625](http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201010222450625.png)



## 四、方案

### 4.1 生成题目的算法设计思路

线程池多线程并发，创建指定数量的题目，并利用Set集合进行去重操作。

核心代码如下：

执行生成题目的线程任务类

```kotlin
    /**
     * 执行生成指定数量题目任务
     * 线程类
     */
    private class GenerateWorker(
        private val exerciseNum: Int, //生成数量
        private val numRange: Int, //范围上限
        private val workerIndex: Int,  //工作线程的编号
        private val countDownLatch: CountDownLatch,
        private val tempList : MutableList<Exercise>
    ) : Runnable {
        override fun run() {
            try {
                val start = System.currentTimeMillis()
                val exerciseList = generateExercises(
                    exerciseNum, numRange
                )
                //将 去重但是未编号的 列表 加入缓冲列表中
                tempList.addAll(exerciseList)
                countDownLatch.countDown()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
```

执行完善收尾操作的线程任务：

```kotlin
    /**
     * 用于补充题目序号， 完善，的任务
     */
    private class SupplyWorker(
        private val exerciseNum: Int,
        private val countDownLatch: CountDownLatch,
        private val tempList : MutableList<Exercise> ,
        private var startIndex : Int ,
        private val mHandler: QuestionListHandler   //回调UI线程
    ) : Runnable {
        override fun run() {
            getExerciseOnCount(exerciseNum)
            countDownLatch.countDown()
            //根据已有的列表数据，进行编号
            for(exercise in tempList) {
                startIndex++
                exercise.number = startIndex
            }
            updateGlobalExerciseList(tempList)
            //通知 View层更新内容
            val message = Message()
            message.what = 666
            mHandler.dispatchMessage(message)
        }
    }
```

提供给外界调用的入口：

```kotlin
fun produce(exerciseNumber: Int, numberRange: Int,startIndex : Int, handler: QuestionListHandler) {
            if (exerciseNumber == 0 || numberRange == 0) {
                return
            }
            //最小范围是2
            if (numberRange < 2) {
                throw RuntimeException("范围上限不可以少于2")
            }
            //手工计时器启动
            start = System.currentTimeMillis()

            //设置本次加载的起始序号
            this.startIndex  = startIndex

            //每个线程的工作量
            val workload = 25
            //记录线程编号
            var index = 1
            //剩余工作量
            var remainWorkload = exerciseNumber
            //启动创建题目的线程组
            val threadCount = if (exerciseNumber % workload == 0)
                exerciseNumber / workload
            else
                exerciseNumber / workload + 1
            //生成线程+一个输出线程
            countDownLatch = CountDownLatch(threadCount + 1)

            //任务正式启动前。清空缓存列表,避免脏数据
            tempExerciseList.clear()

            while (true) {
                //执行生成题目的任务线程，编号对应
                remainWorkload -= if (remainWorkload > workload) {
                    execute(
                        GenerateWorker(
                            workload,
                            numberRange,
                            index++,
                            countDownLatch,
                            tempExerciseList
                        )
                    )
                    workload
                } else {
                    execute(
                        GenerateWorker(
                            remainWorkload,
                            numberRange,
                            index,
                            countDownLatch,
                            tempExerciseList
                        )
                    )
                    break
                }
            }
            //启动完善每一道题的任务
            execute(SupplyWorker(exerciseNumber, countDownLatch  ,tempExerciseList ,startIndex  , handler))
            //数据创建完毕，重置状态
            countDownLatch.await()
            clear()
    }
```

生成题目的操作，实际执行：

```kotlin
    @JvmStatic
    fun generateExercises(exercisesNum: Int, numRange: Int  , startIndex : Int = -1): List<Exercise> {
        //控制编号
        var index = startIndex
        //如果没有传入编号则视为生成时不做编号处理
        val isSetIndex = index!=-1
        //控制生成循环的结束
        var count = 0
        val exerciseList: MutableList<Exercise> = ArrayList()
        while (count < exercisesNum) {
            val exercise = generateQuestion(numRange)
            generateAnswer(exercise)
            //有效题目加入List
            if (validate(exercise)) {
                count++
                if(isSetIndex) {
                    index++
                    //设置序号
                    exercise.number = index
                }
                //生成可以输出的题目样式
                EXERCISE_QUEUE.add(exercise)
                exerciseList.add(exercise)
                //放入去重Set，用作判断
                exercisesSet.add(exercise.simplestFormatQuestion)
            }
        }
        return exerciseList
    }
```



### 4.2 客户端（用户入口）的设计思路

采用单Activity+多Fragment的架构。

- util包提供算法和数据源的能力
- View包承载页面UI的显示，
- bean包存放实体类信息
- widget包存放的是一些自定义控件

![image-20201011122020373](http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011122020373.png)



## 五、效能分析

通过大厂滴滴的开源性能工具平台，**DoKit**，接入App，进行性能的进一步分析，如下：

### 5.1 程序效能

使用APP的出题功能，分别生成1000道，2000道，10000道题目不同的消耗内存情况

- 生成1000道题目时，只需要130MB
- 生成2000道题目时，只需要155MB
- 生成10000道题目的时候，就需要1005MB了

可以认为题目的数量影响着App的运行时所消耗的资源及内存，并随着题目的数量成正比例关系。

<img src="http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201010231808068.png" alt="image-20201010231808068" width="40%" height="40%" />      <img src="http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201010231844289.png" alt="image-20201010231844289" width="40%" height="40%" /> 

<img src="http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201010231855561.png" alt="image-20201010231855561" width="40%" height="40%" /> 

cpu消耗如下：

![image-20201011001304759](http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011001304759.png)



页面打开跳转的时长：

![image-20201011001331114](http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011001331114.png)



### 5.2  性能优化

#### 首轮优化

![image-20201011010219147](http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011010219147.png)

可以看出线程池的线程配置对于大数据量非常重要，从生成10000条题目为例子，在4个线程在并发的创建对象的时候，因为设置的单个线程任务量为2000，无法完成剩下的2000任务，只能等4个线程任务都完成后再创建新线程执行任务。

##### 解决方案：调整线程池的配置，提高线程的数量，提高每个生成题目的线程的任务数量

##### 优化后的结果：

线程池复用线程，分页加载50条数据，逐步的加载出全部的数据，分散内存和CPU的密集消耗。

优化后的生成10000条数据，耗时从10秒提高到了7秒，提升了30%。如果继续对线程池的配置进行优化，效率还会继续提高。

![image-20201011010812858](http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011010812858.png)



进一步优化

#### 但是对于移动应用来说，加载时间的耗时始终是大问题，让用户觉得等待时间过长，同时会有卡顿的问题，亟待解决，所以下面要通过性能分析工具，分析哪个步骤流程才是在生成题目列表的过程中造成卡顿呢？？通过DoKit的卡顿堆栈分析图：

![image-20201011001457567](http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011001457567.png)



![image-20201011001632545](http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011001632545.png)



可以看出卡顿 与 打开页面，创建对应数据，数据创建后，列表UI开始加载数据， 频繁大量地创建View对象息息相关。

##### 进一步解决方案：分页加载

数据量过大的时候，其实不需要一口气全部加载进去，可以采用分页加载的方式加载与创建对象，吞吐量减少的同时CPU和内存的占用也降低了。从另一个角度讲，页面也无必要一口气加载上千条乃至万条的数据 ~~( 屏幕就这么大 ,用户看不过来)~~

设定分页加载题目的方式，每页固定数量为 50 ，若所需数量不足50条，则继续加载所需的数量题目。

打开性能检测平台的监控 CPU，内存  ，帧率

**未加载数据前：**

<img src="http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011104239338.png" alt="image-20201011104239338" width="40%" height="40%" />

**打开页面，加载10000条数据：**

<img src="http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011104308452.png" alt="image-20201011104308452" width="40%" height="40%" />        <img src="http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011104425338.png" alt="image-20201011104425338" width="40%" height="40%" />



可以看出，优化后，加载大数据，对页面的影响小了很多，CPU和内存的短时间消耗急剧降低，对App这种只拥有固定内存（少量）的进程，可以有很不错的效果。



## 六、APP真机测试报告

### 6.1 测试1：程序生成四则计算题和答案是否符合题目要求

<img src="http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/2020-10-11_12-4-36.PNG" alt="2020-10-11_12-4-36" width="38%" height="38%" />



**结果说明：**

以上为测试生成100道数值10以内的题目的截图

可以得出结论，看到题目是符合去重的要求且答案是正确的



### 6.2  测试2：程序是否能正确判断用户输入答案的正确性

以 6 道题目为例子，查看练习情况报告，可以看到可以给出正确答案提示，以及完成的情况，错对情况，得分率，很详细。

<img src="http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011120046984.png" alt="image-20201011120046984" width="40%" height="40%" />

### 6.3 测试3：能否支持一万道题目的生成与显示

在出题模式下执行生成10000道数值范围在5以内的题目的功能

<img src="http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011115726814.png" alt="image-20201011115726814" width="40%" height="40%" />      <img src="http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011115734721.png" alt="image-20201011115734721" width="40%" height="40%" />



###  最终效果

| <img src="http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011122643614.png" alt="image-20201011122643614"  /> | <img src="http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011122651433.png" alt="image-20201011122651433" /> |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
|                           **首页**                           |                         **题目列表**                         |
| <img src="http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011133555193.png" alt="image-20201011133555193"  /> | <img src="http://picbed-dmingou.oss-cn-shenzhen.aliyuncs.com/img/image-20201011133603583.png" alt="image-20201011133603583" /> |
|                         **练习做题**                         |                       **练习情况报告**                       |

## 七、总结

### 项目小结

**获得的经验：**

虽说只是做了一个小工程，从中也学习到了多线程的相关知识，数据结构的使用和APP界面的设计与布局。

**不足的地方**:

没有做查看历史做题的历史记录。

生成大批量的题目的时候，会随着页面加载越来越多的题目，随着RecyclerView持有的子itemView数量逐渐增多会导致分页加载，RecyclerView时会有卡顿感。

### 结对感受

期待ing，个人感觉是各司其职，对方请教时再给建议，要结合实际情况，不过分追求效果。


