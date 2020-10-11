package com.odm.calculatepractice.util

import java.util.regex.Pattern

object OtherUtil {
    /**
     * 返回运行耗时信息
     *
     * @param begin 开始执行的时间（时间戳毫秒数）
     * @return
     */
    @JvmStatic
    fun getRunTime(begin: Long): String {
        val time = System.currentTimeMillis() - begin
        return if (time < 1000) {
            time.toString() + "ms"
        } else {
            time.div(1000).toString() + "s"
        }
    }

    /**
     * 字符串匹配
     * @param REGEX 需要查找字符串
     * @param INPUT 被查找的字符串
     * @return num 查找到的数量
     */
    @JvmStatic
    fun searchStr(REGEX: String?, INPUT: String?): Int {
        var num = 0
        val p = Pattern.compile(REGEX)
        val m = p.matcher(INPUT)
        while (m.find()) {
            num++
        }
        return num
    }

    /**
     * 返回查找的字符串（第一个）
     * @param REGEX 需要查找字符串
     * @param INPUT 被查找的字符串
     */
    @JvmStatic
    fun getStr(REGEX: String?, INPUT: String?): String {
        val p = Pattern.compile(REGEX)
        val m = p.matcher(INPUT)
        m.find()
        return m.group()
    }
}