package com.my.elk.controller;

import com.my.elk.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

    // 定义一个全局的记录器，通过LoggerFactory获取
    private final static Logger log = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping("/user")
    public User findOne() {
        User user = new User();
        user.setId(1);
        user.setName("neil");

        log.info(user.toString());

        //int i = 1/0;
        return user;
    }
}
