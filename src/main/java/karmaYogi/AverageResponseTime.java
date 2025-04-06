package karmaYogi;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import io.github.bonigarcia.wdm.WebDriverManager;

public class AverageResponseTime {

	public static void main(String[] args) {

		WebDriverManager.chromedriver().setup(); // setup chrome
		long totalLoadTime = 0; // here i have declared a variable which later be used as current time

		for (int i = 1; i <= 5; i++) { // loop is for 5 as thread group of 5 users.We can increase it as well

			WebDriver driver = new ChromeDriver(); // passing options in driver else i setup my browser with headless
													// setting

			try {
				driver.manage().deleteAllCookies(); // deleting cookies before launching
				driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20)); // waiting for page to load
																					// properly. Max time is of 20 secs
				long startTime = System.currentTimeMillis(); // here i am getting my current system time
				driver.get("https://igotkarmayogi.gov.in/#/"); // launching URL

				// here i am waiting until the page is fully loaded
				while (!((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete")) {
					Thread.sleep(100);
				}

				long endTime = System.currentTimeMillis(); // getting end system time when it has finised executing
															// above code
				long loadTime = endTime - startTime; // this gives me the difference
				totalLoadTime += loadTime; // adding the differnce time at global variable

				System.out.println("User " + i + " - Page Load Time: " + loadTime + " ms"); // user wise time to load
																							// the web page

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				driver.quit(); // quitting driver after every user has been executed
			}

			try {
				Thread.sleep(2000); // i am waiting for 2 secs before next user so that we can have a real world
									// scenario
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		long averageLoadTime = totalLoadTime / 5; // calculaing the avaerge response time
		System.out.println("Average Page Load Time for 5 users: " + averageLoadTime + " ms");
	}

}
