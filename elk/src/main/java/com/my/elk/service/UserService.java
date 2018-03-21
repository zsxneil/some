package com.my.elk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);

    public void testMdc() {
        logger.info("this is from userService");
    }
}
