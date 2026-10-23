
package CRM_Vtiger_BITM_POM;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CRM_Vtiger_BITM_ProjectsPage {

	private final WebDriver driver;
	private final WebDriverWait wait;

	private final String defaultBaseUrl = "http://49.249.29.4:8888/index.php";

	// Project form
	private final By projectNameField = By.name("projectname");

	private final By saveButtons = By.xpath("//input[@type='submit' and " + "(contains(@value,'Save') or contains(@title,'Save'))]" + " | //button[normalize-space()='Save']"
			+ " | //input[@type='button' and contains(@value,'Save')]");

	private final By cancelButtons = By.xpath("//input[@name='button' and contains(@title,'Cancel')]"
			+ " | //input[@name='button' and contains(@value,'Cancel')]" + " | //button[normalize-space()='Cancel']");

	// Projects list
	private final By searchTextField = By.name("search_text");

	private final By searchFieldDropdown = By.id("bas_searchfield");

	private final By searchNowButton = By.xpath("//input[contains(@value,'Search Now')]" + " | //button[contains(normalize-space(.),'Search Now')]");

	private final By searchIcon = By.xpath("//img[contains(@title,'Search')]");

	private final By listViewContents = By.id("ListViewContents");

	// Add Project button
	private final By addProjectButton = By.xpath("//a[contains(@href,'module=Project') " + "and contains(@href,'action=EditView')]" + " | //input[@title='Create']"
			+ " | //img[contains(@src,'add')]" + " | //img[contains(@alt,'Create')]");

	public CRM_Vtiger_BITM_ProjectsPage(WebDriver driver) {
		if (driver == null) {
			throw new IllegalArgumentException("WebDriver cannot be null.");
		}

		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	// OPEN PROJECTS LIST

	public void openProjectsList(String baseUrl) {
		String cleanUrl = (baseUrl == null || baseUrl.trim().isEmpty()) ? defaultBaseUrl : baseUrl.trim();

		if (cleanUrl.contains("?")) {
			cleanUrl = cleanUrl.substring(0, cleanUrl.indexOf("?"));
		}

		driver.get(cleanUrl + "?module=Project&action=index");

		wait.until(d -> d.getCurrentUrl().contains("module=Project") && d.getCurrentUrl().contains("action=index"));

		System.out.println("Projects list opened: " + driver.getCurrentUrl());
	}

	// VERIFY PROJECTS LIST

	public boolean isProjectsPageDisplayed() {
		try {
			String currentUrl = driver.getCurrentUrl();

			boolean correctModule = currentUrl.contains("module=Project") && currentUrl.contains("action=index");

			boolean searchDisplayed = isDisplayed(searchTextField) || isDisplayed(listViewContents)
					|| isDisplayed(addProjectButton);

			System.out.println("Current URL: " + currentUrl);
			System.out.println("Correct Projects module: " + correctModule);
			System.out.println("Projects list controls displayed: " + searchDisplayed);

			return correctModule && searchDisplayed;

		} catch (Exception e) {
			System.out.println("Projects page verification failed: " + e.getMessage());
			return false;
		}
	}

	// OPEN CREATE PROJECT FORM

	public void clickAddProject() {
		boolean clicked = false;

		List<WebElement> buttons = driver.findElements(addProjectButton);

		for (WebElement button : buttons) {
			try {
				if (button.isDisplayed() && button.isEnabled()) {
					button.click();
					clicked = true;
					break;
				}
			} catch (Exception e) {
				// Try another matching Create control.
			}
		}

		if (!clicked) {
			String currentUrl = driver.getCurrentUrl();

			String cleanUrl = currentUrl.contains("?") ? currentUrl.substring(0, currentUrl.indexOf("?")) : currentUrl;

			driver.get(cleanUrl + "?module=Project&action=EditView");
		}

		wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField));

		System.out.println("Create Project form opened.");
	}

	public boolean isProjectNameFieldDisplayed() {
		try {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField)).isDisplayed();

		} catch (TimeoutException e) {
			return false;
		}
	}

	// ENTER PROJECT NAME

	public void enterProjectName(String name) {
		WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField));

		field.clear();

		if (name != null && !name.isEmpty()) {
			field.sendKeys(name);
		}

		System.out.println("Project Name entered: " + field.getAttribute("value"));
	}

	public String getProjectName() {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField)).getAttribute("value");
	}

	// SAVE PROJECT

	public void clickSave() {
		List<WebElement> buttons = driver.findElements(saveButtons);

		System.out.println("Save buttons found: " + buttons.size());

		for (WebElement button : buttons) {
			try {
				if (button.isDisplayed() && button.isEnabled()) {
					button.click();
					System.out.println("Save button clicked.");
					return;
				}
			} catch (Exception e) {
				// Try the next matching Save control.
			}
		}

		throw new IllegalStateException("No visible and enabled Save button was found.");
	}

	// CANCEL PROJECT CREATION

	public void clickCancel() {
		WebElement cancel = wait.until(ExpectedConditions.elementToBeClickable(cancelButtons));

		cancel.click();

		wait.until(d -> d.getCurrentUrl().contains("action=index") 	|| d.findElements(projectNameField).stream().noneMatch(WebElement::isDisplayed));

		System.out.println("Create Project form cancelled.");
	}

	// HTML5 VALIDATION

	public boolean isProjectNameInvalid() {
		try {
			WebElement field = wait.until(ExpectedConditions.presenceOfElementLocated(projectNameField));

			JavascriptExecutor js = (JavascriptExecutor) driver;

			return Boolean.TRUE.equals(js.executeScript("return !arguments[0].checkValidity();", field));

		} catch (TimeoutException e) {
			return false;
		}
	}

	public String getProjectNameValidationMessage() {
		try {
			WebElement field = wait.until(ExpectedConditions.presenceOfElementLocated(projectNameField));

			JavascriptExecutor js = (JavascriptExecutor) driver;

			return (String) js.executeScript("return arguments[0].validationMessage;", field);

		} catch (TimeoutException e) {
			return "";
		}
	}

	// ALERT HANDLING

	public boolean isAlertPresent() {
		try {
			wait.until(ExpectedConditions.alertIsPresent());
			return true;

		} catch (TimeoutException e) {
			return false;
		}
	}

	public String acceptAlertIfPresent() {
		try {
			Alert alert = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.alertIsPresent());

			String message = alert.getText();
			alert.accept();

			return message;

		} catch (TimeoutException e) {
			return null;
		}
	}

	// SEARCH PROJECTS

	public void searchProject(String projectName) {
		if (projectName == null || projectName.trim().isEmpty()) {
			throw new IllegalArgumentException("Project search text cannot be empty.");
		}

		if (!driver.getCurrentUrl().contains("action=index")) {
			openProjectsList(defaultBaseUrl);
		}

		// Reveal the search controls if necessary.
		if (!isDisplayed(searchTextField)) {
			if (isDisplayed(searchIcon)) {
				wait.until(ExpectedConditions.elementToBeClickable(searchIcon)).click();
			}
		}

		WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(searchTextField));

		searchBox.clear();
		searchBox.sendKeys(projectName);

		// Select Project Name search criterion when available.
		if (isDisplayed(searchFieldDropdown)) {
			try {
				Select select = new Select(driver.findElement(searchFieldDropdown));

				for (WebElement option : select.getOptions()) {
					if ("projectname".equalsIgnoreCase(option.getAttribute("value"))) {
						select.selectByValue("projectname");
						break;
					}
				}
			} catch (Exception e) {
				System.out.println("Could not select Project Name criterion: " + e.getMessage());
			}
		}

		boolean submitted = false;

		for (WebElement button : driver.findElements(searchNowButton)) {
			try {
				if (button.isDisplayed() && button.isEnabled()) {
					button.click();
					submitted = true;
					break;
				}
			} catch (Exception e) {
				// Try another search control.
			}
		}

		if (!submitted) {
			searchBox.sendKeys(Keys.ENTER);
		}

		wait.until(d -> !d.findElements(listViewContents).isEmpty() || d.getPageSource().contains("No records found")
				|| d.getPageSource().contains("No Records Found"));

		System.out.println("Project search submitted: " + projectName);
	}

	// VERIFY PROJECT EXISTS IN CURRENT RESULTS

	public boolean isProjectListed(String projectName) {
		if (projectName == null || projectName.trim().isEmpty()) {
			return false;
		}

		By projectLink = By.xpath("//a[normalize-space(.)=" + xpathLiteral(projectName.trim()) + "]");

		try {
			for (WebElement element : driver.findElements(projectLink)) {
				try {
					if (element.isDisplayed()) {
						return true;
					}
				} catch (Exception e) {
					// Ignore stale elements.
				}
			}

			return false;

		} catch (Exception e) {
			return false;
		}
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

		result.append(")");
		return result.toString();
	}
}
