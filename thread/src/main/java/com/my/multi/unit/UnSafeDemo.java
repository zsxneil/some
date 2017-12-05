package com.my.multi.unit;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/12/5.
 */
public class UnSafeDemo {
    private int i = 0;
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");//Internal reference
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);

        //获取字段i在内存中偏移量
        long offset = unsafe.objectFieldOffset(UnSafeDemo.class.getDeclaredField("i"));

        //创建对象实例，设置字段的值
        UnSafeDemo unSafeDemo = new UnSafeDemo();
        unsafe.putInt(unSafeDemo,offset,100);

        //打印结果
        System.out.println(unSafeDemo.i);
    }
}
