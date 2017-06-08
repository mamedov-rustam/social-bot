package com.social.bot.instagram.common;


import com.social.bot.vk.utils.VkUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;


import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

public final class InstagramBotUtils {
    public static final String LOGIN_PAGE_URL="https://www.instagram.com/";
    public static final String USER_PROFILE_TEMPLATE_URL="https://www.instagram.com/%s";

    private InstagramBotUtils() {
    }

    public static void login(ChromeDriver driver, String userLogin, String userPassword) {
        System.out.println("Start login...");
        driver.get(LOGIN_PAGE_URL);

        WebDriverWait waitForLoginLink = new WebDriverWait(driver, 15);
        WebElement loginLink = waitForLoginLink.until(elementToBeClickable(cssSelector(InstagramSelectors.Css.LOGIN_LINK)));
        loginLink.click();

        VkUtils.randomSleep(250L);

        WebElement usernameInput = driver.findElementByCssSelector(InstagramSelectors.Css.USERNAME_INPUT);
        usernameInput.sendKeys(userLogin);

        WebElement passwordInput = driver.findElementByCssSelector(InstagramSelectors.Css.PASSWORD_INPUT);
        passwordInput.sendKeys(userPassword);

        driver.findElement(cssSelector(InstagramSelectors.Css.LOGIN_BUTTON)).click();

        WebDriverWait waitForProfileLink = new WebDriverWait(driver, 15);
        waitForProfileLink.until(elementToBeClickable(cssSelector(InstagramSelectors.Css.PROFILE_LINK)));
        System.out.println("Successfully login.");
    }
}
