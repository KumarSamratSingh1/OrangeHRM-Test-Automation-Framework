package com.hrm.base;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.hrm.util.Config;
import com.hrm.util.WebDriverFactory;

import io.qameta.allure.Attachment;

public class TestBase {

	public static WebDriver driver;
	protected static String appUrl;

	public void initialization() {
		driver = null;
		appUrl = Config.getProperty("app.url.local");
		driver = WebDriverFactory.createDriver();
		System.out.println("Navigating to URL: " + appUrl);
		if (appUrl != null && !appUrl.isEmpty()) {
			driver.get(appUrl);
		}
		driver.manage().window().maximize();
		try {
			DOMConfigurator.configure("log4j.xml");
		} catch (Exception ignored) {
		}
	}

	public WebDriver getDriver() {
		return driver;
	}
	
	@Attachment(value = "Screenshot of {0}", type = "image/png")
	public byte[] takeScreenShot(String ScreenShotName) throws IOException {
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		File destDir = new File(System.getProperty("user.dir") + File.separator + "Screenshots");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		String dest = destDir.getAbsolutePath() + File.separator + ScreenShotName + "_" + timestamp + ".png";

		FileHandler.copy(source, new File(dest));
		System.out.println("Screenshot Taken: " + dest);
		return (byte[]) ts.getScreenshotAs(OutputType.BYTES);
	}

	public void waitForElementToBeClickable(WebElement ele) {
		new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(ExpectedConditions.elementToBeClickable(ele));
	}

	public void setFirefoxProfile() {
		driver = null;
		appUrl = Config.getProperty("app.url.local");
		System.out.println("Opening firefox browser");
		FirefoxProfile profile = new FirefoxProfile();

		// Set Location to store files after downloading.
		profile.setPreference("browser.download.dir", System.getProperty("user.dir") + File.separator + "Download_Resume");
		profile.setPreference("browser.download.folderList", 2);

		profile.setPreference("browser.download.manager.showWhenStarting", false);
		profile.setPreference("browser.helperApps.neverAsk.openFile",
				"text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml");
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml");
		profile.setPreference("browser.helperApps.alwaysAsk.force", false);
		profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
		profile.setPreference("browser.download.manager.focusWhenStarting", false);
		profile.setPreference("browser.download.manager.useWindow", false);
		profile.setPreference("browser.download.manager.showAlertOnComplete", false);
		profile.setPreference("browser.download.manager.closeWhenDone", false);
		FirefoxOptions option = new FirefoxOptions();
		option.setProfile(profile);
		driver = new FirefoxDriver(option);
		if (appUrl != null && !appUrl.isEmpty()) {
			driver.get(appUrl);
		}
		driver.manage().window().maximize();
		try {
			DOMConfigurator.configure("log4j.xml");
		} catch (Exception ignored) {
		}
	}
	
	@Attachment(value = "Screenshot of {0}", type = "image/png")
	public byte[] takeScreenShotForFailedTestCase(String ScreenShotName) throws IOException {
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		File destDir = new File(System.getProperty("user.dir") + File.separator + "Screenshots" + File.separator + "FailedTestCase");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		String dest = destDir.getAbsolutePath() + File.separator + ScreenShotName + "_" + timestamp + ".png";

		FileHandler.copy(source, new File(dest));
		System.out.println("Screenshot Taken: " + dest);
		return (byte[]) ts.getScreenshotAs(OutputType.BYTES);
	}
}