package com.social.bot.instagram.runner.unfollower;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.social.bot.instagram.common.InstagramBotUtils;
import com.social.bot.instagram.runner.unfollower.view.Following;
import com.social.bot.vk.utils.VkUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.social.bot.instagram.common.InstagramBotUtils.USER_PROFILE_TEMPLATE_URL;
import static com.social.bot.instagram.common.InstagramSelectors.Css.FOLLOW_BUTTON;
import static com.social.bot.instagram.common.InstagramSelectors.Css._FOLLOW_BUTTON;


@Order(5)
@Service
public class UnfollowerBotRunner implements ApplicationRunner {
    @Value("${instagram.bot.unfollower.enable}")
    private boolean isEnabled;
    @Value("${instagram.bot.unfollow.maximum}")
    private Long unfollowMaximum;
    @Value("${instagram.bot.unfollow.delay}")
    private Long delayInSeconds;
    @Value("${instagram.user.login}")
    private String userLogin;
    @Value("${instagram.user.password}")
    private String userPassword;
    @Value("${webdriver.chrome.driver}")
    private String chromeDriverPath;
    @Value("classpath:script/load-all-followings.js")
    private Resource loadAllFollowingScript;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!isEnabled) {
            return;
        }

        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeDriver driver = new ChromeDriver();

        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        System.out.println("Instagram unfollower bot started.");
        System.out.println("Users for unfollowing: " + unfollowMaximum);
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        long appStartTime = System.currentTimeMillis();

        InstagramBotUtils.login(driver, userLogin, userPassword);

        String loadAllFollowingsScript = FileUtils.readFileToString(loadAllFollowingScript.getFile());
        String jsonFollowings = (String) driver.executeScript(loadAllFollowingsScript);
        List<Following> followings = mapToFollowingList(jsonFollowings);


        long maxUnfollowCounter = unfollowMaximum;
        for(Following following : followings) {
            try {
                if (maxUnfollowCounter == 0) {
                    return;
                }

                String url = String.format(USER_PROFILE_TEMPLATE_URL, following.getUsername());
                driver.get(url);

                WebElement followButton = findFollowButton(driver);
                followButton.click();

                maxUnfollowCounter--;
                while (followButton.getText().equals("Following")) {
                    VkUtils.randomSleep(1000L);
                }

                System.out.println("\n---------------------------------");
                System.out.println("Unfollowed from " + following.getUsername());
                System.out.println("Remains: " + maxUnfollowCounter);
                System.out.println("---------------------------------");

                if (maxUnfollowCounter != 0) {
                    Thread.sleep(delayInSeconds * 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long spentTimeInSeconds = (System.currentTimeMillis() - appStartTime) / 1000;
        long spentMinutes = spentTimeInSeconds / 60;
        long spentSeconds = spentTimeInSeconds % 60;
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        System.out.println("Instagram unfollower bot complete.");
        System.out.println("Unfollowed from " + unfollowMaximum + " users.");
        System.out.println("Total time spent: " + spentMinutes + " minutes " + spentSeconds + " seconds.");
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");

        driver.quit();
    }

    private WebElement findFollowButton(ChromeDriver driver) {
        List<WebElement> followButtonList = driver.findElementsByCssSelector(FOLLOW_BUTTON);
        if (followButtonList.isEmpty()) {
            return driver.findElementByCssSelector(_FOLLOW_BUTTON);
        }

        return followButtonList.get(0);
    }

    @SneakyThrows
    private List<Following> mapToFollowingList(String jsonFollowings) {
        List<Following> followings = new ArrayList<>();
        ArrayNode edges = (ArrayNode) objectMapper.readTree(jsonFollowings).get("data").get("user").get("edge_follow").get("edges");
        for (JsonNode edge : edges) {
            JsonNode node = edge.get("node");
            Following following = objectMapper.convertValue(node, Following.class);
            followings.add(following);
        }

        Collections.reverse(followings);
        return followings;
    }
}
