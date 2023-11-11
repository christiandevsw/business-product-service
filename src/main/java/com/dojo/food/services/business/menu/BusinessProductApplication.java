package com.dojo.food.services.business.menu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.dojo.food.services")
public class BusinessProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusinessProductApplication.class, args);
	}

}
