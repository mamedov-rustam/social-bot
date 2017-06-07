package com.social.bot.instagram.repository;


import com.social.bot.instagram.model.InstagramUser;

import java.util.List;

public interface InstagramUserRepository {
    void saveFilteredUsers(List<InstagramUser> instagramUsers);
}
