package com.odm.calculatepractice.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.odm.calculatepractice.R
import com.odm.calculatepractice.bean.PractiseCondition
import com.odm.calculatepractice.util.GlobalExerciseListHolder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_report.*
import java.util.concurrent.TimeUnit

/**
 * @description: 练习报告页面
 * @author: ODM
 * @date: 2020/10/10
 */
class ReportFragment:BaseFragment() {

    private val rvAdapter = PractiseReportAdapter(mutableListOf())

    private var mDisposable: Disposable? = null

    override fun initViews() {
        //设置标题
        tb_report.getView<TextView>(R.id.tv_title_center).apply {
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            text = "练习情况报告"
        }
        //返回键事件
        tb_report.getView<ImageView>(R.id.ic_title_back).apply {
            setImageResource(R.drawable.ic_close_white)
            setOnClickListener {
                popTo(HomeFragment::class.java, false)
            }
        }
        //隐藏右侧按钮
        tb_report.getView<Button>(R.id.btn_operate_right).visibility = View.GONE

        //初始化RecyclerView
        rv_questions_report.layoutManager = LinearLayoutManager(requireContext())
        rv_questions_report.adapter = rvAdapter
        rv_questions_report.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }

    override fun onStart() {
        super.onStart()
        val condition = arguments?.getParcelable<PractiseCondition>("PRACTISE_CONDITION")
        condition?.let {
            initScorePractiseCondition(it)
        }
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        rvAdapter.setNewInstance(GlobalExerciseListHolder.getGlobalExerciseList())
    }

    /**
     * 设置 答题情况
     * @param condition PractiseCondition
     */
    private fun initScorePractiseCondition(condition: PractiseCondition) {
        tv_correct_count.text = "回答正确题数： " + condition.correctCount + " 道"
        tv_total_count.text = "总题数： " + condition.totalCount + " 道"
        mDisposable = Observable.intervalRange(
            1L,
            (100 * condition.correctCount / condition.totalCount).toLong(),
            500,
            30,
            TimeUnit.MILLISECONDS
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                 //更新进度
                 trp_percent_correct.progress = it.toInt()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //防止脏数据
        GlobalExerciseListHolder.clearGlobalExerciseList()
        mDisposable?.dispose()
        mDisposable = null
    }


    override fun onBackPressedSupport(): Boolean {
        popTo(HomeFragment::class.java, false)
        return true
    }

    override fun getLayoutId(): Int {
        return  R.layout.fragment_report
    }
}