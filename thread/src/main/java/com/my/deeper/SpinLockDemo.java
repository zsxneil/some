package com.my.deeper;

/**
 * 这里的while循环，其实就是所谓的自旋锁(Spin Lock)。
 * 需要注意的是：自旋锁不是真正的锁，其只是解决思路的一种方式，
 * 只要不能继续往下执行，就不断的循环。
 * Created by neil on 2017/12/4.
 */
public class SpinLockDemo {

    int count = 0;

    public void incr() {
        count++;
        System.out.println("程序执行了" + count + "次");
    }

    public static void main(String[] args) {
        SpinLockDemo lockDemo = new SpinLockDemo();
        while (lockDemo.count != 1000000) {//这段代码其实是一个“自旋锁”
            lockDemo.incr();
        }
        System.out.println("执行其他代码...");
    }
}
