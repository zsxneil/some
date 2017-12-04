package com.my.deeper;

/**
 * Created by neil on 2017/12/4.
 */
public class StackOverFlowDemo {

    int count = 0;

    //递归造成栈溢出
    public void recursiveMehtod(){
        if (count == 1000000) {//递归方法执行1000000次时，结束
            return;
        }
        count++;
        System.out.println("程序执行了" + count + "次");
        recursiveMehtod();
    }

    public static void main(String[] args) {
        StackOverFlowDemo stackOverFlowDemo = new StackOverFlowDemo();
        stackOverFlowDemo.recursiveMehtod();
        System.out.println("执行其他代码");
    }
}
