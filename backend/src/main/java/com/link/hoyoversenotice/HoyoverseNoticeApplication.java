package com.link.hoyoversenotice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HoyoverseNoticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoyoverseNoticeApplication.class, args);
	}

}