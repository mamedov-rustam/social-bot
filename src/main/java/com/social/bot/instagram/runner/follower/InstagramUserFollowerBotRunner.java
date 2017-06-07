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

import static com.social.bot.instagram.runner.follower.InstagramSelectors.*;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

@Service
public class InstagramUserFollowerBotRunner implements ApplicationRunner {
    private static final String LOGIN_PAGE_URL="https://www.instagram.com/";
    private static final String USER_PROFILE_TEMPLATE_URL="https://www.instagram.com/%s";

    @Value("${instagram.bot.follower.enable}")
    private boolean isEnabled;
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
        login(driver);

        instagramUserRepository.loadFilteredUsers().forEach(u -> likeAndFollow(driver, u));

        System.out.println("All right!");
        System.exit(0);
    }

    private void login(ChromeDriver driver) {
        driver.get(LOGIN_PAGE_URL);

        WebDriverWait waitForLoginLink = new WebDriverWait(driver, 10);
        WebElement loginLink = waitForLoginLink.until(elementToBeClickable(cssSelector(LOGIN_LINK)));
        loginLink.click();

        VkUtils.randomSleep(250L);

        WebElement usernameInput = driver.findElement(cssSelector(USERNAME_INPUT));
        usernameInput.sendKeys("fly.bra_russia");

        WebElement passwordInput = driver.findElement(cssSelector(PASSWORD_INPUT));
        passwordInput.sendKeys("94ucJIooo");


        driver.findElement(cssSelector(LOGIN_BUTTON)).click();
    }

    private void likeAndFollow(ChromeDriver driver, InstagramUser instagramUser) {
        String userProfileUrl = String.format(USER_PROFILE_TEMPLATE_URL, instagramUser.getUsername());
        driver.get(userProfileUrl);

        // TEMP!
        System.exit(0);
    }
}
