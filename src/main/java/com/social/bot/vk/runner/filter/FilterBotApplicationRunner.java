package com.social.bot.vk.runner.filter;

import com.social.bot.vk.common.VkSearchHttpClient;
import com.social.bot.vk.common.VkSearchRequest;
import com.social.bot.vk.model.InfoField;
import com.social.bot.vk.model.User;
import com.social.bot.vk.service.UserRepository;
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

import static java.util.stream.Collectors.toList;

@Service
@Order(3)
public class FilterBotApplicationRunner implements ApplicationRunner {
    @Value("${vk.bot.filter.users.enable}")
    private boolean isEnabled;
    @Value("${vk.bot.filter.batch.size}")
    private Integer batchSize;
    @Value("${vk.bot.filter.birthday.year.max}")
    private Integer birthdayYearMax;
    @Value("${vk.bot.filter.birthday.year.min}")
    private Integer birthdayYearMin;

    @Autowired
    private VkSearchHttpClient vkSearchHttpClient;
    @Autowired
    private VkSearchRequestService vkSearchRequestService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (!isEnabled) {
            return;
        }

        long startAppTime = System.currentTimeMillis();

        List<User> loadedUsers = userRepository.loadMergedUsers();
        int totalRequests = (int) Math.ceil(loadedUsers.size() / batchSize);

        System.out.println("\n\n\n-*-*-*-*-*-*-*-*-*-*-*-*-");
        System.out.println("Total users: " + loadedUsers.size());
        System.out.println("Batch size: " + batchSize);
        System.out.println("Total requests: " + totalRequests);
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-");

        long totalSavedUsers = 0L;
        for (int currentCounter = 0; currentCounter < totalRequests; currentCounter++) {
            long startTime = System.currentTimeMillis();
            System.out.println("\n---------------------------------------");
            VkSearchRequest request = vkSearchRequestService.createRequest();
            request.setFields(Arrays.asList(InfoField.BIRTHDAY_DATE, InfoField.CONNECTIONS));
            int from = currentCounter * batchSize;
            int to = from + batchSize;
            System.out.println("Filter request #" + (currentCounter + 1));
            List<String> ids = mapToIds(loadedUsers, from, to);
            request.setUserIds(ids);

            VkSearchBatchUsersResponse vkSearchBatchUsersResponse = vkSearchHttpClient.searchForPeopleBatch(request);
            List<User> users = vkSearchBatchUsersResponse.getUsers();
            List<User> filteredUsers = filterByDate(users);
            System.out.println("Filter from " + batchSize + " to " + filteredUsers.size());
            totalSavedUsers += filteredUsers.size();
            userRepository.saveFilteredUsers(filteredUsers);
            double spentTime = (System.currentTimeMillis() - startTime) / 1000d;
            System.out.println("Spent time: " + spentTime + " seconds.");
            System.out.println("Remains requests: " + (totalRequests - (currentCounter + 1)));
            System.out.println("---------------------------------------");
            VkUtils.randomSleep(350L);
        }

        double totalTimeSpent = (System.currentTimeMillis() - startAppTime) / 1000d;
        System.out.println("Total saved users: " + totalSavedUsers);
        System.out.println("Total time spent for filtering: " + totalTimeSpent + " seconds.");

        System.exit(0);
    }

    private List<User> filterByDate(List<User> users) {
        return users.stream()
                .filter(user -> {
                    String birthdayDate = user.getBirthdayDate();
                    return birthdayDate != null && birthdayDate.length() >= 8;
                })
                .filter(user -> {
                    String birthdayDate = user.getBirthdayDate();
                    int year = Integer.parseInt(birthdayDate.split("\\.")[2]);
                    return year >= birthdayYearMin && year <= birthdayYearMax;
                })
                .collect(toList());
    }

    private List<String> mapToIds(List<User> users, int from, int to) {
        List<User> usersPeriod = users.size() <= to ? users.subList(from, users.size() - 1) : users.subList(from, to);
        return usersPeriod.stream().map(User::getId).collect(toList());
    }
}
