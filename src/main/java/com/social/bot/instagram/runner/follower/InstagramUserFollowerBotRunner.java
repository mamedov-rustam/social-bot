package com.social.bot.instagram.runner.follower;

import com.social.bot.instagram.model.InstagramUser;
import com.social.bot.instagram.repository.InstagramUserRepository;
import com.social.bot.instagram.common.InstagramBotUtils;
import com.social.bot.vk.utils.VkUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.social.bot.instagram.common.InstagramBotUtils.USER_PROFILE_TEMPLATE_URL;
import static com.social.bot.instagram.common.InstagramBotUtils.findFollowButton;
import static com.social.bot.instagram.common.InstagramSelectors.*;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

@Service
public class InstagramUserFollowerBotRunner implements ApplicationRunner {
    @Value("${instagram.bot.follower.enable}")
    private boolean isEnabled;
    @Value("${instagram.user.login}")
    private String accountLogin;
    @Value("${instagram.user.password}")
    private String accountPassword;
    @Value("${webdriver.chrome.driver}")
    private String chromeDriverPath;
    @Value("${instagram.bot.follower.delay}")
    private Long delayInSeconds;


    @Autowired
    private InstagramUserRepository instagramUserRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!isEnabled) {
            return;
        }

        Long startTime = System.currentTimeMillis();
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeDriver driver = new ChromeDriver();

        List<InstagramUser> instagramUsers = instagramUserRepository.loadFilteredUsers();
        AtomicInteger counter = new AtomicInteger(instagramUsers.size());
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        System.out.println("Instagram follower bot started.");
        System.out.println("Total users for follow: " + instagramUsers.size());
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");

        InstagramBotUtils.login(driver, accountLogin, accountPassword);
        instagramUsers.forEach(u -> {
            try {
                System.out.println("\n--------------------------");
                boolean isLikedAndFollowed = likeAndFollow(driver, u);
                System.out.println("--------------------------");
                System.out.println("Remains " + counter.decrementAndGet() + " users.");
                System.out.println("--------------------------\n");
                if (isLikedAndFollowed) {
                    Thread.sleep(delayInSeconds * 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        long spentTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000;
        long spentMinutes = spentTimeInSeconds / 60;
        long spentSeconds = spentTimeInSeconds % 60;
        System.out.println("\n\n-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        System.out.println("Instagram follower bot complete his job.");
        System.out.println("Time spent: " + spentMinutes + " minutes " + spentSeconds + " seconds.");
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");

        driver.close();
    }

    private boolean likeAndFollow(ChromeDriver driver, InstagramUser instagramUser) {
        String username = instagramUser.getUsername();
        System.out.println("Start processing user: " + username);
        loadUserProfilePage(driver, username);

        if (driver.findElementsByCssSelector(Css.THIRD_IMAGE).isEmpty()) {
            System.out.println("User '" + username + "' has no photos, or his account has private access.");
            System.out.println("Fucking user will be skipped.");
            return false;
        }

        WebElement followButton = findFollowButton(driver);
        if (followButton.getText().equals("Following")) {
            System.out.println("User already following by " + accountLogin);
            return false;
        }

        System.out.println("Start liking images...");
        likeImage(driver, Css.FIRST_IMAGE);
        likeImage(driver, Css.SECOND_IMAGE);
        likeImage(driver, Css.THIRD_IMAGE);
        driver.executeScript("scroll(0,0)");
        VkUtils.randomSleep(1_000L);
        System.out.println("Images liking successfully done.");

        System.out.println("Start following...");
        followButton = findFollowButton(driver);
        followButton.click();
        while (!followButton.getText().equals("Following")) {
            VkUtils.randomSleep(1_000L);
        }
        System.out.println("Following successfully done.");
        System.out.println("User '" + username + "' processed successfully.");

        return true;
    }

    private void loadUserProfilePage(ChromeDriver driver, String username) {
        String userProfileUrl = String.format(USER_PROFILE_TEMPLATE_URL, username);
        driver.get(userProfileUrl);

        int maxTries = 3;
        while (maxTries != 0) {
            try {
                WebDriverWait profileDropdown = new WebDriverWait(driver, 10);
                profileDropdown.until(elementToBeClickable(cssSelector(Css.PROFILE_DROPDOWN)));
                return;
            } catch (Exception e) {
                maxTries--;
                driver.navigate().refresh();
            }
        }

        throw new IllegalStateException("Couldn't find DOM element from profile dropdown.");
    }

    private void likeImage(ChromeDriver driver, String cssImageSelector) {
        WebElement imageElement = driver.findElementByCssSelector(cssImageSelector);
        imageElement.click();

        WebDriverWait imageLikeButtonWait = new WebDriverWait(driver, 15);
        WebElement imageLikeButton = imageLikeButtonWait.until(elementToBeClickable(cssSelector(Css.LIKE_BUTTON)));
        imageLikeButton.click();

        VkUtils.randomSleep(500L);
        driver.navigate().back();
    }
}
