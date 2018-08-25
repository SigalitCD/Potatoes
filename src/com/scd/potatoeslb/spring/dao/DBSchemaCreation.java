package com.scd.potatoeslb.spring.dao;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DBSchemaCreation {

		JdbcTemplate jdbcTemplate;


		@Autowired
		public DBSchemaCreation(DataSource dataSource) {
			jdbcTemplate = new JdbcTemplate(dataSource);
		}
		
		
		public void createSchema() {
			jdbcTemplate.execute("CREATE IF NOT EXISTS SCHEMA potatoes");
			jdbcTemplate.execute("CREATE IF NOT EXISTS table potatoes.test (first_name character varying(50))");
		}
		
	}
