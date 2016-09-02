package org.DriverFactory.Factory;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import basicTool.StringUtilsTool;
import basicTool.config.ConfigUtil;
import factory.LogFactory;
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
     * create method to get OS type and auto choose the chrome driver for it
     * @return
     */
    private static boolean isMacOS(){
        return osType.indexOf("mac") >= 0;
    }
    private static boolean isWindows(){
        return osType.indexOf("window") >= 0;
    }
    
    public static void switchChromeDriverPath(){
        if(isMacOS())
        {
            System.setProperty("webdriver.chrome.driver",
                    System.getProperty("user.dir") + configUtil.getConfigFileContent("chromeDriverPathMac"));
        }
        else if(isWindows())
        {
            System.setProperty("webdriver.chrome.driver",
                    System.getProperty("user.dir") + configUtil.getConfigFileContent("chromeDriverPath"));
        }
        else {
            System.out.println("Current OS is："+osType+", and Not driver to support now !");
            return;
        }
        
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
//                System.setProperty("webdriver.chrome.driver",
//                        System.getProperty("user.dir") + configUtil.getConfigFileContent("chromeDriverPath"));
                switchChromeDriverPath();
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
//                    System.setProperty("webdriver.chrome.driver",
//                            System.getProperty("user.dir") + configUtil.getConfigFileContent("chromeDriverPath"));
                    switchChromeDriverPath();
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
                    
                    if(StringUtilsTool.isNotEmpty(ConfigUtil.getConfigUtil().getConfigFileContent("resolution")) && (ConfigUtil.getConfigUtil().getConfigFileContent("broswerType")).indexOf("chrome")>=0 )
                    {
                        try {
                            String windowResolution = ConfigUtil.getConfigUtil().getConfigFileContent("resolution");
                            String resolution[] = windowResolution.split("\\*");
                            driver.manage().window().setSize(new Dimension(Integer.parseInt(resolution[0]), Integer.parseInt(resolution[1])));
                        } catch (NumberFormatException e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }
                    }
                    else 
                    {
                        driver.manage().window().maximize();
                    }
                       
                    
                    
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
