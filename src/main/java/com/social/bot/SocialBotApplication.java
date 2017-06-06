package com.social.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SocialBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialBotApplication.class, args);
	}
}
