package listeners;

import base.ExtentManager;
import base.TestBase;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;

import static util.TestUtil.getBase64ScreenshotPath;

public class ExtentListener extends TestBase implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        ExtentManager.startTest(result.getName(), result.getMethod().getDescription());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String logText = "<b>Test :\"" + result.getMethod().getDescription() + "\" completed successfully.</b>";
        Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
        ExtentManager.getTest().log(Status.PASS, m);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String exceptionMessage = (result.getThrowable().getMessage());
        ExtentManager.getTest().fail("<details><summary><b><font color=red>" + "Exception Occurred, click to see details:" +
                "</font></b></summary>" + "<font color=GreenYellow>" + exceptionMessage.replaceAll("],", "]<br>").replaceAll(",",":<br>") + "</details> \n");
//        String screenShotPath = TestUtil.captureScreenshot(result.getName());
        ExtentManager.getTest().fail("<b><font color=red>" + "Screenshot of failure" + "</font></b>",
                MediaEntityBuilder.createScreenCaptureFromBase64String(getBase64ScreenshotPath()).build());
        String logText = "<b>Test Method " + methodName + " Failed</b>";
        Markup m = MarkupHelper.createLabel(logText, ExtentColor.RED);
        ExtentManager.getTest().log(Status.FAIL, m);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String logText = "<b>Test Method " + result.getMethod().getMethodName() + " Skipped</b>";
        Markup m = MarkupHelper.createLabel(logText, ExtentColor.YELLOW);
        ExtentManager.getTest().log(Status.SKIP, m);
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.createInstance().flush();
    }
}
