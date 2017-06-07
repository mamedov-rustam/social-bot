package com.social.bot.vk.service;

import com.social.bot.vk.common.VkSearchHttpClient;
import com.social.bot.vk.common.VkSearchRequest;
import com.social.bot.vk.common.VkSearchResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserSearchService {
    @Value("${vk.fetch.page.size}")
    private Long pageSize;

    private final VkSearchHttpClient vkHttpClient;

    @Autowired
    public UserSearchService(VkSearchHttpClient vkHttpClient) {
        this.vkHttpClient = vkHttpClient;
    }

    public VkSearchResponseWrapper findUsersInSearch(VkSearchRequest vkSearchRequest) {
        return vkHttpClient.searchForPeople(vkSearchRequest);
    }

    public VkSearchResponseWrapper findUsersInGroup(VkSearchRequest vkSearchRequest) {
        return vkHttpClient.searchForPeopleInGroup(vkSearchRequest);
    }

    public Long findTotalPagesWithUsersForGroup(VkSearchRequest vkSearchRequest) {
        VkSearchResponseWrapper vkSearchResponseWrapper = vkHttpClient.searchForPeopleInGroup(vkSearchRequest);
        if (vkSearchResponseWrapper.getError() != null) {
            throw new RuntimeException(vkSearchResponseWrapper.getError().getMessage());
        }

        return (long) Math.ceil(vkSearchResponseWrapper.getResponse().getCount() / (double)pageSize);
    }
}
