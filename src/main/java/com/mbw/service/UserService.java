package com.mbw.service;


import com.mbw.spring.AutoWired;
import com.mbw.spring.Component;
import com.mbw.spring.Scope;

@Component("userService")
@Scope("prototype")
public class UserService {

    @AutoWired
    private OrderService orderService;

    public void test() {
        System.out.println(orderService);
    }

}
