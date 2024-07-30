package util;

import base.ExtentManager;
import base.TestBase;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.google.zxing.Reader;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.Point;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestUtil extends TestBase {
    public static long PAGE_LOAD_TIMEOUT = 30;
    public static long SCRIPT_TIMEOUT = 30;
    public static final Logger logger = LogManager.getLogger();
    public static Select select;
    static LocalDate date = LocalDate.now();
    static JavascriptExecutor js = (JavascriptExecutor) driver;

    // Method to log test step information with screenshot for each step
    public static void logAndReportWithScreenshot(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        String className = ste.getClassName();
        String methodName = ste.getMethodName();
        int lineNumber = ste.getLineNumber();
        String logMessage = className + "." + methodName + "()." + lineNumber + " : " + message;
        logger.info(logMessage);
        ExtentManager.getTest().log(Status.INFO, message, MediaEntityBuilder.createScreenCaptureFromBase64String(getBase64ScreenshotPath()).build());
    }

    // Method to log test step information
    public static void logAndReport(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        String className = ste.getClassName();
        String methodName = ste.getMethodName();
        int lineNumber = ste.getLineNumber();
        String logMessage = className + "." + methodName + "()." + lineNumber + " : " + message;
        logger.info(logMessage);
        ExtentManager.getTest().log(Status.INFO, message);
    }

    // Method to log error type
    public static void logError(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        String className = ste.getClassName();
        String methodName = ste.getMethodName();
        int lineNumber = ste.getLineNumber();
        String logMessage = className + "." + methodName + "()." + lineNumber + " : " + message;
        logger.error(logMessage);
    }

    // Method to log debug type
    public static void logDebug(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        String className = ste.getClassName();
        String methodName = ste.getMethodName();
        int lineNumber = ste.getLineNumber();
        String logMessage = className + "." + methodName + "()." + lineNumber + " : " + message;
        logger.debug(logMessage);
    }


    //Method to verify active links on a web page
    public void verifyActiveLinks() throws IOException {
        List<WebElement> linksList = driver.findElements(By.tagName("a"));
        linksList.addAll(driver.findElements(By.tagName("img")));

        System.out.println("Size of all links----->" + linksList.size());
        List<WebElement> activeLinks = new ArrayList<>();

        for (WebElement element : linksList) {
            if (element.getAttribute("href") != null && (!element.getAttribute("href").contains("javascript"))) {
                activeLinks.add(element);
            }
        }

        System.out.println("Size of all links--->" + activeLinks.size());

        for (WebElement activeLink : activeLinks) {
            HttpURLConnection connection = (HttpURLConnection) new URL(activeLink.getAttribute("href")).openConnection();
            connection.connect();

            if (connection.getResponseCode() == 200) {
                System.out.println(activeLink.getAttribute("href") + " - " + connection.getResponseMessage());
            }
            if (connection.getResponseCode() != 200) {
                System.out.println(activeLink.getAttribute("href") + " - " + connection.getResponseMessage());
            }
            sa.assertEquals(connection.getResponseMessage(), "OK");
            sa.assertAll();
            connection.disconnect();
        }

    }

    public static void waitForTextToBePresent(WebElement element) {
        WebDriverWait mywait = new WebDriverWait(driver, Duration.ofSeconds(20));
        mywait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return (element.getAttribute("value").length() >= 1);
            }
        });

    }

//    //Method to upload file on Mac
//    public static void Upload_file(String filepath) {
//        filepath = userDir + filepath;
//        System.out.println("File Path is :" + filepath);
//        String os = System.getProperty("os.name").toLowerCase();
//        System.out.println("os :" + os.toString());
//
//        if (os.toString().equals("mac os x1")) {
//            System.out.println("In if");
//            String appl = "tell app \"System Events\"\n" +
//                    "keystroke \"G\" using {command down, shift down}\n" +
//                    "delay 2\n" +
//                    "keystroke \"" + filepath + "\"\n" +
//                    "delay 2\n" +
//                    "keystroke return\n" +
//                    "delay 2\n" +
//                    "keystroke return\n" +
//                    "end tell";
//
//            Runtime runtime = Runtime.getRuntime();
//            String[] args = {"osascript", "-e", appl};
//
//            try {
//                Process process = runtime.exec(args);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("In else");
//            // Directly use Selenium for other OS (Linux/Windows)
//            CommonPage.fileUploadElement.sendKeys(filepath);
//        }
//    }

    //Method to cancel file upload on Mac
    public static void cancel_fileUpload() {

        String appl = "tell app \"System Events\"\n" +
                "delay 1\n" +
                "keystroke \"w\" using command down\n" +
                "end tell";

        Runtime runtime = Runtime.getRuntime();
        String[] args = {"osascript", "-e", appl};

        try {
            Process process = runtime.exec(args);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Method to capture screenshot
    public static String captureScreenshot(String screenShotName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        String dest = System.getProperty("user.dir") + "/ErrorScreenshots/" + screenShotName + ".png";
        File destination = new File(dest);
        try {
            FileUtils.copyFile(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Screenshot path =" + dest);
        return dest;
    }


    //Method for sleep
    public static void
    sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Method for click on Element
    public static void clickOnElement(WebElement element) {

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.visibilityOf(element));
            element.click();
        } catch (TimeoutException e) {
            sa.fail("Timeout Exception, " + element.toString() + "  Element not found.");
        } catch (NoSuchElementException e) {
            sa.fail("NoSuchElementException Exception, " + element.toString() + " Element is not present.");
        } catch (ElementNotInteractableException e) {
            sa.fail("ElementNotVisibleException Exception, " + element.toString() + " Element is not visible.");
        }
    }


    //Method for wait until Element Displayed
    public static void waitForElementToBeDisplayed(WebElement element) {

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
        } catch (NoSuchElementException e) {
            sa.fail("NoSuchElementException Exception, " + element.toString() + " Element is not present.");
        } catch (ElementNotInteractableException e) {
            sa.fail("ElementNotVisibleException Exception, " + element.toString() + " Element is not visible.");
        }
    }

    //Method for wait until ALL Element Displayed
    public static void waitForAllElementToBeDisplayed(List<WebElement> element) {

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.visibilityOfAllElements(element));
        } catch (TimeoutException e) {
            sa.fail("Timeout Exception, " + element.toString() + "  Element not found.");
        } catch (NoSuchElementException e) {
            sa.fail("NoSuchElementException Exception, " + element.toString() + " Element is not present.");
        } catch (ElementNotInteractableException e) {
            sa.fail("ElementNotVisibleException Exception, " + element.toString() + " Element is not visible.");
        }
    }

    //Method for wait until Element Disappear
    public static void waitForInvisibilityOfElement(WebElement element) {

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.invisibilityOf(element));
        } catch (TimeoutException | NoSuchElementException | ElementNotInteractableException e) {
            System.out.println("Element is not present.");
        }
    }

    //Method for wait until Element Clickable
    public static void waitForElementToBeClickable(WebElement element) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException e) {
        } catch (NoSuchElementException e) {
            sa.fail("NoSuchElementException Exception, " + element.toString() + " Element is not present.");
        } catch (ElementNotInteractableException e) {
            sa.fail("ElementNotVisibleException Exception, " + element.toString() + " Element is not visible.");
        }
    }

    //Method for wait until expected URL loads
    public static void waitForURLLoads(String URL) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.urlToBe(URL));
        } catch (TimeoutException e) {
            sa.fail("Timeout Exception, " + URL + " URL not found.");
        } catch (NoSuchElementException e) {
            sa.fail("NoSuchElementException Exception, " + URL + " URL is not present.");
        } catch (ElementNotInteractableException e) {
            sa.fail("ElementNotVisibleException Exception, " + URL + " URL is not visible.");
        }
    }

    //Method to get today's date in required format with return type String
    public static String getTodayDate(String formatOfDate) {
        return date.format(DateTimeFormatter.ofPattern(formatOfDate));
    }

    //Method to get future date in required format
    public static LocalDate getFutureDate(int daysToAdd) {
        LocalDate futureDay = date.plusDays(daysToAdd);
        System.out.println("Day after addition is " + futureDay);
        return futureDay;
    }

    //Method to get future date in required format
    public static LocalDate getPastDate(int daysToMinus) {
        LocalDate pastDay = date.minusDays(daysToMinus);
        System.out.println("Day after minus is " + pastDay);
        return pastDay;
    }

    //Method to get formatted date
    public static String getFormattedDate(LocalDate dateToBeFormatted, String formatOfDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatOfDate);
        return formatter.format(dateToBeFormatted);
    }

    //Method to get Day, Month and Year from date
    public static List<String> getDayMonthYearFromGivenDate(LocalDate dateToBeSplit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM");
        String dayFromDate = String.valueOf(dateToBeSplit.getDayOfMonth());
        String monthFromDate = formatter.format(dateToBeSplit);
        String yearFromDate = String.valueOf(dateToBeSplit.getYear());
        List<String> dayMonthAndYear = new ArrayList<>();
        Collections.addAll(dayMonthAndYear, dayFromDate, monthFromDate, yearFromDate);
        return dayMonthAndYear;
    }

    //Method for image comparison
    public static void imageComparison(WebElement actualElement, String expectedImageName) {

        BufferedImage expectedImage = null;
        try {
            expectedImage = ImageIO.read(new File(System.getProperty("user.dir") + "/Images/" + expectedImageName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Screenshot logoImageScreenshot = new AShot().coordsProvider(new WebDriverCoordsProvider()).takeScreenshot(driver, actualElement);
        BufferedImage actualImage = logoImageScreenshot.getImage();

        ImageDiffer imgDiff = new ImageDiffer();
        ImageDiff diff = imgDiff.makeDiff(actualImage, expectedImage);
        if (!diff.hasDiff())
            sa.assertFalse(diff.hasDiff(), "Images are Same...");
        else
            sa.assertFalse(diff.hasDiff(), "Actual Image is Different than " + expectedImageName + "...");
        sleep(1000);
        sa.assertAll();
    }

    //Method for taking WebElement screenshot
    public static void takeScreenshot(WebElement element, String imageName) {
        sleep(2000);
        Screenshot screenshot = new AShot().coordsProvider(new WebDriverCoordsProvider()).takeScreenshot(driver, element);
        try {
            ImageIO.write(screenshot.getImage(), "PNG", new File(System.getProperty("user.dir") + "/Images/" + imageName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        sleep(2000);
    }

    //Method web element image comparison
    public void webElementImageComparison(WebElement actualElement, WebElement expectedElement) {
        Screenshot actualImageScreenshot = new AShot().takeScreenshot(driver, actualElement);
        BufferedImage actualImage = actualImageScreenshot.getImage();

        Screenshot expectedImageScreenshot = new AShot().takeScreenshot(driver, expectedElement);
        BufferedImage expectedImage = expectedImageScreenshot.getImage();

        ImageDiffer imgDiff = new ImageDiffer();
        ImageDiff diff = imgDiff.makeDiff(actualImage, expectedImage);
        if (!diff.hasDiff())
            sa.assertFalse(diff.hasDiff(), "Images are Same...");
        else
            sa.assertFalse(diff.hasDiff(), "Images are Different...");
        sleep(1000);
        sa.assertAll();
    }

    //Method for wait until loader get disable
    public static void waitForLoader(WebElement loader) {
        sleep(1000);
        if (loader.isDisplayed()) {
            waitForInvisibilityOfElement(loader);
        }
        sleep(1000);
    }

    //Method for wait until loader get disable
    public static void waitForToaster(WebElement toaster) {
        sleep(1000);
        if (toaster.isDisplayed()) {
            waitForInvisibilityOfElement(toaster);
        }
        sleep(1000);
    }

    //Method to move cursor to element
    public static void moveToElement(WebElement element) {
        Actions builder = new Actions(driver);
        builder.moveToElement(element).build().perform();
    }

    //Method to test barcode
    public void testBarCode(String imgPath) throws FormatException, ChecksumException, NotFoundException, IOException, com.google.zxing.NotFoundException {
        Reader reader = new MultiFormatReader();
        // Pass the image a parameter, which converts the image into binary bitmap
        InputStream barCodeIS = new FileInputStream(imgPath);
        BufferedImage buffImage = ImageIO.read(barCodeIS);

        LuminanceSource lsrc = new BufferedImageLuminanceSource(buffImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(lsrc));

        // Reader decodes the bitmap & extract the data
        Result result = reader.decode(bitmap);

        String code = result.getText();
        System.out.println("Image Barcode / QR Code : " + code);

    }

    //Method to drag and drop element
    public static void dragAndDropElement(WebElement dragFrom, WebElement dragTo) throws Exception {
        // Setup robot
        Robot robot = new Robot();
        robot.setAutoDelay(500);
        // Get size of elements

        Dimension fromSize = dragFrom.getSize();
        Dimension toSize = dragTo.getSize();
        Point toLocation = dragTo.getLocation();
        Point fromLocation = dragFrom.getLocation();
        //Make Mouse coordinate centre of element
        toLocation.x += toSize.width / 2;
        toLocation.y += toSize.height / 2 + 100;
        fromLocation.x += fromSize.width / 2;
        fromLocation.y += fromSize.height / 2 + 100;

        //Move mouse to drag from location
        robot.mouseMove(fromLocation.x, fromLocation.y);
        //Click and drag
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Actions actions = new Actions(driver);
        actions.dragAndDrop(dragFrom, dragTo).build().perform();

        //Drag events require more than one movement to register
        //Just appearing at destination doesn't work so move halfway first
        robot.mouseMove(((toLocation.x - fromLocation.x) / 2) + fromLocation.x, ((toLocation.y - fromLocation.y) / 2) + fromLocation.y);

        //Move to final position
        robot.mouseMove(toLocation.x, toLocation.y);
        //Drop
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    //New Method to drag and drop element - sometimes old method will not work, then use this method.
    public static void newDragAndDropElement(WebElement source, WebElement destination) {
        sleep(2000);
        Actions builder = new Actions(driver);
        Action dragAndDrop = builder.clickAndHold(source).moveToElement(destination).release(destination).build();
        sleep(2000);
        dragAndDrop.perform();
    }

    //Method to read PDF file
    public void readPDF(String filePath, String assertText) throws IOException {

        URL url = new URL(filePath);

        InputStream is = url.openStream();
        BufferedInputStream fileParse = new BufferedInputStream(is);
        PDDocument document;

        document = PDDocument.load(fileParse);
        String pdfContent = new PDFTextStripper().getText(document);
        System.out.println(pdfContent);

        sa.assertTrue(pdfContent.contains(assertText));
        sa.assertAll();

    }

    //Method to avoid opening link in an new window
    public void avoidSwitchWindow(String linkXpath) {
        WebElement we = driver.findElement(By.cssSelector(linkXpath));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].setAttribute('target','_self');", we);

        we.click();
    }

    //Method to verify Element is present or not
    public static boolean isElementPresent(WebElement element) {
        try {
            element.isDisplayed();
            return true;
        } catch (NoSuchElementException e) {
            System.out.println(element + " Element is currently not visible");
            return false;
        }
    }

    //Method to verify Switch Window
    public static void switchToTab(int index) {
        String windowId = null;
        Set<String> windowIds = driver.getWindowHandles();
        Iterator<String> iterator = windowIds.iterator();
        for (int i = 0; i <= index; i++) {
            windowId = iterator.next();
        }
        driver.switchTo().window(windowId);
    }

    //Open new tab
    public static void openNewTab() {
        ((JavascriptExecutor) driver).executeScript("window.open();");
    }

    // Method to clear input text field
    public static void clearInputText(WebElement element) {
        int docNameLength = element.getAttribute("value").length();
        for (int i = 0; i < docNameLength; i++)
            element.sendKeys(Keys.BACK_SPACE);
    }

    // Method to generate random number
    public static int generateRandomNumber() {
        return (int) Math.floor(Math.random() * 101);
    }

    //Method to get value from properties file
    public static String getPropertyValueByKey(String key) {
        String value = prop.get(key).toString();

        if (StringUtils.isEmpty(value)) {
            try {
                throw new Exception("Value is not specified for key: " + key + " in properties file.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return value;
    }

    //Method to extract the integers from given string
    public static int extractIntegerFromString(String input) {
        StringBuilder storeOutputString = new StringBuilder();
        int extractedNumber;

        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(input);
        while (m.find()) {
            storeOutputString.append(m.group());
        }
        extractedNumber = Integer.parseInt(storeOutputString.toString());

        return extractedNumber;
    }

    public static void selectDropDownValue(WebElement dropDown, String dropdownValue) {
        select = new Select(dropDown);
        if (!dropdownValue.equals(getSelectedDropDownValue(dropDown)))
            select.selectByVisibleText(dropdownValue);
        sa.assertEquals(getSelectedDropDownValue(dropDown), dropdownValue);
        logAndReportWithScreenshot("Successfully verified " + dropdownValue + " is selected as Dropdown value");
    }

    public static void selectDropDownValueByIndex(WebElement dropDown, int index) {
        select = new Select(dropDown);
        select.selectByIndex(index);
        logAndReportWithScreenshot("Value at index " + index + " is selected as Dropdown value");
    }

    public static void verifyAllDropdownValues(WebElement dropDown, String sheetName, String columnName, int rowNo) {
        getAllDropDownValues(dropDown);
        for (int i = 1; i < getAllDropDownValues(dropDown).size(); i++) {
            sa.assertEquals(getAllDropDownValues(dropDown).get(i), excel.getCellData(sheetName, columnName, rowNo));
            rowNo++;
        }
        logAndReportWithScreenshot(getAllDropDownValues(dropDown) + " options from the dropdown are verified successfully.");
    }

    //Method to get data from Excel sheet and return sorted list
    public static List<String> getSortedList(int numberOfRecords, String sheetName, String columnName) {
        List<String> recordListFromExcelSheet = new ArrayList<>();
        for (int i = 0; i < numberOfRecords; i++) {
            recordListFromExcelSheet.add(excel.getCellData(sheetName, columnName, i + 2));
        }
        Collections.sort(recordListFromExcelSheet);
        return recordListFromExcelSheet;
    }

    //Method to get data from Excel sheet and list
    public static List<String> getExpectedList(int rowNoOfTheRecord, int numberOfRecords, String sheetName, String columnName) {
        List<String> recordListFromExcelSheet = new ArrayList<>();
        for (int i = 0; i < numberOfRecords; i++) {
            recordListFromExcelSheet.add(excel.getCellData(sheetName, columnName, i + rowNoOfTheRecord));
        }
        return recordListFromExcelSheet;
    }

    //Method to split a comma separate string
    public static List<String> splitCommaSeparatedString(String inputString, boolean isSpaceAvailable) {
        if (isSpaceAvailable)
            return Arrays.asList(inputString.split(", "));
        else
            return Arrays.asList(inputString.split(","));

    }

    //Method to split a comma separate string
    public static List<String> splitNewLineSeparatedString(String inputString) {
        return Arrays.asList(inputString.split("\u2028"));
    }

    //Method to retrieve all available dropdown options.
    public static List<String> getAllDropDownValues(WebElement dropDown) {
        select = new Select(dropDown);
        List<WebElement> allOptions = select.getOptions();
        ArrayList<String> dropDownList = new ArrayList<>();
        for (WebElement allOption : allOptions) {
            dropDownList.add(allOption.getText());
        }
        return dropDownList;
    }

    public static String getSelectedDropDownValue(WebElement dropDown) {
        select = new Select(dropDown);
        return select.getFirstSelectedOption().getText();
    }

    // Method to click element
    public static void click(WebElement element, String message) {
        waitForElementToBeDisplayed(element);
        element.click();
        sleep(500);
        logAndReport(message);
    }

    // Method to clear element
    public static void clear(WebElement element) {
        waitForElementToBeDisplayed(element);
        element.clear();
    }

    // Method for sendKeys with log into report
    public static void sendKeys(WebElement element, String textToEnter, String message) {
        waitForElementToBeDisplayed(element);
        element.clear();
        element.sendKeys(textToEnter);
        logAndReport(message);
    }

    // Method to getAttribute With custom text for element
    public static String getAttribute(WebElement element, String attribute, String message) {
        waitForElementToBeDisplayed(element);
        String text = element.getAttribute(attribute);
        logAndReportWithScreenshot(message + text);
        return text;
    }

    //Method for assert equal
    public static void assertion(String expectedValue, WebElement actualValue) {
        waitForElementToBeDisplayed(actualValue);
        sa.assertEquals(actualValue.getText(), expectedValue);
    }

    //Method to verify character limit of a field
    public static void verifyCharLimit(WebElement element, int maxAllowedSize) {
        waitForElementToBeDisplayed(element);
        sa.assertEquals(element.getAttribute("value").length(), maxAllowedSize);
        sa.assertTrue(element.getAttribute("value").length() == maxAllowedSize);
        System.out.println("Successfully verified that the TextBox can accept only " + maxAllowedSize + " characters");
    }

    public static void openNewWindow(String URL) {
        ((JavascriptExecutor) driver).executeScript("window.open('" + URL + "', '_blank', 'toolbar=0,location=0,menubar=0');");
    }

    //Method to clear input field using control A and Backspace key
    public static void clearInputField(WebElement inputBox) {
        inputBox.sendKeys(Keys.COMMAND, "a");
        sleep(10);
        inputBox.sendKeys(Keys.BACK_SPACE);
    }

    //Method to make flag Active
    public static void makeFlagActive(WebElement elementToBeActivate, WebElement elementToBeVerified) {
        if (elementToBeVerified.isSelected()) {
            System.out.println("Element is already Active");
        } else {
            click(elementToBeActivate, "Click on button to make it Active");
            sa.assertTrue(elementToBeVerified.isSelected());
        }
    }

    //Method to make flag Inactive
    public static void makeFlagInActive(WebElement elementToBeDeActivate, WebElement elementToBeVerified) {
        if (elementToBeVerified.isSelected()) {
            click(elementToBeDeActivate, "Click on button to make it InActive");
            sa.assertFalse(elementToBeVerified.isSelected());
        } else {
            System.out.println("Element is already InActive");
        }
    }

    //Method to click on flag to make it Active or Inactive
    public static void clickOnActiveInactiveButton(WebElement flagToBeClicked, boolean makeItActive) {
        if (makeItActive) {
            click(flagToBeClicked, "Click on button to make it InActive");
        }
    }

    //Method to get flag values
    public static boolean getFlagValue(WebElement flag) {
        return flag.isSelected();
    }

    //Method to get text form WebElement List
    public static List getTextFromElementList(List<WebElement> webElements, boolean isSortedRequired) {
        List<String> elementNameList = new ArrayList<>();
        for (WebElement webElement : webElements) {
            elementNameList.add(webElement.getText());
        }
        if (isSortedRequired) {
            Collections.sort(elementNameList);
        }
        return elementNameList;
    }

    //Method to create comma separated list
    public static String getCommaSeparatedList(String textToBeFormatted) {
        return textToBeFormatted.replaceAll("\n", ",");
    }

    //Method to create comma separated string form List
    public static String createCommaSeparatedStringFormList(List<String> listOfStringToBeFormatted) {
        return String.join(",", listOfStringToBeFormatted);
    }

    //Method for substring using last index of occurrence
    public static String lastIndexSubstring(String originalString, String endingCharacter) {
        int sepPos = originalString.lastIndexOf(endingCharacter);
        return originalString.substring(0, sepPos + 1);
    }

    //Method to get Base64 string from captured screenshot
    public static String getBase64ScreenshotPath() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }

    //Method to scroll down until element is visible
    public static void scrollTillElement(WebElement elementTillScrollIsRequired) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        do {
            js.executeScript("arguments[0].scrollIntoView(true);", elementTillScrollIsRequired);
        } while (!isElementPresent(elementTillScrollIsRequired));
    }

    //Method to enter text using JavaScript
    public static void enterTextByJavaScript(WebElement textBoxToBeUsed, String valueToBeEntered, String textToBeLogged) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value='" + valueToBeEntered + "';", textBoxToBeUsed);
        logAndReportWithScreenshot(textToBeLogged);
    }

    //Method to remove specified leading character from string
    public static String removeLeadingCharacters(String fromWhichCharacterToBeRemoved, String characterToBeRemoved) {
        return fromWhichCharacterToBeRemoved.replaceFirst("^" + characterToBeRemoved + "+(?!$)", "");
    }

    //Method to make flag Active
    public static void selectRadioButton(WebElement optionToBeSelected) {
        waitForElementToBeDisplayed(optionToBeSelected);
        if (optionToBeSelected.isSelected()) {
            logAndReportWithScreenshot("Specified Option is already Selected");
        } else {
            click(optionToBeSelected, "Clicked on button to Select the Option");
            sa.assertTrue(optionToBeSelected.isSelected());
        }
    }

    //Method to replace all special characters from string
    public static String removeSpecialCharacters(String stringToBeFormatted) {
        return stringToBeFormatted.replaceAll("[^a-zA-Z0-9]", "");
    }

    //Method to Query to Database
    public static ResultSet queryToDatabase(String query) {
        logAndReport("Querying Database to get Results");
        logAndReport("SQL Query: " + query);
        try {
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    //Method to return query details
    public static List<String> getDataFromDatabase(ResultSet resultSet) {
        ResultSetMetaData rsmd = null;
        List<String> databaseValues = new ArrayList<>();
        try {
            rsmd = resultSet.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= numberOfColumns; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnName = rsmd.getColumnName(i);
                    String columnValue = resultSet.getString(i);
                    logAndReport(columnName + "=\t" + columnValue);
                    databaseValues.add(columnValue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return databaseValues;
    }

    public static List getMacAndIPAddress() {
        InetAddress ip;
        StringBuilder sb;
        String ipAddress = null;
        String MACAddress = null;
        try {

            ip = InetAddress.getLocalHost();
            ipAddress = ip.getHostAddress();

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            MACAddress = sb.toString();

        } catch (UnknownHostException e) {

            e.printStackTrace();

        } catch (SocketException e) {

            e.printStackTrace();

        }
        return Arrays.asList(ipAddress, MACAddress);
    }

    //Method to separate int from the string
    public static int[] getIntegers(String s1) {
        String[] parts = s1.split(",");
        int[] ints = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            ints[i] = Integer.parseInt(parts[i]);
        }
        return ints;
    }

    //Method to verify element is clickable or not
    public static boolean isElementClickable(WebElement element) {
        try {
            element.click();
            return true;
        } catch (ElementClickInterceptedException e) {
            System.out.println("Element is not clickable");
            return false;
        }
    }

    //Method to Get text using getText & getAttribute
    public static String getText(WebElement ele) {
        String eleText = null;
        eleText = ele.getText();
        if (eleText.equals("")) {
            eleText = ele.getAttribute("innerText").trim();
            System.out.println("In inner text");
        }
        if (eleText.equals("")) {
            eleText = ele.getAttribute("textContent").trim();
            System.out.println("In textContent");
        }
        if (eleText.equals("")) {
            eleText = ele.getAttribute("innerHTML").trim();
            System.out.println("In innerHTML");
        }
        if (eleText.equals("")) {
            eleText = ele.getAttribute("outerText").trim();
            System.out.println("In Outer text");
        }
        return eleText;
    }

    //Method to click element using JS
    public static void clickUsingJs(WebElement element) {
        js.executeScript("arguments[0].click();", element);
    }

    //Method for double-click on element
    public static void doubleClickOnElement(WebElement element) {
        Actions actions = new Actions(driver);
        // Perform the first click
        actions.click(element);
        // Add a delay of 1 second (1000 milliseconds)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Perform the second click
        actions.click(element);
        // Execute the actions
        actions.perform();
    }

    //Method to verify dropdown values
    public static void verifyDropdownValues(WebElement dropdown, List<WebElement> dropdownValues, String sheetName, String columnName, int rowNumber) {
        waitForElementToBeDisplayed(dropdown);
        click(dropdown, "Clicked on the dropdown");
        for (int i = 0; i < dropdownValues.size(); i++) {
            sa.assertEquals(dropdownValues.get(i).getText(), excel.getCellData(sheetName, columnName, i + rowNumber));
//            System.out.println("Value at number: " + i + " is " + dropdownValues.get(i).getText());
//            System.out.println("Value at number from excel : " + (i + rowNumber) + " is " + excel.getCellData(sheetName, columnName, i + rowNumber));
        }
        logAndReportWithScreenshot("Verified all dropdown values");
    }

    //Method to select value from dropdown
    public static void selectValuesFromDropdown(WebElement dropdown, String valueToBeSelected) {
        click(dropdown,"Clicked on the Entity dropdown");
//        clickUsingJs(dropdown);
        System.out.println("Clicked on the dropdown");
        logAndReportWithScreenshot("Verified dropdown displayed with all the options");
        sleep(1000);
        WebElement value = driver.findElement(By.xpath("//mat-option[normalize-space()='" + valueToBeSelected + "']"));
        click(value, "Selected " + valueToBeSelected + " from dropdown");
        //  dropdown.sendKeys(Keys.TAB);
    }

    //Method to focusOut element
    public static void focusOutElement() {
        Actions action = new Actions(driver);
        action.moveByOffset(0, 0).click().build().perform();
    }

    //Method to get File name from File path
    public static String getFileName(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        return fileName;
    }

    //Method to get todays formatted date using Java-Script
    public static void getTodayFormattedDate(WebElement dateToBeVerify) {
        // Get the current date
        Date currentDate = new Date();

        // Format the date (for example, as yyyy-MM-dd)
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/YY");
        String formattedDate = dateFormat.format(currentDate);

        System.out.println("Formatted Date: " + formattedDate);

        // Example: Fill an input field with the formatted date
        // WebElement inputField = driver.findElement(By.id("dateInput"));

        dateToBeVerify.sendKeys(formattedDate);
        System.out.println("Formatted Date: " + formattedDate);

    }

    //Method to get date formatted date
    public static void verifyDate(WebElement dateAndTime, String expectedTodaysDate) {
        sleep(1000);
        System.out.println("Formatted date :" + expectedTodaysDate);
        sleep(1000);
        String actualDate = getText(dateAndTime).substring(0, dateAndTime.getText().indexOf(","));
        sa.assertEquals(actualDate, expectedTodaysDate);
    }

    //Method to get to get round numbers
    public static double roundNumberUp(double number) {
        double fractionalPart = number - (int) number;
        int lastDigit = (int) (fractionalPart * 10) % 10;
        if (lastDigit >= 5) {
            number = Math.ceil(number);
        } else {
            // Round the number down by taking the floor of the number
            number = Math.floor(number);
        }

        return number;
    }

    //Method to get file name with or without extension
    public static String getFileNameFromPath(String filePath, Boolean isFileExtensionRequired) {
        if (isFileExtensionRequired) {
            return FilenameUtils.getName(filePath);
        }
        return FilenameUtils.getBaseName(filePath);
    }

    //Method to close new tab and switch back to the original tab
    public static void closeNewTab(WebElement element) {
        String originalHandle = driver.getWindowHandle();
        // Switch to the new tab
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalHandle)) {
                driver.switchTo().window(handle);

                // Add your actions on the new tab here
                waitForElementToBeDisplayed(element);
                logAndReport("Verified User navigate back to the original tab");
                // Close the new tab
                driver.close();
                driver.switchTo().window(originalHandle);
            }
        }

    }

//    //Method to gate only date after removing extra details
//    public static void verifyDatewithoutTimeAndDay(WebElement dateElement, String columnName, int dateRowNo) {
//
//        String dateString = dateElement.getText();
//        String pattern = "EEE MMM dd yyyy HH:mm:ss 'GMT'Z (z)";
//
//        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
//        try {
//            Date date = sdf.parse(dateString);
//            // Extract month, day, and year from the parsed date
//            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd yyyy");
//            String extractedDate = outputFormat.format(date);
//
//            String expectedDate = excel.getCellData(sheetName, columnName, dateRowNo);
//
//            if (expectedDate.equals(extractedDate)) {
//                System.out.println("Date verified: " + extractedDate);
//                // Perform your Selenium actions here if the date is verified
//            } else {
//                System.out.println("Date verification failed.");
//                // Handle if the date doesn't match
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    //Method to create two nodes
//    public static void connectTwoNodes(WebElement nodeOne, WebElement nodeTwo) {
//        //         Instantiate the Actions class
//        Actions actions = new Actions(driver);
//
//        // Move to the first element
//        actions.moveToElement(nodeOne);
//
//        // Perform a click-and-hold at the first element
//        actions.clickAndHold();
//        // Move to the second element
//        actions.moveToElement(nodeTwo);
//        // Release the mouse to complete the action
//        actions.release();// Build and perform the action
//        actions.build().perform();
//    }

    //Method to clear element value using javascript
    public static void clearElementValue(WebElement element) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].value = '';", element);
    }

    //Method to wait class to change
    public static void waitForClassToChange(WebElement element, String expectedClass) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Adjust the timeout as needed
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                try {
                    return element.getAttribute("class").equals(expectedClass);
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }
        });
    }
}





