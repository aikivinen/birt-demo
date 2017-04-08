package com.github.aikivinen.birtdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;



@Configuration
public class AppUIConfig {
	

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setDefaultEncoding("UTF-8"); 
		messageSource.setBasename("messages");
		return messageSource;
	}
}
