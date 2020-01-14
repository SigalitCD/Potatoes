package com.scd.potatoeslb;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.scd.potatoeslb.spring.config.AppConfig;
import com.scd.potatoeslb.spring.dao.DBSchemaCreation;

@WebListener
public class InitListener implements ServletContextListener {
		
	private MeteorologyScheduler meteorologyScheduler;
	
    @Override
    public final void contextInitialized(final ServletContextEvent sce) {

    	@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
   	
    	DBSchemaCreation schemaCreator = context.getBean(DBSchemaCreation.class); 
    	schemaCreator.createSchema();   	

    	meteorologyScheduler = new MeteorologyScheduler();
    	meteorologyScheduler.startScheduledTask();
    	
    	}

    @Override
    public final void contextDestroyed(final ServletContextEvent sce) {
    	if ( meteorologyScheduler != null ) {
    		meteorologyScheduler.stopScheduledTask();
    	}
    }
}