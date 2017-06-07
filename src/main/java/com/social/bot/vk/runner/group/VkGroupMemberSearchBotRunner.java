package com.social.bot.vk.runner.group;

import com.social.bot.vk.common.VkSearchRequest;
import com.social.bot.vk.common.VkSearchResponseWrapper;
import com.social.bot.vk.model.User;
import com.social.bot.vk.service.UserRepository;
import com.social.bot.vk.service.UserSearchService;
import com.social.bot.vk.service.VkSearchRequestService;
import com.social.bot.vk.utils.VkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Order(1)
public class VkGroupMemberSearchBotRunner implements ApplicationRunner {
    @Value("${vk.bot.group.members.search.enable}")
    private boolean isEnabled;
    @Value("${vk.filter.birthday.year.max}")
    private Integer birthdayYearMax;
    @Value("${vk.filter.birthday.year.min}")
    private Integer birthdayYearMin;
    @Value("${vk.source.users.directory}")
    private String directoryWithSourceUsers;
    @Value("${vk.group.ids}")
    private String groupIds;

    @Autowired
    private VkSearchRequestService vkSearchRequestService;
    @Autowired
    private UserSearchService userSearchService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (!isEnabled) {
            return;
        }

        Arrays.stream(groupIds.split(","))
                .map(String::trim)
                .filter(groupId -> !VkUtils.isJsonFileExist(directoryWithSourceUsers + "/" + groupId))
                .forEach(this::start);
    }

    private void start(String groupId) {
        VkSearchRequest requestForTotalPages = vkSearchRequestService.createRequest();
        requestForTotalPages.setGroupId(groupId);

        Long totalPages = userSearchService.findTotalPagesWithUsersForGroup(requestForTotalPages);

        long appStartTime = System.currentTimeMillis();
        System.out.println("\n\n\n*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
        System.out.println("Group: " + groupId);
        System.out.println("Pages for fetching: " + totalPages);
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");

        for (Long currentPage = 0L; currentPage < totalPages; currentPage++) {
            VkSearchRequest request = vkSearchRequestService.createRequest(currentPage);
            request.setGroupId(groupId);

            System.out.println("\n-----------------------------");
            long startTime = System.currentTimeMillis();
            System.out.println("Start fetching page #" + (currentPage + 1) + " for " + groupId);
            VkSearchResponseWrapper vkSearchResponseWrapper = userSearchService.findUsersInGroup(request);

            List<User> users = vkSearchResponseWrapper.getResponse().getUsers();
            System.out.println("Fetched: " + users.size());
            List<User> filteredUsers = filterUsers(users);
            System.out.println("With instagram: " + filteredUsers.size());

            userRepository.saveSourceUsers(filteredUsers, groupId);
            double spentTime = (System.currentTimeMillis() - (double) startTime) / 1000;
            System.out.println("Saved successfully.");
            System.out.println("Time spent: " + spentTime + " seconds");
            System.out.println("Remains pages: " + (totalPages - currentPage - 1));
            System.out.println("-----------------------------");
            VkUtils.randomSleep(350L);
        }

        double appSpentTime = (System.currentTimeMillis() - (double) appStartTime) / 1000;

        System.out.println("Total time spent for fetching sources: " + appSpentTime + " seconds");
    }

    private List<User> filterUsers(List<User> users) {
        return users.stream()
                .filter(VkUtils::hasInstagram)
                .filter(user -> VkUtils.isGoodDate(user.getBirthdayDate(), birthdayYearMin, birthdayYearMax))
                .collect(Collectors.toList());
    }
}
