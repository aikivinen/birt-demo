package com.github.aikivinen.birtdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;


@EnableAutoConfiguration(exclude=MongoAutoConfiguration.class) // a bug in spring boot?: https://github.com/spring-projects/spring-boot/issues/2196
@ComponentScan("com.github.aikivinen.birtdemo")
public class App extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
