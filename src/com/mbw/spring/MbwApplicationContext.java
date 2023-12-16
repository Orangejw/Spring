package com.mbw.spring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class MbwApplicationContext {

    private Class configClass;

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

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

                        System.out.println(className);
                        try {
                            Class<?> clazz = classLoader.loadClass(className);

                            if (clazz.isAnnotationPresent(Component.class)) {
                                // Bean

                                String beanName = clazz.getAnnotation(Component.class).value();

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
    }

    public Object getBean(String beanName) {

        return null;
    }
}
