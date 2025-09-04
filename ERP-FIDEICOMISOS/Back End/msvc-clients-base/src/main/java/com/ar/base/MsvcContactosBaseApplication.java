package com.ar.base;

import com.ar.base.config.JksProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication()
@EnableConfigurationProperties(JksProperties.class)
public class MsvcContactosBaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcContactosBaseApplication.class, args);
	}

}
