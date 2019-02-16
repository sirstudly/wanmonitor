
package com.macbackpackers;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RebootRouterHuaweiB525 {

    private static final Logger LOGGER = LoggerFactory.getLogger( RebootRouterHuaweiB525.class );
    private Properties properties = new Properties();

    public static void main( String argv[] ) throws Exception {
        Properties props = new Properties();
        props.load( RebootRouterHuaweiB525.class.getClassLoader().getResourceAsStream( "config.properties" ) );
        new RebootRouterHuaweiB525( props ).rebootRouter();
    }
    
    public RebootRouterHuaweiB525( Properties props ) throws Exception {
        properties = props;
    }

    public void rebootRouter() throws Exception {
        String routerUrl = properties.getProperty( "router.url" );
        LOGGER.info( "Rebooting " + routerUrl );
        int maxWaitSeconds = Integer.parseInt( properties.getProperty( "chromescraper.maxwait.seconds" ) );
        WebDriver driver = createWebDriver( 
                properties.getProperty( "chromescraper.driver.options" ), 
                maxWaitSeconds );

        // load the login page
        driver.get( routerUrl );
        WebElement settingsAnchor = driver.findElement( By.id( "settings" ) );
        settingsAnchor.click();

        // pop-up dialog should appear
        WebElement usernameBox = driver.findElement( By.id( "username" ) );
        WebElement passwordBox = driver.findElement( By.id( "password" ) );
        WebElement submitButton = driver.findElement( By.id( "pop_login" ) );

        WebDriverWait wait = new WebDriverWait( driver, maxWaitSeconds );
        wait.until( ExpectedConditions.visibilityOfAllElements( usernameBox, passwordBox, submitButton ) );

        usernameBox.clear();
        usernameBox.sendKeys( properties.getProperty( "router.username" ) );
        passwordBox.clear();
        passwordBox.sendKeys( properties.getProperty( "router.password" ) );
        submitButton.click();

        // wait until we've moved onto the next page
        wait.until( ExpectedConditions.stalenessOf( submitButton ) );

        WebElement systemMenu = driver.findElement( By.id( "system" ) );
        systemMenu.click();
        WebElement rebootMenu = driver.findElement( By.id( "reboot" ) );
        rebootMenu.click();
        
        WebElement rebootButton = driver.findElement( By.id( "reboot_apply_button" ) );
        rebootButton.click();
        WebElement confirmButton = driver.findElement( By.id( "pop_confirm" ) );
        confirmButton.click();

        LOGGER.info( "Reboot completed." );
        driver.close();
    }

    public WebDriver createWebDriver(String chromeOptions, int maxWaitSeconds) throws Exception {
        System.setProperty( "webdriver.chrome.driver", getClass().getClassLoader().getResource(
                SystemUtils.IS_OS_WINDOWS ? "chromedriver.exe" : "chromedriver" ).getPath() );

        ChromeOptions options = new ChromeOptions();
        options.addArguments( chromeOptions.split( " " ) );

        WebDriver driver = new ChromeDriver( options );

        // configure wait-time when finding elements on the page
        driver.manage().timeouts().implicitlyWait( maxWaitSeconds, TimeUnit.SECONDS );
        driver.manage().timeouts().pageLoadTimeout( maxWaitSeconds * 2, TimeUnit.SECONDS );

        return driver;
    }
    
}
