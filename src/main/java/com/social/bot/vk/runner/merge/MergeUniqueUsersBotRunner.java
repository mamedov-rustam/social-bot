package com.social.bot.vk.runner.merge;

import com.social.bot.vk.model.User;
import com.social.bot.vk.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Order(2)
public class MergeUniqueUsersBotRunner implements ApplicationRunner {
    @Value("${vk.bot.merge.users.enable}")
    private boolean isEnabled;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (!isEnabled) {
            return;
        }

        long startTime = System.currentTimeMillis();
        System.out.println("\n\n\n-------------------------------------------------");
        System.out.println("Start merging source users");

        List<User> usersList = userRepository.loadSourceUsers();
        Set<User> users = new HashSet<>(usersList);
        List<User> uniqueUsers = new ArrayList<>(users);
        userRepository.saveMergedUsers(uniqueUsers);

        System.out.println("Merged from " + usersList.size() + " to " + users.size());
        System.out.println("Time spent for merge: " + (System.currentTimeMillis() - startTime) + " mills.");
        System.out.println("-------------------------------------------------");

        System.exit(0);
    }
}
