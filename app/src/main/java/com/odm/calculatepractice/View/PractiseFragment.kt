package com.odm.calculatepractice.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.odm.calculatepractice.R
import com.odm.calculatepractice.bean.Exercise
import com.odm.calculatepractice.bean.PractiseCondition
import com.odm.calculatepractice.util.CalculationExerciseUtil
import com.odm.calculatepractice.util.GlobalExerciseListHolder
import kotlinx.android.synthetic.main.fragment_practise.*


/**
 * @description: 做题 页面
 * @author: ODM
 * @date: 2020/10/10
 */
class PractiseFragment : BaseFragment(){

    private lateinit var tvTitleMain : TextView

    private var rangeMax = 1

    private var totalCount = 1

    private var currentIndex = 0

    private var currentExercise : Exercise?= null


    private val practiseCondition = PractiseCondition(0,0,0,0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //获取传参
        totalCount = arguments?.getInt("COUNT") ?: 1
        rangeMax = arguments?.getInt("RANGE_MAX") ?: 1
        //更新答题信息实体类
        practiseCondition.totalCount = totalCount
        //清空当前的脏数据
        GlobalExerciseListHolder.clearGlobalExerciseList()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //清空容器，避免脏数据影响操作
        CalculationExerciseUtil.clear()
        //清空当前的脏数据
        GlobalExerciseListHolder.clearGlobalExerciseList()
    }


    override fun initViews() {
        //初始化标题按钮
        tb_practise.getView<ImageView>(R.id.ic_title_back).apply {
            setOnClickListener {
                hideSoftInput()
                pop()
            }
        }

        //初始化标题
        tvTitleMain = tb_practise.getView(R.id.tv_title_center)
        //获取题目
        getNextExercise()

        //设置输入的键盘类型
        et_student_answer_practise.inputType = EditorInfo.TYPE_CLASS_PHONE;


        //初始化跳过按钮
        tb_practise.getView<Button>(R.id.btn_operate_right).apply {
            text = "跳过"
            setOnClickListener {
                ToastUtils.showShort("跳过题号 $currentIndex")
                currentExercise?.let {
                    GlobalExerciseListHolder.addGlobalPractice(it)
                }
                practiseCondition.skipCount += 1

                if(currentIndex == totalCount) {
                    hideSoftInput()
                }

                //判断 并 尝试获取新的题目
                getNextExercise()


            }
        }

        //设置next按钮的点击 反馈效果
        btn_next_practise.setOnTouchListener{ v, event ->
            v.performClick()
            if (event.action == MotionEvent.ACTION_DOWN) {
                //被按压
                btn_next_practise.setBackgroundResource(R.drawable.ic_next_deep_blue)
            } else if (event.action == MotionEvent.ACTION_UP) {
                //抬起
                btn_next_practise.setBackgroundResource(R.drawable.ic_next_blue)
                if(currentIndex == totalCount) {
                    hideSoftInput()
                }
                checkCurrentExerciseAnswer()
            }
            false
        }
    }

    /**
     * 获取一道新的题目，并更新相关的UI
     */
    private fun getNextExercise() {
        //无需获取下一题，直接进入
        if(currentIndex == totalCount) {
            //进入练习情况报告页
            val targetFragment = ReportFragment()
            val bundleData = Bundle()
            bundleData.putParcelable("PRACTISE_CONDITION" , practiseCondition)
            targetFragment.arguments = bundleData
            start(targetFragment, SINGLETOP)
        }
        //仍然可以做题
        if(currentIndex < totalCount) {
            //清空当前答案输入的内容
            et_student_answer_practise.setText("")
            currentIndex++

            if(currentIndex >= 2) {
                //第二题开始，不出现提示
                et_student_answer_practise.hint = ""
            }

            currentExercise = CalculationExerciseUtil.generateExercise(rangeMax)
            //设置该题目的序号
            currentExercise?.number = currentIndex
            tv_current_practise.text = currentExercise?.simplestFormatQuestion ?:"error"
            //更新进度
            val sProgress = "$currentIndex / $totalCount"
            tvTitleMain.text = sProgress
        }
    }

    private fun checkCurrentExerciseAnswer() {
        //当前还有题目没有开启
        if(currentIndex <= totalCount) {
            //判定输入框填写的答案 视为 当前题目完成情况
            //去掉答案输入的空格
            val studentAnswerString = et_student_answer_practise.text.toString().replace(" ","")
            currentExercise?.studentAnswer = studentAnswerString
            when(studentAnswerString) {
                //无作答
                "" -> ToastUtils.showShort("请作答！")
                //回答正确
                currentExercise?.answerProperty() -> {
                    currentExercise?.let {
                        GlobalExerciseListHolder.addGlobalPractice(it)
                    }
                    //更新答题信息情况
                    practiseCondition.correctCount += 1
                    //刷新题目以及相关的UI
                    getNextExercise()
                }
                //回答错误
                else -> {
                    currentExercise?.let {
                        GlobalExerciseListHolder.addGlobalPractice(it)
                    }
                    //更新答题信息情况
                    practiseCondition.wrongCount += 1
                    //刷新题目以及相关的UI
                    getNextExercise()
                }
            }
        }
    }


    override fun getLayoutId(): Int {
       return R.layout.fragment_practise
    }
}