package com.mbw.spring.inter;

public interface BeanPostProcessor {

    public void postProcessBeforeInitialization(String BeanName, Object bean);

    public void postProcessAfterInitialization(String BeanName, Object bean);

}
