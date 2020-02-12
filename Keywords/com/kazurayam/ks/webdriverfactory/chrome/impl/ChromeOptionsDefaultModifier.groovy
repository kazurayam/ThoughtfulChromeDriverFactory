package com.kazurayam.ks.webdriverfactory.chrome.impl

import org.openqa.selenium.chrome.ChromeOptions

public class ChromeOptionsDefaultModifier {

	ChromeOptions filter(ChromeOptions options) {

		options.addArguments("window-size=1024,768")
		options.addArguments("--no-sandbox")

		//options.addArguments("--single-process")
		options.addArguments("disable-infobars")        // disabling infobars
		//chromeOptions.addArguments("disable-extensions")    // disabling extensions
		options.addArguments("disable-gpu")             // applicable to windows os only
		options.addArguments("disable-dev-shm-usage")   // overcome limited resource problems
		//
		return options
	}
}
