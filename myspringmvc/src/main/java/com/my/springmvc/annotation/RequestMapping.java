package com.my.springmvc.annotation;

import java.lang.annotation.*;

/**
 * Created by neil on 2018/1/21.
 * 地址映射处理注解
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE}) //可以用于类及方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    public String value();

}
