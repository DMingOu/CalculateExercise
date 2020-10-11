package com.odm.calculatepractice.bean;

import com.odm.calculatepractice.util.OperatorEnum;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;



/**
 * 用于保存题目信息
 */
public class Exercise {

    //存储运算式链表，实际上采用的是树的结构，用下标来获取
    private ArrayList<String> valueList = new ArrayList<>();
    //运算符个数
    private int operatorCount;
    //答案
    private String answer = "";
    //学生答案
    private String studentAnswer = "";
    //题号
    private int index = 0;

    //规范题目格式
    private String formatQuestion = "";
    //最简式（最后一层表达式且不含括号）
    private String simplestFormatQuestion;

    /**
     * 返回符合格式的题目
     *
     * @return 题目
     */
    public String getFormatQuestion() {
        //内层表达式需要括号
        String bracket = "(e)";
        //最外一层无需要加大括号
        String outerBracket = "e";
        Queue<String> queue = new LinkedList<>();
        //将所有运算数进队列
        for (int i = 2 * operatorCount; i > operatorCount - 1; i--) {
            queue.add(valueList.get(i));
        }

        //取出每个运算符,再从队列取出两个数字进行运算
        //结果再放入队尾中，直到取完所有运算符，此时队列中的数字为最终答案
        for (int i = operatorCount - 1; i >= 0; i--) {
            String operator = valueList.get(i);
            //从队列取出两个数字
            String e1 = queue.remove();
            String e2 = queue.remove();
            //替换掉
            if(i == 0){
                String expression = outerBracket.replace("e", e1 + operator + e2);
                queue.add(expression);
            }else {
                String expression = bracket.replace("e", e1 + operator + e2);
                queue.add(expression);
            }
        }
        //返回问题（序号+题目）
        formatQuestion = index + ".   " + queue.remove();
        return formatQuestion;
    }


    /**
     * 返回最简格式的题目
     *
     * @return 题目
     */
    public String getSimplestFormatQuestion() {
        if (null == simplestFormatQuestion) {
            Queue<String> queue = new LinkedList<>();

            //将所有运算数进队列
            for (int i = 2 * operatorCount; i > operatorCount - 1; i--) {
                queue.add(valueList.get(i));
            }

            //取出每个运算符,再从队列取出两个数字进行运算，结果再放入队尾中，直到取完所有运算符，此时队列中的数字为最终答案
            for (int i = operatorCount - 1; i >= 0; i--) {
                String opSymbol = valueList.get(i);

                //从队列取出两个数字
                String num1 = queue.remove();
                String num2 = queue.remove();
                //计算两数运算后结果
                String answer = OperatorEnum.Companion.getEnumByOpSymbol(opSymbol).op(num1, num2);
                //如果是最后一层则生成最简式
                if (i == 0) {
                    simplestFormatQuestion = num1 + opSymbol + num2;
                }
                queue.add(answer);
            }
        }
        return simplestFormatQuestion;
    }

    public String formatQuestionProperty() {
        if(null==formatQuestion||formatQuestion.trim().isEmpty()){
            getFormatQuestion();
        }
        return formatQuestion;
    }


    //**GETTER                                                                     SETTER**//

    /**
     * 获取符合格式的答案
     */
    public String getFormatAnswer() {
        return index + " ." + answer;
    }

    public void setValueList(ArrayList<String> valueList) {
        this.valueList = valueList;
    }


    public ArrayList<String> getValueList() {
        return valueList;
    }

    public void addValue(String value) {
        this.valueList.add(value);
    }

    public void addValue(int index, String value) {
        this.valueList.add(index, value);
    }

    public int getOperatorNumber() {
        return operatorCount;
    }

    public void setOperatorNumber(int operatorCount) {
        this.operatorCount = operatorCount;
    }

    public String getAnswer() {
        return answer;
    }

    public String answerProperty() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getNumber() {
        return index;
    }

    public void setNumber(int index) {
        this.index = index;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public String studentAnswerProperty() {
        return studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }


    //**GETTER                                                                     SETTER**//



    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Exercise exercise = (Exercise) o;
        return Objects.equals(getSimplestFormatQuestion(), exercise.getSimplestFormatQuestion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(simplestFormatQuestion);
    }
}
