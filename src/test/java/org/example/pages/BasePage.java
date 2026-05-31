package org.example.pages;

import org.example.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Shared Page Object base: holds the driver and centralizes explicit-wait
 * helpers so individual pages stay free of timing boilerplate.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(ConfigReader.timeoutSeconds()));
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    protected boolean isDisplayed(By locator) {
        try {
            return !driver.findElements(locator).isEmpty()
                    && driver.findElement(locator).isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException
                 | org.openqa.selenium.StaleElementReferenceException e) {
            return false;
        }
    }
}
