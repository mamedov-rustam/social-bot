package com.social.bot.vk.runner.people;

import com.social.bot.vk.common.VkSearchRequest;
import com.social.bot.vk.common.VkSearchResponse;
import com.social.bot.vk.model.Country;
import com.social.bot.vk.model.Sex;
import com.social.bot.vk.model.Status;
import com.social.bot.vk.model.User;
import com.social.bot.vk.service.UserRepository;
import com.social.bot.vk.service.VkSearchRequestService;
import com.social.bot.vk.service.UserSearchService;
import com.social.bot.vk.utils.VkUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Order(1)
public class VkPeopleSearchBotRunner implements ApplicationRunner {
    @Value("${vk.bot.people.search.enable}")
    private boolean isEnabled;

    @Autowired
    private UserSearchService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VkSearchRequestService vkSearchRequestService;

    @SneakyThrows
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (!isEnabled) {
            return;
        }

        Long sourceUsers = 0L;
        Country country = Country.UA;
        String cityName = "Kharkiv";

        for (Sex sex : Sex.values()) {
            for (Status status : Status.values()) {
                for (int age = 21; age < 30; age++) {
                    for (int month = 1; month < 12; month++) {
                        VkSearchRequest pageRequest = vkSearchRequestService.createGeoRequest(country, cityName);
                        pageRequest.setSex(sex);
                        pageRequest.setStatus(status);
                        pageRequest.setMinAge(age);
                        pageRequest.setMaxAge(age);
                        pageRequest.setBirthdayMonth(month);

                        VkSearchResponse resp = userService.findUsersInSearch(pageRequest).getResponse();
                        System.out.println("********************");
                        System.out.println("Total items: " + resp.getCount());
                        long missedUsers = resp.getCount() - 1000;
                        if (missedUsers > 0) {
                            System.out.println("Missed users: " + missedUsers);
                        }
                        System.out.println("--------------------");
                        System.out.println("Sex: " + sex);
                        System.out.println("Status: " + status);
                        System.out.println("Age: " + age);
                        System.out.println("Birthday month: " + month);
                        System.out.println("********************");

                        List<User> users = resp.getUsers();
                        sourceUsers += users.size();
                        List<User> usersWithInstagram = VkUtils.withInstagram(users);

                        userRepository.saveSourceUsers(usersWithInstagram, "from_search");
                        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
                        System.out.println("Total saved source users: " + sourceUsers);
                        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
                        VkUtils.randomSleep(500L);
                    }
                }
            }
        }

        System.exit(0);
    }
}
