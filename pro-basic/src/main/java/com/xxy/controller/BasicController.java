package com.xxy.controller;

import com.xxy.service.BasicService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.xxy")
public class BasicController {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(BasicController.class);
        BasicService bean = context.getBean(BasicService.class);
        bean.testUser();
    }
}
