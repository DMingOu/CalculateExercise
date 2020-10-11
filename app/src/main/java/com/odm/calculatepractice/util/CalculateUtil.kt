package com.odm.calculatepractice.util

import com.odm.calculatepractice.Constant
import com.odm.calculatepractice.bean.Exercise
import com.odm.calculatepractice.util.OtherUtil.getStr
import com.odm.calculatepractice.util.OtherUtil.searchStr
import java.util.*

/**
 * 用于处理计算
 */
object CalculateUtil {
    /*    //用于测试增加的 main 方法
    public static void main(String[] args) {
        String s = getImproperFraction("1'1/2");
        System.out.println(s);
        int n = getNumerator("1'1/2");
        System.out.println(n);
        int d = getDenominator("1'1/2");
        System.out.println(d);
        String[] str = switchToCommonDenominator("1'1/2", "2/3");
        System.out.println(str[0] + "\n" + str[1]);
    }*/
    /**
     * 将带分数数化成假分数形式,如过传入的不是带分数，则不做改动，原数返回
     *
     * @param operand 操作数
     * @return 假分数
     */
    fun getImproperFraction(operand: String): String {
        //检测是否为真分数
        return if (searchStr("'", operand) != 0) {
            val str = operand.split("['/]".toRegex()).toTypedArray()
            //分子
            val numerator = str[0].toInt() * str[2].toInt() + str[1].toInt()

            //分母
            val denominator = str[2].toInt()
            "$numerator/$denominator"
        } else {
            operand
        }
    }

    /**
     * 获取分数的分子
     *
     * @param num 分数
     * @return 分子
     */
    fun getNumerator(num: String): Int {
        if ("" == num) {
            return 0
        }
        val numerator: Int
        val str: Array<String>
        //如果是带分数,则获取的是转换为假分数之后的分子
        if (searchStr("'", num) > 0) {
            str = num.split("['/]".toRegex()).toTypedArray()
            numerator = str[0].toInt() * str[2].toInt() + str[1].toInt()
            return numerator
        } else if (searchStr("/", num) > 0) {
            str = num.split("/".toRegex()).toTypedArray()
            numerator = str[0].toInt()
            return numerator
        }
        //如果是整数，则原数返回
        return num.toInt()
    }

    /**
     * 获取分数的分母
     *
     * @param num 分数
     * @return 分母
     */
    fun getDenominator(num: String): Int {
        val denominator: Int
        val str: Array<String>

        //如果是带分数
        if (searchStr("'", num) > 0) { //如果传入的是带分数
            str = num.split("['/]".toRegex()).toTypedArray()
            denominator = str[2].toInt()
            return denominator
        } else if (searchStr("/", num) > 0) { //如果传入的是分数
            str = num.split("/".toRegex()).toTypedArray()
            denominator = str[1].toInt()
            return denominator
        }
        //如果是整数
        return 1
    }

    /**
     * 将两数通分
     *
     * @param num1 需通分数字一
     * @param num2 需通分数字二
     * @return 通分后 str[0]对应num_1, str[1]对应num_2
     */
    fun switchToCommonDenominator(num_1: String , num_2: String): Array<String?> {
        //将分数转换成假分数形式
        var num1 = num_1
        var num2 = num_2
        num1 = getImproperFraction(num1)
        num2 = getImproperFraction(num2)
        val str = arrayOfNulls<String>(2)
        val denominator_1: Int
        val denominator_2: Int
        val numerator_1: Int
        val numerator_2: Int
        //如果a为分数
        return if (searchStr("/", num1) > 0) {
            //如果a为带分数，则转换为假分数
            if (searchStr("'", num1) > 0) {
                num1 = getImproperFraction(num1)
            }

            //如果a、b为分数
            if (searchStr("/", num2) > 0) {
                //如果b为带分数，则转换为假分数
                if (searchStr("'", num2) > 0) {
                    num2 = getImproperFraction(num2)
                }
                //获取分母
                denominator_1 = getDenominator(num1)
                denominator_2 = getDenominator(num2)
                //如果a、b分母相同则不做处理
                if (denominator_1 == denominator_2) {
                    str[0] = num1
                    str[1] = num2
                    return str
                }
                //获取分子
                numerator_1 = getNumerator(num1)
                numerator_2 = getNumerator(num2)
                //通分处理
                str[0] =
                    (numerator_1 * denominator_2).toString() + "/" + denominator_1 * denominator_2
                str[1] =
                    (numerator_2 * denominator_1).toString() + "/" + denominator_1 * denominator_2
                return str
            } else {
                num1 = getImproperFraction(num1)
                denominator_1 = getDenominator(num1)
                num2 =
                    (num2.toInt() * denominator_1).toString() + "/" + num2.toInt() * denominator_1
                str[0] = num1
                str[1] = num2
            }
            str
        } else {
            //如果a为整数，b为分数
            if (searchStr("/", num2) > 0) {
                num2 = getImproperFraction(num2)
                denominator_2 = getDenominator(num2)
                num1 = (num1.toInt() * denominator_2).toString() + "/" + denominator_2
            }
            //如果a、b都为整数
            str[0] = num1
            str[1] = num2
            str
        }
    }

    /**
     * 求两数的最大公约数
     *
     * @param numerator   分子
     * @param denominator 分母
     * @return 最大公约数
     */
    fun getMaxCommonDivisor(numerator: Int, denominator: Int): Int {
        //辗转相除法
        var numerator = numerator
        var denominator = denominator
        while (numerator * denominator != 0) { //任意一个为0时终止循环
            if (numerator > denominator) {
                numerator %= denominator
            } else if (numerator < denominator) {
                denominator %= numerator
            } else {
                return numerator
            }
        }
        return Math.max(numerator, denominator)
    }

    /**
     * 随机生成指定范围内数字
     *
     * @param min 下界，可取
     * @param max 上界，可取
     * @return 随机生成数
     */
    fun getRandomNum(min: Int, max: Int): Int {
        val random = Random()
        return random.nextInt(max) % (max - min + 1) + min
    }

    /**
     * 工具方法： 将一个分数约分
     *
     * @return 分数约分后的结果
     */
    fun reduction(numerator: Int, denominator: Int): String {
        var numerator = numerator
        var denominator = denominator
        if (numerator * denominator == 0) {
            return 0.toString()
        }
        val maxCommonDivisor = getMaxCommonDivisor(numerator, denominator)
        //上下都可整除
        if (numerator % maxCommonDivisor == 0 && denominator % maxCommonDivisor == 0) {
            numerator /= maxCommonDivisor
            denominator /= maxCommonDivisor
        }
        //如果分母为1，返回整数
        return if (denominator == 1) {
            numerator.toString()
        } else "$numerator/$denominator"
    }

    /**
     * 工具方法 ： 判断是否真分数
     *
     * @return 分数字符串是否真分数
     */
    fun isProperFraction(number: String): Boolean {
        //获取分母分子
        val denominator = getDenominator(number)
        val numerator = getNumerator(number)
        return numerator < denominator
    }

    /**
     * 从一个(e1+e2)形式的字符串中解析出 e1 + e2
     * e1和e2可以是子表达式
     *
     * @param expressionString 完整表达式
     * @return 元素1 符号 元素2 组成的字符串数组
     */
    private fun getOperatorAndNumber(expressionString: String): Array<String> {
        //去除最外层括号
        var expression = expressionString
        expression = expression.replaceFirst("\\(".toRegex(), "")
            .substring(0, expression.lastIndexOf(")") - 1)
        var num1: String? = null
        var num2: String? = null
        val operator =
            "(\\" + Constant.PLUS + "" + Constant.MINUS + "|\\" + Constant.MULTIPLY + "\\" + Constant.DIVIDE + ")"
        //三运算符表达式
        val operatorNum = searchStr(operator, expression)
        val reg: String
        reg = if (operatorNum == 3) {
            "\\)$operator\\("
        } else if (operatorNum == 2) {
            "$operator\\("
        } else {
            operator
        }
        num1 = expression.split(reg.toRegex()).toTypedArray()[0]
        num2 = expression.split(reg.toRegex()).toTypedArray()[1]
        if (isExpression(num1)) {
            num1 = "$num1)"
        }
        if (isExpression(num2)) {
            num2 = "($num2"
        }
        //必须先移除num2,因为num2可以是表达式，num1可能是num2的一部分
        val op = expression.substring(num1.length, num1.length + 1)
        return arrayOf(num1, op, num2)
    }

    /**
     * 工具方法：判断是否是表达式
     *
     * @return 是否是表达式
     */
    fun isExpression(str: String): Boolean {
        //如果有括号就是表达式
        return str.contains("(") || str.contains(")")
    }

    /**
     * 工具方法：将字符串解析成Exercises对象
     *
     * @param question 问题字符串
     * @return  Exercises对象
     */
    fun parseExercises(question: String): Exercise {
        val exercises = Exercise()
        //将题目解析成 Exercises 对象的属性
        parseQuestion(exercises, question)
        //解析题目中的题号
        parseNumber(exercises, question)
        return exercises
    }

    /**
     * 工具方法：返回不带题号的答案
     * @param answer 答案
     * @return 不带题号的答案
     */
    fun parseAnswer(answer: String): String {
        return answer.split("\\.".toRegex()).toTypedArray()[1]
    }

    /**
     * 将题目解析成Exercises对象的属性
     *
     * @param question 问题字符串
     */
    private fun parseQuestion(exercise: Exercise, question: String) {
        var expression = question.split("\\.".toRegex()).toTypedArray()[1].replace("\\=", "")
        val queue: Queue<String> = LinkedList()
        val valueList = ArrayList<String>()
        queue.add(expression)
        while (true) {
            expression = queue.remove()
            //如果是运算符则继续解析
            if (isExpression(expression)) {
                val operatorAndNumber = getOperatorAndNumber(expression)
                //保存运算符
                valueList.add(operatorAndNumber[1])
                //将操作时或者子表达式入队
                queue.add(operatorAndNumber[2])
                queue.add(operatorAndNumber[0])
            } else {
                //还没加入操作数时valuelist的大小时运算符个数
                exercise.operatorNumber = valueList.size
                //第一个元素已经被remove所以单独add
                valueList.add(expression)
                valueList.addAll(queue)
                exercise.valueList = valueList
                break
            }
        }
    }

    /**
     * 解析获取题目中的题号
     *
     * @param exercise 题目信息实体类
     * @param question 问题字符串
     */
    private fun parseNumber(exercise: Exercise, question: String) {
        exercise.number = question.split("\\.".toRegex()).toTypedArray()[0].toInt()
    }

    /**
     * 返回与一个表达式等价的最简状态的式子
     * @return  一个表达式等价的最简状态的式子
     */
    fun getEqualsExpression(expression: String): String {
        val operator =
            "(\\" + Constant.PLUS + "|\\" + Constant.MINUS + "|\\" + Constant.MULTIPLY + "|\\" + Constant.DIVIDE + ")"
        //操作数交换
        return expression.split(operator.toRegex()).toTypedArray()[1] + getStr(
            operator,
            expression
        ) + expression.split(operator.toRegex()).toTypedArray()[0]
    }
}