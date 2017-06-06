package com.social.bot;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableFeignClients
@SpringBootApplication
public class SocialBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialBotApplication.class, args);
	}

	@Bean
	HttpClient httpClient() {
		return HttpClientBuilder.create().build();
	}
}
