package com.my.reflect;

import com.my.fastjson.User;

import java.lang.reflect.InvocationTargetException;

public class MethodTest {
    public static void main(String[] args) {
        User user = new User();
        user.setId(1L);
        user.setName("a");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("b");

        try {
            user.getClass().getMethod("setId",Long.class).invoke(user, User.class.getMethod("getId",null).invoke(user2,null));
            //Long id = (Long) User.class.getMethod("getId",null).invoke(user,null);
            System.out.println(user);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
