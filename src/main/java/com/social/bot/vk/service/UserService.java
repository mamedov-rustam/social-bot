package com.social.bot.vk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.bot.vk.common.VkSearchHttpClient;
import com.social.bot.vk.common.VkSearchRequest;
import com.social.bot.vk.common.VkSearchResponse;
import com.social.bot.vk.common.VkSearchResponseWrapper;
import com.social.bot.vk.model.User;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.function.Function;

@Service
public class UserService {
    @Value("${db.users}")
    private String pathToUsersFile;
    @Value("${vk.page.size}")
    private Long pageSize;

    private final VkSearchHttpClient vkHttpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserService(VkSearchHttpClient vkHttpClient, ObjectMapper objectMapper) {
        this.vkHttpClient = vkHttpClient;
        this.objectMapper = objectMapper;
    }

    public VkSearchResponseWrapper findUsersInSearch(VkSearchRequest vkSearchRequest) {
        return vkHttpClient.searchForPeople(vkSearchRequest);
    }

    public VkSearchResponseWrapper findUsersInGroup(VkSearchRequest vkSearchRequest) {
        return vkHttpClient.searchForPeopleInGroup(vkSearchRequest);
    }

    public Long findTotalPagesWithUsersForGroup(VkSearchRequest vkSearchRequest) {
        VkSearchResponseWrapper vkSearchResponseWrapper = vkHttpClient.searchForPeopleInGroup(vkSearchRequest);
        return (long) Math.ceil(vkSearchResponseWrapper.getResponse().getCount() / (double)pageSize);
    }

    @SneakyThrows
    public void save(List<User> users) {
        File file = new File(pathToUsersFile);

        if (!file.exists()) {
            objectMapper.writer().writeValue(file, users);
            return;
        }

        Set<User> newUsers = new HashSet<>(users);
        List<User> savedUsers = Arrays.asList(objectMapper.readValue(file, User[].class));
        newUsers.addAll(savedUsers);

        objectMapper.writer().withDefaultPrettyPrinter().writeValue(file, newUsers);
    }
}
