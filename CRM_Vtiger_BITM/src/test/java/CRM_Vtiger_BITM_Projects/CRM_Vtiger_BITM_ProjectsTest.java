
package CRM_Vtiger_BITM_Projects;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import CRM_Vtiger_BITM_BaseClass.CRM_Vtiger_BITM_BaseClass;
import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_ScreenshotUtility;
import CRM_Vtiger_BITM_POM.CRM_Vtiger_BITM_ProjectsPage;

public class CRM_Vtiger_BITM_ProjectsTest extends CRM_Vtiger_BITM_BaseClass {

	private CRM_Vtiger_BITM_ProjectsPage getProjectsPage() {
		Assert.assertNotNull(driver, "WebDriver was not initialized by BaseClass.");

		return new CRM_Vtiger_BITM_ProjectsPage(driver);
	}

	private void captureNegativeScreenshot(String testName) {
		try {
			CRM_Vtiger_BITM_ScreenshotUtility.captureScreenshot(driver, testName);
		} catch (Exception e) {
			System.err.println("SCREENSHOT ERROR for " + testName + ": " + e.getMessage());
		}
	}

	private String acceptAlertIfPresent() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

			Alert alert = wait.until(ExpectedConditions.alertIsPresent());

			String message = alert.getText();
			alert.accept();

			System.out.println("Alert handled: " + message);
			return message;

		} catch (TimeoutException | NoAlertPresentException e) {
			return null;
		}
	}

	// TEST 1: Verify Projects page
	@Test(priority = 1, groups = { "FT", "Regression" })
	public void verifyProjectsPage() {
		CRM_Vtiger_BITM_ProjectsPage page = getProjectsPage();

		page.openProjectsList(url);

		Assert.assertTrue(page.isProjectsPageDisplayed(), "Projects page was not displayed.");

		System.out.println("PASS: Projects page displayed successfully.");
	}

	// TEST 2: Verify Create Project form
	@Test(priority = 2, groups = { "FT", "Regression" })
	public void verifyCreateProjectForm() {
		CRM_Vtiger_BITM_ProjectsPage page = getProjectsPage();

		page.openProjectsList(url);
		page.clickAddProject();

		Assert.assertTrue(page.isProjectNameFieldDisplayed(), "Create Project form was not displayed.");

		System.out.println("PASS: Create Project form displayed.");
	}

	// TEST 3: Negative test - empty Project Name
	@Test(priority = 3, groups = { "FT", "Negative" })
	public void verifyProjectNameMandatoryValidation() {
		CRM_Vtiger_BITM_ProjectsPage page = getProjectsPage();

		page.openProjectsList(url);
		page.clickAddProject();

		Assert.assertTrue(page.isProjectNameFieldDisplayed(), "Create Project form did not open.");

		page.enterProjectName("");

		Assert.assertTrue(page.getProjectName().trim().isEmpty(), "Project Name should be empty for this test.");

		page.clickSave();

		boolean validationDetected = false;

		// Check for JavaScript alert validation.
		String alertMessage = acceptAlertIfPresent();

		if (alertMessage != null && !alertMessage.trim().isEmpty()) {
			System.out.println("Project validation alert: " + alertMessage);
			validationDetected = true;
		}

		// Check HTML5 required-field validation.
		if (!validationDetected) {
			try {
				if (page.isProjectNameFieldDisplayed() && page.isProjectNameInvalid()) {

					System.out.println("Browser validation message: " + page.getProjectNameValidationMessage());

					validationDetected = true;
				}
			} catch (Exception e) {
				System.out.println("HTML validation check: " + e.getMessage());
			}
		}

		// Check that the form remains open with the field empty.
		if (!validationDetected) {
			try {
				if (page.isProjectNameFieldDisplayed() && page.getProjectName().trim().isEmpty()) {

					System.out.println("Form remains open with Project Name empty.");

					validationDetected = true;
				}
			} catch (Exception e) {
				System.out.println("Form-state check: " + e.getMessage());
			}
		}

		Assert.assertTrue(validationDetected, "Project Name mandatory validation was not confirmed.");

		captureNegativeScreenshot("verifyProjectNameMandatoryValidation");

		System.out.println("PASS: Project Name mandatory validation confirmed.");
	}

	// TEST 4: Negative test - search for nonexistent project
	@Test(priority = 4, groups = { "FT", "Negative" })
	public void verifySearchForNonExistingProject() {
		CRM_Vtiger_BITM_ProjectsPage page = getProjectsPage();

		String nonexistentProject = "NoSuchProject_" + System.currentTimeMillis();

		page.openProjectsList(url);
		page.searchProject(nonexistentProject);

		Assert.assertFalse(page.isProjectListed(nonexistentProject),"A nonexistent project appeared in results: " + nonexistentProject);

		captureNegativeScreenshot("verifySearchForNonExistingProject");

		System.out.println("PASS: Nonexistent project was not listed.");
	}

	// TEST 5: Negative test - cancel project creation
	@Test(priority = 5, groups = { "FT", "Negative" })
	public void verifyCancelProjectCreation() {
		CRM_Vtiger_BITM_ProjectsPage page = getProjectsPage();

		String cancelledProject = "CancelledProject_" + System.currentTimeMillis();

		page.openProjectsList(url);
		page.clickAddProject();

		Assert.assertTrue(page.isProjectNameFieldDisplayed(), "Create Project form did not open.");

		page.enterProjectName(cancelledProject);

		Assert.assertEquals(page.getProjectName(), cancelledProject, "Project name was not entered correctly.");

		// Cancel may trigger a JavaScript confirmation alert.
		page.clickCancel();

		String alertMessage = acceptAlertIfPresent();

		if (alertMessage != null) {
			System.out.println("Cancel confirmation alert: " + alertMessage);
		}

		// Verify that cancellation returned to the list.
		Assert.assertTrue(page.isProjectsPageDisplayed(), "Projects list did not return after cancellation.");

		// Search for the cancelled project.
		page.searchProject(cancelledProject);

		// Confirm that the project was not created.
		Assert.assertFalse(page.isProjectListed(cancelledProject),"Cancelled project was unexpectedly found: " + cancelledProject);

		// Screenshot only after the negative result is confirmed.
		captureNegativeScreenshot("verifyCancelProjectCreation");

		System.out.println("PASS: Cancelled project was not created.");
	}
}
