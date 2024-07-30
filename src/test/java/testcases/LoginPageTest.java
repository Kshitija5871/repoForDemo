package testcases;

import base.TestBase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class LoginPageTest extends TestBase {
//    LoginPage loginPage;

    @BeforeTest
    public void setUp() {
//        loginPage = new LoginPage();
        createExcelSheetObject();
        setFeatureName("Login");
    }

    @BeforeMethod(alwaysRun = true)
    public void createRequiredObjects() {
        createSoftAssertObject();
    }

//    @Test(description = "Verify Labels on Sign in page")
//    public void verifyLabelsOnSignInPage() {
//        loginPage.verifyLabelsOnSignInPage();
//    }

}
