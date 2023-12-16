package com.mbw.service;


import com.mbw.spring.annotation.AutoWired;
import com.mbw.spring.annotation.Component;
import com.mbw.spring.annotation.Scope;
import com.mbw.spring.inter.BeanNameAware;
import com.mbw.spring.inter.InitializingBean;

@Component
@Scope("prototype")
public class UserService implements BeanNameAware, InitializingBean {

    @AutoWired
    private OrderService order;

    private String beanName;

    private String xxx;

    public void test() {
        System.out.println(order);
    }

    @Override
    public void setName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        this.xxx = "xxx";
        System.out.println("Bean初始化");
    }
}
