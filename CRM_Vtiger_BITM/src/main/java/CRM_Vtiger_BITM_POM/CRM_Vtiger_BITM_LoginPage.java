
package CRM_Vtiger_BITM_POM;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CRM_Vtiger_BITM_LoginPage {

	private final WebDriver driver;
	private final WebDriverWait wait;

	private final By usernameTextField = By.name("user_name");

	private final By passwordTextField = By.name("user_password");

	private final By loginButton = By.id("submitButton");

	// Authenticated Home page indicator.
	private final By homePageMarker = By.cssSelector("span.userName");

	public CRM_Vtiger_BITM_LoginPage(WebDriver driver) {

		if (driver == null) {
			throw new IllegalArgumentException("WebDriver must not be null.");
		}

		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	}

	public void enterUsername(String username) {

		WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameTextField));

		field.clear();
		field.sendKeys(username);
	}

	public void enterPassword(String password) {

		WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordTextField));

		field.clear();
		field.sendKeys(password);
	}

	public void clickLoginButton() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

		WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("submitButton")));

		try {
			loginButton.click();
		} catch (org.openqa.selenium.TimeoutException e) {
			System.out.println("Normal login click timed out. Checking browser state.");

			String currentUrl = driver.getCurrentUrl();

			if (currentUrl.contains("module=Home") && currentUrl.contains("action=index")) {
				System.out.println("Home page detected; login may already have succeeded.");
				return;
			}

			throw e;
		}
	}

	public void loginToApplication(String username, String password) {
		enterUsername(username);
		enterPassword(password);
		clickLoginButton();

		boolean homePageDetected = false;

		try {
			homePageDetected = new WebDriverWait(driver, Duration.ofSeconds(25)).until(d -> {
				try {
					String currentUrl = d.getCurrentUrl();

					boolean homeUrl = currentUrl.contains("module=Home") && currentUrl.contains("action=index");

					if (homeUrl) {
						return true;
					}

					return d.findElements(homePageMarker).stream().anyMatch(element -> {
						try {
							return element.isDisplayed();
						} catch (StaleElementReferenceException e) {
							return false;
						}
					});

				} catch (StaleElementReferenceException e) {
					return false;
				}
			});

		} catch (TimeoutException e) {
			// The page may have finished navigating just as the wait expired.
			String currentUrl = driver.getCurrentUrl();

			homePageDetected = currentUrl.contains("module=Home") && currentUrl.contains("action=index");
		}

		if (!homePageDetected) {
			throw new IllegalStateException("Login could not be confirmed. Current URL: " + driver.getCurrentUrl());
		}

		System.out.println("Login successful. Home page detected: " + driver.getCurrentUrl());
	}

	public boolean isLoginPageDisplayed() {

		WebDriverWait loginWait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			return loginWait.until(d -> {
				try {
					// Re-locate the elements on every polling attempt.
					WebElement username = d.findElement(usernameTextField);

					WebElement password = d.findElement(passwordTextField);

					WebElement button = d.findElement(loginButton);

					return username.isDisplayed() && password.isDisplayed() && button.isDisplayed();

				} catch (StaleElementReferenceException e) {
					// The page refreshed. Retry with fresh elements.
					return false;
				}
			});

		} catch (TimeoutException e) {
			return false;
		}
	}

	public boolean isHomePageDisplayed() {
		return waitForHomePage(Duration.ofSeconds(8));
	}

	private boolean waitForHomePage(Duration timeout) {

		WebDriverWait homeWait = new WebDriverWait(driver, timeout);

		try {
			return homeWait.until(d -> {

				// Check the VTiger Home URL.
				String currentUrl = d.getCurrentUrl();

				boolean homeUrl = currentUrl.contains("module=Home") && currentUrl.contains("action=index");

				// Check the visible username marker.
				boolean userNameVisible = d.findElements(homePageMarker).stream().anyMatch(element -> {
					try {
						return element.isDisplayed();
					} catch (StaleElementReferenceException e) {
						return false;
					}
				});

				return homeUrl || userNameVisible;
			});

		} catch (TimeoutException e) {
			return false;
		}
	}
}
