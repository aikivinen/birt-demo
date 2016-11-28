package com.github.aikivinen.birtdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.aikivinen.birtdemo.BirtEngineFactory;

@Configuration
public class BirtConfig {

	@Bean
	protected BirtEngineFactory engineFactory(){ 
		BirtEngineFactory factory = new BirtEngineFactory() ;  
		return factory ; 
	}
}
