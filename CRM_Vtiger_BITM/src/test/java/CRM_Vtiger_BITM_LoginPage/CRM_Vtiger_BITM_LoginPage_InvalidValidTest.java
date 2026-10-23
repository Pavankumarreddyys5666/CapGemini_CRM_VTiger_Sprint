
package CRM_Vtiger_BITM_LoginPage;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import CRM_Vtiger_BITM_BaseClass.CRM_Vtiger_BITM_BaseClass;
import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_ScreenshotUtility;
import CRM_Vtiger_BITM_POM.CRM_Vtiger_BITM_LoginPage;

public class CRM_Vtiger_BITM_LoginPage_InvalidValidTest extends CRM_Vtiger_BITM_BaseClass {

	private final By usernameField = By.name("user_name");
	private final By passwordField = By.name("user_password");
	private final By loginButton = By.id("submitButton");

	private final By profileIcon = By.cssSelector("img[src*='user.PNG']");

	private final By logoutLink = By.cssSelector("#ondemand_sub a[href*='module=Users'][href*='action=Logout']");

	private static final String INVALID_USERNAME = "invalid_username";
	private static final String INVALID_PASSWORD = "invalid_password";

	private CRM_Vtiger_BITM_LoginPage loginPage;

	private void captureNegativeScreenshot(String testName) {
		try {
			CRM_Vtiger_BITM_ScreenshotUtility.captureScreenshot(driver, testName);

			Reporter.log("Screenshot saved for negative test: " + testName, true);

		} catch (Exception e) {
			Reporter.log("Screenshot failed for " + testName + ": " + e.getMessage(), true);
		}
	}

	private void openLoginPage() {
		Assert.assertNotNull(driver, "WebDriver was not initialized.");

		driver.get(url);

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

		wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
		wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
		wait.until(ExpectedConditions.elementToBeClickable(loginButton));

		loginPage = new CRM_Vtiger_BITM_LoginPage(driver);

		Assert.assertTrue(loginPage.isLoginPageDisplayed(),
				"Login page is not displayed. Current URL: " + driver.getCurrentUrl());

		Reporter.log("Login page opened successfully.", true);
	}

	/**
	 * Re-finds the login form elements on every polling attempt. Handles elements
	 * replaced during page refreshes in Edge or Chrome.
	 */
	private boolean isLoginFormDisplayed() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			return wait.until(d -> {
				try {
					WebElement username = d.findElement(usernameField);
					WebElement password = d.findElement(passwordField);
					WebElement button = d.findElement(loginButton);

					return username.isDisplayed() && password.isDisplayed() && button.isDisplayed();

				} catch (StaleElementReferenceException | NoSuchElementException e) {
					return false;
				}
			});

		} catch (TimeoutException e) {
			return false;
		}
	}

	private void verifyLoginRejected(String testUsername, String testPassword, String scenario, String testMethodName) {

		openLoginPage();

		loginPage.enterUsername(testUsername);
		loginPage.enterPassword(testPassword);

		// Preserve the screenshot before submitting invalid credentials.
		captureNegativeScreenshot(testMethodName + "_Input");

		loginPage.clickLoginButton();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		boolean rejectionStateReached;

		try {
			rejectionStateReached = wait.until(d -> {
				try {
					String currentUrl = d.getCurrentUrl();

					boolean onHomePage = currentUrl.contains("module=Home") && currentUrl.contains("action=index");

					if (onHomePage) {
						return true;
					}

					// Fresh lookup on every polling attempt.
					WebElement username = d.findElement(usernameField);

					return username.isDisplayed();

				} catch (StaleElementReferenceException | NoSuchElementException e) {
					return false;
				}
			});

		} catch (TimeoutException e) {
			rejectionStateReached = false;
		}

		boolean homePage = isHomePageUrl(driver);
		boolean loginFormDisplayed = isLoginFormDisplayed();

		boolean rejected = rejectionStateReached && !homePage && loginFormDisplayed;

		Assert.assertTrue(rejected, scenario + " was not rejected or the login form was not restored. "
				+ "Current URL: " + driver.getCurrentUrl());

		Reporter.log("PASS: " + scenario + " was rejected.", true);
	}

	private boolean isHomePageUrl(WebDriver currentDriver) {
		String currentUrl = currentDriver.getCurrentUrl();

		return currentUrl.contains("module=Home") && currentUrl.contains("action=index");
	}

	// TEST 1: Empty username and password
	@Test(priority = 1, groups = { "FT", "Negative" })
	public void emptyUsernameAndPassword_Test() {
		verifyLoginRejected("", "", "Empty username and password", "emptyUsernameAndPassword_Test");
	}

	// TEST 2: Empty username
	@Test(priority = 2, groups = { "FT", "Negative" })
	public void emptyUsername_Test() {
		verifyLoginRejected("", password, "Empty username", "emptyUsername_Test");
	}

	// TEST 3: Empty password
	@Test(priority = 3, groups = { "FT", "Negative" })
	public void emptyPassword_Test() {
		verifyLoginRejected(username, "", "Empty password", "emptyPassword_Test");
	}

	// TEST 4: Invalid username
	@Test(priority = 4, groups = { "FT", "Negative" })
	public void invalidUsername_Test() {
		verifyLoginRejected(INVALID_USERNAME, password, "Invalid username", "invalidUsername_Test");
	}

	// TEST 5: Invalid password
	@Test(priority = 5, groups = { "FT", "Negative" })
	public void invalidPassword_Test() {
		verifyLoginRejected(username, INVALID_PASSWORD, "Invalid password", "invalidPassword_Test");
	}

	// TEST 6: Valid login and logout option
	@Test(priority = 6, groups = { "FT", "Regression" })
	public void verifyLogoutOptionIsDisplayed() {

		Assert.assertNotNull(driver, "WebDriver was not initialized.");

		loginPage = new CRM_Vtiger_BITM_LoginPage(driver);

		// BaseClass leaves this test class on the login page.
		loginPage.loginToApplication(username, password);

		Assert.assertTrue(loginPage.isHomePageDisplayed(),
				"Home page did not open after valid login. " + "Current URL: " + driver.getCurrentUrl());

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

		wait.until(ExpectedConditions.elementToBeClickable(profileIcon)).click();

		Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(logoutLink)).isDisplayed(),
				"Sign Out option is not displayed.");

		Reporter.log("PASS: Sign Out option is displayed.", true);
	}
}
