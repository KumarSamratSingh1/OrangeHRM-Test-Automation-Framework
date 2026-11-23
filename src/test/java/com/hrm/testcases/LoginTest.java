package com.hrm.testcases;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hrm.base.TestBase;
import com.hrm.pages.DashboardPage;
import com.hrm.pages.LoginPage;
import com.hrm.pages.TopNevigationMenuPage;
import com.hrm.util.Config;
import com.hrm.util.Log;

public class LoginTest extends TestBase{
	LoginPage loginPage;
	DashboardPage dashboardPage;
	TopNevigationMenuPage topnevigationmenupage;
	
	@BeforeMethod
	public void setUp() {
		initialization();
		loginPage=new LoginPage();
		dashboardPage=new DashboardPage();
		topnevigationmenupage=new TopNevigationMenuPage();
	}
	
	@Test(priority = 1, description = "HRMS-1: Verify Successful Login with Valid Credentials")
	public void SucessfullLoginTest() throws IOException {	
		try{
			Log.startTestCase("ValidLoginTest");
			String username= Config.getProperty("username");
			Log.info("Username entered in the Username text box");
			String password= Config.getProperty("password");
			Log.info("Password entered in the Username text box");
			loginPage.loginToApp(username,password);
			Log.info("Click action performed on Login button");
			Assert.assertTrue(dashboardPage.WelcomeMessgae().contains("Welcome"));
			takeScreenShot("Dashboard Page");
		}
		catch(Exception e){
			e.printStackTrace();
			Assert.fail("Valid login failed: " + e.getMessage());
		}	
	}

	@Test(priority = 2, description = "HRMS-1B: Verify Invalid Credentials Rejection")
	public void InvalidLoginTest() throws IOException {
		try {
			Log.startTestCase("InvalidLoginTest");
			loginPage.loginToApp("invalid_admin_xyz", "badpassword123");
			String error = loginPage.getErrorMessage();
			Log.info("Error message displayed: " + error);
			Assert.assertTrue(error.contains("Invalid credentials"), "Expected 'Invalid credentials' but got: " + error);
			takeScreenShot("Invalid Credentials Error");
		}
		catch(Exception e){
			e.printStackTrace();
			Assert.fail("Invalid login verification failed: " + e.getMessage());
		}
	}
	
	@AfterMethod
	public void tearDown(){
		try {
			if (driver != null && driver.getCurrentUrl().contains("dashboard") && topnevigationmenupage != null) {
				topnevigationmenupage.ClickOnUserName();
			}
		} catch (Exception ignored) {
		}
		Log.endTestCase();
		if (driver != null) {
			driver.quit();
		}
	}
}

