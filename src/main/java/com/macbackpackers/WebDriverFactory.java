package com.macbackpackers;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class WebDriverFactory {

    @Autowired
    private WanMonitorConfiguration config;

    @Bean
    @Scope( value = "prototype" )
    private WebClient getWebClient() {
        WebClient webClient = new WebClient( BrowserVersion.CHROME ); // return a new instance of this when requested
        webClient.getOptions().setThrowExceptionOnFailingStatusCode( false );
        webClient.getOptions().setThrowExceptionOnScriptError( false );
        webClient.getOptions().setJavaScriptEnabled( true );
        webClient.getOptions().setCssEnabled( false );
        webClient.getOptions().setRedirectEnabled( true );
        webClient.getOptions().setUseInsecureSSL( true );
        webClient.setAjaxController( new NicelyResynchronizingAjaxController() );
        webClient.getOptions().setTimeout( 60000 );
        webClient.setJavaScriptTimeout( 60000 );
        return webClient;
    }

    @Bean
    @Scope( value = "prototype" )
    public ChromeWebDriver createWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments( config.getChromeOptions().split( " " ) );
        ChromeDriver driver = new ChromeDriver( options );
        driver.manage().timeouts().pageLoadTimeout( Duration.ofSeconds( config.getChromeMaxWaitSeconds() ) );

        WebDriverWait wait = new WebDriverWait( driver, Duration.ofSeconds( config.getChromeMaxWaitSeconds() ) );
        return new ChromeWebDriver( driver, wait );
    }
}
