package com.social.bot.vk;

import com.social.bot.vk.client.VkSearchRequest;
import com.social.bot.vk.client.VkSearchResponse;
import com.social.bot.vk.client.VkSearchResponseWrapper;
import com.social.bot.vk.client.model.Country;
import com.social.bot.vk.client.model.Sex;
import com.social.bot.vk.client.model.Status;
import com.social.bot.vk.client.model.User;
import com.social.bot.vk.service.UserService;
import com.social.bot.vk.service.VkSearchRequestService;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

@Service
public class VkBotRunner implements ApplicationRunner {
    @Autowired
    private UserService userService;
    @Autowired
    private VkSearchRequestService vkSearchRequestService;

    @SneakyThrows
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        Long proccessedUsers = 0L;

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

                        VkSearchResponse resp = userService.findUsers(pageRequest).getResponse();
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
                        proccessedUsers += users.size();
                        List<User> usersWithInstagram = withInstagram(users);

                        userService.save(usersWithInstagram);
                        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
                        System.out.println("Total proccessed users: " + proccessedUsers);
                        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
                        randomSleep(700L);
                    }
                }
            }
        }

        System.exit(0);
    }

    @SneakyThrows
    private void randomSleep(Long mills) {
        Random random = new Random();
        double randomDouble = random.nextDouble();
        Thread.sleep(Math.round(randomDouble * mills));
    }

    private List<User> withInstagram(List<User> users) {
        return users.stream()
                .filter(user -> StringUtils.isNotEmpty(user.getInstagram()))
                .collect(toList());
    }
}
