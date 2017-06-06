package com.social.bot.vk.runner.merge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.bot.vk.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class MergeUniqueUsersBotRunner implements ApplicationRunner {
    @Value("${bot.merge.users.directory}")
    private String pathToUserFiles;
    @Value("${bot.merge.users.enable}")
    private boolean isEnabled;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (!isEnabled) {
            return;
        }

        File[] files = new File(pathToUserFiles).listFiles();

        if (files == null) {
            System.out.println("There is no files in " + pathToUserFiles);
            return;
        }

        long startTime = System.currentTimeMillis();
        Set<User> users = new HashSet<>();
        Long objectCount = 0L;
        for (File file : files) {
            System.out.println("Loaded " + file.getName());
            List<User> loadedUsers = Arrays.asList(objectMapper.readValue(file, User[].class));
            objectCount += loadedUsers.size();
            users.addAll(loadedUsers);
        }

        objectMapper.writeValue(new File(pathToUserFiles + "/_result_.json"), users);

        System.out.println("Merged " + objectCount + " objects to " + users.size() + " objects.");
        long spentTime = System.currentTimeMillis() - startTime;
        System.out.println("Spent " + spentTime + " mills.");
    }
}
