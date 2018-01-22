package com.my.springmvc.annotation;

import java.lang.annotation.*;

/**
 * Created by neil on 2018/1/21.
 */
@Documented
@Target(ElementType.FIELD) //作用于字段上，实现注入
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {
    public String value();
}
