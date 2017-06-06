package com.social.bot.vk.runner.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.bot.vk.common.VkSearchHttpClient;
import com.social.bot.vk.common.VkSearchRequest;
import com.social.bot.vk.model.InfoField;
import com.social.bot.vk.model.User;
import com.social.bot.vk.service.UserService;
import com.social.bot.vk.service.VkSearchRequestService;
import com.social.bot.vk.utils.VkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class FilterBotApplicationRunner implements ApplicationRunner {
    @Value("${bot.filter.users.enable}")
    private boolean isEnabled;
    @Value("${bot.filter.users.file}")
    private String pathToMergedFile;
    @Value("${bot.filter.batch.size}")
    private Integer batchSize;
    
    @Autowired
    private VkSearchHttpClient vkSearchHttpClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private VkSearchRequestService vkSearchRequestService;
    @Autowired
    private UserService userService;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (!isEnabled) {
            return;
        }

        long startAppTime = System.currentTimeMillis();

        List<User> loadedUsers = Arrays.asList(objectMapper.readValue(new File(pathToMergedFile), User[].class));
        int totalRequests = (int) Math.ceil(loadedUsers.size() / batchSize);

        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-");
        System.out.println("Total users: " + loadedUsers.size());
        System.out.println("Batch size: " + batchSize);
        System.out.println("Total requests: " + totalRequests);
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-");

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
            userService.save(filteredUsers);
            double spentTime = (System.currentTimeMillis() - startTime) / 1000d;
            System.out.println("Spent time: " + spentTime + " seconds.");
            System.out.println("Remains requests: " + (totalRequests - (currentCounter+1)));
            System.out.println("---------------------------------------");
            VkUtils.randomSleep(350L);
        }

        double totalTimeSpent = (System.currentTimeMillis() - startAppTime) / 1000d;
        System.out.println("Total time spent: " + totalTimeSpent + " seconds.");

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
                    return year <= 1996;
                })
                .collect(toList());
    }

    private List<String> mapToIds(List<User> users, int from, int to) {
        List<User> usersPeriod = users.size() <= to ? users.subList(from, users.size() - 1) : users.subList(from, to);
        return usersPeriod.stream().map(User::getId).collect(toList());
    }
}
