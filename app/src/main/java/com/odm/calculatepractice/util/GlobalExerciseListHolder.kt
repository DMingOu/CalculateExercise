package com.odm.calculatepractice.util

import com.odm.calculatepractice.bean.Exercise

/**
 * @description: 全局的 计算题问题列表 持有者
 * @author: ODM
 * @date: 2020/10/9
 */
object GlobalExerciseListHolder {

    private var  exercises : MutableList<Exercise> = mutableListOf()

    fun getGlobalExerciseList() : MutableList<Exercise>{
        return exercises.toMutableList()
    }

    fun updateGlobalExerciseList(newDataList : List<Exercise>){
        this.exercises = newDataList.toMutableList()
    }


    fun addGlobalExerciseList(dataList : List<Exercise>) {
        this.exercises.addAll(dataList)
    }

    //**练习答题的列表**//

    /**
     * 正确的题目的列表
     */
    private var correctExerciseList = mutableListOf<Exercise?>()


    /**
     * 错误的题目的列表
     */
    private var wrongExerciseList = mutableListOf<Exercise?>()

    /**
     * 跳过的题目列表
     */
    private var skipExerciseList = mutableListOf<Exercise?>()


    fun addGlobalPractice(exercise: Exercise) {
        this.exercises.add(exercise)
    }

    fun updatePractiseState (exercise: Exercise?, state:PractiseState){
        when(state){
            PractiseState.CORRECT -> correctExerciseList.add(exercise)
            PractiseState.WRONG  -> wrongExerciseList.add(exercise)
            PractiseState.SKIP -> skipExerciseList.add(exercise)
            else -> {}
        }
    }

    fun getPracticeStateList( state:PractiseState) : List<Exercise?>{
        return when(state){
            PractiseState.CORRECT -> correctExerciseList
            PractiseState.WRONG  -> wrongExerciseList
            PractiseState.SKIP -> skipExerciseList
            else -> mutableListOf()
        }
    }



    fun clearGlobalExerciseList() {
        this.exercises.clear()
        this.correctExerciseList.clear()
        this.skipExerciseList.clear()
        this.wrongExerciseList.clear()
    }


    enum class PractiseState{
        CORRECT,
        WRONG,
        SKIP
    }

}