package com.odm.calculatepractice.View

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import com.odm.calculatepractice.R
import kotlinx.android.synthetic.main.fragment_main.*
import java.lang.ref.WeakReference

/**
 * @description: 主页面View层
 * @author: ODM
 * @date: 2020/10/8
 */
class HomeFragment : BaseFragment() {

    override fun initViews() {
        tb_home.getView<TextView>(R.id.tv_title_center).apply {
            text = "小学四则运算练习工具"
        }


        //监听输入框的内容
//        et_range.addTextChangedListener(InputStateWatcher(this))
//        et_question_count.addTextChangedListener(InputStateWatcher(this))

        tfb_range.setSimpleTextChangeWatcher { theNewText, isError ->
            //根据预定的规则，判断输入框当前是否有正常输入,，先判断是否非空，再进行转换判断，否则会在输入时报错NumberFormatException
            val hasCorrectRange: Boolean = theNewText !="" && theNewText.length < 10 && theNewText.toLong() > 0L
            val hasCorrectAccount: Boolean = et_question_count?.text.toString()!="" && et_question_count?.text.toString().toLong() in 1..10000L
            btn_generate_questions?.isEnabled = hasCorrectRange and hasCorrectAccount
            btn_direct_practise?.isEnabled = hasCorrectRange and hasCorrectAccount
        }
        tfb_question_count.setSimpleTextChangeWatcher { theNewText, isError ->
            //根据预定的规则，判断输入框当前是否有正常输入,，先判断是否非空，再进行转换判断，否则会在输入时报错NumberFormatException
            val hasCorrectRange: Boolean = et_range.text.toString() !="" && et_range.text.toString().toLong() > 0L
            val hasCorrectAccount: Boolean = theNewText !="" && theNewText.length < 10 && theNewText.toLong() in 1..10000L
            btn_generate_questions?.isEnabled = hasCorrectRange and hasCorrectAccount
            btn_direct_practise?.isEnabled = hasCorrectRange and hasCorrectAccount
        }



        //按钮的点击事件
        btn_direct_practise.setOnClickListener {
            hideSoftInput()
            val targetFragment = PractiseFragment()
            val bundleData = Bundle()
            bundleData.putInt("RANGE_MAX",et_range.text.toString().toInt())
            bundleData.putInt("COUNT" , et_question_count.text.toString().toInt())
            targetFragment.arguments = bundleData

            start(targetFragment , SINGLETOP)
        }

        btn_generate_questions.setOnClickListener {
            hideSoftInput()
            val targetFragment = QuestionListFragment()
            val bundleData = Bundle()
            bundleData.putLong("RANGE_MAX",et_range.text.toString().toLong())
            bundleData.putInt("COUNT" , et_question_count.text.toString().toInt())
            targetFragment.arguments = bundleData

            start(targetFragment , SINGLETOP)
        }


    }




    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }


    /**
     * 账号密码 EditText监听器，当输入符合规则则将按钮设为可用
     * @property mFragmentRef WeakReference<LoginFragment> 对view的弱引用
     * @constructor View层的引用
     */
    class InputStateWatcher(view : HomeFragment) : TextWatcher {
        private val mFragmentRef : WeakReference<HomeFragment> = WeakReference(view)

        override fun afterTextChanged(arg0: Editable) {
/*             if( mFragmentRef.get()?.et_range?.text.toString() != "" && mFragmentRef.get()?.et_range?.text.toString().toLong() <=  0L){
                 mFragmentRef.get()?.tfb_range?.setError("请输入大于等于1的自然数！！",true)
             }else if(mFragmentRef.get()?.et_question_count?.text.toString() != "" &&
                        (mFragmentRef.get()?.et_question_count?.text.toString().toLong() <=  0L || mFragmentRef.get()?.et_range?.text.toString().toLong() > 10000L) ){
                 mFragmentRef.get()?.tfb_question_count?.setError("请输入10000以内的整数！！",true)
             }*/
            val sRange = mFragmentRef.get()?.et_range?.text.toString()
            if(sRange != "") {
                val range = sRange.toLong()
                if(range <= 0L) {
                    mFragmentRef.get()?.tfb_range?.setError("请输入大于等于1的自然数！！",true)
                }
            }
            val sCount = mFragmentRef.get()?.et_question_count?.text.toString()
            if(sCount != "") {
                val count = sCount.toLong()
                if(count <= 0L || count > 10000L){
                    mFragmentRef.get()?.tfb_question_count?.setError("请输入10000以内的正整数！！",true)
                }
            }
        }

        override fun beforeTextChanged(
            arg0: CharSequence, arg1: Int, arg2: Int,
            arg3: Int) {}

        override fun onTextChanged(
            cs: CharSequence, start: Int, before: Int,
            count: Int
        ) {
            //根据预定的规则，判断输入框当前是否有正常输入,，先判断是否非空，再进行转换判断，否则会在输入时报错NumberFormatException
            val rangeMax = mFragmentRef.get()?.et_range?.text.toString()
            val hasCorrectRange: Boolean = rangeMax!="" && rangeMax.length < 10 && rangeMax.toLong() > 0L

            val sCount = mFragmentRef.get()?.et_question_count?.text.toString()
            val hasCorrectAccount: Boolean = sCount!="" && sCount.length < 10 && sCount.toLong() in 1..10000L
            mFragmentRef.get()?.btn_generate_questions?.isEnabled = hasCorrectRange and hasCorrectAccount
            mFragmentRef.get()?.btn_direct_practise?.isEnabled = hasCorrectRange and hasCorrectAccount
        }
    }

}


