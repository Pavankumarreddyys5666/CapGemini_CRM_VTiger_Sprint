package CRM_Vtiger_BITM_ServiceContracts;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import CRM_Vtiger_BITM_BaseClass.CRM_Vtiger_BITM_BaseClass;
import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_ScreenshotUtility;
import CRM_Vtiger_BITM_POM.CRM_Vtiger_BITM_ServiceContractsPage;

public class CRM_Vtiger_BITM_ServiceContractsTest extends CRM_Vtiger_BITM_BaseClass {

	// Capture screenshots for confirmed negative test scenarios.
	private void captureNegativeScreenshot(String testName) {
		try {
			CRM_Vtiger_BITM_ScreenshotUtility.captureScreenshot(driver, testName);
		} catch (Exception e) {
			System.err.println("SCREENSHOT ERROR for " + testName + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	// TC01 - Positive: Verify Service Contracts page opens.
	@Test(priority = 1, groups = { "FT", "Smoke", "Regression" })
	public void verifyServiceContractsPageOpensTest() {

		CRM_Vtiger_BITM_ServiceContractsPage page = new CRM_Vtiger_BITM_ServiceContractsPage(driver);

		page.openServiceContractsList();

		Assert.assertTrue(page.isServiceContractsPageDisplayed(), "Service Contracts page did not open.");

		Assert.assertTrue(page.isContractTableDisplayed() || page.isEmptyStateDisplayed(),
				"Neither the contract table nor empty-state message appeared.");

		System.out.println("PASS: Service Contracts page opened successfully.");
	}

	// TC03 - Positive: Verify Create Service Contract form opens.
	@Test(priority = 2, groups = { "FT", "Regression" })
	public void verifyCreateServiceContractFormTest() {

		CRM_Vtiger_BITM_ServiceContractsPage page = new CRM_Vtiger_BITM_ServiceContractsPage(driver);

		page.openServiceContractsList();
		page.clickCreateButton();

		Assert.assertTrue(page.isCreateFormDisplayed(), "Create Service Contract form did not open.");

		System.out.println("PASS: Create Service Contract form opened.");
	}

	// TC04 - Positive: Create a service contract with a valid Subject.
	@Test(priority = 3, groups = { "FT", "Regression" })
	public void createServiceContractWithValidDetailsTest() {

		CRM_Vtiger_BITM_ServiceContractsPage page = new CRM_Vtiger_BITM_ServiceContractsPage(driver);

		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

		String subject = "Automation Contract " + timestamp;

		page.openServiceContractsList();
		page.clickCreateButton();
		page.enterSubject(subject);

		Assert.assertEquals(page.getSubjectValue(), subject, "Subject was not entered correctly.");

		page.saveContract();

		// Confirm the application navigated away from the create form.
		boolean saved = waitForContractSave(page);

		Assert.assertTrue(saved, "Contract was not saved or the create form is still open.");

		Assert.assertTrue(driver.getPageSource().contains(subject),
				"Saved contract Subject was not found on the resulting page.");

		System.out.println("PASS: Service contract created: " + subject);
	}

	private boolean waitForContractSave(CRM_Vtiger_BITM_ServiceContractsPage page) {

		try {
			WebDriverWait saveWait = new WebDriverWait(driver, Duration.ofSeconds(10));

			saveWait.until(d -> !d.getCurrentUrl().contains("action=EditView")
					&& d.getCurrentUrl().contains("module=ServiceContracts"));

			return !page.isCreateFormDisplayed();

		} catch (TimeoutException e) {
			return false;
		}
	}

	// TC08 - Negative: Try saving with an empty Subject.
	@Test(priority = 4, groups = { "FT", "Negative" })
	public void verifyEmptySubjectRejectedTest() {

		CRM_Vtiger_BITM_ServiceContractsPage page = new CRM_Vtiger_BITM_ServiceContractsPage(driver);

		page.openServiceContractsList();
		page.clickCreateButton();
		page.enterSubject("");

		Assert.assertTrue(page.getSubjectValue().trim().isEmpty(),
				"Subject field should be empty for this negative test.");

		page.saveContract();

		Assert.assertTrue(page.isSubjectMandatoryValidationShown(),
				"Expected mandatory Subject validation was not detected.");

		captureNegativeScreenshot("verifyEmptySubjectRejectedTest");

		System.out.println("PASS: Empty Subject validation was detected.");
	}

	// TC09 - Negative: Search using a nonexistent contract number.
	@Test(priority = 5, groups = { "FT", "Negative" })
	public void verifySearchWithNonexistentContractNumberTest() {

		CRM_Vtiger_BITM_ServiceContractsPage page = new CRM_Vtiger_BITM_ServiceContractsPage(driver);

		String invalidContractNumber = "SERCON99999999_" + System.currentTimeMillis();

		page.openServiceContractsList();
		page.searchByContractNumber(invalidContractNumber);

		Assert.assertFalse(page.isContractNumberDisplayed(invalidContractNumber),
				"A nonexistent contract number unexpectedly appeared in results.");

		captureNegativeScreenshot("verifySearchWithNonexistentContractNumberTest");

		System.out.println("PASS: No matching contract number was found for " + invalidContractNumber);
	}
}
