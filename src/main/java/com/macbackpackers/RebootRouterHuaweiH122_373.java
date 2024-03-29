
package com.macbackpackers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlContains;

@Component( value = "RebootRouterHuaweiH122_373" )
public class RebootRouterHuaweiH122_373 implements RebootRouter {

    private final Logger LOGGER = LoggerFactory.getLogger( RebootRouterHuaweiH122_373.class );

    @Autowired
    private ChromeWebDriver chrome;

    @Override
    public void reboot( RouterConfig config ) throws IOException {
        String routerUrl = config.getUrl();
        LOGGER.info( "Rebooting " + routerUrl );

        WebDriver driver = chrome.getWebDriver();
        WebDriverWait wait = chrome.getWebDriverWait();
        try {
            driver.get( routerUrl );
            WebElement passinput = wait.until( presenceOfElementLocated( By.id( "login_password" ) ) );

            LOGGER.info( "Sending password..." );
            passinput.sendKeys( config.getPassword() );
            WebElement loginBtn = driver.findElement( By.id( "login_btn" ) );
            LOGGER.info( "Login URL is now " + driver.getCurrentUrl() );
            loginBtn.click();
            wait.until( urlContains( "/content.html" ) );
            LOGGER.info( "POST login URL is now " + driver.getCurrentUrl() );

            LOGGER.info( "Clicking on restart button..." );
            WebElement restartBtn = driver.findElement( By.xpath( "//div[@title='Restart']" ) );
            restartBtn.click();

            LOGGER.info( "Clicking on confirmation button..." );
            WebElement continueBtn = wait.until( presenceOfElementLocated( By.xpath( "//div[text()='Continue']" ) ) );
            continueBtn.click();
            LOGGER.info( "onclick will run: " + continueBtn.getAttribute( "onclick" ) );

            LOGGER.info( "DONE!" );
            LOGGER.info( driver.getPageSource() );
        }
        finally {
            driver.close();
        }
    }

}
