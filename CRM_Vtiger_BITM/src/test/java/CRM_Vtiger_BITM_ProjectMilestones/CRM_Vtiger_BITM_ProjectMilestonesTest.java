
package CRM_Vtiger_BITM_ProjectMilestones;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import CRM_Vtiger_BITM_BaseClass.CRM_Vtiger_BITM_BaseClass;
import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_ExcelUtility;
import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_ScreenshotUtility;
import CRM_Vtiger_BITM_POM.CRM_Vtiger_BITM_ProjectMilestonesPage;

public class CRM_Vtiger_BITM_ProjectMilestonesTest extends CRM_Vtiger_BITM_BaseClass {

	private static final String SHEET_NAME = "Project Milestones";

	private static final DateTimeFormatter EXCEL_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy",
			Locale.ENGLISH);

	private final DateTimeFormatter outputDateFormat = DateTimeFormatter.ISO_LOCAL_DATE;

	// SCREENSHOT UTILITY: called only by negative test cases.
	private void captureNegativeScreenshot(String testName) {
		try {
			CRM_Vtiger_BITM_ScreenshotUtility.captureScreenshot(driver, testName);

			Reporter.log("Screenshot saved for negative test: " + testName, true);

		} catch (Exception e) {
			System.err.println("SCREENSHOT ERROR for " + testName + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	// EXCEL UTILITIES
	private List<Map<String, String>> readMilestoneExcelData() throws IOException {

		try (CRM_Vtiger_BITM_ExcelUtility excel = new CRM_Vtiger_BITM_ExcelUtility()) {

			excel.openWorkbook();

			List<Map<String, String>> rows = excel.getAllDataAsMaps(SHEET_NAME);

			Assert.assertNotNull(rows, "Excel data returned null for worksheet: " + SHEET_NAME);

			Assert.assertFalse(rows.isEmpty(), "No data found in worksheet: " + SHEET_NAME);

			return rows;
		}
	}

	private String getValue(Map<String, String> data, String columnName) {

		String value = data.get(columnName);
		return value == null ? "" : value.trim();
	}

	private CRM_Vtiger_BITM_ProjectMilestonesPage getMilestonePage() {
		Assert.assertNotNull(driver, "WebDriver is null. Check BaseClass setup.");

		return new CRM_Vtiger_BITM_ProjectMilestonesPage(driver);
	}

	private String convertDate(String excelDate) {
		if (excelDate == null || excelDate.trim().isEmpty()) {
			throw new IllegalArgumentException("Milestone Date cannot be empty.");
		}

		try {
			LocalDate date = LocalDate.parse(excelDate.trim(), EXCEL_DATE_FORMAT);

			return date.format(outputDateFormat);

		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
					"Invalid milestone date: " + excelDate + ". Expected format: dd-MM-yyyy.", e);
		}
	}

	private String handleSaveAlertIfPresent() {
		try {
			Alert alert = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.alertIsPresent());

			String message = alert.getText();
			alert.accept();

			System.out.println("Browser alert handled: " + message);

			return message;

		} catch (TimeoutException | NoAlertPresentException e) {
			return null;
		}
	}

	// TEST 1: VERIFY PAGE
	@Test(priority = 1, groups = { "FT", "Regression" })
	public void verifyProjectMilestonesPage() {
		CRM_Vtiger_BITM_ProjectMilestonesPage page = getMilestonePage();

		page.navigateToProjectMilestones();

		Assert.assertTrue(page.isProjectMilestonesPageDisplayed(), "Project Milestones page was not displayed.");

		System.out.println("PASS: Project Milestones page displayed.");
	}

	// TEST 2: SEARCH MILESTONES FROM EXCEL
	@Test(priority = 2, groups = { "FT", "Regression" })
	public void verifySearchMilestone() throws IOException {
		List<Map<String, String>> rows = readMilestoneExcelData();

		CRM_Vtiger_BITM_ProjectMilestonesPage page = getMilestonePage();

		for (Map<String, String> data : rows) {
			String milestoneName = getValue(data, "Milestone Name");

			Assert.assertFalse(milestoneName.isEmpty(), "Milestone Name is empty in Excel.");

			page.navigateToProjectMilestones();
			page.searchMilestoneByName(milestoneName);

			Assert.assertTrue(page.isMilestoneDisplayed(milestoneName), "Milestone not found: " + milestoneName);

			System.out.println("PASS: Milestone found: " + milestoneName);
		}
	}

	// TEST 3: CREATE FORM
	@Test(priority = 3, groups = { "FT", "Smoke", "Regression" })
	public void verifyCreateMilestoneForm() {
		CRM_Vtiger_BITM_ProjectMilestonesPage page = getMilestonePage();

		page.navigateToProjectMilestones();
		page.clickCreateButton();

		Assert.assertTrue(page.isCreateFormDisplayed(), "Create Milestone form did not open.");

		System.out.println("PASS: Create Milestone form displayed.");
	}

	// TEST 4: RELATED PROJECT LOOKUP
	@Test(priority = 4, groups = { "FT", "Regression" })
	public void verifyRelatedToLookup() {
		CRM_Vtiger_BITM_ProjectMilestonesPage page = getMilestonePage();

		page.navigateToProjectMilestones();
		page.clickCreateButton();

		Assert.assertTrue(page.isCreateFormDisplayed(), "Create Milestone form did not open.");

		Assert.assertTrue(page.openRelatedToLookup(), "Related Project lookup did not open.");

		System.out.println("PASS: Related Project lookup opened.");
	}

	// TEST 5: CREATE MILESTONES USING EXCEL
	@Test(priority = 5, groups = { "FT", "Smoke", "Regression" })
	public void createMilestoneWithValidDetails() throws IOException {
		List<Map<String, String>> rows = readMilestoneExcelData();

		CRM_Vtiger_BITM_ProjectMilestonesPage page = getMilestonePage();

		int createdCount = 0;
		int skippedCount = 0;

		for (Map<String, String> data : rows) {
			String milestoneName = getValue(data, "Milestone Name");

			String projectName = getValue(data, "Related Project");

			String excelDate = getValue(data, "Milestone Date");

			String milestoneType = getValue(data, "Type");

			Assert.assertFalse(milestoneName.isEmpty(), "Milestone Name is empty in Excel.");

			Assert.assertFalse(projectName.isEmpty(), "Related Project is empty for: " + milestoneName);

			Assert.assertFalse(excelDate.isEmpty(), "Milestone Date is empty for: " + milestoneName);

			String formattedDate = convertDate(excelDate);

			// Skip duplicate records.
			page.navigateToProjectMilestones();
			page.searchMilestoneByName(milestoneName);

			if (page.isMilestoneDisplayed(milestoneName)) {
				System.out.println("SKIPPED: Milestone already exists: " + milestoneName);

				skippedCount++;
				continue;
			}

			page.navigateToProjectMilestones();
			page.clickCreateButton();

			Assert.assertTrue(page.isCreateFormDisplayed(), "Create form did not open for: " + milestoneName);

			page.enterMilestoneName(milestoneName);
			page.selectRelatedProject(projectName);
			page.enterMilestoneDate(formattedDate);

			if (!milestoneType.isEmpty()) {
				page.selectMilestoneType(milestoneType);
			}

			page.clickSave();

			String alertMessage = handleSaveAlertIfPresent();

			Assert.assertNull(alertMessage,
					"Browser alert appeared while saving " + milestoneName + ". Alert: " + alertMessage);

			page.navigateToProjectMilestones();
			page.searchMilestoneByName(milestoneName);

			Assert.assertTrue(page.isMilestoneDisplayed(milestoneName),
					"Milestone was not found after saving: " + milestoneName);

			createdCount++;

			System.out.println("PASS: Created and verified milestone: " + milestoneName);
		}

		System.out.println("================================");
		System.out.println("Milestone creation summary");
		System.out.println("Created: " + createdCount);
		System.out.println("Skipped: " + skippedCount);
		System.out.println("Excel rows: " + rows.size());
		System.out.println("================================");

		Assert.assertEquals(createdCount + skippedCount, rows.size(), "Not all Excel milestone rows were processed.");
	}

	// NEGATIVE TEST 1: EMPTY MILESTONE NAME
	@Test(priority = 6, groups = { "FT", "Negative" })
	public void verifyMilestoneNameMandatoryValidation() {
		CRM_Vtiger_BITM_ProjectMilestonesPage page = getMilestonePage();

		page.navigateToProjectMilestones();
		page.clickCreateButton();

		Assert.assertTrue(page.isCreateFormDisplayed(), "Create Milestone form did not open.");

		page.enterMilestoneName("");
		page.clickSave();

		Assert.assertTrue(page.isMandatoryValidationShown(), "Validation was not detected for empty Milestone Name.");

		captureNegativeScreenshot("verifyMilestoneNameMandatoryValidation");

		System.out.println("PASS: Empty Milestone Name validation checked.");
	}

	// NEGATIVE TEST 2: EMPTY RELATED PROJECT
	@Test(priority = 7, groups = { "FT", "Negative" })
	public void verifyRelatedToMandatoryValidation() {
		CRM_Vtiger_BITM_ProjectMilestonesPage page = getMilestonePage();

		page.navigateToProjectMilestones();
		page.clickCreateButton();

		Assert.assertTrue(page.isCreateFormDisplayed(), "Create Milestone form did not open.");

		page.enterMilestoneName("Negative_Milestone_" + System.currentTimeMillis());

		// Do not select a related project.
		page.clickSave();

		Assert.assertTrue(page.isMandatoryValidationShown(), "Validation was not detected for empty Related To.");

		captureNegativeScreenshot("verifyRelatedToMandatoryValidation");

		System.out.println("PASS: Empty Related To validation checked.");
	}

	// NEGATIVE TEST 3: INVALID MILESTONE DATE
	@Test(priority = 8, groups = { "FT", "Negative" })
	public void verifyInvalidMilestoneDateValidation() {
		CRM_Vtiger_BITM_ProjectMilestonesPage page = getMilestonePage();

		page.navigateToProjectMilestones();
		page.clickCreateButton();

		Assert.assertTrue(page.isCreateFormDisplayed(), "Create Milestone form did not open.");

		page.enterMilestoneName("Invalid_Date_Milestone_" + System.currentTimeMillis());

		page.enterMilestoneDate("not-a-date");

		WebElement dateField = driver.findElement(By.name("projectmilestonedate"));

		JavascriptExecutor js = (JavascriptExecutor) driver;

		Boolean browserValid = (Boolean) js.executeScript("return arguments[0].checkValidity();", dateField);

		// Check browser-side date validation first.
		if (Boolean.FALSE.equals(browserValid)) {
			captureNegativeScreenshot("verifyInvalidMilestoneDateValidation");

			System.out.println("PASS: Browser rejected the invalid milestone date.");
			return;
		}

		// Browser accepts the text, so submit and inspect server-side response.
		page.clickSave();

		String alertMessage = handleSaveAlertIfPresent();

		if (alertMessage != null && !alertMessage.trim().isEmpty()) {
			captureNegativeScreenshot("verifyInvalidMilestoneDateValidation");

			System.out.println("PASS: Invalid date rejected by browser alert: " + alertMessage);
			return;
		}

		// Check visible inline validation text.
		boolean validationMessageFound = driver.findElements(By.xpath("//*[contains(translate(normalize-space(.),"
				+ "'ABCDEFGHIJKLMNOPQRSTUVWXYZ'," + "'abcdefghijklmnopqrstuvwxyz'),'invalid date')"
				+ " or contains(translate(normalize-space(.)," + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
				+ "'abcdefghijklmnopqrstuvwxyz'),'date is required')" + " or contains(translate(normalize-space(.),"
				+ "'ABCDEFGHIJKLMNOPQRSTUVWXYZ'," + "'abcdefghijklmnopqrstuvwxyz'),'enter a valid date')]")).stream()
				.anyMatch(element -> {
					try {
						return element.isDisplayed() && !element.getText().trim().isEmpty();
					} catch (Exception e) {
						return false;
					}
				});

		if (validationMessageFound) {
			captureNegativeScreenshot("verifyInvalidMilestoneDateValidation");

			System.out.println("PASS: Invalid date validation message detected.");
			return;
		}

		Assert.fail("The application did not show a confirmed rejection "
				+ "for the invalid milestone date. Inspect the form "
				+ "and determine whether it accepted or saved the value.");
	}
}
