package com.hrm.util;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class WebDriverFactory {

	public static final int MAX_TIME_ELEMENT_TO_LOAD = 20;

	public static WebDriver createDriver() {
		final boolean IF_RUN_TESTS_ON_SELENIUM_GRID = Boolean.parseBoolean(Config.getProperty("seleniumgrid.runtests"));

		WebDriver driver = (IF_RUN_TESTS_ON_SELENIUM_GRID) ?
				createDriverForRemoteExecution() : createDriverForLocalExecution();

		if (driver != null) {
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(MAX_TIME_ELEMENT_TO_LOAD));
		}
		return driver;
	}

	public static WebDriver createDriverForLocalExecution() {
		String browserType = Config.getProperty("local.browser.type");
		if (browserType == null || browserType.trim().isEmpty()) {
			browserType = "CHROME";
		}
		browserType = browserType.trim().toUpperCase();

		switch (browserType) {
		case "FIREFOX":
			System.out.println("Opening Firefox browser using Selenium Manager...");
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			return new FirefoxDriver(firefoxOptions);

		case "CHROME":
		default:
			if (!browserType.equals("CHROME")) {
				System.out.println("Browser '" + browserType + "' not recognized, defaulting to Chrome.");
			}
			System.out.println("Opening Chrome browser using Selenium Manager...");
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--remote-allow-origins=*");
			return new ChromeDriver(chromeOptions);
		}
	}

	public static WebDriver createDriverForRemoteExecution() {
		return null;
	}

}

