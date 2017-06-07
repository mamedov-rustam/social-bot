package com.social.bot.vk.service;


import com.social.bot.vk.model.VkUser;

import java.util.List;

public interface VkUserRepository {
    void saveSourceUsers(List<VkUser> users, String groupName);

    void saveMergedUsers(List<VkUser> users);

    List<VkUser> loadSourceUsers();

    List<VkUser> loadMergedUsers();
}
