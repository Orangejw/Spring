package com.mbw.spring;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BeanDefinition {

    private Class type;

    private String scope;
}
