package org.DriverFactory.Factory;

import java.net.MalformedURLException;
import java.net.URL;

import org.ConfigUtil.ConfigUtil;
import org.LogFactory.LogFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class DriverFactory {
    private static WebDriver driver = null;
    private static AppiumDriver appiumDriver = null;
    private static Logger log = LogFactory.getLogger(DriverFactory.class);
    private static ConfigUtil configUtil = ConfigUtil.getConfigUtil();
    private static String osType = System.getProperty("os.name").toLowerCase();

    private DriverFactory() {
    }

    /**
     * create method to get OS type and auto choose the driver for it
     * 
     * @return
     */
    private static boolean isMacOS() {
        return osType.indexOf("mac") >= 0;
    }

    private static boolean isWindows() {
        return osType.indexOf("window") >= 0;
    }

    /**
     * Create a new driver for FF,CHROME,IE
     * 
     * @return WebDriver that you want style
     * @throws Exception
     */
    private static WebDriver CreateBroswerDriver() throws Exception {
        if (configUtil.getConfigFileContent("isRemoteDriver").equals("false")) {
            switch (configUtil.getConfigFileContent("broswerType")) {
            case "firefox":
                return new FirefoxDriver(switchLocalDriverPath());
            case "chrome":
                return new ChromeDriver(switchLocalDriverPath());
            case "ie":
                return new InternetExplorerDriver(switchLocalDriverPath());
            case "safari":
                return new SafariDriver(switchLocalDriverPath());
            default:
                return driver;
            }
        } else {
            URL remoteUrl = new URL(configUtil.getConfigFileContent("remoteDriverURL"));
            switch (configUtil.getConfigFileContent("broswerType")) {
            case "firefox":
                return new RemoteWebDriver(remoteUrl, switchLocalDriverPath());
            case "chrome":
                return new RemoteWebDriver(remoteUrl, switchLocalDriverPath());
            case "ie":
                return new RemoteWebDriver(remoteUrl, switchLocalDriverPath());
            case "safari":
                return new RemoteWebDriver(remoteUrl, switchLocalDriverPath());
            default:
                return driver;
            }
        }

    }

    public static WebDriver createNewDriver() throws Exception {
        log.info("Current Driver is null : " + (driver == null));
        if (driver == null) {
            synchronized (WebDriver.class) {
                if (driver == null) {
                    driver = CreateBroswerDriver();
                    setUpDriverSize(driver).get(configUtil.getConfigFileContent("defaultURL"));
                    return driver;
                }
            }
        }
        return driver;
    }

    public static WebDriver getCurrentDriver() throws Exception {
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
                    if (ConfigUtil.getConfigUtil().getConfigFileContent("phonePlatform").equals("iOS")) {
                        capabilities.setCapability("autoAcceptAlerts", true);
                        appiumDriver = new IOSDriver(
                                new URL(ConfigUtil.getConfigUtil().getConfigFileContent("appiumDriverURL")),
                                capabilities);
                    } else
                        appiumDriver = new AndroidDriver(
                                new URL(ConfigUtil.getConfigUtil().getConfigFileContent("appiumDriverURL")),
                                capabilities);
                    return appiumDriver;
                }
            }
        }
        return appiumDriver;
    }

    public static DesiredCapabilities switchLocalDriverPath() throws Exception {
        DesiredCapabilities dc;
        if (isMacOS()) {
            switch (configUtil.getConfigFileContent("broswerType")) {
            case "firefox":
                dc = DesiredCapabilities.firefox();
                return dc;
            case "chrome":
                System.setProperty("webdriver.chrome.driver",
                        System.getProperty("user.dir") + configUtil.getConfigFileContent("chromeDriverPathMac"));
                dc = DesiredCapabilities.chrome();
                return dc;
            case "safari":
                dc = DesiredCapabilities.safari();
                return dc;
            default:
                throw new Exception("Don't support this broswer on local!!!!!");
            }
        } else if (isWindows()) {
            switch (configUtil.getConfigFileContent("broswerType")) {
            case "firefox":
                dc = DesiredCapabilities.firefox();
                return dc;
            case "chrome":
                System.setProperty("webdriver.chrome.driver",
                        System.getProperty("user.dir") + configUtil.getConfigFileContent("chromeDriverPath"));
                dc = DesiredCapabilities.chrome();
                return dc;
            case "ie":
                System.setProperty("webdriver.ie.driver",
                        System.getProperty("user.dir") + configUtil.getConfigFileContent("internetExplorerDriverPath"));
                dc = DesiredCapabilities.internetExplorer();
                return dc;
            default:
                throw new Exception("Don't support this broswer on local!!!!!");
            }
        } else
            throw new Exception("Current OS is：" + osType + ", and Not driver to support now !");

    }

    public static WebDriver setUpDriverSize(WebDriver driver) {
        if (StringUtils.isNotEmpty(ConfigUtil.getConfigUtil().getConfigFileContent("resolution"))
                && !ConfigUtil.getConfigUtil().getConfigFileContent("resolution").equals("MaxSize")) {
            String windowResolution = ConfigUtil.getConfigUtil().getConfigFileContent("resolution");
            String resolution[] = windowResolution.split("\\*");
            driver.manage().window()
                    .setSize(new Dimension(Integer.parseInt(resolution[0]), Integer.parseInt(resolution[1])));
            driver.manage().window().maximize();
        } else {
            driver.manage().window().maximize();
        }
        return driver;
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
