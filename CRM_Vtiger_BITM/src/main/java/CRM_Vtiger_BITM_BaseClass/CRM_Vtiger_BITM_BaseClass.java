
package CRM_Vtiger_BITM_BaseClass;

import java.io.IOException;
import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_FileUtility;
import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_WebDriverUtility;
import CRM_Vtiger_BITM_POM.CRM_Vtiger_BITM_LoginPage;

public class CRM_Vtiger_BITM_BaseClass {

	// Public so the screenshot listener can access the driver.
	public WebDriver driver;

	public CRM_Vtiger_BITM_WebDriverUtility wu = new CRM_Vtiger_BITM_WebDriverUtility();

	public CRM_Vtiger_BITM_FileUtility fu = new CRM_Vtiger_BITM_FileUtility();

	protected String url;
	protected String username;
	protected String password;

	@BeforeSuite(alwaysRun = true)
	public void dbConnection() {
		Reporter.log("Before Suite: Setup started", true);
	}

	@BeforeTest(alwaysRun = true)
	public void beforeTest() {
		Reporter.log("Before Test: Test execution started", true);
	}

	@Parameters("browser")
	@BeforeClass(alwaysRun = true)
	public void navigateToApp(@Optional("chrome") String browser) throws IOException {

		Reporter.log("Before Class: Launching browser", true);

		try {
			// Read application configuration.
			url = fu.getPropertyKeyValue("url");
			username = fu.getPropertyKeyValue("username");
			password = fu.getPropertyKeyValue("password");

			if (url == null || url.isBlank()) {
				throw new IllegalStateException("Application URL is missing in properties.");
			}

			if (username == null || username.isBlank()) {
				throw new IllegalStateException("Username is missing in properties.");
			}

			if (password == null || password.isBlank()) {
				throw new IllegalStateException("Password is missing in properties.");
			}

			// Launch browser.
			driver = wu.launchBrowser(browser);

			if (driver == null) {
				throw new IllegalStateException("WebDriver could not be initialized.");
			}

			// Use explicit waits in the Page Object Model.
			driver.manage().timeouts().implicitlyWait(Duration.ZERO);

			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(45));

			driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

			// Open VTiger.
			driver.get(url);

			Reporter.log("Application opened: " + url, true);

			CRM_Vtiger_BITM_LoginPage loginPage = new CRM_Vtiger_BITM_LoginPage(driver);

			if (!loginPage.isLoginPageDisplayed()) {
				throw new IllegalStateException(
						"VTiger login form was not displayed. Current URL: " + driver.getCurrentUrl());
			}

			Reporter.log("Login form displayed successfully.", true);

			// Login validation tests must start on the login page.
			boolean isLoginValidationClass = getClass().getSimpleName()
					.equals("CRM_Vtiger_BITM_LoginPage_InvalidValidTest");

			if (isLoginValidationClass) {
				Reporter.log("Login validation class detected. " + "Automatic login skipped.", true);
				return;
			}

			// Log in for test classes that require an authenticated session.
			loginPage.loginToApplication(username, password);

			// loginToApplication already verifies the home page.
			Reporter.log("Login completed successfully.", true);

		} catch (IOException | RuntimeException e) {

			Reporter.log("Browser setup or login failed: " + e.getMessage(), true);

			// Clean up the browser if setup fails.
			if (driver != null) {
				try {
					driver.quit();
				} catch (RuntimeException cleanupError) {
					Reporter.log("Browser cleanup failed: " + cleanupError.getMessage(), true);
				} finally {
					driver = null;
				}
			}

			// Keep the original failure visible to TestNG.
			throw e;
		}
	}

	@AfterClass(alwaysRun = true)
	public void closeBrowser() {

		Reporter.log("After Class: Closing browser", true);

		if (driver != null) {
			try {
				driver.quit();
				Reporter.log("Browser closed successfully.", true);

			} catch (RuntimeException e) {
				Reporter.log("Error while closing browser: " + e.getMessage(), true);

			} finally {
				driver = null;
			}
		}
	}

	@AfterTest(alwaysRun = true)
	public void afterTest() {
		Reporter.log("After Test: Test execution completed", true);
	}

	@AfterSuite(alwaysRun = true)
	public void closeConnection() {
		Reporter.log("After Suite: Cleanup completed", true);
	}
}
