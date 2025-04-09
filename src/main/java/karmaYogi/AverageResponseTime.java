package karmaYogi;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.time.Duration;

public class AverageResponseTime {

	final static int users = 5;
	final static int threads = 5;
	public static String baseURL = "https://igotkarmayogi.gov.in/#/";
	public static int pageLoadTimeoutSeconds = 60;
	public static long totalLoadTime = 0;
	public static int proxyPort = 0;

	@Test(invocationCount = users, threadPoolSize = threads)
	public void testPageLoadAndCaptureApis() throws InterruptedException {
		BrowserMobProxy proxy = new BrowserMobProxyServer();
		proxy.start(proxyPort); // 0 means here we are starting on random port
		Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
		ChromeOptions options = new ChromeOptions();
		options.setProxy(seleniumProxy); // Additional options for HTTPS handling and Chrome compatibility
		options.setAcceptInsecureCerts(true);
		options.addArguments("--ignore-certificate-errors");
		options.addArguments("--allow-insecure-localhost");
		options.addArguments("--remote-allow-origins=*");

		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver(options);

		try {
			proxy.newHar("test-har"); // here we are recording har- network logs
			driver.manage().deleteAllCookies();

			long startTime = System.currentTimeMillis(); // here i am getting my current system time
			driver.get(baseURL);

			CaptureApis.waitForAllApiCallsToFinish(proxy, 30); // waits until all the API calls made by the page have
																// finished, or until a timeout of 30 seconds.

			// here i am waiting until the page is fully loaded
			while (!((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete")) {
				Thread.sleep(100);
			}

			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeoutSeconds)); // waiting for page
																									// to load
			long endTime = System.currentTimeMillis(); // getting end system time when it has finised executing above
			long loadTime = endTime - startTime; // this gives me the difference between start and end
			long threadId = Thread.currentThread().getId();

			CaptureApis.printCapturedApis(proxy, threadId);

			// Print final page load time
			System.out.println("Thread: " + threadId + " - Page Load Time: " + loadTime + " ms"); /// getting the
																									/// current threadId
																									/// and respective
																									/// pageLoad time
			totalLoadTime += loadTime;

		} catch (Exception e) {
			e.printStackTrace();
		} finally { // will always execute
			driver.quit(); // closing browser
			proxy.stop(); // closing created proxy

		}
	}

	@AfterClass
	public void printAverageTime() {
		long avg = totalLoadTime / users; // calculation the average load time
		System.out.println(" Average Page Load Time: " + avg + " ms ");
	}
}
