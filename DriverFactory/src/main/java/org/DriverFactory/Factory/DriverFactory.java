package org.DriverFactory.Factory;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import BasicTool.Config.ConfigUtil;
import Factory.LogFactory;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class DriverFactory {
    private static WebDriver driver = null;
    private static AppiumDriver appiumDriver = null;
    private static Logger log = LogFactory.getLogger(DriverFactory.class);
    private static ConfigUtil configUtil = ConfigUtil.getConfigUtil();

    private DriverFactory() {
    }

    /**
     * Create a new driver for FF,CHROME,IE
     * 
     * @return WebDriver that you want style
     */
    private static WebDriver CreateBroswerDriver() {
        if (configUtil.getConfigFileContent("isRemoteDriver").equals("false")) {
            switch (configUtil.getConfigFileContent("broswerType")) {
            case "firefox":
                driver = new FirefoxDriver();
                return driver;
            case "chrome":
                System.setProperty("webdriver.chrome.driver",
                        System.getProperty("user.dir") + configUtil.getConfigFileContent("chromeDriverPath"));
                DesiredCapabilities dc = DesiredCapabilities.chrome();
                driver = new ChromeDriver(dc);
                return driver;
            case "ie":
                System.setProperty("webdriver.ie.driver",
                        System.getProperty("user.dir") + configUtil.getConfigFileContent("internetExplorerDriverPath"));
                dc = DesiredCapabilities.internetExplorer();
                driver = new InternetExplorerDriver(dc);
                return driver;
            case "safari":
                return driver;
            default:
                try {
                    throw new Exception("Don't support this broswer on local!!!!!");
                } catch (Exception e) {
                    e.printStackTrace();
                    return driver;
                }
            }
        } else {
            try {
                URL remoteUrl = new URL(configUtil.getConfigFileContent("remoteDriverURL"));
                switch (configUtil.getConfigFileContent("broswerType")) {
                case "firefox":
                    DesiredCapabilities dc = DesiredCapabilities.firefox();
                    driver = new RemoteWebDriver(remoteUrl, dc);
                    return driver;
                case "chrome":
                    System.setProperty("webdriver.chrome.driver",
                            System.getProperty("user.dir") + configUtil.getConfigFileContent("chromeDriverPath"));
                    dc = DesiredCapabilities.chrome();
                    driver = new RemoteWebDriver(remoteUrl, dc);
                    return driver;
                case "ie":
                    System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")
                            + configUtil.getConfigFileContent("internetExplorerDriverPath"));
                    dc = DesiredCapabilities.internetExplorer();
                    driver = new RemoteWebDriver(remoteUrl, dc);
                    return driver;
                case "safari":
                    dc = DesiredCapabilities.safari();
                    driver = new RemoteWebDriver(remoteUrl, dc);
                    return driver;
                default:
                    return driver;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return driver;
            }
        }

    }

    public static WebDriver createNewDriver() {
        log.info("Current Driver is null : " + (driver == null));
        if (driver == null) {
            synchronized (WebDriver.class) {
                if (driver == null) {
                    driver = CreateBroswerDriver();
                    driver.manage().window().maximize();
                    driver.get(configUtil.getConfigFileContent("defaultURL"));
                    return driver;
                }
            }
        }
        return driver;
    }

    public static WebDriver getCurrentDriver() {
        return createNewDriver();
    }

    /**
     * Create a new Appium driver for iOS,Android
     * 
     * 
     * @return Appium drive that you want style
     */

    public static AppiumDriver createAppiumDriver() throws MalformedURLException {
        log.info("Current Driver is null : " + (appiumDriver == null));
        if (appiumDriver == null) {
            synchronized (WebDriver.class) {
                if (appiumDriver == null) {
                    DesiredCapabilities capabilities = new DesiredCapabilities();
                    capabilities.setCapability("platformName",
                            ConfigUtil.getConfigUtil().getConfigFileContent("phonePlatform"));
                    capabilities.setCapability("platformVersion",
                            ConfigUtil.getConfigUtil().getConfigFileContent("platformVersion"));
                    capabilities.setCapability("deviceName",
                            ConfigUtil.getConfigUtil().getConfigFileContent("deviceName"));
                    capabilities.setCapability("app",
                            ConfigUtil.getConfigUtil().getConfigFileContent("applactionLocation"));
                    if (ConfigUtil.getConfigUtil().getConfigFileContent("phonePlatform").equals("iOS"))
                        appiumDriver = new IOSDriver(
                                new URL(ConfigUtil.getConfigUtil().getConfigFileContent("appiumDriverURL")),
                                capabilities);
                    else
                        appiumDriver = new AndroidDriver(
                                new URL(ConfigUtil.getConfigUtil().getConfigFileContent("appiumDriverURL")),
                                capabilities);
                    return appiumDriver;
                }
            }
        }
        return appiumDriver;
    }

    /**
     * Close broswer driver
     */
    public static void CloseDriver() {

        driver.quit();
        driver = null;
    }

    /**
     * Close appium driver
     */
    public static void closeAppiumDriver() {

        appiumDriver.quit();
        appiumDriver = null;

    }

}
