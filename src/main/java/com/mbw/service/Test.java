package com.mbw.service;

import com.mbw.spring.MbwApplicationContext;

public class Test {
    public static void main(String[] args) {
        MbwApplicationContext mbwApplicationContext = new MbwApplicationContext(AppConfig.class);

        UserService userService = (UserService) mbwApplicationContext.getBean("userService");
        userService.test();
    }
}
