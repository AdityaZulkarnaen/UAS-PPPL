package org.example.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

/**
 * Thread-safe WebDriver lifecycle holder. Selenium Manager (built into
 * Selenium 4.6+) downloads the matching chromedriver automatically.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            DRIVER.set(create());
        }
        return DRIVER.get();
    }

    private static WebDriver create() {
        String browser = ConfigReader.browser().toLowerCase();
        if (!browser.equals("chrome")) {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
        ChromeOptions options = new ChromeOptions();
        if (ConfigReader.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");

        ChromeDriver driver = new ChromeDriver(options);
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(2));
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
