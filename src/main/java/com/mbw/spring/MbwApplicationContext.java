package com.mbw.spring;

import com.mbw.spring.annotation.AutoWired;
import com.mbw.spring.annotation.Component;
import com.mbw.spring.annotation.ComponentScan;
import com.mbw.spring.annotation.Scope;
import com.mbw.spring.inter.BeanNameAware;
import com.mbw.spring.inter.InitializingBean;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class MbwApplicationContext {

    private Class configClass;

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();


    public MbwApplicationContext(Class configClass) {
        this.configClass = configClass;

        // 扫描
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);

            // 扫描路径
            String path = componentScanAnnotation.value();
            String start = componentScanAnnotation.value().split("\\.")[0];
            path = path.replace('.', '/');

            // D:\code\out\production\code
            ClassLoader classLoader = MbwApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);

            File file = new File(resource.getFile());

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                
                for (File f : files) {
                    String fileName = f.getAbsolutePath();

                    if (fileName.endsWith(".class")) {

                        String className = fileName.substring(fileName.indexOf(start), fileName.indexOf(".class"));
                        className = className.replace("\\", ".");

                        try {
                            Class<?> clazz = classLoader.loadClass(className);

                            if (clazz.isAnnotationPresent(Component.class)) {
                                // Bean
                                String beanName = clazz.getAnnotation(Component.class).value();

                                if (beanName.equals("")) {
                                    beanName = Introspector.decapitalize(clazz.getSimpleName());
                                }

                                BeanDefinition beanDefinition = BeanDefinition.builder()
                                        .type(clazz)
                                        .scope(clazz.isAnnotationPresent(Scope.class)
                                                ? clazz.getAnnotation(Scope.class).value()
                                                : "singleton")
                                        .build();

                                beanDefinitionMap.put(beanName, beanDefinition);

                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        
        // 创建单例Bean对象
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBaen(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        }
    }

    private Object createBaen(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();

        try {
            Object instance = clazz.getConstructor().newInstance();

            // 依赖注入
            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(AutoWired.class)) {
                    f.setAccessible(true);
                    f.set(instance, getBean(Introspector.decapitalize(f.getType().getSimpleName())));
                }
            }

            // Aware
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setName(beanName);
            }

            // 初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            // 初始化后 AOP


            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Object getBean(String beanName) {

        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

        if (beanDefinition == null) {
            throw new NullPointerException();
        } else {
            String scope = beanDefinition.getScope();
            if (scope.equals("singleton")) {
                Object bean = singletonObjects.get(beanName);
                if (bean == null) {
                    bean = createBaen(beanName, beanDefinition);
                    singletonObjects.put(beanName, bean);
                }
                return bean;
            } else {
                // 多例
                return createBaen(beanName, beanDefinition);
            }
        }
    }
}
