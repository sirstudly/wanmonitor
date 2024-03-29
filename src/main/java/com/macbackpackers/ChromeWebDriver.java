package com.macbackpackers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChromeWebDriver {
    private WebDriver webDriver;
    private WebDriverWait webDriverWait;

    public ChromeWebDriver( WebDriver webDriver, WebDriverWait webDriverWait ) {
        this.webDriver = webDriver;
        this.webDriverWait = webDriverWait;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void setWebDriver( WebDriver webDriver ) {
        this.webDriver = webDriver;
    }

    public WebDriverWait getWebDriverWait() {
        return webDriverWait;
    }

    public void setWebDriverWait( WebDriverWait webDriverWait ) {
        this.webDriverWait = webDriverWait;
    }
}

