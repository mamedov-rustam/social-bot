package com.social.bot.vk.runner.group;

import com.social.bot.vk.common.VkSearchRequest;
import com.social.bot.vk.common.VkSearchResponseWrapper;
import com.social.bot.vk.model.User;
import com.social.bot.vk.service.UserService;
import com.social.bot.vk.service.VkSearchRequestService;
import com.social.bot.vk.utils.VkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VkGroupMemberSearchBotRunner implements ApplicationRunner {
    @Value("${vk.bot.group.members.search.enable}")
    private boolean isEnabled;
    @Value("${vk.bot.group.id}")
    private String groupId;

    @Autowired
    private VkSearchRequestService vkSearchRequestService;
    @Autowired
    private UserService userService;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (!isEnabled) {
            return;
        }

        VkSearchRequest requestForTotalPages = vkSearchRequestService.createRequest();
        requestForTotalPages.setGroupId(groupId);

        Long totalPages = userService.findTotalPagesWithUsersForGroup(requestForTotalPages);

        long appStartTime = System.currentTimeMillis();
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
        System.out.println("Group: " + groupId);
        System.out.println("Pages for fetching: " + totalPages);
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");

        for (Long currentPage = 0L; currentPage < totalPages; currentPage++) {
            VkSearchRequest request = vkSearchRequestService.createRequest(currentPage);
            request.setGroupId(groupId);

            System.out.println("\n-----------------------------");
            long startTime = System.currentTimeMillis();
            System.out.println("Start fetching page #" + (currentPage + 1));
            VkSearchResponseWrapper vkSearchResponseWrapper = userService.findUsersInGroup(request);

            List<User> users = vkSearchResponseWrapper.getResponse().getUsers();
            System.out.println("Fetched: " + users.size());
            List<User> usersWithInstagram = VkUtils.withInstagram(users);
            System.out.println("With instagram: " + usersWithInstagram.size());

            userService.save(usersWithInstagram);
            double spentTime = (System.currentTimeMillis() - (double) startTime) / 1000;
            System.out.println("Saved successfully.");
            System.out.println("Time spent: " + spentTime + " seconds");
            System.out.println("Remains pages: " + (totalPages - currentPage - 1));
            System.out.println("-----------------------------");
            VkUtils.randomSleep(500L);
        }

        double appSpentTime = (System.currentTimeMillis() - (double) appStartTime) / 1000;

        System.out.println("Total time spent: " + appSpentTime + " seconds");
        System.exit(0);
    }
}
