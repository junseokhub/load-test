package com.mvc.load;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class LoadApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoadApplication.class, args);
	}

}
