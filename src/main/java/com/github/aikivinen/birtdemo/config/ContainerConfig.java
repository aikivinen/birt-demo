package com.github.aikivinen.birtdemo.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.vaadin.v7.data.util.sqlcontainer.SQLContainer;
import com.vaadin.v7.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.v7.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.v7.data.util.sqlcontainer.query.TableQuery;


@Configuration
public class ContainerConfig {

	@Bean
	protected DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/birtdemo");
		dataSource.setUsername("birtuser");
		dataSource.setPassword("birt");
		return dataSource;
	}

	@Scope("prototype")
	@Bean
	protected SQLContainer sqlContainer(String table, boolean setVersionColumn) {
		JDBCConnectionPool pool = null;
		SQLContainer container = null;

		try {
			pool = new SimpleJDBCConnectionPool("org.postgresql.Driver",
					"jdbc:postgresql://localhost:5432/birtdemo", "birtuser",
					"birt", 2, 10);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TableQuery tq = new TableQuery(table, pool);
		// if (setVersionColumn)
		// tq.setVersionColumn("optlock");
		try {
			container = new SQLContainer(tq);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		container.setAutoCommit(false);
		return container;
	}
}
