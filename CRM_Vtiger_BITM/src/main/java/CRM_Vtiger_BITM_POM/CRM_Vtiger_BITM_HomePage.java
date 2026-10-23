
package CRM_Vtiger_BITM_POM;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CRM_Vtiger_BITM_HomePage {

	private final WebDriver driver;
	private final WebDriverWait wait;

	// Home page locators
	private final By homePageHeading = By.xpath("//*[contains(normalize-space(.),'My Home Page')]");

	private final By homeMenu = By.xpath("//a[contains(@href,'module=Home')]");

	private final By userName = By.cssSelector("span.userName");

	// Logout link may appear after opening the user menu
	private final By logoutLinks = By.xpath("//a[normalize-space()='Sign Out' " + "or normalize-space()='Logout' "	+ "or contains(@href,'action=Logout')]");

	public CRM_Vtiger_BITM_HomePage(WebDriver driver) {
		if (driver == null) {
			throw new IllegalArgumentException("WebDriver cannot be null.");
		}

		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	
	 // Verifies that the VTiger Home page is displayed.
	 
	public boolean isHomePageDisplayed() {
		try {
			wait.until(d -> d.getCurrentUrl().contains("module=Home") || isElementVisible(homePageHeading)
					|| isElementVisible(homeMenu));

			return driver.getCurrentUrl().contains("module=Home") || isElementVisible(homePageHeading)
					|| isElementVisible(homeMenu);

		} catch (Exception e) {
			System.out.println("Home page verification failed. Current URL: " + driver.getCurrentUrl());
			return false;
		}
	}

	public boolean isHomeMenuDisplayed() {
		return isElementVisible(homeMenu);
	}

	public boolean isHomePageHeadingDisplayed() {
		return isElementVisible(homePageHeading);
	}

	public boolean isUserNameDisplayed() {
		return isElementVisible(userName);
	}

	 
	//  Checks whether a logout option is displayed. Opens the username menu if necessary.
	 
	public boolean isLogoutDisplayed() {

		if (hasVisibleLogoutLink()) {
			return true;
		}

		try {
			WebElement usernameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(userName));

			wait.until(ExpectedConditions.elementToBeClickable(usernameElement));

			usernameElement.click();

			wait.until(d -> hasVisibleLogoutLink());

			return hasVisibleLogoutLink();

		} catch (Exception e) {
			System.out.println("Logout option was not found or the user menu " + "could not be opened.");
			return false;
		}
	}

	
	  // Clicks the visible logout option.
	
	public void clickLogout() {

		if (!hasVisibleLogoutLink()) {
			isLogoutDisplayed();
		}

		List<WebElement> links = driver.findElements(logoutLinks);

		for (WebElement link : links) {
			if (link.isDisplayed() && link.isEnabled()) {
				wait.until(ExpectedConditions.elementToBeClickable(link));
				link.click();
				return;
			}
		}

		throw new org.openqa.selenium.NoSuchElementException("No visible, clickable logout link was found.");
	}

	public String getHomePageTitle() {
		return driver.getTitle();
	}

	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}

	public void clickHomeMenu() {
		wait.until(ExpectedConditions.elementToBeClickable(homeMenu)).click();
	}

	private boolean hasVisibleLogoutLink() {
		for (WebElement link : driver.findElements(logoutLinks)) {
			try {
				if (link.isDisplayed()) {
					System.out.println("Logout option found: " + link.getText());
					return true;
				}
			} catch (org.openqa.selenium.StaleElementReferenceException e) {
				// The page updated; ignore this element and continue.
			}
		}

		return false;
	}

	private boolean isElementVisible(By locator) {
		try {
			for (WebElement element : driver.findElements(locator)) {
				if (element.isDisplayed()) {
					return true;
				}
			}
		} catch (Exception e) {
			// Element is absent or temporarily unavailable.
		}

		return false;
	}
}
