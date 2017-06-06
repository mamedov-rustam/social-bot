package com.social.bot.vk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.bot.vk.client.VkHttpClient;
import com.social.bot.vk.client.VkSearchRequest;
import com.social.bot.vk.client.VkSearchResponseWrapper;
import com.social.bot.vk.client.model.User;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    @Value("${db.users}")
    private String pathToUsersFile;
    @Value("${vk.page.size}")
    private Long pageSize;

    private final VkHttpClient vkHttpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserService(VkHttpClient vkHttpClient, ObjectMapper objectMapper) {
        this.vkHttpClient = vkHttpClient;
        this.objectMapper = objectMapper;
    }

    // ToDo: change to simple List<User>
    public VkSearchResponseWrapper findUsers(VkSearchRequest vkSearchRequest) {
        return vkHttpClient.searchForUsers(vkSearchRequest);
    }

    public Long findTotalPages(VkSearchRequest vkSearchRequest) {
        VkSearchRequest requestClone = vkSearchRequest.clone();
        requestClone.setCount(1L);
        requestClone.setOffset(0L);

        Long totalCount = vkHttpClient.searchForUsers(requestClone).getResponse().getCount();
        return totalCount / pageSize;
    }

    @SneakyThrows
    public void save(List<User> users) {
        File file = new File(pathToUsersFile);

        if (!file.exists()) {
            objectMapper.writer().writeValue(file, users);
            return;
        }

        List<User> newUsers = new ArrayList<>(users);
        List<User> savedUsers = Arrays.asList(objectMapper.readValue(file, User[].class));
        newUsers.addAll(savedUsers);

        objectMapper.writer().withDefaultPrettyPrinter().writeValue(file, newUsers);
    }
}
