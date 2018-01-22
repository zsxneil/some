package com.my.springmvc.annotation;

import java.lang.annotation.*;

/**
 * Created by neil on 2018/1/21.
 * 持久层注解
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {
    public String value();
}
