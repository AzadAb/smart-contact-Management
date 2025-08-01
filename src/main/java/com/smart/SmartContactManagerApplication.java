package com.smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartContactManagerApplication {

	public static void main(String[] args) {
		EnvConfig configs = new EnvConfig(); 
		SpringApplication.run(SmartContactManagerApplication.class, args);
	}

}
