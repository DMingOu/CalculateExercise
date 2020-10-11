package com.odm.calculatepractice.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * @description: 练习情况信息实体类
 * @author: ODM
 * @date: 2020/10/10
 */
data class PractiseCondition(
    var totalCount: Int,
    var correctCount: Int,
    var wrongCount: Int,
    var skipCount: Int
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readInt(),
        source.readInt(),
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(totalCount)
        writeInt(correctCount)
        writeInt(wrongCount)
        writeInt(skipCount)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PractiseCondition> =
            object : Parcelable.Creator<PractiseCondition> {
                override fun createFromParcel(source: Parcel): PractiseCondition =
                    PractiseCondition(source)

                override fun newArray(size: Int): Array<PractiseCondition?> = arrayOfNulls(size)
            }
    }

    override fun toString(): String {
        return """PractiseCondition : [
                    totalCount: $totalCount
                    correctCount: $correctCount
                    wrongCount : $wrongCount
                    skipCount : $skipCount
                  ]"""
    }
}