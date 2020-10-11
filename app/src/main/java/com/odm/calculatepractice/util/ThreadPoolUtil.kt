package com.odm.calculatepractice.util

import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 线程池执行者工具类
 */
object ThreadPoolUtil {
    /**
     * 核心线程数（默认线程数）
     */
    private const val CORE_POOL_SIZE = 5

    /**
     * 最大线程数
     */
    private const val MAX_POOL_SIZE = 5

    /**
     * 允许线程空闲时间（单位：默认为秒）
     */
    private const val KEEP_ALIVE_TIME = 10

    /**
     * 缓冲队列大小
     */
    private const val QUEUE_CAPACITY = 20000

    private val executor = ThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAX_POOL_SIZE,
        KEEP_ALIVE_TIME.toLong(),
        TimeUnit.SECONDS,
        LinkedBlockingDeque(QUEUE_CAPACITY)
    )

    @JvmStatic
    fun execute(runnable: Runnable?) {
        executor.execute(runnable)
    }
}