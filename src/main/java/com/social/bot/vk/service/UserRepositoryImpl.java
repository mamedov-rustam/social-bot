package com.social.bot.vk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.bot.vk.model.User;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class UserRepositoryImpl implements UserRepository {
    @Value("${vk.source.users.directory}")
    private String directoryWithSourceUsers;
    @Value("${vk.merged.users.file}")
    private String fileWithMergedUsers;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void saveSourceUsers(List<User> users, String groupName) {
        String path = directoryWithSourceUsers + "/" + groupName + ".json";
        save(users, path);
    }

    @Override
    public void saveMergedUsers(List<User> users) {
        save(users, fileWithMergedUsers);
    }

    @Override
    public List<User> loadSourceUsers() {
        File[] files = new File(directoryWithSourceUsers).listFiles();
        return Arrays.stream(files)
                .map(this::load)
                .flatMap(List::stream)
                .collect(toList());
    }

    @Override
    public List<User> loadMergedUsers() {
        return load(fileWithMergedUsers);
    }

    @SneakyThrows
    private List<User> load(File file) {
        return new ArrayList<>(Arrays.asList(objectMapper.readValue(file, User[].class)));
    }

    @SneakyThrows
    private List<User> load(String filePath) {
        return load(new File(filePath));
    }

    @SneakyThrows
    private void save(List<User> users, String path) {
        File file = new File(path);

        if (!file.exists()) {
            File dir = new File(file.getParent());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            objectMapper.writer().writeValue(file, users);
            return;
        }

        Set<User> newUsers = new HashSet<>(users);
        List<User> savedUsers = Arrays.asList(objectMapper.readValue(file, User[].class));
        newUsers.addAll(savedUsers);

        objectMapper.writer().withDefaultPrettyPrinter().writeValue(file, newUsers);
    }
}
