package com.kazurayam.ks.webdriverfactory.chrome

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.kazurayam.ks.webdriverfactory.desiredcapabilities.DesiredCapabilitiesDefaultBuilder
import com.kazurayam.ks.webdriverfactory.desiredcapabilities.DesiredCapabilitiesModifier
import com.kazurayam.ks.webdriverfactory.utils.Assert
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.util.KeywordUtil

import groovy.json.JsonOutput

public class ChromeDriverFactoryImpl extends ChromeDriverFactory {

	static Logger logger_ = LoggerFactory.getLogger(ChromeDriverFactoryImpl.class)

	static {
		// dynamically add toJsonText() method to ChromeOptions class
		ChromeOptions.metaClass.toString = {
			return JsonOutput.prettyPrint(JsonOutput.toJson(delegate.asMap()))
		}
	}

	private List<ChromePreferencesModifier> chromePreferencesFilters_
	private List<ChromeOptionsModifier> chromeOptionsFilters_
	private List<DesiredCapabilitiesModifier> desiredCapabilitiesFilters_

	ChromeDriverFactoryImpl() {
		chromePreferencesFilters_   = new ArrayList<ChromePreferencesModifier>()
		chromeOptionsFilters_       = new ArrayList<ChromeOptionsModifier>()
		desiredCapabilitiesFilters_ = new ArrayList<DesiredCapabilitiesModifier>()
	}


	@Override
	WebDriver openChromeDriver() {
		return openChromeDriver(RunConfiguration.getDefaultFailureHandling())
	}


	/**
	 * Open a Chrome browser without specifying Profle
	 * 
	 * @param flowControl
	 * @return
	 */
	@Override
	WebDriver openChromeDriver(FailureHandling flowControl) {
		Objects.requireNonNull(flowControl, "flowControl must not be null")
		//
		ChromeDriverUtils.enableChromeDriverLog(Paths.get(RunConfiguration.getProjectDir()).resolve('tmp'))
		//
		Path chromeDriverPath = ChromeDriverUtils.getChromeDriverPath()
		System.setProperty('webdriver.chrome.driver', chromeDriverPath.toString())
		//
		Map<String, Object> chromePreferences = new ChromePreferencesDefaultBuilder().build()
		ChromeOptions chromeOptions = new ChromeOptionsDefaultBuilder().build(chromePreferences)
		//
		DesiredCapabilities cap = new DesiredCapabilitiesDefaultBuilder().build(chromeOptions)
		WebDriver driver = new ChromeDriver(cap)
		return driver
	}

	@Override
	WebDriver openChromeDriverWithProfile(String userName) {
		return openChromeDriverWithProfile(userName, RunConfiguration.getDefaultFailureHandling())
	}

	/**
	 * Based on the post https://forum.katalon.com/t/open-browser-with-custom-profile/19268 by Thanh To
	 *
	 * Chrome's User Data directory is OS dependent. The User Data Diretory is described in the document
	 * https://chromium.googlesource.com/chromium/src/+/HEAD/docs/user_data_dir.md#Current-Location
	 *
	 * @param userProfile
	 * @param flowControl
	 * @return
	 */
	@Override
	WebDriver openChromeDriverWithProfile(String userName, FailureHandling flowControl) {
		Objects.requireNonNull(userName, "userName must not be null")
		Objects.requireNonNull(flowControl, "flowControl must not be null")
		//
		ChromeDriverUtils.enableChromeDriverLog(Paths.get(RunConfiguration.getProjectDir()).resolve('tmp'))
		//
		Path chromeDriverPath = ChromeDriverUtils.getChromeDriverPath()
		System.setProperty('webdriver.chrome.driver', chromeDriverPath.toString())
		//
		Path profileDirectory = ChromeDriverUtils.getChromeProfileDirectory(userName)
		//
		if (profileDirectory != null) {
			if (Files.exists(profileDirectory) && profileDirectory.toFile().canWrite()) {
				Map<String, Object> chromePreferences = new ChromePreferencesDefaultBuilder().build()
				ChromeOptions chromeOptions = new ChromeOptionsDefaultBuilder().build(chromePreferences)

				// use the Profile as specified
				Path userDataDirectory = ChromeDriverUtils.getChromeUserDataDirectory()
				chromeOptions.addArguments("user-data-dir=" + userDataDirectory.toString())
				chromeOptions.addArguments("profile-directory=${profileDirectory.getFileName().toString()}")
				KeywordUtil.logInfo("#openChromeDriver chromeOptions=" + chromeOptions.toString())

				DesiredCapabilities cap = new DesiredCapabilitiesDefaultBuilder().build(chromeOptions)
				WebDriver driver = new ChromeDriver(cap)
				return driver
			} else {
				Assert.stepFailed("Profile directory \"${profileDirectory.toString()}\" is not present", flowControl)
			}
		} else {
			Assert.stepFailed("Profile directory for userName \"${userName}\" is not found." +
					"\n" + ChromeProfileFinder.listChromeProfiles(),
					flowControl)
		}
	}


	/**
	 * Usage:
	 * <PRE>
	 * import com.kazurayam.webdriverfactory4ks.ChromeDriverFactory
	 * import import org.openqa.selenium.WebDriver
	 * import com.kms.katalon.core.webui.driver.DriverFactory
	 * 
	 * ChromeDriverFactory cdFactory = new ChromeDriverFactory()
	 *
	 * // open Chrome browser with the profile stored in the directory 'User Data\Default'
	 * WebDriver driver = cdFactory.openChromeDriverWithProfileDirectory('Default')
	 * DriverFactory.changeWebDriver(driver)
	 * WebUI.navigateToUrl('http://demoaut.katalon.com/')
	 * WebUI.delay(3)
	 * WebUI.closeBrowser()
	 * </PRE>
	 */
	@Override
	WebDriver openChromeDriverWithProfileDirectory(String directoryName) {
		return openChromeDriverWithProfileDirectory(directoryName, RunConfiguration.getDefaultFailureHandling())
	}

	@Override
	WebDriver openChromeDriverWithProfileDirectory(String directoryName, FailureHandling flowControl) {
		Objects.requireNonNull(directoryName, "directoryName must not be null")
		Path userDataDir = ChromeDriverUtils.getChromeUserDataDirectory()
		if (userDataDir != null) {
			if (Files.exists(userDataDir)) {
				Path profileDirectory = userDataDir.resolve(directoryName)
				if (Files.exists(profileDirectory)) {
					ChromeProfile chromeProfile = ChromeProfileFinder.getChromeProfileByDirectoryName(profileDirectory)
					return openChromeDriverWithProfile(chromeProfile.getName(), flowControl)
				}
			} else {
				Assert.stepFailed("${userDataDir} is not found", flowControl)
			}
		} else {
			Assert.stepFailed("unable to identify the User Data Directory of Chrome browser", flowControl)
		}
	}
}