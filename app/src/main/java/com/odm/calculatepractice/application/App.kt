package com.odm.calculatepractice.application

import android.app.Application
import com.didichuxing.doraemonkit.DoraemonKit

/**
 * @description: Application类
 * @author: ODM
 * @date: 2020/10/10
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //接入滴滴性能测试平台DoraemonKit
        DoraemonKit.install(this, "59b09ef90e482c06c2ad4dcce83ce47a")
    }


}