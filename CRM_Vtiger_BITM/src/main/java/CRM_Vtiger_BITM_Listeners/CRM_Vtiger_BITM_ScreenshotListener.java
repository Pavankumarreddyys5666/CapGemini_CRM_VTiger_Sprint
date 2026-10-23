
package CRM_Vtiger_BITM_Listeners;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_ScreenshotUtility;

public class CRM_Vtiger_BITM_ScreenshotListener implements ITestListener {

	// Only these TestNG groups are considered negative tests.
	private static final Set<String> NEGATIVE_GROUPS = new HashSet<>(Arrays.asList("negative", "nev"));

	@Override
	public void onTestFailure(ITestResult result) {

		// Ignore failed tests that do not belong to a negative group.
		if (!isNegativeTest(result)) {
			System.out.println("SCREENSHOT SKIPPED: Failed test is not in a negative group: " + result.getMethod().getMethodName());
			return;
		}

		String className = result.getTestClass().getRealClass().getSimpleName();

		String methodName = result.getMethod().getMethodName();

		String screenshotName = className + "_" + methodName;

		try {
			WebDriver driver = getDriver(result.getInstance());

			if (driver == null) {
				System.err.println("SCREENSHOT FAILED: WebDriver is null for " + screenshotName);
				return;
			}

			CRM_Vtiger_BITM_ScreenshotUtility.captureScreenshot(driver, screenshotName);

			System.out.println("SCREENSHOT CAPTURE REQUESTED: " + screenshotName);

		} catch (Exception e) {
			System.err.println("SCREENSHOT FAILED for " + screenshotName);
			e.printStackTrace();
		}
	}

	
	 // Checks whether the failed test belongs to a negative group.
	 
	private boolean isNegativeTest(ITestResult result) {

		String[] groups = result.getMethod().getGroups();

		if (groups == null || groups.length == 0) {
			return false;
		}

		for (String group : groups) {
			if (group != null && NEGATIVE_GROUPS.contains(group.trim().toLowerCase(Locale.ROOT))) {
				return true;
			}
		}

		return false;
	}

	
	 // Finds the WebDriver field in the test class or its superclasses.

	 private WebDriver getDriver(Object testInstance) throws IllegalAccessException {

		if (testInstance == null) {
			return null;
		}

		Class<?> currentClass = testInstance.getClass();

		while (currentClass != null) {
			try {
				Field driverField = currentClass.getDeclaredField("driver");

				driverField.setAccessible(true);

				Object driverObject = driverField.get(testInstance);

				if (driverObject instanceof WebDriver) {
					return (WebDriver) driverObject;
				}

				return null;

			} catch (NoSuchFieldException e) {
				currentClass = currentClass.getSuperclass();
			}
		}

		System.err.println("WebDriver field named 'driver' was not found " + "in the test class hierarchy.");

		return null;
	}
}
