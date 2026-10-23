
package CRM_Vtiger_BITM_POM;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CRM_Vtiger_BITM_ProjectTasksPage {

	private final WebDriver driver;
	private final WebDriverWait wait;

	private static final String BASE_URL = "http://49.249.29.4:8888/index.php";

	private final By taskNameLinks = By.xpath("//table//tr[td]//a[contains(@href,'ProjectTask')]");

	private final By createButton = By.xpath("//img[contains(@title,'Create Project Task')]" + " | //input[contains(@title,'Create Project Task')]"
					+ " | //a[contains(@href,'module=ProjectTask')" + " and contains(@href,'action=EditView')]");

	// Task form fields
	private final By taskNameField = By.name("projecttaskname");

	private final By priorityDropdown = By.name("projecttaskpriority");

	private final By taskTypeDropdown = By.name("projecttasktype");

	private final By taskCodeField = By.name("projecttaskcode");

	private final By assignedToDropdown = By.name("assigned_user_id");

	private final By progressDropdown = By.name("projecttaskprogress");

	private final By workedHoursField = By.name("projecttaskhours");

	private final By startDateField = By.name("projecttaskstartdate");

	private final By endDateField = By.name("projecttaskenddate");

	private final By descriptionField = By.name("description");

	// Related Project
	private final By projectValueField = By.id("projectid_display");

	private final By projectLookupButton = By.xpath("//img[contains(@onclick,'projectid')" + " and (@title='Select' or contains(@alt,'Select'))]");

	// Save and Cancel
	private final By saveButton = By.xpath("//input[@name='button' and contains(@title,'Save')]"
			+ " | //input[@type='submit' and contains(@value,'Save')]" + " | //button[normalize-space()='Save']");

	private final By cancelButton = By.xpath("//input[@name='button' and contains(@title,'Cancel')]"
			+ " | //input[contains(@value,'Cancel')]" + " | //button[normalize-space()='Cancel']");

	private final By inlineValidationMessages = By.xpath("//*[contains(@class,'errorMessage')" + " or contains(@class,'validation-error')"
					+ " or contains(@class,'fieldError')]" + "[normalize-space(.)!='']");

	public CRM_Vtiger_BITM_ProjectTasksPage(WebDriver driver) {
		if (driver == null) {
			throw new IllegalArgumentException("WebDriver cannot be null.");
		}

		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	// NAVIGATION

	public void navigateToProjectTasks() {
		driver.get(BASE_URL + "?module=ProjectTask&action=index");

		wait.until(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(createButton),	ExpectedConditions.presenceOfElementLocated(taskNameLinks)));

		System.out.println("Project Tasks page opened.");
	}

	public boolean isProjectTasksPageDisplayed() {
		try {
			String currentUrl = driver.getCurrentUrl();

			boolean correctModule = currentUrl.contains("module=ProjectTask");

			boolean listElementPresent = isDisplayed(createButton) || isDisplayed(taskNameLinks);

			System.out.println("Current URL: " + currentUrl);
			System.out.println("Project Tasks module detected: " + correctModule);
			System.out.println("Project Tasks list element detected: " + listElementPresent);

			return correctModule && listElementPresent;

		} catch (Exception e) {
			System.out.println("Project Tasks page verification failed: " + e.getMessage());
			return false;
		}
	}

	// CREATE FORM

	public void clickCreateButton() {
		wait.until(ExpectedConditions.elementToBeClickable(createButton)).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(taskNameField));

		System.out.println("Create Project Task form opened.");
	}

	public boolean isCreateFormDisplayed() {
		try {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(taskNameField)).isDisplayed();

		} catch (TimeoutException e) {
			return false;
		}
	}

	// TASK NAME

	public void enterTaskName(String taskName) {
		WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(taskNameField));

		field.clear();

		if (taskName != null && !taskName.trim().isEmpty()) {
			field.sendKeys(taskName.trim());
		}

		System.out.println("Task Name entered: " + field.getAttribute("value"));
	}

	public String getTaskNameValue() {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(taskNameField)).getAttribute("value");
	}

	// DROPDOWNS

	private void selectDropdownOption(By locator, String requestedValue, String fieldName) {

		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

		Select dropdown = new Select(element);

		for (WebElement option : dropdown.getOptions()) {
			String visibleText = option.getText().trim();
			String optionValue = option.getAttribute("value");

			if (visibleText.equalsIgnoreCase(requestedValue.trim())
					|| (optionValue != null && optionValue.equalsIgnoreCase(requestedValue.trim()))) {

				if (!option.isEnabled()) {
					continue;
				}

				dropdown.selectByVisibleText(visibleText);

				System.out.println(fieldName + " selected: " + visibleText);
				return;
			}
		}

		System.out.println("Available " + fieldName + " options:");

		for (WebElement option : dropdown.getOptions()) {
			System.out.println(" - " + option.getText().trim() + " [value=" + option.getAttribute("value") + "]");
		}

		throw new IllegalArgumentException(fieldName + " option not found: " + requestedValue);
	}

	public void selectPriority(String priority) {
		selectDropdownOption(priorityDropdown, priority, "Priority");
	}

	public void selectTaskType(String type) {
		selectDropdownOption(taskTypeDropdown, type, "Task Type");
	}

	public void enterTaskCode(String code) {
		enterText(taskCodeField, code);
	}

	public void selectAssignedUser(String username) {
		selectDropdownOption(assignedToDropdown, username, "Assigned User");
	}

	public void selectProgress(String progress) {
		selectDropdownOption(progressDropdown, progress, "Progress");
	}

	public void enterWorkedHours(String hours) {
		enterText(workedHoursField, hours);
	}

	public void enterStartDate(String date) {
		enterText(startDateField, date);
	}

	public void enterEndDate(String date) {
		enterText(endDateField, date);
	}

	public void enterDescription(String description) {
		enterText(descriptionField, description);
	}

	private void enterText(By locator, String value) {
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

		element.clear();

		if (value != null && !value.trim().isEmpty()) {
			element.sendKeys(value.trim());
		}
	}

	// RELATED PROJECT LOOKUP

	public void openProjectLookup() {
		Set<String> oldWindows = driver.getWindowHandles();

		WebElement lookup = wait.until(ExpectedConditions.elementToBeClickable(projectLookupButton));

		lookup.click();

		wait.until(d -> d.getWindowHandles().size() > oldWindows.size());

		for (String handle : driver.getWindowHandles()) {
			if (!oldWindows.contains(handle)) {
				driver.switchTo().window(handle);
				break;
			}
		}

		wait.until(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.name("search_text")),
				ExpectedConditions.presenceOfElementLocated(By.xpath("//table//tr[td]//a"))));

		System.out.println("Project lookup opened: " + driver.getCurrentUrl());
	}

	public void selectRelatedProject(String projectName) {
		if (projectName == null || projectName.trim().isEmpty()) {
			throw new IllegalArgumentException("Project name cannot be empty.");
		}

		By projectLink = By.xpath("//a[normalize-space(.)=" + xpathLiteral(projectName) + "]");

		WebElement link = wait.until(ExpectedConditions.elementToBeClickable(projectLink));

		link.click();

		wait.until(d -> d.getWindowHandles().size() == 1);

		String mainWindow = driver.getWindowHandles().iterator().next();

		driver.switchTo().window(mainWindow);

		wait.until(d -> {
			WebElement field = d.findElement(projectValueField);
			return projectName.equals(field.getAttribute("value"));
		});

		System.out.println("Related Project selected: " + projectName);
	}

	public String getProjectValue() {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(projectValueField)).getAttribute("value");
	}

	// SAVE AND CANCEL

	public void saveTask() {
		wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();

		System.out.println("Project Task Save button clicked.");
	}

	public void cancelForm() {
		wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();

		System.out.println("Cancel button clicked.");
	}

	public boolean isBackOnProjectTasksList() {
		try {
			wait.until(d -> d.getCurrentUrl().contains("module=ProjectTask")
					&& !d.getCurrentUrl().contains("action=EditView"));

			return isProjectTasksPageDisplayed();

		} catch (TimeoutException e) {
			return false;
		}
	}

	// VALIDATION CHECKS

	public boolean isMandatoryValidationShown() {
		return isSubmissionRejected();
	}

	public boolean isSubmissionRejected() {
		// 1. JavaScript alert
		try {
			Alert alert = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.alertIsPresent());

			String message = alert.getText();

			System.out.println("Validation alert: " + message);

			alert.accept();

			if (message != null && !message.trim().isEmpty()) {
				return true;
			}

		} catch (TimeoutException e) {
			// No alert; inspect other validation mechanisms.
		}

		// 2. Visible inline validation
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

		// 3. HTML5 browser validation on relevant fields
		By[] fields = { taskNameField, projectValueField, startDateField, endDateField };

		for (By locator : fields) {
			try {
				List<WebElement> elements = driver.findElements(locator);

				for (WebElement field : elements) {
					if (!field.isDisplayed()) {
						continue;
					}

					Object valid = ((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();",
							field);

					if (Boolean.FALSE.equals(valid)) {
						Object message = ((JavascriptExecutor) driver).executeScript("return arguments[0].validationMessage;", field);

						System.out.println("Browser validation: " + message);
						return true;
					}
				}
			} catch (Exception e) {
				// Continue checking other fields.
			}
		}

		// 4. Form remains open after save attempt
		return isCreateFormDisplayed();
	}

	public boolean isTaskNameInvalid() {
		return isFieldInvalid(taskNameField);
	}

	public boolean isStartDateInvalid() {
		return isFieldInvalid(startDateField);
	}

	public boolean isEndDateInvalid() {
		return isFieldInvalid(endDateField);
	}

	private boolean isFieldInvalid(By locator) {
		try {
			WebElement field = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

			Object result = ((JavascriptExecutor) driver).executeScript("return !arguments[0].checkValidity();", field);

			return Boolean.TRUE.equals(result);

		} catch (TimeoutException e) {
			return false;
		}
	}

	// TASK VERIFICATION

	public boolean isTaskDisplayed(String taskName) {
		By taskLink = By.xpath("//a[normalize-space(.)=" + xpathLiteral(taskName) + "]");

		for (WebElement element : driver.findElements(taskLink)) {
			try {
				if (element.isDisplayed()) {
					return true;
				}
			} catch (Exception e) {
				// Ignore stale elements.
			}
		}

		return false;
	}

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
}
