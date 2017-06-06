package com.social.bot.vk.client;

import feign.Feign;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.cloud.netflix.feign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VkHttpClientConfiguration {
    @Bean
    public VkHttpClient clientReportClient() {
        return Feign.builder()
                .decoder(new JacksonDecoder())
                .encoder(new FormEncoder(new JacksonEncoder()))
                .target(VkHttpClient.class, "https://api.vk.com/method");
    }
}
