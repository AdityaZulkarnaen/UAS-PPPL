package org.example.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.example.steps,org.example.hooks")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty,"
                + "html:target/cucumber-reports/cucumber.html,"
                + "json:target/cucumber-reports/cucumber.json")
//@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@login")
//@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@map")
//@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@lapor_masalah")
@ConfigurationParameter(
        key = FILTER_TAGS_PROPERTY_NAME,
        value = "@TC-M11"
)
public class RunCucumberTest {
}