package com.social.bot.vk;

import com.social.bot.vk.client.VkHttpClient;
import com.social.bot.vk.client.VkSearchResponse;
import com.social.bot.vk.client.VkSearchRequest;
import com.social.bot.vk.client.VkSearchResponseWrapper;
import com.social.bot.vk.client.model.InfoField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class VkBotRunner implements ApplicationRunner {
    @Value("${vk.api.access.token}")
    private String accessToken;
    @Value("${vk.api.version}")
    private String apiVersion;
    @Autowired
    private VkHttpClient vkHttpClient;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        VkSearchRequest request = VkSearchRequest.builder()
                .version(apiVersion)
                .accessToken(accessToken)
                .fields(Arrays.asList(InfoField.PHOTO))
                .build();
        VkSearchResponseWrapper vkSearchResponseWrapper = vkHttpClient.searchForUsers(request);

        if (vkSearchResponseWrapper.hasError()) {
            System.out.println(vkSearchResponseWrapper.getError());
            return;
        }

        VkSearchResponse response = vkSearchResponseWrapper.getResponse();
        System.out.println(response);
    }
}
