package com.ar.base;

import com.ar.base.config.JksProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication()
@EnableConfigurationProperties(JksProperties.class)
@EnableFeignClients
public class MsvcAccountingBaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcAccountingBaseApplication.class, args);
	}

}
