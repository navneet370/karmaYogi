package karmaYogi;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.core.har.HarEntry;

import java.util.List;

public class CaptureApis {

	// Wait for APIs to stabilize
	public static void waitForAllApiCallsToFinish(BrowserMobProxy proxy, int timeoutSeconds)
			throws InterruptedException {
		int stableCount = 0;
		int maxStable = 5;
		int lastCount = -1;

		// If same count as last iteration, increment stable counter
		for (int i = 0; i < timeoutSeconds * 2; i++) {
			int currentCount = proxy.getHar().getLog().getEntries().size();
			if (currentCount == lastCount) {
				stableCount++;
			} else {
				stableCount = 0;
			}

			// If count hasn’t changed in 5 checks (2.5 sec), assume stable
			if (stableCount >= maxStable) {
				System.out.println("API calls have stabilized.");
				return;
			}

			lastCount = currentCount;
			Thread.sleep(500);
		}

		System.out.println("Timed out waiting for API calls to finish.");
	}

	// Print APIs captured in HAR log
	public static void printCapturedApis(BrowserMobProxy proxy, long threadId) {
		System.out.println("\nThread: " + threadId);
		System.out.println("--- API Calls Captured by Thread " + threadId + " ---");

		List<HarEntry> entries = proxy.getHar().getLog().getEntries();
		for (HarEntry entry : entries) {
			String url = entry.getRequest().getUrl();
			// Filter only relevant APIs
			if (url.contains("/api/") || url.contains("json") || url.contains("ajax")) {
				System.out.println(url);
			}
		}
	}
}
