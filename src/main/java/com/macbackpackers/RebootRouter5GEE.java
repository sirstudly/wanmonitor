
package com.macbackpackers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.stalenessOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

@Component( value = "RebootRouter5GEE" )
public class RebootRouter5GEE implements RebootRouter {

    private final Logger LOGGER = LoggerFactory.getLogger( RebootRouter5GEE.class );

    @Autowired
    private ChromeWebDriver chrome;

    @Override
    public void reboot( RouterConfig config ) {

        String routerUrl = config.getUrl();
        LOGGER.info( "Rebooting " + routerUrl );

        WebDriver driver = chrome.getWebDriver();
        WebDriverWait wait = chrome.getWebDriverWait();
        try {
            driver.get( routerUrl );
            WebElement loginBtn = wait.until( presenceOfElementLocated( By.xpath( "//input[@value='Log in']" ) ) );
            loginBtn.click();

            wait.until( visibilityOfElementLocated( By.xpath( "//div[text()='Log in password']" ) ) );
            WebElement passinput = wait.until( visibilityOfElementLocated( By.xpath( "//input[@id='userpassword']" ) ) );

            LOGGER.info( "Entering router password..." );
            passinput.sendKeys( config.getPassword() );
            loginBtn = driver.findElement( By.id( "login_btn" ) );
            loginBtn.click();
            wait.until( stalenessOf( loginBtn ) );

            LOGGER.info( "Waiting for loading to complete..." );
            WebElement loadingBox = wait.until( visibilityOfElementLocated( By.id( "LoadingBox" ) ) );
            wait.until( stalenessOf( loadingBox ) );
            LOGGER.info( "Login URL is now " + driver.getCurrentUrl() );

            LOGGER.info( "Navigating to Device Management..." );
            wait.until( visibilityOfElementLocated( By.xpath( "//li/h2[text()='Device']" ) ) ).click();
            LOGGER.info( "Triggering dropdown menu..." );
            wait.until( visibilityOfElementLocated( By.xpath( "//a[contains(text(), 'Device management') and @tabindex]" ) ) ).click();
            LOGGER.info( "Going to Restart and reset page..." );
            wait.until( visibilityOfElementLocated( By.xpath( "(//div[@id='device.deviceMgt.restartReset'])[2]" ) ) ).click();

            LOGGER.info( "Clicking on restart button..." );
            WebElement restartBtn = driver.findElement( By.id( "restart_btn" ) );
            restartBtn.click();
            LOGGER.info( "Clicking on restart confirmation button..." );
            restartBtn = driver.findElement( By.xpath( "//div/button[text()='Restart']" ) );
            restartBtn.click();
            wait.until( stalenessOf( restartBtn ) );

            LOGGER.info( "DONE!" );
            LOGGER.info( driver.getPageSource() );
        }
        finally {
            driver.close();
        }
    }

}
