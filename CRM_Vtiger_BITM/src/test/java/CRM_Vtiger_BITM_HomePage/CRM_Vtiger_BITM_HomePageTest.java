
package CRM_Vtiger_BITM_HomePage;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import CRM_Vtiger_BITM_BaseClass.CRM_Vtiger_BITM_BaseClass;
import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_ScreenshotUtility;

public class CRM_Vtiger_BITM_HomePageTest extends CRM_Vtiger_BITM_BaseClass {

	// Locators identified from VTiger DevTools.
	private final By profileIcon = By.cssSelector("img[src='themes/softed/images/user.PNG']");

	private final By signOutLink = By.cssSelector("#ondemand_sub a[href*='module=Users'][href*='action=Logout']");

	/**
	 * Positive test: Verify that the VTiger Home page opens after login.
	 */
	@Test(groups = { "Smoke", "FT" })
	public void verifyHomePageIsDisplayed() {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		wait.until(d -> d.getCurrentUrl().contains("module=Home")
				|| d.findElements(By.cssSelector("img[src*='user']")).stream().anyMatch(WebElement::isDisplayed));

		Assert.assertTrue(
				driver.getCurrentUrl().contains("module=Home") || driver.getCurrentUrl().contains("action=index"),
				"VTiger Home page was not displayed. Current URL: " + driver.getCurrentUrl());

		System.out.println("PASS: VTiger Home page is displayed.");
	}

	/**
	 * Positive test: Verify that the user menu displays the Sign Out option.
	 */
	@Test(groups = { "FT", "Regression" })
	public void verifyLogoutOptionIsDisplayed() {

	    WebDriverWait wait = new WebDriverWait(
	            driver, Duration.ofSeconds(15));

	    // Verify that the user is on the Home page.
	    wait.until(ExpectedConditions.urlContains("module=Home"));

	    Assert.assertTrue(
	            driver.getCurrentUrl().contains("module=Home"),
	            "User is not on the Home page. Current URL: "
	                    + driver.getCurrentUrl());

	    // Wait until the profile icon is clickable.
	    WebElement profile = wait.until(
	            ExpectedConditions.elementToBeClickable(profileIcon));

	    profile.click();

	    // Wait for Sign Out to become visible.
	    WebElement signOut = wait.until(
	            ExpectedConditions.visibilityOfElementLocated(signOutLink));

	    Assert.assertTrue(
	            signOut.isDisplayed(),
	            "Sign Out option is not displayed after clicking the profile icon.");

	    System.out.println(
	            "PASS: Sign Out option is displayed in the user menu.");
	}
	

	/**
	 * Negative test: Verify that Sign Out is hidden before opening the profile
	 * menu.
	 */
	@Test(groups = { "FT", "Negative" })
	public void verifyLogoutOptionHiddenBeforeOpeningProfileMenu() {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		Assert.assertTrue(
				driver.getCurrentUrl().contains("module=Home") || driver.getCurrentUrl().contains("action=index"),
				"User is not on the Home page. Current URL: " + driver.getCurrentUrl());

		// Wait for the Home page profile icon to appear.
		wait.until(ExpectedConditions.visibilityOfElementLocated(profileIcon));

		// Do not click the profile icon.
		// Check whether Sign Out is already visible.
		boolean signOutVisible = driver.findElements(signOutLink).stream().anyMatch(WebElement::isDisplayed);

		Assert.assertFalse(signOutVisible, "Sign Out should not be visible before opening the profile menu.");

		// Capture screenshot only after the negative assertion passes.
		try {
			CRM_Vtiger_BITM_ScreenshotUtility.captureScreenshot(driver,
					"verifyLogoutOptionHiddenBeforeOpeningProfileMenu");

		} catch (Exception e) {
			System.err.println(
					"SCREENSHOT ERROR for " + "verifyLogoutOptionHiddenBeforeOpeningProfileMenu: " + e.getMessage());
			e.printStackTrace();
		}

		System.out.println("PASS: Sign Out is hidden before opening the profile menu.");
	}
}
