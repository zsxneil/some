package com.my.elk.controller;

import com.my.elk.model.User;
import com.my.elk.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/home")
public class HomeController {

    // 定义一个全局的记录器，通过LoggerFactory获取
    private final static Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    UserService userService;

    @RequestMapping("/user")
    public User findOne(HttpServletRequest request) {
        User user = new User();
        user.setId(1);
        user.setName("neil");

        MDC.put("ip", request.getRemoteAddr());
        MDC.put("userId", user.getId() + "");

        log.info(user.toString());
        userService.testMdc();

        //int i = 1/0;
        return user;
    }
}
