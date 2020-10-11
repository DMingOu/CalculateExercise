package com.odm.calculatepractice.util

import com.odm.calculatepractice.bean.Exercise
import com.odm.calculatepractice.util.OperatorEnum.Companion.getEnumByOpSymbol
import com.odm.calculatepractice.util.OperatorEnum.Companion.getEnumByOpValue
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.locks.ReentrantLock

/**
 * @author ODM
 */
object CalculationExerciseUtil {
    //并发队列，用来存放题目
    private val EXERCISE_QUEUE: Queue<Exercise> = ConcurrentLinkedQueue()

    //并发集合，用来校验题目是否重复
    private val exercisesSet: MutableSet<String> = CopyOnWriteArraySet()

    //并发锁，用于控制线程执行顺序
    private val reentrantLock = ReentrantLock()

    private val isFull = reentrantLock.newCondition()

    /**
     * 清空容器
     */
    @JvmStatic
    fun clear() {
        EXERCISE_QUEUE.clear()
        exercisesSet.clear()
    }

    /**
     * 获取指定数量的完整问题
     * @param exercisesNum
     */
    @JvmStatic
    fun getExerciseOnCount(exercisesNum: Int) {
        var count = 0
        while (count < exercisesNum) {
            val exercises = EXERCISE_QUEUE.peek() ?: continue
            //设置题号
            count++
            exercises.number = count
            EXERCISE_QUEUE.remove()
        }
        clear()
    }

    /**
     * 返回一条可用的非重复的练习题目
     * @param rangeMax 范围上限
     * @return 一条非重复的练习题目实体类
     */
    fun generateExercise(rangeMax: Int): Exercise {
        val exercise = generateQuestion(rangeMax)
        generateAnswer(exercise)
        return if (validate(exercise)) {
            //放入集合
            exercisesSet.add(exercise.simplestFormatQuestion)
            //最简式
            exercisesSet.add(CalculateUtil.getEqualsExpression(exercise.simplestFormatQuestion))
            exercise
        } else {
            generateExercise(rangeMax)
        }
    }

    /**
     * 生成指定数目包含答案的有效题目
     *
     * @param exercisesNum  题目的数量
     * @param numRange 范围上限
     * @param startIndex 题目的起始编号
     */
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
                //放入集合
                exercisesSet.add(exercise.simplestFormatQuestion)
                //最简式
//                exercisesSet.add(CalculateUtil.getEqualsExpression(exercise.simplestFormatQuestion))
            }
        }
        return exerciseList
    }

    /**
     * 检查题目是否有效：是否重复，数值合理
     *
     * @param exercise
     * @return
     */
    private fun validate(exercise: Exercise): Boolean {
        //如果计算结果为null说明计算过程出现负数或者假分数，不符合要求
        return if (exercise.answer == null) {
            false
        } else {
            !exercisesSet.contains(exercise.simplestFormatQuestion)
        }
    }

    /**
     * 生成答案
     */
    private fun generateAnswer(e: Exercise) {
        val queue: Queue<String> = LinkedList()
        val eValueList = e.valueList

        //将所有运算数进队列
        for (i in 2 * e.operatorNumber downTo e.operatorNumber - 1 + 1) {
            queue.add(eValueList[i])
        }

        //取出每个运算符,再从队列取出两个数字进行运算，结果再放入队尾中，直到取完所有运算符，此时队列中的数字为最终答案
        for (i in e.operatorNumber - 1 downTo 0) {
            val opSymbol = eValueList[i]

            //从队列取出两个数字
            val num1 = queue.remove()
            val num2 = queue.remove()
            //计算两数运算后结果
            val answer = getEnumByOpSymbol(opSymbol)!!.op(num1, num2)

            //计算过程出现不符合条件的数值，就返回null
            if (answer == null) {
                e.answer = null
                return
            }
            queue.add(answer)
        }
        e.answer = queue.remove()
    }

    /**
     * 生成运算式
     *
     * @return
     */
    private fun generateQuestion(numRange: Int): Exercise {
        val e = Exercise()
        //随机运算符数量
        val signalNum = CalculateUtil.getRandomNum(1, 3)
        e.operatorNumber = signalNum
        for (i in 0 until e.operatorNumber) {
            //添加运算符
            e.addValue(i, generateOperator())
        }
        for (i in e.operatorNumber until 2 * e.operatorNumber + 1) {
            //添加运算数
            e.addValue(i, generateNum(numRange))
        }
        return e
    }

    /**
     * 生成运算符
     *
     * @return
     */
    private fun generateOperator(): String {
        val random = Random()
        //随机生成操作符索引
        val opNum = random.nextInt(4)
        val operatorEnum = getEnumByOpValue(opNum)
        return operatorEnum?.opSymbol ?: throw RuntimeException("不存在下表标对应的运算符")
    }

    /**
     * 生成运算数
     *
     * @param numRange 随机生成数的范围
     * @return
     */
    private fun generateNum(numRange: Int): String {
        val random = Random()
        //分子
        var numerator = 0
        //分母
        var denominator = 0


        //如果分母为0则重新生成
        while (denominator == 0 || numerator / denominator.toFloat() > numRange) {
            denominator = random.nextInt(10)
            numerator = random.nextInt(10)
        }

        //如果为整数
        return if (numerator % denominator == 0) {
            (numerator / denominator).toString()
        } else {
            //如果为假分数，则转换成带分数
            val l: Int
            //化为带分数之后新的分子
            val newNumerator: Int
            if (numerator > denominator) {
                //约分
                //获取最大公约数
                val maxCommonDivisor = CalculateUtil.getMaxCommonDivisor(numerator, denominator)
                if (maxCommonDivisor != 1) {
                    numerator /= maxCommonDivisor
                    denominator /= maxCommonDivisor
                }
                //计算新分子和带数
                newNumerator = numerator % denominator
                l = (numerator - newNumerator) / denominator
                "$l'$newNumerator/$denominator"
            } else {
                //约分
                //获取最大公约数
                val maxCommonDivisor = CalculateUtil.getMaxCommonDivisor(numerator, denominator)
                if (maxCommonDivisor != 1) {
                    numerator /= maxCommonDivisor
                    denominator /= maxCommonDivisor
                }
                "$numerator/$denominator"
            }
        }
    }
}