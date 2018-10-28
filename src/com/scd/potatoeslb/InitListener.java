package com.scd.potatoeslb;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.scd.potatoeslb.spring.config.AppConfig;
import com.scd.potatoeslb.spring.dao.DBSchemaCreation;

@WebListener
public class InitListener implements ServletContextListener {
	
	private static final boolean bCreateDB = true;
	
	private MeteorologyScheduler mst;
	
    @Override
    public final void contextInitialized(final ServletContextEvent sce) {

    	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

    	//let this code out of comment when we want it to work!
    	mst = new MeteorologyScheduler();
		mst.startScheduledTask();
    	
    	DBSchemaCreation schemaCreator = context.getBean(DBSchemaCreation.class);
    	schemaCreator.createSchema();   	
    	}

    @Override
    public final void contextDestroyed(final ServletContextEvent sce) {
    	if ( mst != null ) {
    		mst.stopScheduledTask();
    	}
    }
}