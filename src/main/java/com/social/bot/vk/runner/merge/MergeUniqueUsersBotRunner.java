package com.social.bot.vk.runner.merge;

import com.social.bot.vk.model.VkUser;
import com.social.bot.vk.service.VkUserRepository;
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
    private VkUserRepository userRepository;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (!isEnabled) {
            return;
        }

        long startTime = System.currentTimeMillis();
        System.out.println("\n\n\n-------------------------------------------------");
        System.out.println("Start merging source users");

        List<VkUser> usersList = userRepository.loadSourceUsers();
        Set<VkUser> users = new HashSet<>(usersList);
        List<VkUser> uniqueUsers = new ArrayList<>(users);
        userRepository.saveMergedUsers(uniqueUsers);

        System.out.println("Merged from " + usersList.size() + " to " + users.size());
        System.out.println("Time spent for merge: " + (System.currentTimeMillis() - startTime) + " mills.");
        System.out.println("-------------------------------------------------");
    }
}
