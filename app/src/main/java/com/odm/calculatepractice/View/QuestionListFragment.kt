package com.odm.calculatepractice.View

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.odm.calculatepractice.R
import com.odm.calculatepractice.bean.Exercise
import com.odm.calculatepractice.util.ExerciseProducer
import com.odm.calculatepractice.util.GlobalExerciseListHolder
import com.odm.calculatepractice.widget.DividerDecoration
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_question_list.*
import java.lang.ref.WeakReference

/**
 * @description: 四则运算题目列表
 * @author: ODM
 * @date: 2020/10/8
 */

class QuestionListFragment : BaseFragment() {

    private val rvAdapter = QuestionListAdapter(mutableListOf())

    private lateinit var mHandler : QuestionListHandler

    private var loadingPopup : BasePopupView ?= null

    private var totalCount = 0

    private var rangeMax =  0

    private val PAGE_SIZE = 100

    /**
     * 已加载的页数
     */
    private var pageIndex = 0

    private var mDisposable: Disposable ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        totalCount = arguments?.getInt("COUNT") ?: 1
        rangeMax = arguments?.getLong("RANGE_MAX")?.toInt() ?: 1
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //获取主线程的handler
        mHandler = QuestionListHandler(this)
        super.onActivityCreated(savedInstanceState)
    }

    override fun initViews() {
        showLoadingPopup()

        tb_question_list.getView<ImageView>(R.id.ic_title_back).apply {
            setImageResource(R.drawable.ic_arrorw_left_black)
            setOnClickListener {
                pop()
            }
        }

        //设置标题文字
        tb_question_list.getView<TextView>(R.id.tv_title_center).text = "四则运算题列表"

        //隐藏右侧按钮
        tb_question_list.getView<Button>(R.id.btn_operate_right).visibility = View.GONE

        tv_tips_question_list.text = "使用Tips: 长按单条题目可以显示答案"


        tv_count_question_list.text = "题目数量： ".plus(totalCount)

        tv_range_question_list.text = "数字范围：1 至 ".plus(rangeMax)


        //初始化 RecyclerView
        rv_question_list.layoutManager = LinearLayoutManager(requireContext())
        rv_question_list.adapter = rvAdapter
        //去除滑动阴影
        rv_question_list.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        if(rv_question_list.itemDecorationCount == 0) {
            rv_question_list.addItemDecoration(DividerDecoration(requireContext()))
        }

        rvAdapter.setOnItemLongClickListener { adapter, itemView, position ->
            //设置答案
            itemView.findViewById<TextView>(R.id.tv_answer).text = " = ${rvAdapter.getItem(position).answer}"
            true
        }



        //设置上拉加载的监听事件
        rvAdapter.loadMoreModule.setOnLoadMoreListener { loadMoreQuestions() }
        rvAdapter.loadMoreModule.isEnableLoadMore = true
        //自动加载第一页
        rvAdapter.loadMoreModule.isAutoLoadMore = true
        //数据不满，继续加载多一页？
        rvAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        //上拉预加载
        rvAdapter.loadMoreModule.preLoadNumber = 5
    }


    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        //获取首页的题目的列表数据
        loadMoreQuestions()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mDisposable?.dispose()
        mDisposable = null
        GlobalExerciseListHolder.clearGlobalExerciseList()
    }

    private fun loadMoreQuestions(){
        /**
         * 异步回调执行生成任务
         */
        mDisposable = Observable.create<MutableList<Exercise>> {
            //加载所需要的大小的数据
            val needCount = if( (totalCount - PAGE_SIZE*pageIndex) > PAGE_SIZE){
                PAGE_SIZE
            }else {
                (totalCount - PAGE_SIZE*pageIndex)
            }
//            val dataList = CalculationQuestionUtil.generateExercises(needCount , rangeMax ,pageIndex * PAGE_SIZE)
//            it.onNext(dataList.toMutableList())

            ExerciseProducer().produce(needCount , rangeMax ,pageIndex * PAGE_SIZE , mHandler)

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { dataList ->
                //隐藏加载对话框
                hideLoadingPopup()
                //启用 上拉加载下一页
                rvAdapter.loadMoreModule.isEnableLoadMore = true
                //根据接收的数据，加载
                if (rvAdapter.itemCount < PAGE_SIZE) {
                    //如果是加载的第一页数据，用 setData()
                    rvAdapter.setNewInstance(dataList)
                } else {
                    //不是第一页，则用add
                    rvAdapter.addData(dataList)
                }
                //成功加载了一页，页数+1
                pageIndex++
                //判断当前是否仍可加载更多
                if (totalCount <= PAGE_SIZE * pageIndex) {
                    //如果如果所需要显示的总数量是小于每一页的数据的，不够一页,显示没有更多数据布局
                    //如果当前已经加载数据大于等于 总所需题目也结束
                    rvAdapter.loadMoreModule.loadMoreEnd()
                    ToastUtils.showShort("$totalCount 条题目加载完毕")
                } else {
                    rvAdapter.loadMoreModule.loadMoreComplete()
                }
            }

    }


    private fun showLoadingPopup() {
        if(loadingPopup != null){
            loadingPopup?.show()
            return
        }
        loadingPopup = XPopup.Builder(requireContext())
            .asLoading()
            .setTitle("正在加载题目列表中")
            .show()
    }

    fun hideLoadingPopup(exercises: MutableList<Exercise> ?= null) {
        loadingPopup?.smartDismiss()
    }


    /**
     * 接收到 问题生成后的消息回调
     * @property mFragment WeakReference<QuestionListFragment>
     * @constructor
     */
    class QuestionListHandler(fragment: QuestionListFragment) : Handler() {
        private val mFragment: WeakReference<QuestionListFragment> = WeakReference(fragment)

        override fun handleMessage(msg: Message) {
            if (mFragment.get() == null) {
                return
            }
            val fragment = mFragment.get() as? QuestionListFragment
            when (msg.what) {
                666 -> {
                    fragment?._mActivity?.runOnUiThread {
                        fragment.apply {
                            //隐藏加载对话框
                            hideLoadingPopup()
                            //启用 上拉加载下一页
                            rvAdapter.loadMoreModule.isEnableLoadMore = true
                            //根据接收的数据，加载
                            if (rvAdapter.itemCount < PAGE_SIZE) {
                                //如果是加载的第一页数据，用 setData()
                                rvAdapter.setNewInstance(GlobalExerciseListHolder.getGlobalExerciseList())
                            } else {
                                //不是第一页，则用add
                                rvAdapter.addData(GlobalExerciseListHolder.getGlobalExerciseList())
                            }
                            //成功加载了一页，页数+1
                            pageIndex++
                            //判断当前是否仍可加载更多
                            if (totalCount <= PAGE_SIZE * pageIndex) {
                                //如果如果所需要显示的总数量是小于每一页的数据的，不够一页,显示没有更多数据布局
                                //如果当前已经加载数据大于等于 总所需题目也结束
                                rvAdapter.loadMoreModule.loadMoreEnd()
                                ToastUtils.showShort("$totalCount 条题目加载完毕")
                            } else {
                                rvAdapter.loadMoreModule.loadMoreComplete()
                            }
                        }
                    }
                }
                else -> {
                }
            }
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_question_list
    }





}