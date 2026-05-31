package org.example.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.example.utils.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Manages the WebDriver lifecycle and captures a screenshot on failure,
 * attaching it to the Cucumber report.
 */
public class Hooks {

    @Before
    public void setUp() {
        // Force driver creation at the start of each scenario.
        DriverFactory.getDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverFactory.getDriver();
        if (scenario.isFailed() && driver instanceof TakesScreenshot ts) {
            byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png",
                    "failure-" + scenario.getName());
        }
        DriverFactory.quitDriver();
    }
}
