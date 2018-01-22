package com.my.springmvc.annotation;

import java.lang.annotation.*;

/**
 * Created by neil on 2018/1/21.
 */
@Documented //JAVADOC
@Target(ElementType.TYPE) //作用于类上
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller { //限制annoatation的生命周期，这里自定义的注解需要运行时保留

    //作用于类的注解有一个value属性，说白了就是controller的名称
    public String value();
}
