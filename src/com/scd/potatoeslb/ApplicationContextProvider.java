package com.scd.potatoeslb;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.scd.potatoeslb.spring.config.AppConfig;


/*
 * 
 * Utility Class to return the Spring ApplicationContext
 * 
 * 
 */
public class ApplicationContextProvider implements ApplicationContextAware {


    private static AnnotationConfigApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext arg0)
            throws BeansException {
        if (arg0 != null) {
            context=(AnnotationConfigApplicationContext) arg0;
        }
    }

    public synchronized static AnnotationConfigApplicationContext getApplicationContext(){
        if (context==null) {
            context = new AnnotationConfigApplicationContext(AppConfig.class);
        }
        return context;
    }
}
