package org.example.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * JUnit5 suite that runs all Cucumber features and emits pretty + HTML + JSON
 * output. The JSON (target/cucumber-reports/cucumber.json) feeds both the
 * maven-cucumber-reporting HTML report and the bug-summary script.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.example.steps,org.example.hooks")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty,"
                + "html:target/cucumber-reports/cucumber.html,"
                + "json:target/cucumber-reports/cucumber.json")
public class RunCucumberTest {
}
