
package CRM_Vtiger_BITM_POM;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CRM_Vtiger_BITM_ServiceContractsPage {

	private final WebDriver driver;
	private final WebDriverWait wait;

	private static final String BASE_URL = "http://49.249.29.4:8888/index.php";

	// List page
	private final By serviceContractsHeading = By.xpath("//*[normalize-space()='Service Contracts']");

	private final By searchField = By.name("search_text");
	private final By searchDropdown = By.id("bas_searchfield");

	private final By searchNowButton = By.xpath("//input[@name='submit' and contains(@value,'Search Now')]"
			+ " | //input[contains(@value,'Search Now')]" + " | //button[contains(normalize-space(.),'Search Now')]");

	private final By emptyStateMessage = By.xpath("//*[contains(normalize-space(.),'No Service Contract Found')]");

	private final By contractNumberLinks = By.xpath("//a[contains(@href,'module=ServiceContracts')" + " and contains(@href,'action=DetailView')]");

	private final By contractTableHeaders = By.xpath("//th[normalize-space()='Contract No']" + " | //td[normalize-space()='Contract No']");

	private final By tableRows = By.xpath("//table//tr[td]");

	// Create form
	private final By createButton = By.xpath("//a[contains(@href,'module=ServiceContracts')" + " and contains(@href,'action=EditView')]"
					+ " | //input[contains(@title,'Create')]" + " | //img[contains(@alt,'Create')]");

	private final By subjectField = By.name("subject");

	private final By saveButton = By.xpath("//input[@name='button' and contains(@title,'Save')]"
			+ " | //input[@type='submit' and contains(@value,'Save')]" + " | //button[normalize-space()='Save']");

	private final By cancelButton = By.xpath("//input[@name='button' and contains(@title,'Cancel')]"
			+ " | //input[contains(@value,'Cancel')]" + " | //button[normalize-space()='Cancel']");

	public CRM_Vtiger_BITM_ServiceContractsPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	// Open Service Contracts list
	public void openServiceContractsList() {
		driver.get(BASE_URL + "?module=ServiceContracts&action=index");

		wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOfElementLocated(searchField),
				ExpectedConditions.presenceOfElementLocated(serviceContractsHeading),
				ExpectedConditions.presenceOfElementLocated(emptyStateMessage)));

		System.out.println("Service Contracts list opened.");
		System.out.println("Current URL: " + driver.getCurrentUrl());
	}

	// Verify list page
	public boolean isServiceContractsPageDisplayed() {
		try {
			return driver.getCurrentUrl().contains("module=ServiceContracts") && !driver.getCurrentUrl().contains("action=EditView")
					&& (driver.findElements(searchField).stream().anyMatch(WebElement::isDisplayed)
							|| driver.findElements(serviceContractsHeading).stream().anyMatch(WebElement::isDisplayed)
							|| isEmptyStateDisplayed() || isContractTableDisplayed());
		} catch (Exception e) {
			System.out.println("Service Contracts page verification failed: " + e.getMessage());
			return false;
		}
	}

	// Verify table header
	public boolean isContractTableDisplayed() {
		return driver.findElements(contractTableHeaders).stream().anyMatch(WebElement::isDisplayed);
	}

	// Verify empty state
	public boolean isEmptyStateDisplayed() {
		for (WebElement message : driver.findElements(emptyStateMessage)) {
			try {
				if (message.isDisplayed()) {
					return true;
				}
			} catch (StaleElementReferenceException ignored) {
				// Retry with current DOM on the next call.
			}
		}
		return false;
	}

	// Count visible contract detail links
	public int getContractCount() {
		int count = 0;

		for (WebElement contract : driver.findElements(contractNumberLinks)) {
			try {
				if (contract.isDisplayed()) {
					count++;
				}
			} catch (StaleElementReferenceException ignored) {
				// Ignore stale elements.
			}
		}

		return count;
	}

	// Open Create Service Contract form
	public void clickCreateButton() {
		WebElement button = wait.until(ExpectedConditions.elementToBeClickable(createButton));

		button.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(subjectField));

		System.out.println("Create Service Contract form opened.");
	}

	// Verify Create form
	public boolean isCreateFormDisplayed() {
		try {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(subjectField)).isDisplayed();
		} catch (TimeoutException e) {
			return false;
		}
	}

	// Enter Subject
	public void enterSubject(String subject) {
		WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(subjectField));

		field.clear();

		if (subject != null && !subject.trim().isEmpty()) {
			field.sendKeys(subject.trim());
		}

		System.out.println("Subject entered: " + field.getAttribute("value"));
	}

	// Read Subject
	public String getSubjectValue() {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(subjectField)).getAttribute("value");
	}

	// Save contract
	public void saveContract() {
		wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();

		System.out.println("Save button clicked.");
	}

	// Accept validation alert if present
	public String acceptAlertIfPresent() {
		try {
			Alert alert = new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.alertIsPresent());

			String message = alert.getText();
			alert.accept();

			System.out.println("Validation alert: " + message);
			return message;

		} catch (TimeoutException e) {
			return null;
		}
	}

	// Check Subject mandatory validation
	public boolean isSubjectMandatoryValidationShown() {
		String alertMessage = acceptAlertIfPresent();

		if (alertMessage != null && !alertMessage.trim().isEmpty()) {
			return true;
		}

		try {
			WebElement field = driver.findElement(subjectField);

			boolean formStillOpen = field.isDisplayed();
			boolean subjectEmpty = field.getAttribute("value").trim().isEmpty();

			JavascriptExecutor js = (JavascriptExecutor) driver;

			boolean htmlInvalid = Boolean.TRUE.equals(js.executeScript("return !arguments[0].checkValidity();", field));

			return htmlInvalid || (formStillOpen && subjectEmpty);

		} catch (Exception e) {
			return false;
		}
	}

	// Cancel form
	public void cancelForm() {
		wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();

		System.out.println("Cancel button clicked.");
	}

	// Verify return to list after Cancel
	public boolean isBackOnServiceContractsList() {
		try {
			wait.until(d -> d.getCurrentUrl().contains("module=ServiceContracts")	&& !d.getCurrentUrl().contains("action=EditView"));

			return isServiceContractsPageDisplayed();

		} catch (TimeoutException e) {
			return false;
		}
	}

	// Search helper
	private void performSearch(String fieldName, String value) {
		if (!driver.getCurrentUrl().contains("module=ServiceContracts") || driver.getCurrentUrl().contains("action=EditView")
				|| driver.getCurrentUrl().contains("action=DetailView")) {
			openServiceContractsList();
		}

		WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(searchField));

		searchBox.clear();
		searchBox.sendKeys(value);

		Select dropdown = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(searchDropdown)));

		boolean optionFound = false;

		for (WebElement option : dropdown.getOptions()) {
			if (option.getText().trim().equalsIgnoreCase(fieldName)) {
				dropdown.selectByVisibleText(option.getText());
				optionFound = true;
				break;
			}
		}

		if (!optionFound) {
			throw new IllegalArgumentException("Search option not found: " + fieldName);
		}

		WebElement button = wait.until(ExpectedConditions.elementToBeClickable(searchNowButton));

		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", button);

		button.click();

		// Allow the list/search response to settle.
		wait.until(d -> d.getCurrentUrl().contains("module=ServiceContracts"));

		System.out.println("Search submitted. Field: " + fieldName + ", Value: " + value);
		System.out.println("Current URL: " + driver.getCurrentUrl());
	}

	public void searchByContractNumber(String contractNumber) {
		performSearch("Contract No", contractNumber);
	}

	public void searchServiceContractBySubject(String subject) {
		performSearch("Subject", subject);
	}

	// Verify contract number in visible results
	public boolean isContractNumberDisplayed(String contractNumber) {
		for (WebElement row : driver.findElements(tableRows)) {
			try {
				if (row.isDisplayed() && row.getText().contains(contractNumber)) {
					return true;
				}
			} catch (StaleElementReferenceException ignored) {
				// Ignore stale row.
			}
		}

		return false;
	}

	// Verify Subject in visible result rows
	public boolean isServiceContractSubjectDisplayed(String subject) {
		for (WebElement row : driver.findElements(tableRows)) {
			try {
				if (row.isDisplayed()) {
					String rowText = row.getText().trim();

					System.out.println("Search result row: " + rowText);

					if (rowText.toLowerCase().contains(subject.toLowerCase())) {
						return true;
					}
				}
			} catch (StaleElementReferenceException ignored) {
				// Ignore stale row.
			}
		}

		System.out.println("Subject not found in visible results: " + subject);
		return false;
	}

	// Open first contract
	public void openFirstContract() {
		WebElement contract = wait.until(ExpectedConditions.elementToBeClickable(contractNumberLinks));

		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", contract);

		contract.click();

		wait.until(ExpectedConditions.or(ExpectedConditions.urlContains("action=DetailView"),
				ExpectedConditions.urlContains("record=")));

		System.out.println("Contract detail page opened: " + driver.getCurrentUrl());
	}

	// Verify detail page
	public boolean isContractDetailPageDisplayed() {
		String currentUrl = driver.getCurrentUrl();

		return currentUrl.contains("module=ServiceContracts")
				&& (currentUrl.contains("action=DetailView") || currentUrl.contains("record="));
	}
}
