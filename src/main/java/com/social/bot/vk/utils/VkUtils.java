package com.social.bot.vk.utils;


import com.social.bot.vk.model.User;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

public class VkUtils {

    public static List<User> withInstagram(List<User> users) {
        return users.stream()
                .filter(user -> StringUtils.isNotEmpty(user.getInstagram()))
                .collect(toList());
    }

    public static boolean isJsonFileExist(String path) {
        File file = new File(path + ".json");
        return file.isFile() && file.exists();
    }

    @SneakyThrows
    public static void randomSleep(Long mills) {
        Random random = new Random();
        double randomDouble = random.nextDouble();
        Thread.sleep(Math.round(randomDouble * mills));
    }
}
