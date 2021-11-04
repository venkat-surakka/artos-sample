package com.tests.sample3_selenium;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.artos.annotation.AfterTestUnit;
import com.artos.annotation.BeforeTestUnit;
import com.artos.annotation.TestCase;
import com.artos.annotation.TestPlan;
import com.artos.annotation.Unit;
import com.artos.framework.Enums.TestStatus;
import com.artos.framework.infra.TestContext;
import com.artos.interfaces.TestExecutable;
import com.artos.utils.Guard;
import com.artos.utils.ImageCompare;
import com.artos.utils.UtilsFile;

@TestPlan(preparedBy = "arpit", preparationDate = "6/03/2019", bdd = "GIVEN webpage has not changed AND firefox is working THEN visual regression is successful")
@TestCase
public class Sample_Selenium implements TestExecutable {

	@Unit
	public void testThatDoesntShowStacktrace(TestContext context) throws InterruptedException {
		WebDriver chromeDriver = (WebDriver) context.getGlobalObject("CHROME_DRIVER");
		chromeDriver.navigate().to("https://www.theartos.com");

		chromeDriver.manage().window().maximize();

		chromeDriver.findElement(By.linkText("TUTORIALS")).click();
		Thread.sleep(2000);
		String actText = chromeDriver.findElement(By.xpath("//h2")).getText();

		// This is not getting logged in stack trace since this is junit assertion
		Assert.assertEquals("LATEST TUTORIALS FAILED", actText);
	}

	@Unit
	public void testThatShowsStacktrace(TestContext context) throws InterruptedException {
		WebDriver chromeDriver = (WebDriver) context.getGlobalObject("CHROME_DRIVER");
		chromeDriver.navigate().to("https://www.theartos.com");

		chromeDriver.manage().window().maximize();

		chromeDriver.findElement(By.linkText("TUTORIALS")).click();
		Thread.sleep(2000);
		String actText = chromeDriver.findElement(By.xpath("//h2")).getText();

		if(actText.equals("LATEST TUTORIAL"))
			context.setTestStatus(TestStatus.PASS, "Tutorials title is as expected");
		else
			context.setTestStatus(TestStatus.FAIL, "Tutorials title is not as expected - expected: LATEST TUTORIALS, actual: " + actText);
	}

	@Unit(skip = true)
	public void testUnit_1(TestContext context) throws Exception {
		// --------------------------------------------------------------------------------------------
		// Launch web-site
		WebDriver fireFoxDriver = (WebDriver) context.getGlobalObject("FIREFOX_DRIVER");
		fireFoxDriver.navigate().to("https://www.theartos.com");
		Thread.sleep(8000);

		// Take snapshot
		String relativePathToImage = "./reporting/testImage.png";
		File destFile = takeSnapShot(fireFoxDriver, relativePathToImage);

		// Store snapshot into Extent report
		context.setTestStatus(TestStatus.PASS, destFile, "Managed to display page successfully");

		// Compare snapshot to previously captured golden sample image
		File goldSample = new File("./assets/png/Gold_Sample.png");
		ImageCompare ic = new ImageCompare();
		ic.compare(goldSample, destFile, new File("./reporting/diff/"), "Result_Image");

		// Get result file
		File resultFile = ic.getResultImage();

		// Store result image to report
		if (Guard.isEquals(100, ic.getPercentageMatch())) {
			context.setTestStatus(TestStatus.PASS, resultFile, "Snapshot matches Golden sample image");
		} else {
			context.setTestStatus(TestStatus.FAIL, resultFile, "Snapshot does not match Golden sample image");
		}
		// --------------------------------------------------------------------------------------------
	}

	@BeforeTestUnit
	public void beforeTest(TestContext context) {
//		System.setProperty("webdriver.gecko.driver", "./assets/driver/geckodriver_64bit.exe");
//		WebDriver firefoxDriver = new FirefoxDriver();
//		firefoxDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
//		context.setGlobalObject("FIREFOX_DRIVER", firefoxDriver);

		 System.setProperty("webdriver.chrome.driver", "C:\\working\\artos-sample\\Artos-test-project\\assets\\driver\\chromedriver.exe");
		 WebDriver chromeDriver = new ChromeDriver();
		 context.setGlobalObject("CHROME_DRIVER", chromeDriver);
	}

	@AfterTestUnit
	public void tearDown(TestContext context) {
//		WebDriver fireFoxDriver = (WebDriver) context.getGlobalObject("FIREFOX_DRIVER");
//		fireFoxDriver.quit();

		 WebDriver chromeDriver = (WebDriver)
		 context.getGlobalObject("CHROME_DRIVER");
		 chromeDriver.quit();
	}

	public static File takeSnapShot(WebDriver webdriver, String fileWithPath) throws Exception {

		// Convert web driver object to TakeScreenshot
		TakesScreenshot scrShot = ((TakesScreenshot) webdriver);

		// Call getScreenshotAs method to create image file
		File srcFile = scrShot.getScreenshotAs(OutputType.FILE);

		// Move image file to new destination
		File destFile = new File(fileWithPath);

		// Copy file at destination
		UtilsFile.copyFile(srcFile, destFile, true);

		return destFile;
	}

}
