
package CRM_Vtiger_BITM_POM;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CRM_Vtiger_BITM_ProjectMilestonesPage {

	private final WebDriver driver;
	private final WebDriverWait wait;

	private final String baseUrl = "http://49.249.29.4:8888/index.php";

	private final String listUrl = baseUrl + "?module=ProjectMilestone&action=index";

	private final String createUrl = baseUrl + "?module=ProjectMilestone&action=EditView";

	// Navigation and create form

	private final By createButton = By.xpath("//a[contains(@href,'ProjectMilestone') " + "and contains(@href,'EditView')]"
					+ " | //a[contains(@title,'Create') " + "and contains(@href,'ProjectMilestone')]"
					+ " | //input[contains(@value,'Create') " + "and not(@type='hidden')]");

	private final By milestoneNameField = By.name("projectmilestonename");

	private final By milestoneDateField = By.name("projectmilestonedate");

	private final By milestoneTypeDropdown = By.name("projectmilestonetype");

	private final By projectField = By.id("projectid_display");

	private final By projectLookupButton = By.xpath("//img[@title='Select' " + "and contains(@onclick,'projectid_type')]"
					+ " | //input[@name='projectid' " + "and @type='hidden']/following-sibling::img");

	private final By saveButton = By.xpath("//input[@name='button' and contains(@title,'Save')]"
			+ " | //input[@name='button' and contains(@value,'Save')]"
			+ " | //button[contains(normalize-space(.),'Save')]");

	private final By cancelButton = By.xpath("//input[@name='button' and contains(@title,'Cancel')]"
			+ " | //input[@name='button' and contains(@value,'Cancel')]"
			+ " | //button[contains(normalize-space(.),'Cancel')]");

	// List search

	private final By listSearchButton = By.xpath("//img[contains(@title,'Search in Project Milestones')]" + " | //img[contains(@title,'Search')]");

	private final By listSearchText = By.name("search_text");

	private final By listSearchField = By.id("bas_searchfield");

	private final By listSearchNowButton = By.xpath("//input[contains(@value,'Search Now')]" + " | //button[contains(normalize-space(.),'Search Now')]");

	private final By listViewContents = By.id("ListViewContents");

	// Related-project lookup popup

	private final By popupSearchText = By.name("search_text");

	private final By popupSearchField = By.name("search_field");

	private final By popupSearchButton = By.name("search");

	// Validation messages

	private final By inlineValidationMessages = By.xpath("//*[contains(@class,'errorMessage')"
			+ " or contains(@class,'validation-error')" + " or contains(@class,'fieldError')"
			+ " or contains(@class,'error')]" + "[normalize-space(.)!='']");

	public CRM_Vtiger_BITM_ProjectMilestonesPage(WebDriver driver) {

		if (driver == null) {
			throw new IllegalArgumentException("WebDriver cannot be null.");
		}

		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	// NAVIGATION

	public void navigateToProjectMilestones() {
		acceptAlertIfPresent();

		try {
			driver.get(listUrl);

		} catch (TimeoutException e) {
			System.out.println("Navigation timed out. Checking current page.");

			try {
				((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.stop();");
			} catch (Exception ignored) {
				// Continue checking the page.
			}
		}

		WebDriverWait pageWait = new WebDriverWait(driver, Duration.ofSeconds(20));

		pageWait.until(d -> {
			try {
				String currentUrl = d.getCurrentUrl();

				if (currentUrl == null || !currentUrl.contains("ProjectMilestone")) {
					return false;
				}

				return isDisplayed(createButton) || !d.findElements(listViewContents).isEmpty()
						|| d.getPageSource().contains("Project Milestones");

			} catch (org.openqa.selenium.UnhandledAlertException e) {
				acceptAlertIfPresent();
				return false;
			}
		});

		System.out.println("Project Milestones page opened: " + driver.getCurrentUrl());
	}

	public boolean isProjectMilestonesPageDisplayed() {
		try {
			String currentUrl = driver.getCurrentUrl();

			if (currentUrl == null || !currentUrl.contains("ProjectMilestone")) {
				return false;
			}

			return isDisplayed(createButton) || !driver.findElements(listViewContents).isEmpty()
					|| driver.getPageSource().contains("Project Milestones");

		} catch (Exception e) {
			System.out.println("Could not verify Project Milestones page: " + e.getMessage());
			return false;
		}
	}

	// CREATE FORM

	public void clickCreateButton() {
		boolean clicked = false;

		try {
			for (WebElement element : driver.findElements(createButton)) {

				if (element.isDisplayed() && element.isEnabled()) {
					element.click();
					clicked = true;
					break;
				}
			}

		} catch (Exception e) {
			System.out.println("Create link could not be clicked: " + e.getMessage());
		}

		if (!clicked) {
			driver.get(createUrl);
		}

		wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneNameField));

		System.out.println("Create Milestone form opened.");
	}

	public boolean isCreateFormDisplayed() {
		try {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneNameField)).isDisplayed();

		} catch (TimeoutException e) {
			return false;
		}
	}

	public void enterMilestoneName(String name) {
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneNameField));

		element.clear();

		if (name != null && !name.isEmpty()) {
			element.sendKeys(name);
		}
	}

	public String getMilestoneName() {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneNameField)).getAttribute("value");
	}

	public void enterMilestoneDate(String date) {
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneDateField));

		element.clear();

		if (date != null && !date.isEmpty()) {
			element.sendKeys(date);
		}

		System.out.println("Milestone date entered: " + element.getAttribute("value"));
	}

	public void selectMilestoneType(String type) {
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneTypeDropdown));

		Select select = new Select(element);

		try {
			select.selectByVisibleText(type);

		} catch (NoSuchElementException e) {
			List<String> options = new ArrayList<>();

			for (WebElement option : select.getOptions()) {
				options.add(option.getText().trim());
			}

			throw new IllegalArgumentException("Milestone type '" + type + "' is not available. Options: " + options,
					e);
		}
	}

	public String getRelatedProject() {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(projectField)).getAttribute("value");
	}

	// RELATED PROJECT LOOKUP

	public boolean openRelatedToLookup() {
		String parentWindow = driver.getWindowHandle();
		Set<String> oldHandles = driver.getWindowHandles();

		try {
			WebElement lookupButton = wait.until(ExpectedConditions.elementToBeClickable(projectLookupButton));

			lookupButton.click();

			try {
				wait.until(ExpectedConditions.numberOfWindowsToBe(oldHandles.size() + 1));

				Set<String> newHandles = driver.getWindowHandles();
				newHandles.removeAll(oldHandles);

				if (!newHandles.isEmpty()) {
					driver.switchTo().window(newHandles.iterator().next());

					wait.until(d -> "complete".equals(
							((org.openqa.selenium.JavascriptExecutor) d).executeScript("return document.readyState")));

					boolean displayed = waitForLookupControls();

					System.out.println("Related Project lookup popup detected: " + displayed);

					return displayed;
				}

			} catch (TimeoutException e) {
				System.out.println("No separate lookup window detected.");
			}

			if (driver.getWindowHandles().contains(parentWindow)) {
				driver.switchTo().window(parentWindow);
			}

			boolean displayed = waitForLookupControls();

			System.out.println("Related Project lookup in current window: " + displayed);

			return displayed;

		} catch (Exception e) {
			System.out.println("Project lookup error: " + e.getClass().getSimpleName() + ": " + e.getMessage());

			return false;

		} finally {
			try {
				if (driver.getWindowHandles().contains(parentWindow)) {
					driver.switchTo().window(parentWindow);
				}
			} catch (Exception e) {
				System.out.println("Could not restore parent window: " + e.getMessage());
			}
		}
	}

	private boolean waitForLookupControls() {
		try {
			return new WebDriverWait(driver, Duration.ofSeconds(5)).until(
					d -> !d.findElements(popupSearchText).isEmpty() || !d.findElements(popupSearchField).isEmpty());

		} catch (TimeoutException e) {
			return false;
		}
	}

	public void selectRelatedProject(String projectName) {
		if (projectName == null || projectName.trim().isEmpty()) {
			throw new IllegalArgumentException("Related Project name cannot be empty.");
		}

		String parentWindow = driver.getWindowHandle();
		Set<String> oldHandles = driver.getWindowHandles();

		try {
			wait.until(ExpectedConditions.elementToBeClickable(projectLookupButton)).click();

			wait.until(ExpectedConditions.numberOfWindowsToBe(oldHandles.size() + 1));

			Set<String> newHandles = driver.getWindowHandles();
			newHandles.removeAll(oldHandles);

			if (newHandles.isEmpty()) {
				throw new RuntimeException("Project lookup popup did not open.");
			}

			driver.switchTo().window(newHandles.iterator().next());

			WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(popupSearchText));

			searchBox.clear();
			searchBox.sendKeys(projectName);

			if (!driver.findElements(popupSearchField).isEmpty()) {
				WebElement dropdown = driver.findElement(popupSearchField);

				Select select = new Select(dropdown);
				boolean optionFound = false;

				for (WebElement option : select.getOptions()) {
					if ("projectname".equalsIgnoreCase(option.getAttribute("value"))) {

						select.selectByValue("projectname");
						optionFound = true;
						break;
					}
				}

				if (!optionFound) {
					System.out.println("Project Name search criterion not found; " + "using the default criterion.");
				}
			}

			if (!driver.findElements(popupSearchButton).isEmpty()) {
				wait.until(ExpectedConditions.elementToBeClickable(popupSearchButton)).click();
			}

			By projectLink = By.xpath("//a[normalize-space(.)=" + xpathLiteral(projectName) + "]");

			wait.until(ExpectedConditions.elementToBeClickable(projectLink)).click();

			wait.until(ExpectedConditions.numberOfWindowsToBe(oldHandles.size()));

			driver.switchTo().window(parentWindow);

			wait.until(ExpectedConditions.visibilityOfElementLocated(milestoneNameField));

			wait.until(ExpectedConditions.attributeToBe(projectField, "value", projectName));

			System.out.println("Selected project: " + projectName);

		} catch (Exception e) {
			throw new RuntimeException("Unable to select project: " + projectName, e);

		} finally {
			closePopupAndReturn(parentWindow);
		}
	}

	private String xpathLiteral(String value) {
		if (!value.contains("'")) {
			return "'" + value + "'";
		}

		if (!value.contains("\"")) {
			return "\"" + value + "\"";
		}

		String[] parts = value.split("'", -1);
		StringBuilder result = new StringBuilder("concat(");

		for (int i = 0; i < parts.length; i++) {
			if (i > 0) {
				result.append(", \"'\", ");
			}

			result.append("'").append(parts[i]).append("'");
		}

		return result.append(")").toString();
	}

	private void closePopupAndReturn(String parentWindow) {
		try {
			for (String handle : driver.getWindowHandles()) {
				if (!handle.equals(parentWindow)) {
					driver.switchTo().window(handle);
					driver.close();
				}
			}

			if (driver.getWindowHandles().contains(parentWindow)) {
				driver.switchTo().window(parentWindow);
			}

		} catch (Exception e) {
			System.out.println("Popup cleanup warning: " + e.getMessage());
		}
	}

	// SAVE AND CANCEL

	public void clickSave() {
		wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
	}

	public void clickCancel() {
		wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();
	}

	// SEARCH MILESTONES

	public void searchMilestoneByName(String milestoneName) {
		if (milestoneName == null || milestoneName.trim().isEmpty()) {
			throw new IllegalArgumentException("Milestone name cannot be empty.");
		}

		if (!isDisplayed(listSearchText)) {
			wait.until(ExpectedConditions.elementToBeClickable(listSearchButton)).click();
		}

		WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(listSearchText));

		searchBox.clear();
		searchBox.sendKeys(milestoneName);

		WebElement searchDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(listSearchField));

		Select select = new Select(searchDropdown);
		select.selectByValue("projectmilestonename");

		wait.until(ExpectedConditions.elementToBeClickable(listSearchNowButton)).click();

		wait.until(ExpectedConditions.presenceOfElementLocated(listViewContents));

		System.out.println("Search submitted for milestone: " + milestoneName);
	}

	public boolean isMilestoneDisplayed(String milestoneName) {
		By milestoneLink = By.xpath("//a[normalize-space(.)=" + xpathLiteral(milestoneName) + "]");

		try {
			return wait.until(d -> {
				for (WebElement link : d.findElements(milestoneLink)) {
					try {
						if (link.isDisplayed()) {
							return true;
						}
					} catch (StaleElementReferenceException ignored) {
						// Retry with current DOM.
					}
				}

				return false;
			});

		} catch (TimeoutException e) {
			return false;
		}
	}

	// NEGATIVE-TEST VALIDATION

	public boolean isMandatoryValidationShown() {
		// 1. Check JavaScript alert.
		try {
			Alert alert = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.alertIsPresent());

			String message = alert.getText();

			System.out.println("Mandatory validation alert: " + message);

			alert.accept();

			if (message != null) {
				String normalized = message.toLowerCase();

				if (normalized.contains("cannot be empty") || normalized.contains("cannot be none")
						|| normalized.contains("required") || normalized.contains("mandatory")
						|| normalized.contains("enter")) {
					return true;
				}
			}

		} catch (TimeoutException e) {
			System.out.println("No JavaScript alert. Checking inline validation.");
		}

		// 2. Check visible inline errors.
		try {
			for (WebElement error : driver.findElements(inlineValidationMessages)) {

				if (error.isDisplayed() && !error.getText().trim().isEmpty()) {

					System.out.println("Inline validation: " + error.getText());

					return true;
				}
			}

		} catch (Exception e) {
			System.out.println("Inline validation check: " + e.getMessage());
		}

		// 3. Check HTML5 required-field validation.
		try {
			WebElement nameField = driver.findElement(milestoneNameField);

			Object valid = ((org.openqa.selenium.JavascriptExecutor) driver)
					.executeScript("return arguments[0].checkValidity();", nameField);

			if (Boolean.FALSE.equals(valid)) {
				System.out.println("HTML5 validation rejected the empty field.");

				return true;
			}

		} catch (Exception ignored) {
			// Continue to the final check.
		}

		// 4. Supporting evidence: form remains open and field is empty.
		try {
			if (isCreateFormDisplayed() && getMilestoneName().trim().isEmpty()) {

				System.out.println("Create form remains open with empty milestone name.");

				return true;
			}

		} catch (Exception ignored) {
			// The form may have navigated away.
		}

		return false;
	}

	// ALERT HANDLING

	private void acceptAlertIfPresent() {
		try {
			Alert alert = new WebDriverWait(driver, Duration.ofSeconds(2)).until(ExpectedConditions.alertIsPresent());

			System.out.println("JavaScript alert: " + alert.getText());

			alert.accept();

		} catch (TimeoutException e) {
			// No alert is present.
		} catch (org.openqa.selenium.NoAlertPresentException e) {
			// The alert disappeared before handling.
		}
	}

	// GENERAL HELPERS

	private boolean isDisplayed(By locator) {
		try {
			for (WebElement element : driver.findElements(locator)) {
				if (element.isDisplayed()) {
					return true;
				}
			}

		} catch (Exception e) {
			return false;
		}

		return false;
	}
}
