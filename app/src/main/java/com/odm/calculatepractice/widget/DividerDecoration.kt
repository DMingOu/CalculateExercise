package com.odm.calculatepractice.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.odm.calculatepractice.R


/**
 * @description: 下划线 ItemDecoration
 * @author: ODM
 * @date: 2020/8/20
 */
class DividerDecoration(context: Context) : RecyclerView.ItemDecoration(){

    private var divideHeight = 0

    private val dividerPaint: Paint

    private var leftMargin = 0F
    private var rightMargin = 0F
    private var topMargin = 0F
    private var bottomMargin = 0F

    init {
        //设置画笔
        dividerPaint = Paint()
        //设置分割线颜色
        dividerPaint.color = context.resources.getColor(R.color.divide_line)
        //设置分割线宽度
//        divideHeight = context.resources.getDimensionPixelSize(R.dimen.divide_height_common)
        divideHeight = 1
    }

    constructor(context: Context, leftMargin:Float ,rightMargin:Float):this(context){
        this.leftMargin = leftMargin
        this.rightMargin = rightMargin
    }


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        //改变宽度
        outRect.bottom = divideHeight
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        //得到列表所有的条目
        val childCount = parent.childCount
        //得到条目的宽和高
        val left = parent.paddingLeft.toFloat() + leftMargin
        val right = (parent.width - parent.paddingRight).toFloat() + rightMargin
        for (i in 0 until childCount - 1) {
            val view: View = parent.getChildAt(i)
            //计算每一个条目的顶点和底部 float值
            val top: Float = view.bottom.toFloat()
            val bottom: Float = (view.bottom + divideHeight).toFloat()
            //重新绘制
            c.drawRect(left, top, right, bottom, dividerPaint)
        }
    }

}