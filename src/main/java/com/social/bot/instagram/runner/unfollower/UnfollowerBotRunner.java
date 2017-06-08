package com.social.bot.instagram.runner.unfollower;

import com.social.bot.instagram.common.InstagramBotUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Order(5)
@Service
public class UnfollowerBotRunner implements ApplicationRunner {
    @Value("${instagram.bot.unfollower.enable}")
    private boolean isEnabled;
    @Value("${instagram.user.login}")
    private String userLogin;
    @Value("${instagram.user.password}")
    private String userPassword;
    @Value("${webdriver.chrome.driver}")
    private String chromeDriverPath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!isEnabled) {
            return;
        }

        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeDriver driver = new ChromeDriver();

        InstagramBotUtils.login(driver, userLogin, userPassword);
        System.out.println("All right!");

        driver.close();
    }
}
