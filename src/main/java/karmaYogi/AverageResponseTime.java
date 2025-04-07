package karmaYogi;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;
import org.testng.annotations.Test;
import org.openqa.selenium.JavascriptExecutor;
import io.github.bonigarcia.wdm.WebDriverManager;

public class AverageResponseTime {

	@Test(invocationCount = 5, threadPoolSize = 5)
	public void measurePageLoadTime() {
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();

		try {
			driver.manage().deleteAllCookies(); //deleting cookies before launching
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60)); // waiting for page to load

			long startTime = System.currentTimeMillis(); //here i am getting my current system time
			driver.get("https://igotkarmayogi.gov.in/#/"); //launching URL

			// here i am waiting until the page is fully loaded
			while (!((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete")) {
				Thread.sleep(100);
			}

			long endTime = System.currentTimeMillis(); // getting end system time when it has finised executing above code
			long loadTime = endTime - startTime; // this gives me the difference between start and end

			System.out.println("Thread: " + Thread.currentThread().getId() + " - Page Load Time: " + loadTime + " ms");

		} catch (Exception e) {
			e.printStackTrace();
		} finally { // will always execute
			driver.quit(); //closing browser
		}
	}
}