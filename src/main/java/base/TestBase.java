package base;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverListener;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;
import util.ExcelApiTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.util.Properties;

/**
 * Created by Kshitija Gaikwad on 20/03/23, 4.02 PM.
 */

public class TestBase {

    public static WebDriver driver;
    public static String envURL;
    public static Properties prop;
    public static String URL;
    public static SoftAssert sa;
    public static ExcelApiTest excel;
    public static JSONObject jsonObject;
    public static Connection connection = null;
    public static Statement statement;
    public static String featureName;
    public static ResultSet rs;
    public static String browser;
    public static EventFiringDecorator e_driver;
    public static WebDriverListener eventListener;
    public static String userDir;


    public TestBase() {
        PageFactory.initElements(driver, this);
    }

    @Parameters({"browserName", "urlLink"})
    @BeforeTest
    public static void initialization(String browserName, String urlLink) {
        browser = browserName;
        envURL = urlLink;
        userDir = System.getProperty("user.dir");
        System.out.println("user dir is" + userDir);
        try {
            prop = new Properties();
            FileInputStream ip = new FileInputStream(System.getProperty("user.dir") + "/src/main/java/config/config.properties");
            prop.load(ip);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        //Browser name will be passed from pom.xml file through command prompt
        switch (browserName.toLowerCase()) {
            case "firefox":
                driver = new FirefoxDriver();
                break;
            case "safari":
                driver = new SafariDriver();
                break;
            case "edge":
                driver = new EdgeDriver();
                break;
            case "chrome":

            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                if (prop.getProperty("Headless").equalsIgnoreCase("True")) {
                    chromeOptions.addArguments("--headless"); //when on githubActions
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-popup-blocking");
                }
                chromeOptions.addArguments("--window-size=1920,1080");
                driver = new ChromeDriver(chromeOptions);
        }
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
//        driver.manage().deleteAllCookies();
//        driver.manage().window().maximize();
        URL = prop.getProperty(envURL);
        driver.get(URL);
    }

    public void createSoftAssertObject() {
        sa = new SoftAssert();
    }

    public static void createExcelSheetObject() {
        try {
            excel = new ExcelApiTest(System.getProperty("user.dir") + "/src/main/java/testdata/synapzeData.xlsx");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void createJSONObject() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        try {
            FileReader reader = new FileReader(System.getProperty("user.dir") + "/src/main/java/testdata/english/dateFormatters.json");
            Object obj = jsonParser.parse(reader);
            jsonObject = (JSONObject) obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void connectToDatabase(String whichDB) {
        String databaseURL = null;
        if (whichDB.equalsIgnoreCase("EDC")) {
            databaseURL = prop.getProperty("EDCDBURL");
        } else {
            databaseURL = prop.getProperty("EDCAdminDBURL");
        }
        try {
            System.out.println("Connecting to Database...");
            connection = DriverManager.getConnection(databaseURL, prop.getProperty("DBUsername"), prop.getProperty("DBPassword"));
            if (connection != null) {
                System.out.println("Connected to the Database...");
            }
            statement = connection.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void setFeatureName(String nameOfFeature) {
        featureName = nameOfFeature;
    }

    @AfterTest
    public void afterTest() {
        // driver.quit();
        try {
            if (statement != null || connection != null) {
                statement.close();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

