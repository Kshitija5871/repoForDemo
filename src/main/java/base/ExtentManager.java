package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import util.TestUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtentManager extends TestBase {
    static ExtentReports extent;
    static Map<Integer, ExtentTest> extentTestMap = new HashMap();
    static String reportPath = System.getProperty("user.dir") +
            "/Reports/Synapze Automation Report.html";

    public static synchronized ExtentReports createInstance() {

        if (extent == null) {
            ExtentSparkReporter sparkReport = new ExtentSparkReporter(reportPath);
            sparkReport.config().setDocumentTitle("Synapze Automation Report");
            sparkReport.config().setReportName("Automation Test Results");
            sparkReport.config().setTheme(Theme.DARK);
            sparkReport.config().thumbnailForBase64(true);

            extent = new ExtentReports();
            extent.setSystemInfo("Product Name", TestUtil.getPropertyValueByKey("ProductName"));
            extent.setSystemInfo("Feature", featureName);
            extent.setSystemInfo("Environment", TestUtil.getPropertyValueByKey(envURL));
            extent.setSystemInfo("Executed on Browser: ", browser);
            extent.setSystemInfo("Executed on OS", System.getProperty("os.name"));
            extent.setSystemInfo("Executed By", System.getProperty("user.name"));
            extent.setSystemInfo("Build Version", TestUtil.getPropertyValueByKey("BuildVersion"));
            extent.attachReporter(sparkReport);
        }
        return extent;
    }

    public static synchronized ExtentTest getTest() {
        return extentTestMap.get((int) (long) (Thread.currentThread().getId()));
    }

    public static synchronized ExtentTest startTest(String desc, String testName) {
        ExtentTest test = createInstance().createTest(testName, desc);
        extentTestMap.put((int) (long) (Thread.currentThread().getId()), test);
        return test;
    }
}
