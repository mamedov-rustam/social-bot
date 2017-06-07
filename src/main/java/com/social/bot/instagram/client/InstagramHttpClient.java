package com.social.bot.instagram.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.bot.instagram.model.InstagramUserResponseWrapper;
import lombok.SneakyThrows;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class InstagramHttpClient {
    private static final String INSTAGRAM_USER_INFO_URL_TEMPLATE = "https://www.instagram.com/%s/?__a=1";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public InstagramHttpClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public InstagramUserResponseWrapper loadUserInfo(String username) {
        String url = String.format(INSTAGRAM_USER_INFO_URL_TEMPLATE, username);
        HttpGet request = new HttpGet(url);
        InputStream input = httpClient.execute(request).getEntity().getContent();

        try {
            return objectMapper.readValue(input, InstagramUserResponseWrapper.class);
        } catch (IOException e) {
            return InstagramUserResponseWrapper.error(e.getMessage());
        }
    }
}
