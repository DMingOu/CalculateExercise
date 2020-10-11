package com.odm.calculatepractice.util

import android.os.Message
import com.odm.calculatepractice.View.QuestionListFragment.QuestionListHandler
import com.odm.calculatepractice.bean.Exercise
import com.odm.calculatepractice.util.CalculationExerciseUtil.clear
import com.odm.calculatepractice.util.CalculationExerciseUtil.generateExercises
import com.odm.calculatepractice.util.CalculationExerciseUtil.getExerciseOnCount
import com.odm.calculatepractice.util.GlobalExerciseListHolder.updateGlobalExerciseList
import com.odm.calculatepractice.util.OtherUtil.getRunTime
import com.odm.calculatepractice.util.ThreadPoolUtil.execute
import java.util.concurrent.CountDownLatch

class ExerciseProducer {

    private lateinit var countDownLatch: CountDownLatch

    private val tempExerciseList = mutableListOf<Exercise>()

    private var startIndex : Int = 0

    /**
     * 执行生成题目功能
     */
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
            println("生成题目总数：" + exerciseNum + "  总耗时：" + getRunTime(start))
        }
    }


    /**
     * 用于执行生成题目的任务,输出到全局变量
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
                println("创建题目工作线程" + workerIndex + " 生成题目数：" + exerciseNum + " 耗时：" + getRunTime(start))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    companion object {
        //记录总耗时
        private var start: Long = 0
    }
}