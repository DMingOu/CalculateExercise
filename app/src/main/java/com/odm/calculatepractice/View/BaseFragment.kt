package com.odm.calculatepractice.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.yokeyword.fragmentation.SupportFragment

/**
 * @description: Fragment基类
 * @author: ODM
 * @date: 2020/10/8
 */
abstract class BaseFragment : SupportFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //隐藏输入法
        hideSoftInput()
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
    }

    override fun onStart() {
        super.onStart()
        //加载数据之类
    }



    /**
     * 初始化View
     */
    protected abstract fun initViews()

    /**
     * 返回布局Id
     * @return 布局Id
     */
    protected abstract fun getLayoutId(): Int

}