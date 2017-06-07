package com.social.bot.instagram.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.bot.instagram.model.InstagramUser;
import com.social.bot.repository.AbstractRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstagramUserRepositoryImpl extends AbstractRepository<InstagramUser> implements InstagramUserRepository{
    @Value("${instagram.filter.output}")
    private String instagramFilteredOutFile;

    public InstagramUserRepositoryImpl(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    public void saveFilteredUsers(List<InstagramUser> instagramUsers) {
        save(instagramUsers, InstagramUser[].class, instagramFilteredOutFile);
    }
}
