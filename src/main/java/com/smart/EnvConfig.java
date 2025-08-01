package com.smart;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
	
	static {
		Dotenv dotenv = Dotenv.configure()
				.directory(".")
				.load();
		
		
		System.setProperty("DATASOURCE_URL", dotenv.get("DATASOURCE_URL"));
        System.setProperty("DATASOURCE_USER", dotenv.get("DATASOURCE_USER"));
        System.setProperty("DATASOURCE_PASSWORD", dotenv.get("DATASOURCE_PASSWORD"));
		
		
	}

}
