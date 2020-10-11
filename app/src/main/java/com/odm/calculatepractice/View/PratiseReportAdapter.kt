package com.odm.calculatepractice.View

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.odm.calculatepractice.R
import com.odm.calculatepractice.bean.Exercise

/**
 * @description: 练习报告 列表适配器
 * @author: ODM
 * @date: 2020/10/10
 */
class PractiseReportAdapter (dataList:MutableList<Exercise>): BaseQuickAdapter<Exercise, BaseViewHolder>(R.layout.item_question_report,dataList) {

    private val stuAnswerPre = "你的答案： "
    private val correctAnswerPre = "正确答案： "

    override fun convert(holder: BaseViewHolder, item: Exercise) {
        holder.setText(R.id.tv_question, item.formatQuestionProperty())
        holder.setText(R.id.tv_student_answer , stuAnswerPre + item.studentAnswer)
        holder.setText(R.id.tv_correct_answer , correctAnswerPre + item.answer)

        val statusResId = when(item.studentAnswer) {
            "" -> R.drawable.ic_skip
            item.answer -> R.drawable.ic_correct
            else -> R.drawable.ic_wrong
        }
        holder.setImageResource(R.id.iv_status_report , statusResId)
    }
}