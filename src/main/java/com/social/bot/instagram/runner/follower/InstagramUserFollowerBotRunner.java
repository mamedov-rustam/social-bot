package com.social.bot.instagram.runner.follower;

import com.social.bot.instagram.model.InstagramUser;
import com.social.bot.instagram.repository.InstagramUserRepository;
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

import static com.social.bot.instagram.runner.follower.InstagramSelectors.*;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

@Service
public class InstagramUserFollowerBotRunner implements ApplicationRunner {
    private static final String LOGIN_PAGE_URL="https://www.instagram.com/";
    private static final String USER_PROFILE_TEMPLATE_URL="https://www.instagram.com/%s";

    @Value("${instagram.bot.follower.enable}")
    private boolean isEnabled;
    @Value("${instagram.user.login}")
    private String userLogin;
    @Value("${instagram.user.password}")
    private String userPassword;
    @Value("${webdriver.chrome.driver}")
    private String chromeDriverPath;


    @Autowired
    private InstagramUserRepository instagramUserRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!isEnabled) {
            return;
        }

        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeDriver driver = new ChromeDriver();

        List<InstagramUser> instagramUsers = instagramUserRepository.loadFilteredUsers();
        AtomicInteger counter = new AtomicInteger(instagramUsers.size());
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        System.out.println("Instagram follower bot started.");
        System.out.println("Total users for follow: " + instagramUsers.size());
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");

        login(driver);
        instagramUsers.forEach(u -> {
            try {
                System.out.println("\n--------------------------");
                likeAndFollow(driver, u);
                System.out.println("--------------------------");
                System.out.println("Remains " + counter.decrementAndGet() + " users.");
                System.out.println("--------------------------\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println("All right!");
        System.exit(0);
    }

    private void login(ChromeDriver driver) {
        System.out.println("Start login...");
        driver.get(LOGIN_PAGE_URL);

        WebDriverWait waitForLoginLink = new WebDriverWait(driver, 15);
        WebElement loginLink = waitForLoginLink.until(elementToBeClickable(cssSelector(Css.LOGIN_LINK)));
        loginLink.click();

        VkUtils.randomSleep(250L);

        WebElement usernameInput = driver.findElementByCssSelector(Css.USERNAME_INPUT);
        usernameInput.sendKeys(userLogin);

        WebElement passwordInput = driver.findElementByCssSelector(Css.PASSWORD_INPUT);
        passwordInput.sendKeys(userPassword);

        driver.findElement(cssSelector(Css.LOGIN_BUTTON)).click();

        WebDriverWait waitForProfileLink = new WebDriverWait(driver, 15);
        waitForProfileLink.until(elementToBeClickable(cssSelector(Css.PROFILE_LINK)));
        System.out.println("Successfully login.");
    }

    private void likeAndFollow(ChromeDriver driver, InstagramUser instagramUser) {
        String username = instagramUser.getUsername();
        System.out.println("Start processing user: " + username);
        String userProfileUrl = String.format(USER_PROFILE_TEMPLATE_URL, username);
        driver.get(userProfileUrl);

        System.out.println("Start liking images...");
        likeImage(driver, Css.FIRST_IMAGE);
        likeImage(driver, Css.SECOND_IMAGE);
        likeImage(driver, Css.THIRD_IMAGE);
        System.out.println("Images liking successfully done.");

        System.out.println("Start following...");
        WebElement followButton = driver.findElementByCssSelector(Css.FOLLOW_BUTTON);
        followButton.click();

        while (!followButton.getText().equals("Following")) {
            VkUtils.randomSleep(1_000L);
        }
        System.out.println("Following successfully done.");
        System.out.println("User processed successfully.");
    }

    private void likeImage(ChromeDriver driver, String cssImageSelector) {
        WebElement firstImageElement = driver.findElementByCssSelector(cssImageSelector);
        firstImageElement.click();

        WebDriverWait firstImageLikeButtonWait = new WebDriverWait(driver, 15);
        WebElement firstImageLikeButton = firstImageLikeButtonWait.until(elementToBeClickable(cssSelector(Css.LIKE_BUTTON)));
        firstImageLikeButton.click();

        VkUtils.randomSleep(500L);
        driver.navigate().back();
    }
}
