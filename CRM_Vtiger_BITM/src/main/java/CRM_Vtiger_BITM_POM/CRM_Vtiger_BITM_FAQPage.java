
package CRM_Vtiger_BITM_POM;

import java.time.Duration;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CRM_Vtiger_BITM_FAQPage {

	private final WebDriver driver;
	private final WebDriverWait wait;

	private final By searchText = By.name("search_text");
	private final By searchField = By.id("bas_searchfield");

	private final By searchNow = By.xpath("//input[@name='submit' and contains(@value,'Search Now')]"
			+ " | //input[contains(@value,'Search Now')]" + " | //button[contains(normalize-space(.),'Search Now')]");

	private final By faqQuestionLinks = By
			.xpath("//a[contains(@href,'module=Faq')" + " and contains(@href,'action=DetailView')]");

	public CRM_Vtiger_BITM_FAQPage(WebDriver driver) {
		if (driver == null) {
			throw new IllegalArgumentException("WebDriver cannot be null.");
		}

		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	// 1. OPEN FAQ LIST

	public void openFAQList(String baseUrl) {
		if (baseUrl == null || baseUrl.trim().isEmpty()) {
			throw new IllegalArgumentException("FAQ base URL cannot be empty.");
		}

		String cleanUrl = baseUrl.trim();

		if (cleanUrl.contains("?")) {
			cleanUrl = cleanUrl.substring(0, cleanUrl.indexOf("?"));
		}

		driver.get(cleanUrl + "?module=Faq&action=index");

		wait.until(ExpectedConditions.visibilityOfElementLocated(searchText));

		System.out.println("FAQ list opened.");
	}

	public boolean isFAQPageDisplayed() {
		try {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//*[normalize-space()='FAQ' " + "or normalize-space()='FAQs']"))).isDisplayed();

		} catch (TimeoutException e) {
			return false;
		}
	}

	// 2. SEARCH FAQ BY QUESTION

	public void searchFAQByQuestion(String question) {
		if (question == null || question.trim().isEmpty()) {
			throw new IllegalArgumentException("FAQ search question cannot be empty.");
		}

		WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(searchText));

		searchBox.clear();
		searchBox.sendKeys(question.trim());

		WebElement searchDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(searchField));

		Select dropdown = new Select(searchDropdown);
		boolean questionOptionFound = false;

		for (WebElement option : dropdown.getOptions()) {
			if ("Question".equalsIgnoreCase(option.getText().trim())) {

				dropdown.selectByVisibleText(option.getText().trim());

				questionOptionFound = true;
				break;
			}
		}

		if (!questionOptionFound) {
			throw new NoSuchElementException("Question option is not available in " + "the FAQ search dropdown.");
		}

		wait.until(ExpectedConditions.elementToBeClickable(searchNow)).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(searchText));

		System.out.println("FAQ search executed for: " + question);
	}

	// 3. MATCH FULL OR TRUNCATED QUESTION TEXT

	private boolean matchesQuestion(String displayedText, String expectedQuestion) {

		if (displayedText == null || expectedQuestion == null) {
			return false;
		}

		String displayed = displayedText.trim();
		String expected = expectedQuestion.trim();

		if (displayed.equalsIgnoreCase(expected)) {
			return true;
		}

		// Handle text truncated with three dots.
		if (displayed.endsWith("...")) {
			String prefix = displayed.substring(0, displayed.length() - 3).trim();

			return !prefix.isEmpty() && expected.regionMatches(true, 0, prefix, 0, prefix.length());
		}

		// Handle the Unicode ellipsis character.
		if (displayed.endsWith("\u2026")) {
			String prefix = displayed.substring(0, displayed.length() - 1).trim();

			return !prefix.isEmpty() && expected.regionMatches(true, 0, prefix, 0, prefix.length());
		}

		return false;
	}

	// 4. VERIFY FAQ QUESTION IS LISTED

	public boolean isQuestionListed(String question) {
		if (question == null || question.trim().isEmpty()) {
			return false;
		}

		String expectedQuestion = question.trim();

		try {
			WebDriverWait questionWait = new WebDriverWait(driver, Duration.ofSeconds(10));

			return questionWait.until(d -> {
				List<WebElement> links = d.findElements(faqQuestionLinks);

				for (WebElement link : links) {
					try {
						if (!link.isDisplayed()) {
							continue;
						}

						String href = link.getAttribute("href");

						if (href == null || !href.contains("module=Faq") || !href.contains("action=DetailView")) {
							continue;
						}

						if (matchesQuestion(link.getText(), expectedQuestion)) {

							System.out.println("FAQ question found: " + expectedQuestion);

							return true;
						}

					} catch (StaleElementReferenceException ignored) {
						// Retry using refreshed elements.
					}
				}

				return false;
			});

		} catch (TimeoutException e) {
			System.out.println("FAQ question not found: " + expectedQuestion);

			printAvailableFAQLinks();
			return false;
		}
	}

	// 5. DEBUG INFORMATION FOR FAQ LINKS

	private void printAvailableFAQLinks() {
		try {
			List<WebElement> links = driver.findElements(faqQuestionLinks);

			System.out.println("FAQ detail links found: " + links.size());

			for (WebElement link : links) {
				try {
					if (link.isDisplayed()) {
						System.out.println("FAQ text: " + link.getText() + " | title: " + link.getAttribute("title")
								+ " | href: " + link.getAttribute("href"));
					}

				} catch (StaleElementReferenceException ignored) {
					// Ignore rows refreshed by the application.
				}
			}

		} catch (Exception e) {
			System.out.println("Could not inspect FAQ links: " + e.getMessage());
		}
	}

	// 6. OPEN FAQ DETAIL PAGE

	public void openFAQQuestion(String question) {
		if (question == null || question.trim().isEmpty()) {
			throw new IllegalArgumentException("FAQ question cannot be empty.");
		}

		String expectedQuestion = question.trim();
		WebDriverWait questionWait = new WebDriverWait(driver, Duration.ofSeconds(15));

		for (int attempt = 1; attempt <= 3; attempt++) {
			try {
				// Re-find the elements on every attempt.
				WebElement matchingLink = questionWait.until(d -> {
					List<WebElement> links = d.findElements(faqQuestionLinks);

					for (WebElement link : links) {
						try {
							if (!link.isDisplayed()) {
								continue;
							}

							String href = link.getAttribute("href");
							String displayedText = link.getText();

							if (href != null && href.contains("module=Faq") && href.contains("action=DetailView")
									&& matchesQuestion(displayedText, expectedQuestion) && link.isEnabled()) {
								return link;
							}
						} catch (StaleElementReferenceException ignored) {
							// The list refreshed; locate the element again.
						}
					}

					return null;
				});

				// Recheck that the page has not replaced the link.
				if (!matchingLink.isDisplayed()) {
					continue;
				}

				matchingLink.click();

				// Confirm that the FAQ detail page has opened.
				questionWait.until(d -> d.getCurrentUrl().contains("action=DetailView"));

				System.out.println("FAQ question opened: " + expectedQuestion);
				return;

			} catch (StaleElementReferenceException e) {
				if (attempt == 3) {
					throw new RuntimeException("FAQ link kept refreshing after 3 attempts: " + expectedQuestion, e);
				}
			} catch (TimeoutException e) {
				if (attempt == 3) {
					throw new RuntimeException("Could not open FAQ detail page for: " + expectedQuestion
							+ ". Current URL: " + driver.getCurrentUrl(), e);
				}
			}
		}
	}

	// 7. VERIFY QUESTION ON DETAIL PAGE

	public boolean isQuestionDisplayedOnDetailPage(String question) {

		if (question == null || question.trim().isEmpty()) {
			return false;
		}

		try {
			return new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
				String pageSource = d.getPageSource();

				return pageSource.contains(question) || pageSource.contains(question.replace("&", "&amp;"));
			});

		} catch (TimeoutException e) {
			System.out.println("FAQ question not found on detail page: " + question);

			return false;
		}
	}

	// 8. GET CURRENT URL

	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}
}
