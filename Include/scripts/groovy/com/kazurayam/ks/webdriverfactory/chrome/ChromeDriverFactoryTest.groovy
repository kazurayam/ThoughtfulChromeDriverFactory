package com.kazurayam.ks.webdriverfactory.chrome

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities

import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kazurayam.junit4ks.IgnoreRest

/**
 * @author kazurayam
 */
@RunWith(JUnit4.class)
public class ChromeDriverFactoryTest {

	@Before
	void setup() {}

	/**
	 * Basic case.
	 * Instantiate a ChromeDriver to open a Chrome browser with the default profile.
	 * 
	 */
	@Test
	void test_newChromeDriver() {
		ChromeDriverFactoryImpl cdFactory = new ChromeDriverFactoryImpl()
		WebDriver driver = cdFactory.newChromeDriver()
		assertThat(driver, is(notNullValue()))

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		WebUI.comment("DesiredCapabilities: ${dc.toString()}")
		//assertThat(cdFactory.getChromeProfile().getName(), is('kazurayam'))

		DriverFactory.changeWebDriver(driver)
		WebUI.navigateToUrl('http://demoaut.katalon.com/')
		WebUI.delay(3)
		WebUI.closeBrowser()
	}

	/**
	 * Instantiate a ChromeDriver to open a Chrome browser specifying a user profile "Katalon"
	 * 
	 */
	@Test
	void test_newChromeDriverWithProfile() {
		ChromeDriverFactoryImpl cdFactory = new ChromeDriverFactoryImpl()
		WebDriver driver = cdFactory.newChromeDriverWithProfile('Katalon')
		assertThat(driver, is(notNullValue()))

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		WebUI.comment("DesiredCapabilities: ${dc.toString()}")

		WebUI.comment("ChromeDriver has been instantiated with profile Katalon")
		DriverFactory.changeWebDriver(driver)
		WebUI.navigateToUrl('http://demoaut.katalon.com/')
		WebUI.delay(3)
		WebUI.closeBrowser()
	}

	/**
	 * Instantiate a ChromeDriver to open Chrome browser specifying a profile directory "Default"
	 * 
	 */
	@Test
	void test_newChromeDriverWithProfileDirectory() {
		ChromeDriverFactoryImpl cdFactory = new ChromeDriverFactoryImpl()
		WebDriver driver = cdFactory.newChromeDriverWithProfileDirectory('Default')
		assertThat(driver, is(notNullValue()))

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		WebUI.comment("DesiredCapabilities: ${dc.toString()}")

		WebUI.comment("ChromeDriver has been instantiated with profile directory Default")
		DriverFactory.changeWebDriver(driver)
		WebUI.navigateToUrl('http://demoaut.katalon.com/')
		WebUI.delay(3)
		WebUI.closeBrowser()
	}
}
