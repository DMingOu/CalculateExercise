package com.odm.calculatepractice.util

import com.odm.calculatepractice.Constant
import com.odm.calculatepractice.util.CalculateUtil.getDenominator
import com.odm.calculatepractice.util.CalculateUtil.getNumerator
import com.odm.calculatepractice.util.CalculateUtil.isProperFraction
import com.odm.calculatepractice.util.CalculateUtil.reduction
import com.odm.calculatepractice.util.CalculateUtil.switchToCommonDenominator

/***********************************************************
 * 运算符
 */
enum class OperatorEnum(
    /**
     * field
     * 运算符标志
     */
    val opSymbol: String,
    /**
     * 运算符对应索引
     */
    var opValue: Int, priority: Int
) : OperationService {
    /**
     * 加法
     */
    ADD(Constant.PLUS, 0, 2) {
        override fun op(num1: String?, num2: String?): String? {
            //通分
            val numbers = switchToCommonDenominator(
                num1!!,
                num2!!
            )
            //获取分母分子
            val denominator = getDenominator(numbers[0]!!)
            val numerator = getNumerator(numbers[0]!!) + getNumerator(
                numbers[1]!!
            )
            return reduction(numerator, denominator)
        }
    },
    MINUS(Constant.MINUS, 1, 2) {
        override fun op(num1: String?, num2: String?): String? {
            //通分
            val numbers = switchToCommonDenominator(
                num1!!,
                num2!!
            )
            //获取分母分子
            val denominator = getDenominator(numbers[0]!!)
            val numerator = getNumerator(numbers[0]!!) - getNumerator(
                numbers[1]!!
            )
            //出现负数返回null
            return if (numerator < 0) {
                null
            } else {
                reduction(numerator, denominator)
            }
        }
    },
    MULTIPLY(Constant.MULTIPLY, 2, 1) {
        override fun op(num1: String?, num2: String?): String? {
            //获取分母 与 分子
            val denominator1 = getDenominator(num1!!)
            val numerator1 = getNumerator(num1)
            val denominator2 = getDenominator(num2!!)
            val numerator2 = getNumerator(num2)
            return reduction(numerator1 * numerator2, denominator1 * denominator2)
        }
    },
    DIVIDE(Constant.DIVIDE, 3, 1) {
        override fun op(num1: String?, num2: String?): String? {

            //获取分母分子
            val denominator2 = getDenominator(num2!!)
            val numerator2 = getNumerator(num2)
            //被除数不为0
            if (numerator2 == 0) {
                return null
            }
            //获取倒数
            val reciprocal = "$denominator2/$numerator2"
            //做乘法
            val result = MULTIPLY.op(num1, reciprocal)
            //如果不是真分数返回Null
            return if (isProperFraction(reciprocal)) {
                result
            } else {
                null
            }
        }
    };
    //methods

    /**
     * 运算符优先级，数字小则计算的时候优先级高
     */
    val priority: Int

    companion object {
        /**
         * 通过索引获取枚举
         *
         * @param opNum 操作符索引
         * @return OperatorEnum
         */
        fun getEnumByOpValue(opNum: Int): OperatorEnum? {
            for (operatorEnum in values()) {
                if (operatorEnum.opValue == opNum) {
                    return operatorEnum
                }
            }
            return null
        }

        /**
         * 通过运算符标志获取枚举
         *
         * @param opSymbol 运算符标志
         * @return OperatorEnum
         */
        fun getEnumByOpSymbol(opSymbol: String?): OperatorEnum? {
            for (operatorEnum in values()) {
                if (operatorEnum.opSymbol.equals(opSymbol, ignoreCase = true)) {
                    return operatorEnum
                }
            }
            return null
        }
    }

    /**
     * constructor
     */
    init {
        opValue = opValue
        this.priority = priority
    }
}