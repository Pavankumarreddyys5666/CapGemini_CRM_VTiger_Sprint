
package CRM_Vtiger_BITM_FAQ;

import org.testng.Assert;
import org.testng.annotations.Test;

import CRM_Vtiger_BITM_BaseClass.CRM_Vtiger_BITM_BaseClass;
import CRM_Vtiger_BITM_POM.CRM_Vtiger_BITM_FAQPage;
import CRM_Vtiger_BITM_GenericUtility.CRM_Vtiger_BITM_ScreenshotUtility;

public class CRM_Vtiger_BITM_FAQTest extends CRM_Vtiger_BITM_BaseClass {

	private static final String FAQ_URL = "http://49.249.29.4:8888/index.php";

	private static final String EXISTING_QUESTION = "How can I pair my smart watch with my account?";

	private static final String NONEXISTENT_QUESTION = "NoSuchFAQ_" + System.currentTimeMillis();

	private CRM_Vtiger_BITM_FAQPage getFAQPage() {
		Assert.assertNotNull(driver, "WebDriver was not initialized by BaseClass.");

		return new CRM_Vtiger_BITM_FAQPage(driver);
	}

	// Capture screenshot only after the expected negative behavior
	// has been confirmed.
	private void captureNegativeScreenshot(String testName) {
		try {
			CRM_Vtiger_BITM_ScreenshotUtility.captureScreenshot(driver, testName);

		} catch (Exception e) {
			System.err.println("SCREENSHOT ERROR for " + testName + ": " + e.getMessage());
		}
	}

	// TEST 1: Positive - Verify FAQ page opens

	@Test(priority = 1, groups = { "FT", "Regression" })
	public void verifyFAQPageOpensTest() {
		CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();

		faqPage.openFAQList(FAQ_URL);

		Assert.assertTrue(faqPage.isFAQPageDisplayed(), "FAQ page heading was not displayed.");

		System.out.println("PASS: FAQ page opened successfully.");
	}

	// TEST 2: Positive - Verify FAQ search

	@Test(priority = 2, groups = { "FT", "Regression" })
	public void verifyFAQSearchTest() {
		CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();

		faqPage.openFAQList(FAQ_URL);
		faqPage.searchFAQByQuestion(EXISTING_QUESTION);

		Assert.assertTrue(faqPage.isQuestionListed(EXISTING_QUESTION),
				"FAQ search did not return the expected question: " + EXISTING_QUESTION);

		System.out.println("PASS: FAQ search verified.");
	}

	// TEST 3: Positive - Verify FAQ answer/detail can be viewed

	@Test(priority = 3, groups = { "FT", "Regression" })
	public void verifyFAQAnswerCanBeViewedTest() {
		CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();

		faqPage.openFAQList(FAQ_URL);
		faqPage.searchFAQByQuestion(EXISTING_QUESTION);

		Assert.assertTrue(faqPage.isQuestionListed(EXISTING_QUESTION),
				"FAQ question was not found: " + EXISTING_QUESTION);

		faqPage.openFAQQuestion(EXISTING_QUESTION);

		Assert.assertTrue(faqPage.isQuestionDisplayedOnDetailPage(EXISTING_QUESTION),
				"FAQ detail page did not display the expected question.");

		System.out.println("PASS: FAQ answer/detail can be viewed.");
	}

	// TEST 4: Negative - Search for a nonexistent FAQ

	@Test(priority = 4, groups = { "FT", "Negative" })
	public void verifyNonexistentFAQSearchTest() {
		CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();

		faqPage.openFAQList(FAQ_URL);
		faqPage.searchFAQByQuestion(NONEXISTENT_QUESTION);

		boolean questionFound = faqPage.isQuestionListed(NONEXISTENT_QUESTION);

		Assert.assertFalse(questionFound, "A nonexistent FAQ was unexpectedly found: " + NONEXISTENT_QUESTION);

		// Capture only after confirming the question is absent.
		captureNegativeScreenshot("verifyNonexistentFAQSearchTest");

		System.out.println("PASS: Nonexistent FAQ was not found.");
	}

	// TEST 5: Negative - Reject an empty search question

	@Test(priority = 5, groups = { "FT", "Negative" })
	public void verifyEmptyFAQSearchTest() {
		CRM_Vtiger_BITM_FAQPage faqPage = getFAQPage();

		faqPage.openFAQList(FAQ_URL);

		boolean emptyQuestionRejected = false;

		try {
			faqPage.searchFAQByQuestion("");

		} catch (IllegalArgumentException e) {
			emptyQuestionRejected = true;

			System.out.println("Expected empty-search rejection: " + e.getMessage());
		}

		Assert.assertTrue(emptyQuestionRejected, "The page object did not reject an empty FAQ question.");

		// Capture only after the expected rejection is confirmed.
		captureNegativeScreenshot("verifyEmptyFAQSearchTest");

		System.out.println("PASS: Empty FAQ search input was rejected.");
	}
}
