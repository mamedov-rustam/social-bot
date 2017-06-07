package com.social.bot.vk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.bot.repository.AbstractRepository;
import com.social.bot.vk.model.VkUser;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class VkUserRepositoryImpl extends AbstractRepository<VkUser> implements VkUserRepository {
    @Value("${vk.source.users.directory}")
    private String directoryWithSourceUsers;
    @Value("${vk.merged.users.output}")
    private String fileWithMergedUsers;

    public VkUserRepositoryImpl(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void saveSourceUsers(List<VkUser> users, String groupName) {
        String path = directoryWithSourceUsers + "/" + groupName + ".json";
        save(users, VkUser[].class, path);
    }

    @Override
    public void saveMergedUsers(List<VkUser> users) {
        save(users, VkUser[].class, fileWithMergedUsers, true);
    }

    @Override
    public List<VkUser> loadSourceUsers() {
        File[] files = new File(directoryWithSourceUsers).listFiles();
        return Arrays.stream(files)
                .map(this::load)
                .flatMap(List::stream)
                .collect(toList());
    }

    @Override
    public List<VkUser> loadMergedUsers() {
        return load(fileWithMergedUsers);
    }

    @SneakyThrows
    private List<VkUser> load(File file) {
        return load(VkUser[].class, file);
    }

    @SneakyThrows
    private List<VkUser> load(String filePath) {
        return load(VkUser[].class, filePath);
    }
}
