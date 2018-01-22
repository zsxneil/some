package com.my.springmvc.annotation;

import java.lang.annotation.*;

/**
 * Created by neil on 2018/1/21.
 * 业务层注解
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    public String value();
}
