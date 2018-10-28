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
			System.out.println("Before schema creation");
			jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS potatoes");
			
			//jdbcTemplate.execute("SET search_path TO potatoes, public;");
			
			String username = jdbcTemplate.queryForObject("SELECT CURRENT_USER", String.class);
			
			jdbcTemplate.execute("ALTER ROLE " + username + " SET search_path TO potatoes, public");

			jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS potatoes.farmer_id_seq");
			jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS potatoes.meteorological_station_id_seq");	
			jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS potatoes.meteorology_id_seq");
			jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS potatoes.report_id_seq");	
			
			jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS potatoes.farmer " + 
					"( " + 
					"    id integer NOT NULL DEFAULT nextval('potatoes.farmer_id_seq'::regclass), " + 
					"    first_name character varying(50) COLLATE pg_catalog.\"default\" NOT NULL, " + 
					"    last_name character varying(50) COLLATE pg_catalog.\"default\" NOT NULL, " + 
					"    phone_number character varying(50) COLLATE pg_catalog.\"default\", " + 
					"    CONSTRAINT farmer_pkey PRIMARY KEY (id) ) " + 
					"WITH ( OIDS = FALSE )");

			jdbcTemplate.execute("-- Index: farmer_first_name_last_name_idx " + 
					"CREATE INDEX IF NOT EXISTS farmer_first_name_last_name_idx " + 
					"    ON potatoes.farmer USING btree " + 
					"    (last_name COLLATE pg_catalog.\"default\", first_name COLLATE pg_catalog.\"default\")");
			
			jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS potatoes.meteorological_station " + 
					"( " + 
					"    id integer NOT NULL DEFAULT nextval('potatoes.meteorological_station_id_seq'::regclass), " + 
					"    name character varying(20) COLLATE pg_catalog.\"default\" NOT NULL, " + 
					"    ims_id integer NOT NULL, " + 
					"    rh_channel integer NOT NULL, " + 
					"    wd_channel integer NOT NULL, " + 
					"    last_update_time timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, " + 
					"    is_active boolean NOT NULL, " + 
					"    CONSTRAINT meteorological_station_pkey PRIMARY KEY (id) ) " + 
					"WITH ( OIDS = FALSE )");
			
			jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS potatoes.meteorology " + 
					"( " + 
					"    id integer NOT NULL DEFAULT nextval('potatoes.meteorology_id_seq'::regclass), " + 
					"    \"time\" timestamp without time zone, " + 
					"    station_id integer NOT NULL, " + 
					"    is_valid boolean NOT NULL, " + 
					"    relative_humidity integer, " + 
					"    wind_direction integer, " + 
					"    CONSTRAINT meteorology_pkey PRIMARY KEY (id), " + 
					"    CONSTRAINT meteorology_station_id_fkey FOREIGN KEY (station_id) " + 
					"        REFERENCES potatoes.meteorological_station (id) MATCH SIMPLE " + 
					"        ON UPDATE RESTRICT " + 
					"        ON DELETE CASCADE ) " + 
					"WITH ( OIDS = FALSE )");
						
			jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS potatoes.report " + 
					"( " + 
					"    id integer NOT NULL DEFAULT nextval('potatoes.report_id_seq'::regclass), " + 
					"    report_time timestamp without time zone, " + 
					"    farmer_id integer NOT NULL, " + 
					"    longitude character varying(20) COLLATE pg_catalog.\"default\", " + 
					"    latitude character varying(20) COLLATE pg_catalog.\"default\", " + 
					"    CONSTRAINT report_pkey PRIMARY KEY (id), " + 
					"    CONSTRAINT report_farmer_id_fkey FOREIGN KEY (farmer_id) " + 
					"        REFERENCES potatoes.farmer (id) MATCH SIMPLE " + 
					"        ON UPDATE RESTRICT " + 
					"        ON DELETE CASCADE ) " + 
					"WITH ( OIDS = FALSE )");			
			
			jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS report_report_time_idx " + 
					"    ON potatoes.report USING btree " + 
					"    (report_time) " );			
			
			System.out.println("After schema creation");
		}
		
	}
