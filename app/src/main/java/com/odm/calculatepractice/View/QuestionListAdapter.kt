package com.odm.calculatepractice.View

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.odm.calculatepractice.R
import com.odm.calculatepractice.bean.Exercise

/**
 * @description: 问题列表适配器
 * @author: ODM
 * @date: 2020/10/8
 */
class QuestionListAdapter(dataList:MutableList<Exercise>):BaseQuickAdapter<Exercise,BaseViewHolder> (
    R.layout.item_question,dataList) ,LoadMoreModule{


    override fun convert(holder: BaseViewHolder, item: Exercise) {
        holder.setText(R.id.tv_question, item.formatQuestionProperty() )
    }


}