package com.scd.potatoeslb;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.scd.potatoeslb.spring.config.AppConfig;

@WebListener
public class InitListener implements ServletContextListener {
	
	private MeteorologyScheduler mst;
	
    @Override
    public final void contextInitialized(final ServletContextEvent sce) {

    	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		mst = new MeteorologyScheduler();
		mst.startScheduledTask();
    }

    @Override
    public final void contextDestroyed(final ServletContextEvent sce) {
    	if ( mst != null ) {
    		mst.stopScheduledTask();
    	}
    	

    }
}