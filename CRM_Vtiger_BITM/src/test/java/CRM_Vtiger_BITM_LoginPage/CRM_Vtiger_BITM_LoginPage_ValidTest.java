
package CRM_Vtiger_BITM_LoginPage;

import org.testng.Assert;
import org.testng.annotations.Test;

import CRM_Vtiger_BITM_BaseClass.CRM_Vtiger_BITM_BaseClass;
import CRM_Vtiger_BITM_POM.CRM_Vtiger_BITM_LoginPage;

public class CRM_Vtiger_BITM_LoginPage_ValidTest extends CRM_Vtiger_BITM_BaseClass {

	@Test(groups = { "FT", "Smoke", "Regression" })
	public void verifyLoginWithValidCredentials() {

		Assert.assertNotNull(driver, "WebDriver was not initialized.");

		CRM_Vtiger_BITM_LoginPage loginPage = new CRM_Vtiger_BITM_LoginPage(driver);

		Assert.assertTrue(loginPage.isHomePageDisplayed(), "Home page was not displayed after login.");

		Assert.assertFalse(loginPage.isLoginPageDisplayed(), "Login form is still displayed after login.");

		System.out.println("PASS: Valid login opened the VTiger Home page.");
	}
}
