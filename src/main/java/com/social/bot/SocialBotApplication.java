package com.social.bot;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SocialBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialBotApplication.class, args);
	}

	@Bean
	HttpClient httpClient() {
		return HttpClientBuilder.create().build();
	}

	@Bean
	ApplicationRunner shutDownRunner() {
		return (args) -> System.exit(0);
	}
}
