
package CRM_Vtiger_BITM_GenericUtility;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class CRM_Vtiger_BITM_WebDriverUtility {

	private WebDriver driver;

	public WebDriver launchBrowser(String browser) {

		if (browser == null || browser.trim().isEmpty()) {
			browser = "chrome";
		}

		String selectedBrowser = browser.trim().toLowerCase();

		try {
			switch (selectedBrowser) {

			case "chrome":

				ChromeOptions chromeOptions = new ChromeOptions();

				Map<String, Object> chromePrefs = new HashMap<>();
				chromePrefs.put("credentials_enable_service", false);
				chromePrefs.put("profile.password_manager_enabled", false);
				chromePrefs.put("profile.password_manager_leak_detection", false);

				chromeOptions.setExperimentalOption("prefs", chromePrefs);
				chromeOptions.addArguments("--disable-save-password-bubble");
				chromeOptions.addArguments("--disable-features=PasswordLeakDetection");
				chromeOptions.addArguments("--disable-notifications");
				chromeOptions.addArguments("--no-first-run");

				driver = new ChromeDriver(chromeOptions);
				break;

			case "edge":

				EdgeOptions edgeOptions = new EdgeOptions();

				Map<String, Object> edgePrefs = new HashMap<>();
				edgePrefs.put("credentials_enable_service", false);
				edgePrefs.put("profile.password_manager_enabled", false);
				edgePrefs.put("profile.password_manager_leak_detection", false);

				edgeOptions.setExperimentalOption("prefs", edgePrefs);
				edgeOptions.addArguments("--disable-save-password-bubble");
				edgeOptions.addArguments("--disable-notifications");
				edgeOptions.addArguments("--no-first-run");

				driver = new EdgeDriver(edgeOptions);
				break;

			default:
				throw new IllegalArgumentException(
						"Unsupported browser: " + browser + ". Supported browsers: chrome, edge.");
			}

			driver.manage().timeouts().implicitlyWait(Duration.ZERO);

			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

			driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(60));

			driver.manage().window().maximize();

			System.out.println("Browser launched successfully: " + selectedBrowser);

			return driver;

		} catch (RuntimeException e) {

			if (driver != null) {
				try {
					driver.quit();
				} catch (RuntimeException ignored) {
					// Ignore cleanup errors.
				}
				driver = null;
			}

			throw new RuntimeException("Failed to launch browser: " + selectedBrowser
					+ ". Check browser installation, driver compatibility, " + "and system resources.", e);
		}
	}

	public void maximizeWindow() {
		if (driver != null) {
			driver.manage().window().maximize();
		}
	}

	public void minimizeWindow() {
		if (driver != null) {
			driver.manage().window().minimize();
		}
	}

	public void navigateToUrl(String url) {
		if (driver != null) {
			driver.get(url);
		}
	}

	public String getCurrentUrl() {
		if (driver != null) {
			return driver.getCurrentUrl();
		}
		return null;
	}

	public String getPageTitle() {
		if (driver != null) {
			return driver.getTitle();
		}
		return null;
	}

	public void refreshPage() {
		if (driver != null) {
			driver.navigate().refresh();
		}
	}

	public void closeCurrentBrowser() {
		if (driver != null) {
			driver.close();
		}
	}

	public void quitBrowser() {
		if (driver != null) {
			try {
				driver.quit();
			} finally {
				driver = null;
			}
		}
	}
}
