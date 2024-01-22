package utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.testng.ITestResult;
import org.testng.annotations.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reporter {

    protected static ExtentReports extentReports; // Makes first appointment to extent report
    protected static ExtentTest extentTest; // Records information such as test pass or failed. We also use it for screenshots
    protected static ExtentHtmlReporter extentHtmlReporter; // Edits html report

    // Just before starting the test (not before the test method, but before the whole test process)
    @BeforeMethod(alwaysRun = true)

    @Parameters({"author", "test"})

    public void setUpTest(String author, String test) {

        extentReports = new ExtentReports(); // Starts reporting

        // After the report is created, you write here where you want your report to be added.
        String date = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        String filePath = System.getProperty("user.dir") + "/test-output/" + author + "/" + test + "-report-" + ".html";

        // Initialize the report we want to create (in html format), specify the file path with filePath.
        extentHtmlReporter = new ExtentHtmlReporter(filePath);
        extentReports.attachReporter(extentHtmlReporter);

        // You can add the information you want here.
        extentReports.setSystemInfo("Environment","QA");
        extentReports.setSystemInfo("Browser", ConfigReader.getProperty("browser")); // chrome, firefox
        extentReports.setSystemInfo("Automation Engineer", author);
        extentHtmlReporter.config().setDocumentTitle("TestNG Test");
        extentHtmlReporter.config().setReportName("TestNG Reports");
    }

    // After each test method, if there is an error in the test, it takes a screenshot and adds it to the report
    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(ITestResult result) throws IOException {

        if (result.getStatus() == ITestResult.FAILURE) { // If test failed
            String screenshotLocation = Reusable.getScreenshot(result.getName());
            extentTest.fail(result.getName());
            extentTest.addScreenCaptureFromPath(screenshotLocation);
            extentTest.fail(result.getThrowable());
        } else if (result.getStatus() == ITestResult.SKIP) { // If the test is not passed without running
            extentTest.skip("Test Case is skipped: " + result.getName()); // Ignore ones
        }
        Driver.closeDriver();
    }

    // To end reporting
    @AfterMethod(alwaysRun = true)
    public void tearDownTest() {

        extentReports.flush();
    }
}
