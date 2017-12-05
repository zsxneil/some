package com.my.multi.unit;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/12/5.
 */
public class UnSafeDemo2 {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");// Internal reference
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);

        // This creates an instance of player class without any initialization
        Player p = (Player) unsafe.allocateInstance(Player.class);
        System.out.println(p.getAge());// Print 0

        p.setAge(45); // Let's now set age 45 to un-initialized object
        System.out.println(p.getAge());// Print 45
    }


    class Player{
        private int age = 12;

        public Player(int age) {
            this.age = 50;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
