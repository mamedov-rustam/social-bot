package com.social.bot.vk.service;


import com.social.bot.vk.model.User;

import java.util.List;

public interface UserRepository {
    void saveSourceUsers(List<User> users, String groupName);

    void saveMergedUsers(List<User> users);

    void saveFilteredUsers(List<User> users);

    List<User> loadSourceUsers();

    List<User> loadMergedUsers();

    List<User> loadFilteredUsers();
}
