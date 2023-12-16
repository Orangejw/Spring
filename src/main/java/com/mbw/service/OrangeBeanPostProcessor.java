package com.mbw.service;

import com.mbw.spring.annotation.Component;
import com.mbw.spring.inter.BeanPostProcessor;

@Component
public class OrangeBeanPostProcessor implements BeanPostProcessor {

    @Override
    public void postProcessBeforeInitialization(String BeanName, Object bean) {
        if (bean instanceof UserService) {
            System.out.println("UserService postProcessBeforeInitialization");
        }
    }

    @Override
    public void postProcessAfterInitialization(String BeanName, Object bean) {
        if (bean instanceof UserService) {
            System.out.println("UserService postProcessAfterInitialization");
        }
    }
}
