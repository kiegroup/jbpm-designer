package tests;
import util.OryxSeleneseTestCase;

public class LoginWithOpenIdTest extends OryxSeleneseTestCase {
	public void setUp() throws Exception {
		super.setUp();
	}
	public void testLogin() throws Exception {
		selenium.open("/backend/poem/repository");
		selenium.type("openid_login_openid", "http://claimid.com/oryxtest");
		selenium.click("css=input[class=button][type=submit]");
		String[] args2 = {"css=#username"};
		proc.doCommand("waitForElementPresent", args2);
		//selenium.wait("300000000");
		selenium.waitForPageToLoad("300000000");
		selenium.type("username", "oryxtest");
		selenium.type("password", "oryxtest");
		selenium.click("//input[@value='Login']");
		selenium.waitForPageToLoad("300000000");
		verifyTrue(selenium.isTextPresent("Hi, http://claimid.com/oryxtest"));
	}
}