
package CRM_Vtiger_BITM_GenericUtility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class CRM_Vtiger_BITM_ScreenshotUtility {

	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

	private CRM_Vtiger_BITM_ScreenshotUtility() {
	}

	public static Path captureScreenshot(WebDriver driver, String testName) throws IOException {
		return saveScreenshot(driver, testName);
	}

	public static Path captureFailure(WebDriver driver, String testName) throws IOException {
		return saveScreenshot(driver, testName);
	}

	private static Path saveScreenshot(WebDriver driver, String testName) throws IOException {

		if (driver == null) {
			throw new IllegalArgumentException("WebDriver is null. Screenshot cannot be captured.");
		}

		if (!(driver instanceof TakesScreenshot)) {
			throw new IllegalArgumentException("WebDriver does not support screenshots.");
		}

		// Save under the current Eclipse project's working directory.
		Path projectDirectory = Paths.get(System.getProperty("user.dir")).toAbsolutePath();

		Path screenshotDirectory = projectDirectory.resolve("Screenshot");

		Files.createDirectories(screenshotDirectory);

		String safeName = (testName == null || testName.trim().isEmpty()) ? "Test_Screenshot": testName.replaceAll("[^A-Za-z0-9_-]", "_");

		String fileName = safeName + "_" + LocalDateTime.now().format(TIME_FORMAT) + ".png";

		Path destination = screenshotDirectory.resolve(fileName).toAbsolutePath();

		File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

		Files.copy(source.toPath(), destination);

		System.out.println("SCREENSHOT SAVED SUCCESSFULLY: " + destination);

		return destination;
	}
}
